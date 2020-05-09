package ru.klaw.utility;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class PhononTypeMapper extends DefaultTypeMapper {

    public static final PhononTypeMapper INSTANCE = new PhononTypeMapper();

    private PhononTypeMapper() {
        TypeConverter booleanConverter = new TypeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext context) {
                return Boolean.TRUE.equals(value) ? 1 : 0;
            }
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                return (Integer) value != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        };
        addTypeConverter(Boolean.class, booleanConverter);
    }
}
