package ru.klaw.steamaudio.computedevice;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"type", "maxCUsToReserve", "fractionCUsForIRUpdate"})
public class ComputeDeviceFilter extends PhononStructure {
    /**
     * @see ComputeDeviceType
     * The type of device to use.
     * C type : IPLComputeDeviceType
     */
    public ComputeDeviceType type;
    /**
     * The maximum number of GPU compute units (CUs) that the
     * application will reserve on the device. When set to zero,
     * resource reservation is disabled and the entire GPU is used.
     * C type : IPLint32
     */
    public int maxCUsToReserve;
    /**
     * Fraction of maximum reserved CUs that should be used
     * for impulse response (IR) update. The IR update includes
     * any simulation performed by Radeon Rays to calculate IR and/or
     * pre-transformation of the IR for convolution with input audio.
     * The remaining reserved CUs are used for convolution.
     * Below are typical scenarios:
     * - <b>Using only AMD TrueAudio Next with Steam Audio.</b>
     * Set fractionCUsForIRUpdate to a value greater than 0 and less
     * than 1 in this case. This ensures that reserved CUs are
     * available for IR update as well as convolution. For example,
     * setting maxCUsToReserve to 8 and fractionCUsForIRUpdate
     * to .5 will use 4 reserved CUs for convolution and 4 reserved
     * CUs to pre-transform IR calculated on CPU or GPU.
     * - <b>Using AMD TrueAudio Next and AMD Radeon Rays with Steam Audio.</b>
     * Choosing fractionCUsForIRUpdate may require some experimentation
     * to utilize reserved CUs optimally. For example, setting
     * maxCUsToReserve to 8 and fractionCUsForIRUpdate to .5 will use
     * 4 reserved CUs for convolution and 4 reserved CUs for IR update.
     * However, if IR calculation has high latency with these settings,
     * you may want to increase fractionCUsForIRUpdate to devote
     * additional reserved CUs for IR update.
     * - <b>Using only AMD Radeon Rays with Steam Audio.</b>
     * Set fractionCUsForIRUpdate to 1 to make sure all the
     * reserved CUs are used for calculating IRs using Radeon Rays
     * and pre-transforming the calculated IRs.
     * If the number of reserved CUs assigned for convolution or IR
     * update are 0, then the entire GPU minus the reserved CUs are
     * used for the corresponding calculations. For example,
     * if maxCUsToReserve is set to 8 and fractionCUsForIRUpdate
     * is set to 0 then all the reserved CUs are used for convolution and
     * the rest of the GPU is used for IR update.
     * C type : IPLfloat32
     */
    public float fractionCUsForIRUpdate;

}
