package ru.klaw.steamaudio.audiobuffers.enums;

import ru.klaw.utility.JnaEnum;

public enum ChannelLayoutType  implements JnaEnum<ChannelLayoutType>{
    /**
     * Indicates that each channel of audio data is intended to be played
     * back by a single speaker. This corresponds to most multi-speaker mono,
     * stereo, or surround sound configurations.
     */
    SPEAKERS,
    /**
     * Indicates that each channel of audio data is to be interpreted as a
     * series of Ambisonics coefficients. Playing back such an audio buffer
     * requires a software or hardware Ambisonics decoder. Phonon contains a
     * software Ambisonics decoder.
     */
    AMBISONICS;


    @Override
    public int getIntValue() {
        return this.ordinal();
    }

    @Override
    public ChannelLayoutType getForValue(int i) {
        return values()[i];
    }
}
