package ru.klaw.steamaudio.simulation;

import ru.klaw.utility.JnaEnum;

public enum SimulationType implements JnaEnum<SimulationType>{
        /**
         * Real-time simulation. Sound propagation from all sound sources is
         * constantly updated in a separate thread, as the player moves and interacts
         * with the scene. This is a very performance-intensive approach, and requires
         * the user to have a powerful PC for optimal results. This is also the type
         * of simulation to choose when generating baked data.
         */
        REALTIME,
        /**
         * Simulation using baked data. If baked data has been generated for the scene
         * and sound sources, simulation will be carried out by looking up information
         * from the baked data. This approach has much lower CPU usage than real-time
         * simulation, but at the cost of increased memory usage.
         */
        BAKED;


    @Override
    public int getIntValue() {
        return ordinal();
    }

    @Override
    public SimulationType getForValue(int i) {
        return values()[i];
    }
}
