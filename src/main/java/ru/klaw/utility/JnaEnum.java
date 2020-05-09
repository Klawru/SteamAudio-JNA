package ru.klaw.utility;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public interface JnaEnum<T> extends NativeMapped {
    int getIntValue();
    T getForValue(int i);

    @Override
    default Object fromNative(Object nativeValue, FromNativeContext context) {
        return getForValue((int) nativeValue);
    }

    @Override
    default Object toNative() {
        return getIntValue();
    }

    @Override
    default Class<?> nativeType(){
        return Integer.class;
    }
}
