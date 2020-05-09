package ru.klaw.steamaudio.binauralrenderer;

import ru.klaw.utility.JnaEnum;

public enum HrtfDatabaseType implements JnaEnum<HrtfDatabaseType> {
    /**
     * The built-in HRTF database.
     */
    DEFAULT,
    /**
     * An HRTF database loaded from a SOFA file. SOFA is an AES standard
     * file format for storing and exchanging acoustic data, including HRTFs.
     * For more information on the SOFA format, see
     * https://www.sofaconventions.org/
     */
    SOFA;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public HrtfDatabaseType getForValue(int i) {
        return values()[i];
    }
}
