package ru.klaw.steamaudio.renderingsettings;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"samplingRate", "frameSize", "convolutionType"})
public class RenderingSettings extends PhononStructure {
    /**
     * The sampling rate (in Hz) of any audio to be processed by
     * All audio that is passed to Phonon must use the same sampling rate.
     * Phonon will output audio at the same sampling rate as its input;
     * no sampling rate conversion will be performed.
     * Supported sampling rates are 24000 Hz, 44100 Hz, and 48000 Hz.
     * C type : IPLint32
     */
    public int samplingRate;
    /**
     * The number of samples in a single frame of audio.
     * The value of this parameter should be obtained from your audio engine.
     * C type : IPLint32
     */
    public int frameSize;
    /**
     * @see ConvolutionType
     * The convolution algorithm to use for any Convolution Effect
     * objects created for this audio processing pipeline.
     * C type : IPLConvolutionType
     */
    public ConvolutionType convolutionType;

}
