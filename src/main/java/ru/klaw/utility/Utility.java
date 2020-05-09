package ru.klaw.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utility {

    public static HashMap<String, Object> mapOf(String option, Object instance) {
        HashMap<String, Object> settings = new HashMap<>();
        settings.put(option, instance);
        return settings;
    }
}
