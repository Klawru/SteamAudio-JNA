package ru.klaw.steamaudio.directsound.enums;

import ru.klaw.utility.JnaEnum;

public enum AirAbsorptionModelType implements JnaEnum<AirAbsorptionModelType> {
    /**
     * The default air absorption model.
     * Same as EXPONENTIAL with coefficients = {0.0002, 0.0017, 0.0182}.
     */
    DEFAULT,
    /**
     * An exponential decay model for air absorption. The attenuated amplitude of
     * sound at distance r is exp(-kr), with k being a frequency-dependent
     * coefficient.
     */
    EXPONENTIAL,
    /**
     * A user-specified air absorption model that uses a callback function
     * whenever the air absorption value needs to be calculated.
     */
    CALLBACK;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public AirAbsorptionModelType getForValue(int i) {
        return values()[i];
    }
}
