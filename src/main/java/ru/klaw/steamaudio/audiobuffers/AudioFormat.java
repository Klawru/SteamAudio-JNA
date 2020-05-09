package ru.klaw.steamaudio.audiobuffers;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.audiobuffers.enums.*;
import ru.klaw.utility.PhononStructure;

@AllArgsConstructor
@NoArgsConstructor
@FieldOrder({"channelLayoutType", "channelLayout", "numSpeakers", "speakerDirections", "ambisonicsOrder", "ambisonicsOrdering", "ambisonicsNormalization", "channelOrder"})
public class AudioFormat extends PhononStructure implements Structure.ByValue {
    /**
     * @see ChannelLayoutType
     * Indicates whether or not the audio should be
     * interpreted as Ambisonics data.
     * C type : IPLChannelLayoutType
     */
    public ChannelLayoutType channelLayoutType;
    /**
     * @see ChannelLayout
     * Specifies the speaker configuration used for
     * multi-channel, speaker-based audio data.
     * Ignored if channelLayoutType is AMBISONICS.
     * C type : IPLChannelLayout
     */
    public ChannelLayout channelLayout;
    /**
     * The number of channels in the audio data.
     * Only used if channelLayoutType is SPEAKERS and channelLayout is CUSTOM.
     * C type : IPLint32
     */
    public int numSpeakers;
    /**
     * An array of IPLVector3 objects indicating the direction of each speaker relative to the user.
     * Only used if channelLayoutType is  SPEAKERS and channelLayout is  CUSTOM.
     * Can be NULL.
     * C type : IPLVector3*
     */
    public Pointer speakerDirections;
    /**
     * The order of Ambisonics to use. Must be between 0 and 3.
     * Ignored if channelLayoutType is SPEAKERS.
     * C type : IPLint32
     */
    public int ambisonicsOrder;
    /**
     * @see AmbisonicsOrdering
     * The ordering of Ambisonics channels within the data.
     * Ignored if channelLayoutType is SPEAKERS.
     * C type : IPLAmbisonicsOrdering
     */
    public AmbisonicsOrdering ambisonicsOrdering;
    /**
     * @see AmbisonicsNormalization
     * The normalization scheme used for Ambisonics data.
     * Ignored if channelLayoutType is SPEAKERS.
     * C type : IPLAmbisonicsNormalization
     */
    public AmbisonicsNormalization ambisonicsNormalization;
    /**
     * @see ChannelOrder
     * Whether the audio data is interleaved or deinterleaved.
     * C type : IPLChannelOrder
     */
    public ChannelOrder channelOrder;

    /**
     * Constructor for ChannelLayoutType is SPEAKERS and channelLayout is'n CUSTOM.
     */
    public AudioFormat(ChannelLayoutType channelLayoutType, ChannelLayout channelLayout, ChannelOrder channelOrder) {
        this.channelLayoutType = channelLayoutType;
        this.channelLayout = channelLayout;
        this.channelOrder = channelOrder;
    }

    /**
     * Constructor for ChannelLayoutType is SPEAKERS and channelLayout is CUSTOM.
     */
    public AudioFormat(ChannelLayoutType channelLayoutType, int ambisonicsOrder, AmbisonicsOrdering ambisonicsOrdering, AmbisonicsNormalization ambisonicsNormalization, ChannelOrder channelOrder) {
        this.channelLayoutType = channelLayoutType;
        this.ambisonicsOrder = ambisonicsOrder;
        this.ambisonicsOrdering = ambisonicsOrdering;
        this.ambisonicsNormalization = ambisonicsNormalization;
        this.channelOrder = channelOrder;
    }

    /**
     * Constructor for ChannelLayoutType is Ambisonics
     */
    public AudioFormat(ChannelLayoutType channelLayoutType, ChannelLayout channelLayout, int numSpeakers, Pointer speakerDirections, ChannelOrder channelOrder) {
        this.channelLayoutType = channelLayoutType;
        this.channelLayout = channelLayout;
        this.numSpeakers = numSpeakers;
        this.speakerDirections = speakerDirections;
        this.channelOrder = channelOrder;
    }

    public static AudioFormat mono(){
        return new AudioFormat(ChannelLayoutType.SPEAKERS,ChannelLayout.MONO,ChannelOrder.INTERLEAVED);
    }
    public static AudioFormat stereo(){
        return new AudioFormat(ChannelLayoutType.SPEAKERS,ChannelLayout.STEREO,ChannelOrder.INTERLEAVED);
    }

}
