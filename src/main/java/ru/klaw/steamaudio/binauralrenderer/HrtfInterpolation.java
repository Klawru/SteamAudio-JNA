package ru.klaw.steamaudio.binauralrenderer;

import ru.klaw.utility.JnaEnum;

public enum HrtfInterpolation implements JnaEnum<HrtfInterpolation> {
    /**
     * Nearest-neighbor filtering, i.e., no interpolation. Selects the
     * measurement location that is closest to the source's actual location.
     */
    NEAREST,
    /**
     * Bilinear filtering. Incurs a relatively high CPU overhead as compared to
     * nearest-neighbor filtering, so use this for sounds where it has a
     * significant benefit.
     */
    BILINEAR;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public HrtfInterpolation getForValue(int i) {
        return values()[i];
    }
}
