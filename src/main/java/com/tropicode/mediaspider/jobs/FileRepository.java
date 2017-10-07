package com.tropicode.mediaspider.jobs;

import com.tropicode.mediaspider.dto.MediaFile;
import com.tropicode.mediaspider.dto.MediaPath;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class FileRepository {

    public final int BUFFER_SIZE = 1024 * 1024;

    private Map<Long, MediaFile> fileMap = new HashMap<>();
    private Map<Integer, MediaPath> pathMap = new HashMap<>();
    private byte[] bytes = new byte[BUFFER_SIZE];


    public FileVisitResult registerFile(Path file, BasicFileAttributes attrs) throws Exception {
        long fileHash = hashFile(file);
        MediaFile mediaFile = fileMap.get(fileHash);
        if (mediaFile == null) {
            mediaFile = new MediaFile(file, attrs);
            fileMap.put(fileHash, mediaFile);
        } else {
            mediaFile.addDuplicate();
            mediaFile.setEarliestTime(attrs);
        }

        registerPaths(file.getParent());
        return FileVisitResult.CONTINUE;
    }


    private void registerPaths(Path path) {

    }


    private long hashFile(Path file) throws Exception {
        try (FileInputStream in = new FileInputStream(file.toFile());) {
            FileChannel channel = in.getChannel();
            CRC32 crc = new CRC32();
            int length = (int) channel.size();
            MappedByteBuffer mb = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);
            int nGet;
            while (mb.hasRemaining()) {
                nGet = Math.min(mb.remaining(), BUFFER_SIZE);
                mb.get(bytes, 0, nGet);
                crc.update(bytes, 0, nGet);
            }
            return crc.getValue();
        }
    }
}
