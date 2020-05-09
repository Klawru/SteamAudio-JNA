package ru.klaw.steamaudio.directsound;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.geometry.Vector3;
import ru.klaw.utility.PhononStructure;

/**
 * Specifies information associated with a sound source.
 */
@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"position", "ahead", "up", "right", "directivity", "distanceAttenuationModel", "airAbsorptionModel"})
public class Source extends PhononStructure {
    /**
     * World-space position of the source.
     * C type : IPLVector3
     */
    public Vector3 position;
    /**
     * Unit vector pointing forwards from the source.
     * C type : IPLVector3
     */
    public Vector3 ahead;
    /**
     * Unit vector pointing upwards from the source.
     * C type : IPLVector3
     */
    public Vector3 up;
    /**
     * Unit vector pointing to the right of the source.
     * C type : IPLVector3
     */
    public Vector3 right;
    /**
     * The source's directivity pattern.
     * C type : IPLDirectivity
     */
    public Directivity directivity;
    /**
     * The source's distance attenuation model.
     * C type : IPLDistanceAttenuationModel
     */
    public DistanceAttenuationModel distanceAttenuationModel;
    /**
     * The source's air absorption model.
     * C type : IPLAirAbsorptionModel
     */
    public AirAbsorptionModel airAbsorptionModel;

}
