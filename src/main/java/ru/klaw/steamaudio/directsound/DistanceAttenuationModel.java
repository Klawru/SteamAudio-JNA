package ru.klaw.steamaudio.directsound;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.steamaudio.directsound.enums.DistanceAttenuationModelType;
import ru.klaw.utility.PhononStructure;

import static ru.klaw.steamaudio.Phonon.DistanceAttenuationCallback;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"type", "minDistance", "callback", "userData", "dirty"})
public class DistanceAttenuationModel extends PhononStructure {
    /**
     * @see DistanceAttenuationModelType
     * The type of distance attenuation model to use.
     * C type : IPLDistanceAttenuationModelType
     */
    public DistanceAttenuationModelType type;
    /**
     * The minimum distance parameter for the model.
     * Only used if type is INVERSEDISTANCE.
     * C type : IPLfloat32
     */
    public float minDistance;
    /**
     * The callback function to call when evaluating
     * distance attenuation. Only used if type is
     * IPL_DISTANCEATTENUATION_CALLBACK.
     * C type : IPLDistanceAttenuationCallback
     */
    public DistanceAttenuationCallback callback;
    /**
     * User-specified data that should be passed to the callback
     * function when it is called. Use this to pass in any
     * source-specific data that must be known to the
     * callback function. Only used if type is
     * IPL_DISTANCEATTENUATION_CALLBACK.
     * C type : void*
     */
    public Pointer userData;
    /**
     * Flag indicating whether userData has been changed.
     * When type is set to IPL_DISTANCEATTENUATION_CALLBACK,
     * Steam Audio can avoid repeating some calculations
     * if dirty is set to false; these calculations are
     * should only be carried out when userData changes,
     * in which case dirty should be set to true.
     * C type : IPLbool
     */
    public boolean dirty;

    public DistanceAttenuationModel(DistanceAttenuationModelType type, DistanceAttenuationCallback callback, Pointer userData, boolean dirty) {
        this.type = type;
        this.callback = callback;
        this.userData = userData;
        this.dirty = dirty;
    }

    public DistanceAttenuationModel(DistanceAttenuationModelType type, float minDistance) {
        this.type = type;
        this.minDistance = minDistance;
    }

    public DistanceAttenuationModel(DistanceAttenuationModelType type) {
        this.type = type;
    }

    public static DistanceAttenuationModel getDefault() {
        return new DistanceAttenuationModel(DistanceAttenuationModelType.DEFAULT);
    }

    @NoArgsConstructor
    public static class ByReference extends DistanceAttenuationModel implements Structure.ByReference {

    }

    @NoArgsConstructor
    public static class ByValue extends DistanceAttenuationModel implements Structure.ByValue {

    }

}
