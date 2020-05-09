package ru.klaw.utility;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Structure.FieldOrder({"pointers"})
public class PointerArray extends PhononStructure implements Structure.ByReference{
    public Pointer[] pointers;
    public PointerArray(Memory[] memories) {
        pointers = new Pointer[memories.length];
        System.arraycopy(memories, 0, pointers, 0, memories.length);
    }
}
