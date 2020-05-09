package ru.klaw.steamaudio.simulation;

import ru.klaw.utility.JnaEnum;

public enum SceneType implements JnaEnum<SceneType>{
    /**
     * Phonon's built-in ray tracer which supports multi-threading.
     */
    PHONON,
    /**
     * The Intel Embree ray tracer. This is a highly-optimized multi-threaded CPU
     * implementation, and is likely to be faster than the Phonon ray tracer. However,
     * Embree support requires a 64-bit CPU, and is not available on Android.
     */
    EMBREE,
    /**
     * The AMD Radeon Rays ray tracer. This is an OpenCL implementation, and can
     * use either the CPU or the GPU. If using the GPU, it is likely to be
     * significantly faster than the Phonon ray tracer. However, on heavy
     * real-time simulation workloads, it may impact the application's frame rate.
     */
    RADEONRAYS,
    /**
     * Allows you to specify callbacks to your own ray tracer. Useful if your
     * application already uses a high-performance ray tracer. This option uses
     * the least amount of memory at run-time, since it does not have to build
     * any ray tracing data structures of its own.
     */
    CUSTOM;

    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public SceneType getForValue(int i) {
        return values()[i];
    }
}
