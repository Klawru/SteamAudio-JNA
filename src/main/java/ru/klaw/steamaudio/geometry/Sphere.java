package ru.klaw.steamaudio.geometry;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;


/**
 * A sphere.
 *
 * Spheres are used to define a region of influence around a point.
 */
@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"center", "radius"})
public class Sphere extends PhononStructure {
    /**
     * The center.
     * C type : IPLVector3
     */
    public Vector3 center;
    /**
     * The radius.
     * C type : IPLfloat32
     */
    public float radius;
}
