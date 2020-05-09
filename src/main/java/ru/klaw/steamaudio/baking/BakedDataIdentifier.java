package ru.klaw.steamaudio.baking;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
public class BakedDataIdentifier extends PhononStructure {
    /**
     * 32-bit signed integer that uniquely identifies this set of baked data.
     * C type : IPLint32
     */
    public int identifier;
    /**
     * @see BakedDataType
     * How this set of baked data should be interpreted.
     * C type : IPLBakedDataType
     */
    public BakedDataType type;
}
