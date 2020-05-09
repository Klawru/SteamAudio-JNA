package ru.klaw.steamaudio.directsound.enums;

import ru.klaw.utility.JnaEnum;

public enum DistanceAttenuationModelType implements JnaEnum<DistanceAttenuationModelType> {
    /**
     * The default distance attenuation model.
     * Same as INVERSEDISTANCE with minDistance = 1.
     */
    DEFAULT,
    /**
     * A physically-based inverse-distance attenuation
     * model. The attenuated amplitude of a source is
     * 1 / max(distance, minDistance), where
     * distance is the length of the path from the
     * source to the listener, and minDistance is a
     * parameter(@see DistanceAttenuationModel).
     */
    INVERSEDISTANCE,
    /**
     * A user-specified distance attenuation model that uses
     * a callback function whenever the distance attenuation
     * value needs to be calculated.
     */
    CALLBACK;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public DistanceAttenuationModelType getForValue(int i) {
        return values()[i];
    }
}
