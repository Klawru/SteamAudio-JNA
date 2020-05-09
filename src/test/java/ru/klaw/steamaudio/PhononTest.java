package ru.klaw.steamaudio;

import com.sun.jna.ptr.PointerByReference;
import lombok.var;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.klaw.UtilityTest;
import ru.klaw.steamaudio.binauralrenderer.HrtfDatabaseType;
import ru.klaw.steamaudio.binauralrenderer.HrtfParams;
import ru.klaw.steamaudio.renderingsettings.ConvolutionType;
import ru.klaw.steamaudio.renderingsettings.RenderingSettings;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.klaw.steamaudio.Phonon.Error.STATUS_SUCCESS;

public class PhononTest {
    protected static final Phonon phonon = Phonon.INSTANCE;
    protected static final int sampleRate = 44100;
    protected static final int frameSize = 1024;
    protected static final int byteToFloat = 4;
    protected static final PointerByReference renderer = new PointerByReference();
    protected static final PointerByReference context = new PointerByReference();
    protected static RenderingSettings renderingSettings;
    protected static byte[] inputAudio;


    @BeforeAll
    static void init() throws IOException {
        int statusCode;
        statusCode = phonon.iplCreateContext(System.out::print, null, null, context);
        assertEquals(STATUS_SUCCESS, statusCode);
        renderingSettings = new RenderingSettings(sampleRate, frameSize, ConvolutionType.PHONON);
        var HrtfParams = new HrtfParams(HrtfDatabaseType.DEFAULT);
        //Create binaural renderer
        statusCode = phonon.iplCreateBinauralRenderer(context.getValue(), renderingSettings, HrtfParams, renderer);
        assertEquals(STATUS_SUCCESS, statusCode);

        inputAudio = UtilityTest.readFile("inputAudio.raw");
    }


    @AfterAll
    static void cleanup() {
        phonon.iplDestroyBinauralRenderer(renderer);
        phonon.iplDestroyBinauralEffect(context);
        phonon.iplCleanup();
    }
}
