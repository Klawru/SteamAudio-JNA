package ru.klaw;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@UtilityClass
public class UtilityTest {
    public byte[] readFile(String fileName) throws IOException {
        RandomAccessFile r = new RandomAccessFile(UtilityTest.class.getClassLoader().getResource(fileName).getPath(), "r");
        try (FileChannel channel = r.getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) (channel.size()));
            channel.read(byteBuffer);
            return byteBuffer.array();
        }
    }

    public void saveToFile(String path, byte[] outAudio) throws IOException {
        Files.write(Paths.get(path), outAudio, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}
