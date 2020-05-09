package ru.klaw.steamaudio.audiobuffers;

import com.sun.jna.Memory;
import org.junit.jupiter.api.Test;
import ru.klaw.steamaudio.PhononTest;
import ru.klaw.UtilityTest;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayout;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayoutType;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelOrder;
import ru.klaw.utility.PointerArray;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AudioBufferTest extends PhononTest {

    @Test
    void constructorTest() {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.MONO, ChannelOrder.INTERLEAVED);
        Memory memory = new Memory(frameSize * byteToFloat);
        AudioBuffer audioBuffer = new AudioBuffer(audioFormat, 1024, memory);
    }

    @Test
    void constructorTest2() {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.INTERLEAVED);
        Memory memory = new Memory(frameSize * byteToFloat * 2);
        AudioBuffer audioBuffer = new AudioBuffer(audioFormat, 1024, memory);
        assertNotNull(audioBuffer.interleavedBuffer);
    }

    @Test
    void constructorTest3() throws IOException {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.DEINTERLEAVED);
        Memory[] data = new Memory[2];
        data[0] = new Memory(frameSize * byteToFloat*2);//Left channel
        data[1] = (Memory) data[0].share(frameSize*byteToFloat);//Right channel

        AudioBuffer audioBuffer = new AudioBuffer(audioFormat, 1024, new PointerArray(data));
        assertNotNull(audioBuffer.deinterleavedBuffer);
    }

    @Test
    void mixAudioBuffer() throws IOException {
        byte[] rain = UtilityTest.readFile("rain.raw");
        byte[] thunder = UtilityTest.readFile("thunder.raw");
        int maxSize = Math.max(rain.length, thunder.length);

        AudioBuffer[] inputs = (AudioBuffer[]) new AudioBuffer().toArray(2);
        inputs[0].of(AudioFormat.mono(), maxSize / byteToFloat, new Memory(maxSize));
        inputs[1].of(AudioFormat.mono(), maxSize / byteToFloat, new Memory(maxSize));
        inputs[0].interleavedBuffer.write(0, rain, 0, rain.length);
        inputs[1].interleavedBuffer.write(0, thunder, 0, thunder.length);

        Memory out = new Memory(maxSize);
        AudioBuffer output = new AudioBuffer(AudioFormat.mono(), maxSize / 4, out);
        phonon.iplMixAudioBuffers(2, inputs, output);

        //UtilityTest.saveToFile("rainThunderMix.raw",out.getByteArray(0,maxSize));
    }


}