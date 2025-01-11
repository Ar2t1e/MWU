package com.modularwarfare.client.input;

public enum KeyType {
    GunReload("Reload Gun", 0x13), // R
    ClientReload("Reload Client", 0x43), // F9
    Inspect("Inspect", 0x31), // N
    GunUnload("Unload Key", 0x16), // U
    Flashlight("Flashlight", 0x23); // H


    //Keyboard
    public String displayName;
    public int keyCode;

    KeyType(String displayName, int keyCode) {
        this.displayName = displayName;
        this.keyCode = keyCode;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
