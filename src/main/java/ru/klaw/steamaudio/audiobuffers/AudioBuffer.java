package ru.klaw.steamaudio.audiobuffers;

import com.sun.jna.Pointer;
import com.sun.jna.Structure.FieldOrder;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;
import ru.klaw.utility.PointerArray;

@NoArgsConstructor
@FieldOrder({"format", "numSamples", "interleavedBuffer", "deinterleavedBuffer"})
public class AudioBuffer extends PhononStructure {
    /**
     * The format of the audio data.
     * C type : IPLAudioFormat
     */
    public AudioFormat format;
    /**
     * The number of samples in the audio buffer. The total number of
     * elements in the audio buffer is equal to numSamples * format.numSpeakers.
     * C type : IPLint32
     */
    public int numSamples;
    /**
     * A pointer to a contiguous block of memory containing interleaved
     * audio data in the format described by format.
     * Can be NULL if format.channelOrder is DEINTERLEAVED.
     * C type : IPLfloat32*
     */
    public Pointer interleavedBuffer;
    /**
     * A pointer to an array of pointers, each of which points to a block
     * of memory containing audio data for a single channel of audio data
     * in the format described by format.
     * In other words, deinterleaved audio data doesn't have to be stored contiguously in memory.
     * Can be NULL if format.channelOrder is INTERLEAVED.
     * C type : IPLfloat32**
     */
    public PointerArray deinterleavedBuffer;

    public AudioBuffer(AudioFormat format, int numSamples, PointerArray deinterleavedBuffer) {
        this.format = format;
        this.numSamples = numSamples;
        this.deinterleavedBuffer = deinterleavedBuffer;
    }

    public AudioBuffer(AudioFormat format, int numSamples, Pointer interleavedBuffer) {
        this.format = format;
        this.numSamples = numSamples;
        this.interleavedBuffer = interleavedBuffer;
    }

    public void of(AudioFormat format, int numSamples, PointerArray deinterleavedBuffer) {
        this.format = format;
        this.numSamples = numSamples;
        this.deinterleavedBuffer = deinterleavedBuffer;
    }

    public void of(AudioFormat format, int numSamples, Pointer interleavedBuffer) {
        this.format = format;
        this.numSamples = numSamples;
        this.interleavedBuffer = interleavedBuffer;
    }



}
