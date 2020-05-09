package ru.klaw.steamaudio.scene;

import com.sun.jna.Structure;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@Structure.FieldOrder({"indices"})
public class Triangle extends PhononStructure {
    /**
     * Indices of the three vertices of this triangle. Each triangle must be specified
     * using three vertices; triangle strip or fan representations are not supported.
     * C type : IPLint32[3]
     */
    public int[] indices = new int[3];

    /**
     * @param indices Indices of the three vertices of this triangle. Each triangle must be specified
     *                using three vertices; triangle strip or fan representations are not supported.
     *                C type : IPLint32[3]
     */
    public Triangle(int[] indices) {
        super();
        if ((indices.length != this.indices.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.indices = indices;
    }

    @NoArgsConstructor
    public static class ByReference extends Triangle implements Structure.ByReference {

    }

    @NoArgsConstructor
    public static class ByValue extends Triangle implements Structure.ByValue {

    }

}
