package ru.klaw.steamaudio.directsound;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import ru.klaw.utility.PhononStructure;

import java.util.Arrays;
import java.util.List;

import static ru.klaw.steamaudio.Phonon.DirectivityCallback;


public class Directivity extends PhononStructure {
    /**
     * Controls the blend between a monopole (omnidirectional) and dipole
     * directivity pattern. 0.0 means pure monopole, 1.0 means pure
     * dipole. 0.5 results in a cardioid pattern.
     * C type : IPLfloat32
     */
    public float dipoleWeight;
    /**
     * Controls the width of the dipole directivity pattern. Higher
     * values mean sharper, more focused dipoles.
     * C type : IPLfloat32
     */
    public float dipolePower;
    /**
     * Pointer to a function to call when the directivity pattern needs to be evaluated.
     * C type : IPLDirectivityCallback
     */
    public DirectivityCallback callback;
    /**
     * User-specified data that should be passed to the callback function
     * when it is called. Use this to pass in any source-specific
     * data that must be known to the directivity callback function.
     * C type : void*
     */
    public Pointer userData;

    public Directivity() {
        super();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("dipoleWeight", "dipolePower", "callback", "userData");
    }

    /**
     * @param dipoleWeight Controls the blend between a monopole (omnidirectional) and dipole
     *                     directivity pattern. 0.0 means pure monopole, 1.0 means pure
     *                     dipole. 0.5 results in a cardioid pattern.
     *                     C type : IPLfloat32
     * @param dipolePower  Controls the width of the dipole directivity pattern. Higher
     *                     values mean sharper, more focused dipoles.
     *                     C type : IPLfloat32
     * @param callback     Pointer to a function to call when the directivity pattern needs
     *                     to be evaluated.
     *                     C type : IPLDirectivityCallback
     * @param userData     User-specified data that should be passed to the callback function
     *                     when it is called. Use this to pass in any source-specific
     *                     data that must be known to the directivity callback function.
     *                     C type : void*
     */
    public Directivity(float dipoleWeight, float dipolePower, DirectivityCallback callback, Pointer userData) {
        super();
        this.dipoleWeight = dipoleWeight;
        this.dipolePower = dipolePower;
        this.callback = callback;
        this.userData = userData;
    }

    public static class ByReference extends Directivity implements Structure.ByReference {

        public ByReference() {
        }

        public ByReference(float dipoleWeight, float dipolePower, DirectivityCallback callback, Pointer userData) {
            super(dipoleWeight, dipolePower, callback, userData);
        }
    }

    public static class ByValue extends Directivity implements Structure.ByValue {

        public ByValue() {
        }

        public ByValue(float dipoleWeight, float dipolePower, DirectivityCallback callback, Pointer userData) {
            super(dipoleWeight, dipolePower, callback, userData);
        }
    }

}
