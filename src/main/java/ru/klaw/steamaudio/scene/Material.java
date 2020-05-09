package ru.klaw.steamaudio.scene;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

/**
 * The acoustic properties of a surface.
 * <p>
 * You can specify the acoustic material properties of each triangle,
 * although typically many triangles will share a common material.
 * The acoustic material properties are specified for three frequency
 * bands with center frequencies of 400 Hz, 2.5 KHz, and 15 KHz.
 * <p>
 * Below are the acoustic material properties for a few standard materials.
 * {"generic",{0.10f,0.20f,0.30f,0.05f,0.100f,0.050f,0.030f}}
 * {"brick",{0.03f,0.04f,0.07f,0.05f,0.015f,0.015f,0.015f}}
 * {"concrete",{0.05f,0.07f,0.08f,0.05f,0.015f,0.002f,0.001f}}
 * {"ceramic",{0.01f,0.02f,0.02f,0.05f,0.060f,0.044f,0.011f}}
 * {"gravel",{0.60f,0.70f,0.80f,0.05f,0.031f,0.012f,0.008f}},
 * {"carpet",{0.24f,0.69f,0.73f,0.05f,0.020f,0.005f,0.003f}}
 * {"glass",{0.06f,0.03f,0.02f,0.05f,0.060f,0.044f,0.011f}}
 * {"plaster",{0.12f,0.06f,0.04f,0.05f,0.056f,0.056f,0.004f}}
 * {"wood",{0.11f,0.07f,0.06f,0.05f,0.070f,0.014f,0.005f}}
 * {"metal",{0.20f,0.07f,0.06f,0.05f,0.200f,0.025f,0.010f}}
 * {"rock",{0.13f,0.20f,0.24f,0.05f,0.015f,0.002f,0.001f}}
 */
@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"lowFreqAbsorption", "midFreqAbsorption", "highFreqAbsorption", "scattering", "lowFreqTransmission", "midFreqTransmission", "highFreqTransmission"})
public class Material extends PhononStructure {
    /**
     * Fraction of sound energy absorbed at low frequencies. Between 0.0 and 1.0.
     * C type : IPLfloat32
     */
    public float lowFreqAbsorption;
    /**
     * Fraction of sound energy absorbed at middle frequencies. Between 0.0 and 1.0.
     * C type : IPLfloat32
     */
    public float midFreqAbsorption;
    /**
     * Fraction of sound energy absorbed at high frequencies. Between 0.0 and
     * 1.0.
     * C type : IPLfloat32
     */
    public float highFreqAbsorption;
    /**
     * Fraction of sound energy that is scattered in a random direction when
     * it reaches the surface. Between 0.0 and 1.0. A value of 0.0 describes
     * a smooth surface with mirror-like reflection properties; a value of 1.0
     * describes rough surface with diffuse reflection properties.
     * C type : IPLfloat32
     */
    public float scattering;
    /**
     * Fraction of sound energy transmitted through at low frequencies.
     * Between 0.0 and 1.0.
     * <b>Used only for direct sound occlusion calculations</b>.
     * C type : IPLfloat32
     */
    public float lowFreqTransmission;
    /**
     * Fraction of sound energy transmitted through at middle frequencies.
     * Between 0.0 and 1.0.
     * <b>Used only for direct sound occlusion calculations</b>.
     * C type : IPLfloat32
     */
    public float midFreqTransmission;
    /**
     * Fraction of sound energy transmitted through at high frequencies.
     * Between 0.0 and 1.0.
     * <b>Used only for direct sound occlusion calculations</b>.
     * C type : IPLfloat32
     */
    public float highFreqTransmission;

    public Material of(float lowFreqAbsorption, float midFreqAbsorption, float highFreqAbsorption,
                       float scattering,
                       float lowFreqTransmission, float midFreqTransmission, float highFreqTransmission) {

        this.lowFreqAbsorption = lowFreqAbsorption;
        this.midFreqAbsorption = midFreqAbsorption;
        this.highFreqAbsorption = highFreqAbsorption;
        this.scattering = scattering;
        this.lowFreqTransmission = lowFreqTransmission;
        this.midFreqTransmission = midFreqTransmission;
        this.highFreqTransmission = highFreqTransmission;
        return this;
    }


    @NoArgsConstructor
    public static class ByReference extends Material implements Structure.ByReference {
    }

    @NoArgsConstructor
    public static class ByValue extends Material implements Structure.ByValue {
    }

}
