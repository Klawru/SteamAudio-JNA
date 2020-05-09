package ru.klaw.steamaudio.directsound.enums;

import ru.klaw.utility.JnaEnum;

/**
 * The method to use when rendering occluded or partially occluded sound.
 *
 * Phonon can model sound passing through solid objects, and optionally apply frequency-dependent transmission filters.
 */
public enum DirectOcclusionMode implements JnaEnum<DirectOcclusionMode> {

    /**
     * Does not perform any occlusion checks. Sound will be
     * audible through solid objects.
     */
     NONE,
    /**
     * Perform occlusion checks but do not model transmission.
     * Occluded sound will be completely inaudible.
     */
     NOTRANSMISSION,
    /**
     * Perform occlusion checks and model transmission; occluded
     * sound will be scaled by a frequency-independent
     * attenuation value. This value is calculated based on the
     * transmission properties of the object occluding the
     * direct sound path.
     */
     TRANSMISSION_BY_VOLUME,
    /**
     * Perform occlusion checks and model transmission; occluded
     * sound will be rendered with a frequency-dependent
     * transmission filter. This filter is calculated based on
     * the transmission properties of the object occluding the
     * direct sound path.
     */
    TRANSMISSION_BY_FREQUENCY;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public DirectOcclusionMode getForValue(int i) {
        return values()[i];
    }
}
