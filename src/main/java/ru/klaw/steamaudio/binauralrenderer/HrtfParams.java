package ru.klaw.steamaudio.binauralrenderer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure.FieldOrder;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@FieldOrder({"type", "hrtfData", "sofaFileName"})
public class HrtfParams extends PhononStructure {
    /**
     * @see HrtfDatabaseType
     * Type of HRTF database to use.
     * C type : IPLHrtfDatabaseType
     */
    public HrtfDatabaseType type;
    /**
     * Reserved. Must be NULL.
     * C type : IPLbyte*
     */
    public Pointer hrtfData;
    /**
     * Name of the SOFA file from which to load HRTF data. Can
     * be a relative or absolute path. Must be a null-terminated
     * UTF-8 string.
     * C type : IPLstring
     */
    public String sofaFileName;

    public HrtfParams(HrtfDatabaseType type) {
        this.type = type;
    }

    public HrtfParams(HrtfDatabaseType type, String sofaFilePath) {
        this.type = type;
        this.sofaFileName = sofaFilePath;
    }

}
