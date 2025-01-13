package com.modularwarfare.client.fpp.enhanced.animation;

import com.modularwarfare.client.ClientRenderHooks;
import com.modularwarfare.client.fpp.basic.animations.ReloadType;
import com.modularwarfare.client.fpp.basic.renderers.RenderParameters;
import com.modularwarfare.client.fpp.enhanced.AnimationType;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.gui.GuiAttachmentModified;
import com.modularwarfare.client.handler.ClientTickHandler;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.utility.maths.Interpolation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import org.lwjgl.input.Mouse;

import static com.modularwarfare.client.fpp.basic.renderers.RenderParameters.smoothing;

public class AnimationController {
    public final EntityLivingBase player;
    private GunEnhancedRenderConfig config;
    public static ActionPlayback playback;
    public static double DEFAULT;
    public static double DRAW;
    public static double ADS;
    public static double RELOAD;
    public static double SPRINT;
    public static double SPRINT_LOOP;
    public static double SPRINT_RANDOM;
    public static double INSPECT = 1;
    public static double INSPECT_EMPTY = 1;
    public static double FIRE;
    public static double MODE_CHANGE;
    public long sprintCoolTime = 0;
    public long sprintLoopCoolTime = 0;
    public int oldCurrentItem;
    public ItemStack oldItemstack;
    public boolean nextResetDefault = false;
    public boolean hasPlayedDrawSound = true;
    public ISound inspectSound = null;
    public ISound inspectSoundEmpty = null;

    public static float getAttachmentProgress() {
        return lastAtachModeProgress + (atachModeProgress - lastAtachModeProgress) * smoothing;
    }

    public static float atachModeProgress = 0F;
    public static float lastAtachModeProgress = 0F;
    public static boolean attachmentMode = false;
    private static final AnimationType[] RELOAD_TYPE = new AnimationType[] {
            AnimationType.PRE_LOAD,AnimationType.LOAD,AnimationType.POST_LOAD,
            AnimationType.PRE_UNLOAD,AnimationType.PRE_UNLOAD_EMPTY,
            AnimationType.UNLOAD,AnimationType.UNLOAD_EMPTY, AnimationType.POST_UNLOAD,AnimationType.POST_UNLOAD_EMPTY,
            AnimationType.PRE_RELOAD,AnimationType.PRE_RELOAD_EMPTY,AnimationType.RELOAD_FIRST,AnimationType.RELOAD_FIRST_EMPTY,
            AnimationType.RELOAD_SECOND,AnimationType.RELOAD_SECOND_EMPTY,
            AnimationType.RELOAD_FIRST_QUICKLY,AnimationType.RELOAD_SECOND_QUICKLY,
            AnimationType.POST_RELOAD,AnimationType.POST_RELOAD_EMPTY,AnimationType.POST_RELOAD_SECOND,
    };

    private static final AnimationType[] FIRE_TYPE = new AnimationType[] {
            AnimationType.FIRE,AnimationType.FIRE_EMPTY,AnimationType.FIRE_SECOND,AnimationType.FIRE_THIRD,
            AnimationType.PRE_FIRE, AnimationType.POST_FIRE,
            AnimationType.POST_FIRE_EMPTY,
    };

    public AnimationController(EntityLivingBase player, GunEnhancedRenderConfig config){
        this.config = config;
        playback = new ActionPlayback(config);
        playback.action = AnimationType.DEFAULT;
        this.player = player;
    }

    public void reset(boolean resetSprint) {
        DEFAULT = 0;
        DRAW = 0;
        hasPlayedDrawSound = false;
        ADS = 0;
        RELOAD = 0;
        if(resetSprint) {
            SPRINT = 0;
        }
        SPRINT_LOOP = 0;
        INSPECT = 1;
        INSPECT_EMPTY = 1;
        if(inspectSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(inspectSound);
            inspectSound = null;
        }
        if(inspectSoundEmpty != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(inspectSoundEmpty);
            inspectSoundEmpty = null;
        }
        FIRE = 0;
        MODE_CHANGE = 1;
        updateActionAndTime();
    }

    public void resetView() {
        INSPECT_EMPTY = 1;
        INSPECT = 1;
        MODE_CHANGE = 1;
    }

    public void onTickRender(float stepTick) {
        if(config==null) {
            return;
        }

        attachmentMode = Minecraft.getMinecraft().currentScreen instanceof GuiAttachmentModified;

        lastAtachModeProgress = atachModeProgress;
        if (!attachmentMode) {
            atachModeProgress *= 0.85F;
        } else {
            atachModeProgress = 1F - (1F - atachModeProgress) * (0.85F);
        }

        long time = System.currentTimeMillis();
        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);
        float moveDistance = player.distanceWalkedModified-player.prevDistanceWalkedModified;

        /** DEFAULT **/
        double defaultSpeed = config.animations.get(AnimationType.DEFAULT).getSpeed(config.FPS) * stepTick;
        if (playback.action==AnimationType.DEFAULT_EMPTY) {
            defaultSpeed = config.animations.get(AnimationType.DEFAULT_EMPTY).getSpeed(config.FPS) * stepTick;
        }
        if (DEFAULT == 0) {
            if (player.getHeldItemMainhand().getItem() instanceof ItemGun&&player instanceof EntityPlayer) {
                GunType type = ((ItemGun)player.getHeldItemMainhand().getItem()).type;
                if(playback.action == AnimationType.DEFAULT_EMPTY) {
                    type.playClientSound((EntityPlayer)player, WeaponSoundType.IdleEmpty);
                }else if(playback.action == AnimationType.DEFAULT){
                    type.playClientSound((EntityPlayer)player, WeaponSoundType.Idle);
                }
            }
        }
        DEFAULT = Math.max(0F, DEFAULT + defaultSpeed);
        if (DEFAULT > 1) {
            DEFAULT = 0;
        }

        /** DRAW **/
        if (DRAW==0) {
            if (player.getHeldItemMainhand().getItem() instanceof ItemGun&&player instanceof EntityPlayer) {
                GunType type = ((ItemGun)player.getHeldItemMainhand().getItem()).type;
                if(playback.action == AnimationType.DRAW_EMPTY) {
                    type.playClientSound((EntityPlayer)player, WeaponSoundType.DrawEmpty);
                }else if (playback.action == AnimationType.DRAW){
                    type.playClientSound((EntityPlayer)player, WeaponSoundType.Draw);
                }
            }
        }
        if (!config.animations.containsKey (AnimationType.DRAW) && !config.animations.containsKey (AnimationType.DRAW_EMPTY)) {
            DRAW = 1;
        }else {
            double drawSpeed = config.animations.get(AnimationType.DRAW).getSpeed(config.FPS) * stepTick;
            if(playback.action == AnimationType.DRAW_EMPTY) {
                drawSpeed = config.animations.get(AnimationType.DRAW_EMPTY).getSpeed(config.FPS) * stepTick;
            }
            DRAW += drawSpeed;
            if(DRAW >= 1) {
                DRAW = 1;
            }
        }

        /** INSPECT **/
        if (INSPECT == 0) {
            if (player.getHeldItemMainhand().getItem() instanceof ItemGun&&player instanceof EntityPlayer) {
                GunType type = ((ItemGun)player.getHeldItemMainhand().getItem()).type;
                SoundEvent se = type.getSound((EntityPlayer)player, WeaponSoundType.Inspect);
                if(se != null) {
                    inspectSound= PositionedSoundRecord.getRecord(se, 1, 1);
                    Minecraft.getMinecraft().getSoundHandler().playSound(inspectSound);
                }
            }
        }if (INSPECT == 1) {
            if(inspectSound != null) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(inspectSound);
                inspectSound = null;
            }
        }if (!config.animations.containsKey (AnimationType.INSPECT)) {
            INSPECT = 1;
        }else {
            double modeChangeVal = config.animations.get(AnimationType.INSPECT).getSpeed(config.FPS) * stepTick;
            INSPECT += modeChangeVal;
            if(INSPECT >= 1) {
                INSPECT = 1;
            }
        }

        /** INSPECT EMPTY **/
        if (INSPECT_EMPTY == 0) {
            if (player.getHeldItemMainhand().getItem() instanceof ItemGun&&player instanceof EntityPlayer) {
                GunType type = ((ItemGun)player.getHeldItemMainhand().getItem()).type;
                SoundEvent se = type.getSound((EntityPlayer)player, WeaponSoundType.InspectEmpty);
                if(se != null) {
                    inspectSoundEmpty = PositionedSoundRecord.getRecord(se, 1, 1);
                    Minecraft.getMinecraft().getSoundHandler().playSound(inspectSoundEmpty);
                }
            }
        }if (INSPECT_EMPTY == 1) {
            if (inspectSoundEmpty != null) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(inspectSoundEmpty);
                inspectSoundEmpty = null;
            }
        } if (!config.animations.containsKey(AnimationType.INSPECT_EMPTY)) {
            INSPECT_EMPTY = 1;
        }else {
            double modeChangeVal = config.animations.get(AnimationType.INSPECT_EMPTY).getSpeed(config.FPS) * stepTick;
            INSPECT_EMPTY += modeChangeVal;
            if (INSPECT_EMPTY >= 1) {
                INSPECT_EMPTY = 1;
            }
        }

        /** ADS **/
        boolean aimChargeMisc = ClientRenderHooks.getEnhancedAnimMachine(player).reloading;
        double adsSpeed = config.animations.get(AnimationType.AIM).getSpeed(config.FPS) * stepTick;
        double val = 0;
        if (RenderParameters.collideFrontDistance == 0 && Minecraft.getMinecraft().inGameHasFocus
                && Mouse.isButtonDown(1) && !aimChargeMisc && INSPECT == 1F && INSPECT_EMPTY == 1F) {
            val = ADS + adsSpeed * (2 - ADS);
        } else {
            val = ADS - adsSpeed * (1 + ADS);
        }
        RenderParameters.adsSwitch = (float)ADS;

        if(!isDrawing()) {
            ADS = Math.max(0, Math.min(1, val));
        }else {
            ADS = 0;
        }

        if(!anim.shooting) {
            FIRE = 0;
        }

        if(!anim.reloading) {
            RELOAD = 0;
        }

        /**
         * Sprinting
         */
        double sprintSpeed = Math.sin(SPRINT * 3.14) * 0.09f;
        if (sprintSpeed < 0.03f) {
            sprintSpeed = 0.03f;
        }
        sprintSpeed *= stepTick;
        double sprintValue = 0;

        boolean flag = (player.onGround||player.fallDistance<=2f);

        if (player.isSprinting() && moveDistance > 0.05 && flag) {
            if (time > sprintCoolTime) {
                sprintValue = SPRINT + sprintSpeed;
            }
        } else {
            sprintCoolTime = time + 100;
            sprintValue = SPRINT - sprintSpeed;
        }
        if (anim.gunRecoil > 0.1F || ADS > 0.8 || RELOAD > 0 || INSPECT < 1 || INSPECT_EMPTY < 1) {
            sprintValue = SPRINT - sprintSpeed * 2.5f;
        }

        SPRINT = Math.max(0, Math.min(1, sprintValue));

        /** SPRINT_LOOP **/
        if (!config.animations.containsKey(AnimationType.SPRINT)) {
            SPRINT_LOOP = 0;
            SPRINT_RANDOM = 0;
        } else {
            double sprintLoopSpeed = config.animations.get(AnimationType.SPRINT).getSpeed(config.FPS) * stepTick
                    * (moveDistance / 0.15f);
            boolean flagSprintRand = false;
            if (flag) {
                if (time > sprintLoopCoolTime) {
                    if (player.isSprinting()) {
                        SPRINT_LOOP += sprintLoopSpeed;
                        SPRINT_RANDOM += sprintLoopSpeed;
                        flagSprintRand = true;
                    }
                }
            } else {
                sprintLoopCoolTime = time + 100;
            }
            if (!flagSprintRand) {
                SPRINT_RANDOM -= config.animations.get(AnimationType.SPRINT).getSpeed(config.FPS) * 3 * stepTick;
            }
            if (SPRINT_LOOP > 1) {
                SPRINT_LOOP = 0;
            }
            if (SPRINT_RANDOM > 1) {
                SPRINT_RANDOM = 0;
            }
            if (SPRINT_RANDOM < 0) {
                SPRINT_RANDOM = 0;
            }
            if (Double.isNaN(SPRINT_RANDOM)) {
                SPRINT_RANDOM = 0;
            }
        }
        /** MODE CHANGE **/
        if(!config.animations.containsKey(AnimationType.MODE_CHANGE) && !config.animations.containsKey(AnimationType.MODE_CHANGE_EMPTY)) {
            MODE_CHANGE = 1;
        }else {
            double modeChangeVal = config.animations.get(AnimationType.MODE_CHANGE).getSpeed(config.FPS) * stepTick;
            if(playback.action == AnimationType.MODE_CHANGE_EMPTY) {
                modeChangeVal = config.animations.get(AnimationType.MODE_CHANGE_EMPTY).getSpeed(config.FPS) * stepTick;
            }
            MODE_CHANGE += modeChangeVal;
            if(MODE_CHANGE >= 1) {
                MODE_CHANGE = 1;
            }
        }

        ClientRenderHooks.getEnhancedAnimMachine(player).onRenderTickUpdate(stepTick);

        updateActionAndTime();
    }

    public AnimationType getPlayingAnimation() {
        return playback.action;
    }

    public void updateCurrentItem() {
        if(config == null) {
            return;
        }
        if(!(player instanceof EntityPlayer)) {
            return;
        }
        ItemStack stack = player.getHeldItemMainhand();
        Item item = stack.getItem();
        if (item instanceof ItemGun) {
            GunType type = ((ItemGun) item).type;
            if (!type.allowAimingSprint && ADS > 0.2f) {
                player.setSprinting(false);
            }
            if (!type.allowReloadingSprint && RELOAD > 0f) {
                player.setSprinting(false);
            }
            if (!type.allowFiringSprint && FIRE > 0f) {
                player.setSprinting(false);
            }
            if (ADS == 1) {
                if (!ClientRenderHooks.isAiming) {
                    ClientRenderHooks.isAiming = true;
                }
            } else {
                if (ClientRenderHooks.isAiming) {
                    ClientRenderHooks.isAiming = false;
                }
            }
        }
        boolean resetFlag = false;
        if(oldCurrentItem != ((EntityPlayer)player).inventory.currentItem){
            resetFlag = true;
            oldCurrentItem = ((EntityPlayer)player).inventory.currentItem;
        }
        if(oldItemstack != player.getHeldItemMainhand()) {
            if(oldItemstack==null||oldItemstack.isEmpty()) {
                resetFlag = true;
            }
            oldItemstack=player.getHeldItemMainhand();
        }
        if(resetFlag) {
            reset(true);
        }
    }

    public void updateAction() {
        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);
        boolean flag = nextResetDefault;
        nextResetDefault = false;
        if (DRAW < 1F) {
            playback.action = AnimationType.DRAW;
            if(!ItemGun.hasNextShot(player.getHeldItemMainhand())) {
                if(config.animations.containsKey(AnimationType.DRAW_EMPTY)) {
                    playback.action = AnimationType.DRAW_EMPTY;
                }
            }
        }else if (RELOAD > 0F) {
            resetView();
            playback.action = anim.getReloadAnimationType();
        }else if(FIRE>0F) {
            resetView();
            playback.action = anim.getShootingAnimationType();
        }else if (INSPECT < 1) {
            playback.action = AnimationType.INSPECT;
        }else if (INSPECT_EMPTY < 1) {
            playback.action = AnimationType.INSPECT_EMPTY;
        }else if (MODE_CHANGE  < 1) {
            playback.action = AnimationType.MODE_CHANGE;
            if(!ItemGun.hasNextShot(player.getHeldItemMainhand())) {
                if(config.animations.containsKey(AnimationType.MODE_CHANGE_EMPTY)) {
                    playback.action = AnimationType.MODE_CHANGE_EMPTY;
                }
            }
        }else if (playback.hasPlayed||(playback.action != AnimationType.DEFAULT && playback.action !=AnimationType.DEFAULT_EMPTY)) {
            if(flag) {
                playback.action = AnimationType.DEFAULT;
                if(!ItemGun.hasNextShot(player.getHeldItemMainhand())) {
                    if(config.animations.containsKey(AnimationType.DEFAULT_EMPTY)) {
                        playback.action = AnimationType.DEFAULT_EMPTY;
                    }
                }
            }
            nextResetDefault = true;
        }
    }

    public void updateTime() {
        if(playback.action == null) {
            return;
        }
        switch (playback.action){
            case DEFAULT:
                playback.updateTime(DEFAULT);
                break;
            case DEFAULT_EMPTY:
                playback.updateTime(DEFAULT);
                break;
            case DRAW:
                playback.updateTime(DRAW);
                break;
            case DRAW_EMPTY:
                playback.updateTime(DRAW);
                break;
            case INSPECT:
                playback.updateTime(INSPECT);
                break;
            case INSPECT_EMPTY:
                playback.updateTime(INSPECT_EMPTY);
                break;
            case MODE_CHANGE:
                playback.updateTime(MODE_CHANGE);
                break;
            case MODE_CHANGE_EMPTY:
                playback.updateTime(MODE_CHANGE);
                break;
            default:
                break;
        }
        for(AnimationType reloadType:RELOAD_TYPE) {
            if(playback.action==reloadType) {
                playback.updateTime(RELOAD);
                break;
            }
        }
        for(AnimationType fireType:FIRE_TYPE) {
            if(playback.action==fireType) {
                playback.updateTime(FIRE);
                break;
            }
        }
    }

    public void updateActionAndTime() {
        updateAction();
        updateTime();
    }

    public float getTime(){
        return (float)playback.time;
    }

    public float getSprintTime(){
        if(config.animations.get(AnimationType.SPRINT)==null) {
            return 0;
        }
        double startTime = config.animations.get(AnimationType.SPRINT).getStartTime(config.FPS);
        double endTime = config.animations.get(AnimationType.SPRINT).getEndTime(config.FPS);
        double result=Interpolation.LINEAR.interpolate(startTime, endTime, SPRINT_LOOP);
        if(Double.isNaN(result)) {
            return 0;
        }
        return(float) result;
    }

    public void setConfig(GunEnhancedRenderConfig config){
        this.config = config;
    }

    public GunEnhancedRenderConfig getConfig(){
        return this.config;
    }

    public boolean isDrawing() {
        if(player == null) {
            return false;
        }
        Item item = player.getHeldItemMainhand().getItem();
        if (item instanceof ItemGun) {
            if (((ItemGun) item).type.animationType.equals(WeaponAnimationType.ENHANCED)) {
                return playback.action == AnimationType.DRAW;
            }
        }
        return false;
    }

    public boolean isCouldReload() {
        if(player == null) {
            return true;
        }
        Item item = player.getHeldItemMainhand().getItem();
        if (item instanceof ItemGun) {
            if (((ItemGun) item).type.animationType.equals(WeaponAnimationType.ENHANCED)) {
                if (isDrawing()) {
                    return false;
                }
                return !ClientRenderHooks.getEnhancedAnimMachine(player).reloading;
            }
        }
        return true;
    }

    public boolean isCouldShoot() {
        if(player == null) {
            return true;
        }

        if (player.getHeldItemMainhand().getItem() instanceof ItemGun) {
            if (((ItemGun) player.getHeldItemMainhand().getItem()).type.animationType.equals(WeaponAnimationType.ENHANCED)) {
                if (isDrawing()) {
                    return false;
                }
                return !ClientRenderHooks.getEnhancedAnimMachine(player).reloading;
            }
        }
        return true;
    }

    public ItemStack getRenderAmmo(ItemStack ammo) {
        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);
        if(anim.reloading) {
            AnimationType reloadAni=anim.getReloadAnimationType();
            if (anim.getReloadType() == ReloadType.Full && (reloadAni == AnimationType.PRE_RELOAD
                    || reloadAni == AnimationType.RELOAD_FIRST || reloadAni == AnimationType.RELOAD_FIRST_QUICKLY)) {
                return ammo;
            }if (reloadAni == AnimationType.PRE_UNLOAD || reloadAni == AnimationType.UNLOAD|| reloadAni == AnimationType.POST_UNLOAD
                    || reloadAni == AnimationType.PRE_UNLOAD_EMPTY || reloadAni == AnimationType.UNLOAD_EMPTY|| reloadAni == AnimationType.POST_UNLOAD_EMPTY) {
                return ammo;
            }
        }if (ClientTickHandler.reloadEnhancedPrognosisAmmoRendering != null
                && !ClientTickHandler.reloadEnhancedPrognosisAmmoRendering.isEmpty()) {
            return ClientTickHandler.reloadEnhancedPrognosisAmmoRendering;
        }
        return ammo;
    }

    public boolean shouldRenderAmmo() {
        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);
        if(anim.reloading) {
            return anim.getReloadAnimationType() != AnimationType.POST_UNLOAD && anim.getReloadAnimationType() != AnimationType.POST_UNLOAD_EMPTY;
        }if (ClientTickHandler.reloadEnhancedPrognosisAmmoRendering != null
                && !ClientTickHandler.reloadEnhancedPrognosisAmmoRendering.isEmpty()) {
            return true;
        }
        return ItemGun.hasAmmoLoaded(player.getHeldItemMainhand());
    }

}
