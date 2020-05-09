package ru.klaw.steamaudio.geometry;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;


/**
 * An axis-aligned box.
 *
 * Axis-aligned boxes are used to specify a volume of 3D space.
 */
@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"minCoordinates", "maxCoordinates"})
public class Box extends PhononStructure {
    /**
     * The minimum coordinates of any vertex.
     * C type : IPLVector3
     */
    public Vector3 minCoordinates;
    /**
     * The maximum coordinates of any vertex.
     * C type : IPLVector3
     */
    public Vector3 maxCoordinates;

    @NoArgsConstructor
    public static class ByReference extends Box implements Structure.ByReference {
    }
    @NoArgsConstructor
    public static class ByValue extends Box implements Structure.ByValue {

    }

}
