package com.modularwarfare.client.fpp.enhanced;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

@JsonAdapter(AnimationType.AnimationTypeJsonAdapter.class)
public enum AnimationType {
    DEFAULT("default"),
    DEFAULT_EMPTY("defaultEmpty"),
    DRAW("draw"),
    DRAW_EMPTY("drawEmpty"),
    AIM("aim"),
    INSPECT("inspect"),
    INSPECT_EMPTY("inspectEmpty"),
    PRE_LOAD("preLoad"),
    LOAD("load"),
    POST_LOAD("postLoad"),
    PRE_UNLOAD("preUnload"),
    PRE_UNLOAD_EMPTY("preUnloadEmpty"),
    UNLOAD("unload"),
    UNLOAD_EMPTY("unloadEmpty"),
    POST_UNLOAD("postUnload"),
    POST_UNLOAD_EMPTY("postUnloadEmpty"),
    PRE_RELOAD("preReload"),
    PRE_RELOAD_EMPTY("preReloadEmpty"),
    RELOAD_FIRST("reloadFirst"),
    RELOAD_FIRST_EMPTY("reloadFirstEmpty"),
    RELOAD_SECOND("reloadSecond"),
    RELOAD_SECOND_EMPTY("reloadSecondEmpty"),
    RELOAD_FIRST_QUICKLY("reloadFirstQuickly"),
    RELOAD_SECOND_QUICKLY("reloadSecondQuickly"),
    POST_RELOAD("postReload"),
    POST_RELOAD_SECOND("postReloadSecond"),
    POST_RELOAD_EMPTY("postReloadEmpty"),
    PRE_FIRE("preFire"),
    FIRE("fire"),
    FIRE_SECOND("fireSecond"),
    FIRE_THIRD("fireThird"),
    POST_FIRE("postFire"),
    FIRE_EMPTY("fireEmpty"),
    POST_FIRE_EMPTY("postFireEmpty"),
    MODE_CHANGE("modeChange"),
    MODE_CHANGE_EMPTY("modeChangeEmpty"),
    SPRINT("sprint");

    public String serializedName;
    private AnimationType(String name) {
        serializedName=name;
    }
    
    public static class AnimationTypeJsonAdapter extends TypeAdapter<AnimationType>{

        @Override
        public AnimationType read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                throw new AnimationTypeException("wrong animation type format");
            }
            return fromString(in.nextString());
        }

        @Override
        public void write(JsonWriter out, AnimationType t) throws IOException {
            out.value(t.serializedName);
        }
        
        public static class AnimationTypeException extends RuntimeException{
            public AnimationTypeException(String str) {
                super(str);
            }
        }

        public static AnimationType fromString(String modeName) {
            for (AnimationType animationType : values()) {
                if (animationType.serializedName.equalsIgnoreCase(modeName)) {
                    return animationType;
                }
            }
            throw new AnimationTypeException("wrong animation type:"+modeName);
        }
        
    }
}
