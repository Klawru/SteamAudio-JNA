package ru.klaw.steamaudio.directsound;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.Phonon;
import ru.klaw.steamaudio.directsound.enums.AirAbsorptionModelType;
import ru.klaw.utility.PhononStructure;


/**
 * An air absorption model for use when calculating frequency-dependent air absorption along a path from the source to the listener.
 */
@NoArgsConstructor
@Structure.FieldOrder({"type", "coefficients", "callback", "userData", "dirty"})
public class AirAbsorptionModel extends PhononStructure {
    /**
     * @see AirAbsorptionModelType
     * The type of air absorption model to use.
     * C type : IPLAirAbsorptionModelType
     */
    public AirAbsorptionModelType type;
    /**
     * The frequency-dependent exponential decay coefficients.
     * Only used if type is EXPONENTIAL.
     * C type : IPLfloat32[3]
     */
    public float[] coefficients = new float[3];
    /**
     * The callback function to call when evaluating
     * air absorption.
     * Only used if type is CALLBACK.
     * C type : IPLAirAbsorptionCallback
     */
    public Phonon.AirAbsorptionCallback callback;
    /**
     * User-specified data that should be passed to the callback
     * function when it is called. Use this to pass in any
     * source-specific data that must be known to the
     * callback function.
     * Only used if type is CALLBACK.
     * C type : void*
     */
    public Pointer userData;
    /**
     * Flag indicating whether userData has been changed.
     * When type is set to IPL_AIRABSORPTION_CALLBACK,
     * Steam Audio can avoid repeating some calculations
     * if dirty is set to false; these calculations are
     * should only be carried out when userData changes,
     * in which case dirty should be set to true.
     * C type : IPLbool
     */
    public boolean dirty;


    public AirAbsorptionModel(AirAbsorptionModelType type) {
        this.type = type;
    }

    public AirAbsorptionModel(AirAbsorptionModelType type, float[] coefficients) {
        this.type = type;
        if ((coefficients.length != this.coefficients.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.coefficients = coefficients;
    }

    public AirAbsorptionModel(AirAbsorptionModelType type, Phonon.AirAbsorptionCallback callback, Pointer userData, boolean dirty) {
        this.type = type;
        this.callback = callback;
        this.userData = userData;
        this.dirty = dirty;
    }

    public static AirAbsorptionModel getDefault() {
        return new AirAbsorptionModel(AirAbsorptionModelType.DEFAULT);
    }


    @NoArgsConstructor
    public static class ByReference extends AirAbsorptionModel implements Structure.ByReference {
        public ByReference(AirAbsorptionModelType type) {
            super(type);
        }

        public ByReference(AirAbsorptionModelType type, float[] coefficients) {
            super(type, coefficients);
        }

        public ByReference(AirAbsorptionModelType type, Phonon.AirAbsorptionCallback callback, Pointer userData, boolean dirty) {
            super(type, callback, userData, dirty);
        }
    }

    @NoArgsConstructor
    public static class ByValue extends AirAbsorptionModel implements Structure.ByValue {

        public ByValue(AirAbsorptionModelType type) {
            super(type);
        }

        public ByValue(AirAbsorptionModelType type, float[] coefficients) {
            super(type, coefficients);
        }

        public ByValue(AirAbsorptionModelType type, Phonon.AirAbsorptionCallback callback, Pointer userData, boolean dirty) {
            super(type, callback, userData, dirty);
        }
    }

}
