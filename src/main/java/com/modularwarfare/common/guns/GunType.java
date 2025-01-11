package com.modularwarfare.common.guns;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.client.fpp.basic.configs.GunRenderConfig;
import com.modularwarfare.client.model.ModelGun;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.fpp.enhanced.models.ModelEnhancedGun;
import com.modularwarfare.common.textures.TextureEnumType;
import com.modularwarfare.common.textures.TextureType;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.objects.SoundEntry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;

public class GunType extends BaseType {
    public WeaponType weaponType;
    public WeaponScopeModeType scopeModeType = WeaponScopeModeType.SIMPLE;
    public WeaponAnimationType animationType = WeaponAnimationType.BASIC;
    public float gunDamage = 0;

    //Playing Random Animation is Aiming
    public boolean isAiming = false;

    //Active Slot Attachment Mode
    public boolean slotSigint = true;
    public boolean slotBarel = true;
    public boolean slotFlashlight = true;
    public boolean slotSkin = true;
    public boolean slotCharm = true;
    public boolean slotGrip = true;
    public boolean slotStock = true;
    public boolean slotHandguard = true;
    public boolean slotLaser = true;
    public boolean slotRaling = true;

    public float moveSpeedModifier = 1F;

    //Hed Shot
    public float gunDamageHeadshotBonus = 0;

    public int weaponMaxRange = 200;
    public int weaponEffectiveRange = 50;
    public int numBullets = 1;
    public int modifyUnloadBullets = 0;
    public float bulletSpread;
    public int roundsPerMin = 1;
    public transient int fireTickDelay = 0;
    public int numBurstRounds = 3;

    //Energy Gun?
    public boolean isEnergyGun = false;

    public float recoilPitch = 10.0F;
    public float recoilYaw = 1.0F;
    public float accuracySneakFactor = 0.75f;
    public float randomRecoilPitch = 0.5F;
    public float randomRecoilYaw = 0.5F;
    public float recoilAimReducer = 0.8F;

    //Accuracy Move, Hover, Sprint. Factor
    public float accuracyMoveFactor = 0.8F;
    public float accuracyHoverFactor = 0.8F;
    public float accuracySprintFactor = 0.8F;

    //Fire Mode
    public WeaponFireMode[] fireModes = new WeaponFireMode[]{WeaponFireMode.SEMI};

    //Accepted Attachments
    public HashMap<AttachmentPresetEnum, ArrayList<String>> acceptedAttachments;

    //Default Attachments
    public HashMap<AttachmentPresetEnum, String> defaultAttachments;


    public int reloadTime = 40;
    public Integer offhandReloadTime;
    public String[] acceptedAmmo;
    public boolean dropBulletCasing = true;
    @SideOnly(value = Side.CLIENT)
    public ModelBiped.ArmPose armPose;
    @SideOnly(value = Side.CLIENT)
    public ModelBiped.ArmPose armPoseAiming;

    //Bullet
    public boolean dynamicAmmo = false;
    public Integer internalAmmoStorage;
    public String[] acceptedBullets;

    //Only Enhanced ASM
    public boolean allowSprintFiring = true;
    public boolean allowReloadFiring = false;
    public boolean allowReloadingSprint=true;
    public boolean allowFiringSprint=true;
    public boolean allowAimingSprint = true;

    //Custom Flash Textures
    public String customFlashTexture;
    public transient TextureType flashType;

    //Extra Lore
    public String extraLore;

     // Shell casing
    public Vec3d shellEjectOffsetNormal = new Vec3d(-1.0f, 0.0f, 1.0f);
    public Vec3d shellEjectOffsetAiming = new Vec3d(0.0f, 0.12f, 1.0f);

    //Custom Hands
    public String customHandsTexture;
    public transient TextureType handsTextureType;

    //custom Trail
    public String customTrailTexture;
    public String customTrailModel;
    public boolean customTrailGlow;

    public static boolean isPackAPunched(ItemStack heldStack) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            return nbtTagCompound.hasKey("punched") ? nbtTagCompound.getBoolean("punched") : false;
        }
        return false;
    }

    public static void setPackAPunched(ItemStack heldStack, boolean bool) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            nbtTagCompound.setBoolean("punched", bool);
            heldStack.setTagCompound(nbtTagCompound);
        }
    }

    public static WeaponFireMode getFireMode(ItemStack heldStack) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            return nbtTagCompound.hasKey("firemode") ? WeaponFireMode.fromString(nbtTagCompound.getString("firemode")) : null;
        }
        return null;
    }

    public static void setFireMode(ItemStack heldStack, WeaponFireMode fireMode) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            nbtTagCompound.setString("firemode", fireMode.name().toLowerCase());
            heldStack.setTagCompound(nbtTagCompound);
        }
    }

    public static ItemStack getAttachment(ItemStack heldStack, AttachmentPresetEnum type) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            return nbtTagCompound.hasKey("attachment_" + type.typeName) ? new ItemStack(nbtTagCompound.getCompoundTag("attachment_" + type.typeName)) : null;
        }
        return null;
    }

    public static void addAttachment(ItemStack heldStack, AttachmentPresetEnum type, ItemStack attachment) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            nbtTagCompound.setTag("attachment_" + type.typeName, attachment.writeToNBT(new NBTTagCompound()));
        }
    }

    public static void removeAttachment(ItemStack heldStack, AttachmentPresetEnum type) {
        if (heldStack.getTagCompound() != null) {
            NBTTagCompound nbtTagCompound = heldStack.getTagCompound();
            nbtTagCompound.removeTag("attachment_" + type.typeName);
        }
    }

    @Override
    public void loadExtraValues() {
        if (maxStackSize == null)
            maxStackSize = 1;

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            armPose = ModelBiped.ArmPose.BLOCK;
            armPoseAiming = ModelBiped.ArmPose.BOW_AND_ARROW;
            //Flash
            if (customFlashTexture != null) {
                if (ModularWarfare.textureTypes.containsKey(customFlashTexture)) {
                    flashType = ModularWarfare.textureTypes.get(customFlashTexture);
                } else {
                    flashType = new TextureType();
                    flashType.initDefaultTextures(TextureEnumType.Flash);
                }
            } else {
                flashType = new TextureType();
                flashType.initDefaultTextures(TextureEnumType.Flash);
            }
            //Hands
            if (customHandsTexture != null) {
                if (ModularWarfare.textureTypes.containsKey(customHandsTexture)) {
                    handsTextureType = ModularWarfare.textureTypes.get(customHandsTexture);
                }
            }
        }
        loadBaseValues();
        fireTickDelay = 1200 / roundsPerMin;
        try {
            for (ArrayList<SoundEntry> entryList : weaponSoundMap.values()) {
                for (SoundEntry soundEntry : entryList) {
                    if (soundEntry.soundName != null) {
                        ModularWarfare.PROXY.registerSound(soundEntry.soundName);
                        if (soundEntry.soundNameDistant != null)
                            ModularWarfare.PROXY.registerSound(soundEntry.soundNameDistant);
                    } else {
                        ModularWarfare.LOGGER.error(String.format("Sound entry event '%s' has null soundName for type '%s'", soundEntry.soundEvent, internalName));
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void reloadModel() {
        if (animationType == WeaponAnimationType.BASIC) {
            model = new ModelGun(ModularWarfare.getRenderConfig(this, GunRenderConfig.class), this);
        } else {
            enhancedModel = new ModelEnhancedGun(ModularWarfare.getRenderConfig(this, GunEnhancedRenderConfig.class), this);
        }
    }

    public boolean hasFireMode(WeaponFireMode fireMode) {
        if (fireModes != null) {
            for (int i = 0; i < fireModes.length; i++) {
                if (fireModes[i] == fireMode) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getAssetDir() {
        return "guns";
    }
}