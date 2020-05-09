package ru.klaw.steamaudio.scene;

import com.sun.jna.Structure;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@Structure.FieldOrder({"elements"})
public class Matrix4x4 extends PhononStructure {
    /**
     * The elements of the matrix, in row-major order.
     * C type : float[4][4]
     */
    public float[] elements = new float[((4) * (4))];

    /**
     * @param elements The elements of the matrix, in row-major order.
     *                 C type : float[4][4]
     */
    public Matrix4x4(float[] elements) {
        super();
        if ((elements.length != this.elements.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.elements = elements;
    }

    @NoArgsConstructor
    public static class ByReference extends Matrix4x4 implements Structure.ByReference {
        public ByReference(float[] elements) {
            super(elements);
        }
    }

    @NoArgsConstructor
    public static class ByValue extends Matrix4x4 implements Structure.ByValue {
        public ByValue(float[] elements) {
            super(elements);
        }
    }

}
