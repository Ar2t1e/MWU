package com.modularwarfare.common.guns;


import com.google.gson.annotations.SerializedName;

public enum AttachmentPresetEnum {
    @SerializedName("sight") Sight("sight"),
    @SerializedName("slide") Slide("slide"),
    @SerializedName("grip") Grip("grip"),
    @SerializedName("flashlight") Flashlight("flashlight"),
    @SerializedName("laser") Laser("laser"),
    @SerializedName("charm") Charm("charm"),
    @SerializedName("skin") Skin("skin"),
    @SerializedName("barrel") Barrel("barrel"),
    @SerializedName("handguard") Handguard("handguard"),
    @SerializedName("stock") Stock("stock"),
    @SerializedName("railing") Raling("railing");

    public String typeName;

    AttachmentPresetEnum(String typeName) {
        this.typeName = typeName;
    }

    public static AttachmentPresetEnum getAttachment(String typeName) {
        for (AttachmentPresetEnum attachmentEnum : values()) {
            if (attachmentEnum.typeName.equalsIgnoreCase(typeName)) {
                return attachmentEnum;
            }
        }
        return AttachmentPresetEnum.Sight;
    }

    public String getName() {
        return this.typeName;
    }
}
