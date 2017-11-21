package com.tropicode.mediaspider.jobs;

import com.google.common.util.concurrent.*;
import com.tropicode.mediaspider.dto.MediaFile;
import com.tropicode.mediaspider.dto.MediaPath;
import com.tropicode.mediaspider.dto.MediaType;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;

public class FileRepository {

    private static final ListeningExecutorService pool;

    public final int BUFFER_SIZE = 1024 * 1024;

    private Map<Long, MediaFile> fileMap = new HashMap<>();
    private Map<Integer, MediaPath> pathMap = new HashMap<>();
    private List<Integer> rootPathHashes = new ArrayList<>();
    private Set<MediaPath> rootPaths = new HashSet<>();
    private Map<Thread, byte[]> buffers = new HashMap<>();

    static {
        int cores = 1;//Runtime.getRuntime().availableProcessors();
        if (cores > 4) {
            cores -= 2;
        }
        if (cores > 2) {
            cores -= 1;
        }
        pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
    }

    public void registerRootPath(Path path) {
        rootPathHashes.add(path.toAbsolutePath().toString().hashCode());
    }


    public ListenableFuture<Long> registerFile(Path file, BasicFileAttributes attrs, MediaType mediaType) throws IOException {
        ListenableFuture<Long> futureFileHash = hashFile(file);
        Futures.addCallback(futureFileHash, new FutureCallback<Long>() {
            @Override
            public void onSuccess(@Nullable Long hash) {
                MediaFile mediaFile = fileMap.get(futureFileHash);
                if (mediaFile == null) {
                    mediaFile = new MediaFile(file, attrs, mediaType);
                    fileMap.put(hash, mediaFile);
                } else {
                    mediaFile.setEarliestTime(attrs);
                }
                mediaFile.addMediaPath(registerPaths(mediaFile));
            }


            @Override
            public void onFailure(Throwable t) {
                throw new RuntimeException("registerFile failed for " + file, t);
            }
        }, pool);
        return futureFileHash;
    }


    private MediaPath registerPaths(MediaFile file) {
        Path filePath = file.getFilePath();
        MediaPath fileMediaPath = null;
        MediaPath lastMediaPath = null;
        Path parentPath = filePath.getParent();
        while (parentPath != null) {
            int parentHash = parentPath.toAbsolutePath().toString().hashCode();
            MediaPath mediaPath = pathMap.get(parentHash);
            if (mediaPath == null) {
                mediaPath = new MediaPath(parentPath);
                pathMap.put(parentHash, mediaPath);
            }

            parentPath = parentPath.getParent();
            if (rootPathHashes.contains(parentHash) || parentPath == null) {
                mediaPath.setRoot(true);
                rootPaths.add(mediaPath);
                parentPath = null;
            }
            if (lastMediaPath != null) {
                mediaPath.addChild(lastMediaPath);
            }
            if (fileMediaPath == null) {
                fileMediaPath = mediaPath;
            }
            lastMediaPath = mediaPath;
        }
        fileMediaPath.incrementFileCount(file.getMediaType());
        return fileMediaPath;
    }


    private ListenableFuture<Long> hashFile(Path file) throws IOException {
        return pool.submit(() -> {
            byte[] buffer = getBuffer();
            try (FileInputStream in = new FileInputStream(file.toFile());) {
                FileChannel channel = in.getChannel();
                CRC32 crc = new CRC32();
                int length = (int) channel.size();
                MappedByteBuffer mb = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);
                int nGet;
                while (mb.hasRemaining()) {
                    nGet = Math.min(mb.remaining(), BUFFER_SIZE);
                    mb.get(buffer, 0, nGet);
                    crc.update(buffer, 0, nGet);
                }
                return crc.getValue();
            }
        });
    }


    private byte[] getBuffer() {
        byte[] buffer = buffers.get(Thread.currentThread());
        if (buffer == null) {
            buffer = new byte[BUFFER_SIZE];
        }
        buffers.put(Thread.currentThread(), buffer);
        return buffer;
    }


    public Map<Long, MediaFile> getFileMap() {
        return fileMap;
    }


    public List<Integer> getRootPathHashes() {
        return rootPathHashes;
    }


    public Set<MediaPath> getRootPaths() {
        return rootPaths;
    }


    public void setRootPaths(Set<MediaPath> rootPaths) {
        this.rootPaths = rootPaths;
    }
}
