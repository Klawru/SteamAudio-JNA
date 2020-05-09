package ru.klaw.steamaudio.geometry;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;


/**
 * A point or vector in 3D space.
 * Phonon uses a right-handed coordinate system, with the positive x-axis pointing right, the positive y-axis pointing up,
 * and the negative z-axis pointing ahead. Position and direction data obtained from a game engine or audio engine must
 * be properly transformed before being passed to any Phonon API function.
 */
@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"x", "y", "z"})
public class Vector3 extends PhononStructure {
    /**
     * The x-coordinate.
     * C type : IPLfloat32
     */
    public float x;
    /**
     * The y-coordinate.
     * C type : IPLfloat32
     */
    public float y;
    /**
     * The z-coordinate.
     * C type : IPLfloat32
     */
    public float z;
}
