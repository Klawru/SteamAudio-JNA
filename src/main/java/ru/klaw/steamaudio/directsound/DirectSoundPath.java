package ru.klaw.steamaudio.directsound;

import com.sun.jna.Structure;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.geometry.Vector3;
import ru.klaw.utility.PhononStructure;

/**
 * Parameters describing a direct sound path.
 *
 * For each frequency band, the attenuation factor applied to the direct sound path is:
 *
 * distanceAttenuation * airAbsorption * (occlusionFactor + (1 - occlusionFactor) * transmissionFactor)
 */
@NoArgsConstructor
@Structure.FieldOrder({"direction", "distanceAttenuation", "airAbsorption", "propagationDelay", "occlusionFactor", "transmissionFactor", "directivityFactor"})
public class DirectSoundPath extends PhononStructure {
    /**
     * Unit vector from the listener to the source.
     * C type : IPLVector3
     */
    public Vector3 direction;
    /**
     * Scaling factor to apply to direct sound, that arises due to the
     * spherical attenuation of sound with distance from the source.
     * Linear scale from 0.0 to 1.0.
     * C type : IPLfloat32
     */
    public float distanceAttenuation;
    /**
     * Scaling factors to apply to direct sound, for low, middle, and high
     * frequencies, that arise due to the scattering of sound waves as they
     * travel through the air. Linear scale from 0.0 to 1.0.
     * C type : IPLfloat32[3]
     */
    public float[] airAbsorption = new float[3];
    /**
     * Time delay (in seconds) due to propagation from the source to the listener.
     * C type : IPLfloat32
     */
    public float propagationDelay;
    /**
     * Scaling factor to apply to direct sound, that arises due to occlusion
     * by scene geometry. Linear scale from 0.0 to 1.0.
     * C type : IPLfloat32
     */
    public float occlusionFactor;
    /**
     * Scaling factors to apply to direct sound, for low, middle, and high
     * frequencies, that arise due to the transmission of sound waves through
     * scene geometry. Linear scale from 0.0 to 1.0.
     * C type : IPLfloat32[3]
     */
    public float[] transmissionFactor = new float[3];
    /**
     * Scaling factor to apply to direct sound, that arises due to the
     * directivity pattern of the source. Linear scale from 0.0 to 1.0.
     * C type : IPLfloat32
     */
    public float directivityFactor;

    /**
     * @param direction           Unit vector from the listener to the source.
     *                            C type : IPLVector3
     * @param distanceAttenuation Scaling factor to apply to direct sound, that arises due to the
     *                            spherical attenuation of sound with distance from the source.
     *                            Linear scale from 0.0 to 1.0.
     *                            C type : IPLfloat32
     * @param airAbsorption       Scaling factors to apply to direct sound, for low, middle, and high
     *                            frequencies, that arise due to the scattering of sound waves as they
     *                            travel through the air. Linear scale from 0.0 to 1.0.
     *                            C type : IPLfloat32[3]
     * @param propagationDelay    Time delay (in seconds) due to propagation from the source to the
     *                            listener.
     *                            C type : IPLfloat32
     * @param occlusionFactor     Scaling factor to apply to direct sound, that arises due to occlusion
     *                            by scene geometry. Linear scale from 0.0 to 1.0.
     *                            C type : IPLfloat32
     * @param transmissionFactor  Scaling factors to apply to direct sound, for low, middle, and high
     *                            frequencies, that arise due to the transmission of sound waves through
     *                            scene geometry. Linear scale from 0.0 to 1.0.
     *                            C type : IPLfloat32[3]
     * @param directivityFactor   Scaling factor to apply to direct sound, that arises due to the
     *                            directivity pattern of the source. Linear scale from 0.0 to 1.0.
     *                            C type : IPLfloat32
     */
    public DirectSoundPath(Vector3 direction, float distanceAttenuation, float[] airAbsorption, float propagationDelay, float occlusionFactor, float[] transmissionFactor, float directivityFactor) {
        super();
        this.direction = direction;
        this.distanceAttenuation = distanceAttenuation;
        if ((airAbsorption.length != this.airAbsorption.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.airAbsorption = airAbsorption;
        this.propagationDelay = propagationDelay;
        this.occlusionFactor = occlusionFactor;
        if ((transmissionFactor.length != this.transmissionFactor.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.transmissionFactor = transmissionFactor;
        this.directivityFactor = directivityFactor;
    }

    @NoArgsConstructor
    public static class ByReference extends DirectSoundPath implements Structure.ByReference {

        public ByReference(Vector3 direction, float distanceAttenuation, float[] airAbsorption, float propagationDelay, float occlusionFactor, float[] transmissionFactor, float directivityFactor) {
            super(direction, distanceAttenuation, airAbsorption, propagationDelay, occlusionFactor, transmissionFactor, directivityFactor);
        }
    }

    @NoArgsConstructor
    public static class ByValue extends DirectSoundPath implements Structure.ByValue {

        public ByValue(Vector3 direction, float distanceAttenuation, float[] airAbsorption, float propagationDelay, float occlusionFactor, float[] transmissionFactor, float directivityFactor) {
            super(direction, distanceAttenuation, airAbsorption, propagationDelay, occlusionFactor, transmissionFactor, directivityFactor);
        }
    }

}
