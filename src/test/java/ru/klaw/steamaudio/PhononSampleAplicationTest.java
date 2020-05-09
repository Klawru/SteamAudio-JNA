package ru.klaw.steamaudio;

import com.sun.jna.Memory;
import com.sun.jna.ptr.PointerByReference;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.klaw.UtilityTest;
import ru.klaw.steamaudio.audiobuffers.AudioBuffer;
import ru.klaw.steamaudio.audiobuffers.AudioFormat;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayout;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayoutType;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelOrder;
import ru.klaw.steamaudio.binauralrenderer.HrtfDatabaseType;
import ru.klaw.steamaudio.binauralrenderer.HrtfInterpolation;
import ru.klaw.steamaudio.binauralrenderer.HrtfParams;
import ru.klaw.steamaudio.geometry.Vector3;
import ru.klaw.steamaudio.renderingsettings.ConvolutionType;
import ru.klaw.steamaudio.renderingsettings.RenderingSettings;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.klaw.steamaudio.Phonon.Error.STATUS_SUCCESS;

class PhononSampleAplicationTest {

    static byte[] inputAudio;
    private static final int byteToFloat = 4;

    @BeforeAll
    static void setUp() throws IOException {
        //Load mono raw audio in format 32-bit float.
        //You can use free tools like Audacity to create and listen to such audio files.
        inputAudio = UtilityTest.readFile("inputAudio.raw");
    }

    @Test
    void IntegrationBinauralEffectTest() throws IOException {
        Phonon phonon = Phonon.INSTANCE;

        int statusCode;
        var context = new PointerByReference();
        statusCode = phonon.iplCreateContext(System.out::println, null, null, context);
        assertEquals(STATUS_SUCCESS, statusCode);

        int sampleRate = 44100;
        int frameSize = 1024;

        var RenderingSettings = new RenderingSettings(sampleRate, frameSize, ConvolutionType.PHONON);
        var HrtfParams = new HrtfParams(HrtfDatabaseType.DEFAULT);

        var renderer = new PointerByReference();
        //Create binaural renderer
        statusCode = phonon.iplCreateBinauralRenderer(context.getValue(), RenderingSettings, HrtfParams, renderer);
        assertEquals(STATUS_SUCCESS, statusCode);

        var mono = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.MONO, ChannelOrder.INTERLEAVED);
        var stereo = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.INTERLEAVED);

        PointerByReference effect = new PointerByReference();
        statusCode = phonon.iplCreateBinauralEffect(renderer.getValue(), mono, stereo, effect);
        assertEquals(STATUS_SUCCESS, statusCode);

        //Audio Buffers
        Memory inMemory = new Memory(inputAudio.length);
        inMemory.write(0, inputAudio, 0, inputAudio.length);
        var inBuffer = new AudioBuffer(mono, frameSize, inMemory);
        //frameSize * 2 (Stereo)
        Memory outMemory = getFloatArray(frameSize * 2);
        var outBuffer = new AudioBuffer(stereo, frameSize, outMemory);

        byte[] outAudio = new byte[inputAudio.length * 2];
        Vector3 vector = new Vector3(1, 1, 1);
        for (int i = 0; i < inputAudio.length / (frameSize * byteToFloat); i++) {
            phonon.iplApplyBinauralEffect(effect.getValue(), renderer.getValue(), inBuffer, vector, HrtfInterpolation.NEAREST, 1f, outBuffer);
            outMemory.read(0, outAudio, (int) outMemory.size() * i, (int) outMemory.size());

            inBuffer.interleavedBuffer = inBuffer.interleavedBuffer.share(frameSize * byteToFloat);
        }
        //Once we've finished using Steam Audio, we must destroy the objects created using the Steam Audio API, and perform last-minute cleanup:
        phonon.iplDestroyBinauralEffect(effect);
        phonon.iplDestroyBinauralRenderer(renderer);
        phonon.iplDestroyContext(context);
        phonon.iplCleanup();

        //The last step is to write the processed audio to disk:
        //Utility.saveToFile("out.raw", outAudio);
    }

    static public Memory getFloatArray(long size) {
        return new Memory(size * byteToFloat);
    }




}