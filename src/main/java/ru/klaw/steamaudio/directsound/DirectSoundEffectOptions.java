package ru.klaw.steamaudio.directsound;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.directsound.enums.DirectOcclusionMode;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"applyDistanceAttenuation", "applyAirAbsorption", "applyDirectivity", "directOcclusionMode"})
public class DirectSoundEffectOptions extends PhononStructure implements Structure.ByValue {
    /**
     * Whether to apply distance attenuation.
     * C type : IPLbool
     */
    public boolean applyDistanceAttenuation;
    /**
     * Whether to apply frequency-dependent air absorption.
     * C type : IPLbool
     */
    public boolean applyAirAbsorption;
    /**
     * Whether to apply source directivity.
     * C type : IPLbool
     */
    public boolean applyDirectivity;
    /**
     * @see DirectOcclusionMode
     * Whether to apply occlusion and transmission. Also
     * lets you specify whether to apply frequency-dependent
     * or frequency-independent transmission.
     * C type : IPLDirectOcclusionMode
     */
    public DirectOcclusionMode directOcclusionMode;


}
