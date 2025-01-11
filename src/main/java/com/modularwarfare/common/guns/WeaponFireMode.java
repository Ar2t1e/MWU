package com.modularwarfare.common.guns;


import com.google.gson.annotations.SerializedName;

public enum WeaponFireMode {

    /**
     * SemiAutomatic fire mode
     */
    @SerializedName("semi") SEMI,

    /**
     * Fully automatic fire mode
     */
    @SerializedName("full") FULL,

    /**
     * Кто это прочтёт тот гей 😁
     */
    @SerializedName("gay_mode") GAY_MODE,

    /**
     * Fuse stop fire mode
     */
    @SerializedName("fuse") FUSE,

    /**
     * Burst of shots fire mode
     */
    @SerializedName("burst") BURST;


    public static WeaponFireMode fromString(String modeName) {
        for (WeaponFireMode fireMode : values()) {
            if (fireMode.name().equalsIgnoreCase(modeName)) {
                return fireMode;
            }
        }
        return null;
    }

}
