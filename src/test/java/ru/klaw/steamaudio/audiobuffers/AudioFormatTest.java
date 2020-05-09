package ru.klaw.steamaudio.audiobuffers;

import com.sun.jna.ptr.PointerByReference;
import org.junit.jupiter.api.Test;
import ru.klaw.steamaudio.Phonon;
import ru.klaw.steamaudio.PhononTest;
import ru.klaw.steamaudio.audiobuffers.enums.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AudioFormatTest extends PhononTest {

    @Test
    void constructorTest() {
        AudioFormat audioFormat = new AudioFormat();
    }

    @Test
    void constructorTest2() {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.INTERLEAVED);
    }

    @Test
    void constructorTest3() {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.AMBISONICS, 0, AmbisonicsOrdering.ACN, AmbisonicsNormalization.FURSEMALHAM, ChannelOrder.DEINTERLEAVED);
    }

    @Test
    void createBinauralEffect() {
        AudioFormat audioFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.INTERLEAVED);
        PointerByReference effect = new PointerByReference();
        int status = phonon.iplCreateBinauralEffect(renderer.getValue(), audioFormat, audioFormat, effect);
        assertEquals(Phonon.Error.STATUS_SUCCESS, status);
        phonon.iplDestroyBinauralEffect(effect);
    }
}