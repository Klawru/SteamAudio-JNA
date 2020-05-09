package ru.klaw.steamaudio.directsound.enums;

import ru.klaw.utility.JnaEnum;

public enum  DirectOcclusionMethod implements JnaEnum<DirectOcclusionMethod> {
    /**
     * Performs a rudimentary occlusion test by checking if the ray from the
     * listener to the source is occluded by any scene geometry. If so, the
     * sound will be considered to be completely occluded. The Environment
     * object created by the game engine must have a valid Scene object for
     * this to work.
     */
    RAYCAST,
    /**
     * Performs a slightly more complicated occlusion test: the source is
     * treated as a sphere, and rays are traced from the listener to various
     * points in the interior of the sphere. The proportion of rays that are
     * occluded by scene geometry determines the how much of the sound
     * source is considered occluded. The Environment object created by the
     * game engine must have a valid Scene object for this to work.
     */
    VOLUMETRIC;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public DirectOcclusionMethod getForValue(int i) {
        return values()[i];
    }
}
