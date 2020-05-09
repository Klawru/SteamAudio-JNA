package ru.klaw.steamaudio.scene;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Materials {

    public Material toGeneric(Material material) {
        return material.of(0.10f, 0.20f, 0.30f, 0.05f, 0.100f, 0.050f, 0.030f);
    }

    public Material toBrick(Material material) {
         return material.of(0.03f, 0.04f, 0.07f, 0.05f, 0.015f, 0.015f, 0.015f);
    }

    public Material toConcrete(Material material) {
         return material.of(0.05f, 0.07f, 0.08f, 0.05f, 0.015f, 0.002f, 0.001f);
    }

    public Material toCeramic(Material material) {
         return material.of(0.01f, 0.02f, 0.02f, 0.05f, 0.060f, 0.044f, 0.011f);
    }

    public Material toGravel(Material material) {
         return material.of(0.60f, 0.70f, 0.80f, 0.05f, 0.031f, 0.012f, 0.008f);
    }

    public Material toCarpet(Material material) {
         return material.of(0.24f, 0.69f, 0.73f, 0.05f, 0.020f, 0.005f, 0.003f);
    }

    public Material toGlass(Material material) {
         return material.of(0.06f, 0.03f, 0.02f, 0.05f, 0.060f, 0.044f, 0.011f);
    }

    public Material toPlaster(Material material) {
         return material.of(0.12f, 0.06f, 0.04f, 0.05f, 0.056f, 0.056f, 0.004f);
    }

    public Material toWood(Material material) {
         return material.of(0.11f, 0.07f, 0.06f, 0.05f, 0.070f, 0.014f, 0.005f);
    }

    public Material toMetal(Material material) {
         return material.of(0.20f, 0.07f, 0.06f, 0.05f, 0.200f, 0.025f, 0.010f);
    }

    public Material toRock(Material material) {
         return material.of(0.13f, 0.20f, 0.24f, 0.05f, 0.015f, 0.002f, 0.001f);
    }

}
