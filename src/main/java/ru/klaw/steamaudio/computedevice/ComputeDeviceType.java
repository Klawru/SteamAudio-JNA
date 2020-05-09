package ru.klaw.steamaudio.computedevice;


import ru.klaw.utility.JnaEnum;

public enum ComputeDeviceType implements JnaEnum<ComputeDeviceType>{
    /**
     * Use a CPU device only.
     */
    CPU,
    /**
     * Use a GPU device only.
     */
    GPU,
    /**
     * Use either a CPU or GPU device, whichever is listed first by the driver.
     */
    ANY;

    @Override
    public int getIntValue() {
        return this.ordinal();
    }

    @Override
    public ComputeDeviceType getForValue(int i) {
        return ComputeDeviceType.values()[i];
    }
}
