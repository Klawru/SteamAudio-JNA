package ru.klaw.steamaudio.acousticprobes;

import ru.klaw.utility.JnaEnum;

public enum ProbePlacement implements JnaEnum<ProbePlacement> {
    /**
     * Places a single probe in the center of the box. The radius of the probe is
     * large enough to fill the interior of the box.
     */
    CENTROID,
    /**
     * Generates probes throughout the volume of the box. The algorithm is adaptive,
     * and generates more probes in regions of higher geometric complexity, and
     * fewer probes around empty space.
     * @deprecated This option is currently not supported.
     */
    @Deprecated
    OCTREE ,
    /**
     * Generates probes that are uniformly-spaced, at a fixed height above solid
     * geometry. A probe will never be generated above another probe unless there is
     * a solid object between them. The goal is to model floors or terrain, and
     * generate probes that are a fixed height above the floor or terrain, and
     * uniformly-spaced along the horizontal plane. This algorithm is not suitable
     * for scenarios where the listener may fly into a region with no probes;
     * if this happens, the listener will not be influenced by any of the baked
     * data.
     */
    UNIFORMFLOOR;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public ProbePlacement getForValue(int i) {
        return values()[i];
    }
}
