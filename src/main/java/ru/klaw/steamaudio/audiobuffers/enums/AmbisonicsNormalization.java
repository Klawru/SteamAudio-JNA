package ru.klaw.steamaudio.audiobuffers.enums;

import ru.klaw.utility.JnaEnum;

public enum AmbisonicsNormalization implements JnaEnum<AmbisonicsNormalization> {
    /**
     * This is the normalization scheme used in Furse-Malham
     * higher-order Ambisonics. Each channel is normalized to not
     * exceed 1.0, and a -3 dB gain correction is applied to channel 0.
     */
    FURSEMALHAM,
    /**
     * Also called Schmidt semi-normalized form. This is the
     * normalization scheme used in the AmbiX format.
     */
    SN3D,
    /**
     * This normalization scheme is based on the mathematical definition of Ambisonics.
     * It is closely related to SN3D by a series of scaling factors.
     * This normalization scheme is used internally throughout Phonon,
     * and using it results in the fastest
     * performance.
     */
    N3D;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public AmbisonicsNormalization getForValue(int i) {
        return values()[i];
    }
}
