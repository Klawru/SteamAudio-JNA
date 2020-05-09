package ru.klaw.steamaudio.renderingsettings;

import ru.klaw.utility.JnaEnum;

public enum ConvolutionType implements JnaEnum<ConvolutionType>{
    /**
     * Phonon's built-in convolution algorithm. This is a highly optimized,
     * but single-threaded CPU-based implementation. With this implementation,
     * there is a significant performance advantage to using
     * ::iplGetMixedEnvironmentalAudio compared to using
     * ::iplGetWetAudioForConvolutionEffect.
     */
    PHONON,
    /**
     * The AMD TrueAudio Next convolution algorithm. This is GPU-based
     * implementation, that requires an AMD GPU that supports
     * AMD TrueAudio Next. With this implementation, there is no major
     * performance advantage to using ::iplGetMixedEnvironmentalAudio
     * as compared to using ::iplGetWetAudioForConvolutionEffect.
     */
    TRUEAUDIONEXT;

    @Override
    public int getIntValue() {
        return this.ordinal();
    }

    @Override
    public ConvolutionType getForValue(int i) {
        return values()[i];
    }
}
