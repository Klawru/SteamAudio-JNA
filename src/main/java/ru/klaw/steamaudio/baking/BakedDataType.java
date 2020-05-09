package ru.klaw.steamaudio.baking;

import ru.klaw.utility.JnaEnum;

public enum BakedDataType implements JnaEnum<BakedDataType> {
    /**
     * Baked sound propagation from a static source to a moving listener.
     */
    STATICSOURCE,
    /**
     * Baked sound propagation from a moving source to a static listener.
     */
    STATICLISTENER,
    /**
     * Baked listener-centric reverb.
     */
    REVERB;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public BakedDataType getForValue(int i) {
        return values()[i];
    }
}
