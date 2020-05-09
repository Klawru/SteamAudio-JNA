package ru.klaw.steamaudio.baking;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"bakeParametric", "bakeConvolution", "irDurationForBake"})
public class BakingSettings extends PhononStructure {
    /**
     * Enables the generation of I3DL2-compliant parametric reverb. This is most
     * suited for calculating reverb in relatively enclosed spaces. It is less
     * suitable for open spaces, or source-to-listener propagation. It consumes
     * very little memory per probe.
     * C type : IPLbool
     */
    public boolean bakeParametric;
    /**
     * Enables the generation of detailed impulse responses for convolution reverb.
     * This is suited for all kinds of spaces, and for reverb as well as
     * source-to-listener propagation. However, it consumes significantly more
     * memory per probe.
     * C type : IPLbool
     */
    public boolean bakeConvolution;
    /**
     * Must be set to the same value as irDuration in IPLSimulationSettings.
     * C type : IPLfloat32
     */
    public float irDurationForBake;


}
