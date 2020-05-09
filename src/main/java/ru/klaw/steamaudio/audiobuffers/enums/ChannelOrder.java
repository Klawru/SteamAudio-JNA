package ru.klaw.steamaudio.audiobuffers.enums;

import ru.klaw.utility.JnaEnum;

public enum ChannelOrder implements JnaEnum<ChannelOrder> {
    /**
     * Sample values for each channel are stored one after another, followed by
     * the next set of sample values for each channel, etc. In the case of
     * 2-channel stereo, this would correspond to **LRLRLRLR...**
     */
    INTERLEAVED,
    /**
     * All sample values for the first channel are stored one after another,
     * followed by the sample values for the next channel, etc. In the case of
     * 2-channel stereo, this would correspond to **LLLL...RRRR...**
     */
    DEINTERLEAVED;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public ChannelOrder getForValue(int i) {
        return values()[i];
    }
}
