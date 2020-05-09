package ru.klaw.steamaudio.audiobuffers.enums;

import ru.klaw.utility.JnaEnum;

public enum AmbisonicsOrdering  implements JnaEnum<AmbisonicsOrdering> {
    /**
     * Specifies the Furse-Malham (FuMa) channel ordering. This is an
     * extension of traditional B-format encoding to higher-order
     * Ambisonics.
     */
    FURSEMALHAM,
    /**
     * Specifies the Ambisonics Channel Number scheme for channel ordering.
     * This is the new standard adopted by the AmbiX Ambisonics format. The
     * position of each Ambisonics channel is uniquely calculated as
     * \f$ ACN = l^2 + l + m \f$.
     */
    ACN ;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public AmbisonicsOrdering getForValue(int i) {
        return values()[i];
    }
}
