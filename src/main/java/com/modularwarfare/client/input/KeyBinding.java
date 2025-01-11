package com.modularwarfare.client.input;

import com.modularwarfare.ModularWarfare;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBinding {
    public static final net.minecraft.client.settings.KeyBinding GunReload = new net.minecraft.client.settings.KeyBinding("key.gun_reload", Keyboard.KEY_R, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding SwitchLaser = new net.minecraft.client.settings.KeyBinding("key.switch_laser", Keyboard.KEY_L, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding FovZoomAdd = new net.minecraft.client.settings.KeyBinding("key.fov_zoom_add", Keyboard.KEY_EQUALS, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding FovZoomDes = new net.minecraft.client.settings.KeyBinding("key.fov_zoom_des", Keyboard.KEY_MINUS, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding ClientReload = new net.minecraft.client.settings.KeyBinding("key.client_reload", Keyboard.KEY_F9, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding FireMode = new net.minecraft.client.settings.KeyBinding("key.fire_mode", Keyboard.KEY_V, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding Inspect = new net.minecraft.client.settings.KeyBinding("key.inspect", Keyboard.KEY_N, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding GunUnload = new net.minecraft.client.settings.KeyBinding("key.gun_unload", Keyboard.KEY_U, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding AddAttachment = new net.minecraft.client.settings.KeyBinding("key.add_attachment", Keyboard.KEY_M, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding Flashlight = new net.minecraft.client.settings.KeyBinding("key.flashlight", Keyboard.KEY_H, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding CustomInventory = new net.minecraft.client.settings.KeyBinding("key.custom_inventory", Keyboard.KEY_B, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding LeftAdd = new net.minecraft.client.settings.KeyBinding("key.left_add", Keyboard.KEY_LEFT, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding RightAdd = new net.minecraft.client.settings.KeyBinding("key.right_add", Keyboard.KEY_RIGHT, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding DownAdd = new net.minecraft.client.settings.KeyBinding("key.down_add", Keyboard.KEY_DOWN, "key.categories.gun");
    public static final net.minecraft.client.settings.KeyBinding UpAdd = new net.minecraft.client.settings.KeyBinding("key.up_add", Keyboard.KEY_UP, "key.categories.gun");
    public static void register() {
        ClientRegistry.registerKeyBinding(GunReload);
        ClientRegistry.registerKeyBinding(SwitchLaser);
        ClientRegistry.registerKeyBinding(FovZoomAdd);
        ClientRegistry.registerKeyBinding(FovZoomDes);
        ClientRegistry.registerKeyBinding(ClientReload);
        ClientRegistry.registerKeyBinding(FireMode);
        ClientRegistry.registerKeyBinding(Inspect);
        ClientRegistry.registerKeyBinding(GunUnload);
        ClientRegistry.registerKeyBinding(AddAttachment);
        ClientRegistry.registerKeyBinding(Flashlight);
        ClientRegistry.registerKeyBinding(CustomInventory);
        ClientRegistry.registerKeyBinding(LeftAdd);
        ClientRegistry.registerKeyBinding(RightAdd);
        ClientRegistry.registerKeyBinding(DownAdd);
        ClientRegistry.registerKeyBinding(UpAdd);
    }
}
