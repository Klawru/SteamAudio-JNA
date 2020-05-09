package ru.klaw.utility;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public abstract class PhononStructure extends Structure {
    public PhononStructure() {
        super(PhononTypeMapper.INSTANCE);
    }

    public PhononStructure(int alignType) {
        super(alignType,PhononTypeMapper.INSTANCE);
    }

    public PhononStructure(Pointer p) {
        super(p,Structure.ALIGN_DEFAULT,PhononTypeMapper.INSTANCE);
    }

    public PhononStructure(Pointer p, int alignType) {
        super(p, alignType,PhononTypeMapper.INSTANCE);
    }
}
