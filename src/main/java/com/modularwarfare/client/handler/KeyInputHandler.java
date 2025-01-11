package com.modularwarfare.client.handler;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.client.ClientProxy;
import com.modularwarfare.client.ClientRenderHooks;
import com.modularwarfare.client.fpp.basic.animations.AnimStateMachine;
import com.modularwarfare.client.fpp.enhanced.AnimationType;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.fpp.enhanced.models.EnhancedModel;
import com.modularwarfare.client.fpp.enhanced.models.ModelEnhancedGun;
import com.modularwarfare.client.gui.GuiAttachmentModified;
import com.modularwarfare.client.input.KeyBinding;
import com.modularwarfare.client.fpp.basic.renderers.RenderGunStatic;
import com.modularwarfare.client.scope.ScopeUtils;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.common.network.*;
import com.modularwarfare.utility.MWSound;
import com.modularwarfare.utility.event.ForgeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler extends ForgeEvent {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayerSP entityPlayer = Minecraft.getMinecraft().player;
        if(KeyBinding.ClientReload.isPressed()) {
            ModularWarfare.loadConfig();

            if (entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemGun) {
                final ItemStack gunStack = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                final GunType gunType = ((ItemGun)gunStack.getItem()).type;
                for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                    ItemStack itemStack = GunType.getAttachment(gunStack, attachment);
                    if (itemStack != null && itemStack.getItem() != Items.AIR) {
                        AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                        if (attachmentType.hasModel()) {
                            attachmentType.reloadModel();
                        }
                    }
                }
                if (gunType.hasModel() && gunType.animationType.equals(WeaponAnimationType.ENHANCED)) {
                    gunType.enhancedModel.config = ModularWarfare.getRenderConfig(gunType, GunEnhancedRenderConfig.class);
                } else if(gunType.hasModel()){
                    gunType.reloadModel();
                }
            }

            if (entityPlayer.isSneaking()) {
                ModularWarfare.PROXY.reloadModels(true);
            }
        }
        if(KeyBinding.FireMode.isPressed()) {
            if (entityPlayer.getHeldItemMainhand() != null && entityPlayer.getHeldItemMainhand().getItem() instanceof ItemGun) {
                ItemGun itemGun = (ItemGun) entityPlayer.getHeldItemMainhand().getItem();
                GunType gunType = itemGun.type;
                PacketGunSwitchMode.switchClient(entityPlayer);
                ModularWarfare.NETWORK.sendToServer(new PacketGunSwitchMode());
                ModularWarfare.PROXY.onModeChangeAnimation(entityPlayer, gunType.internalName);
            }
        }if(KeyBinding.Inspect.isPressed()) {
            ItemGun itemGun = (ItemGun) entityPlayer.getHeldItemMainhand().getItem();
            GunType gunType = itemGun.type;
            EnhancedModel model = gunType.enhancedModel;
            if (entityPlayer.getHeldItemMainhand() != null && entityPlayer.getHeldItemMainhand().getItem() instanceof ItemGun) {
                if (gunType.animationType.equals(WeaponAnimationType.ENHANCED)) {
                    if (ClientProxy.gunEnhancedRenderer.controller != null) {
                        if (((GunEnhancedRenderConfig)model.config).animations.containsKey(AnimationType.INSPECT_EMPTY)){
                            if(!ItemGun.hasNextShot(entityPlayer.getHeldItemMainhand())) {
                                ClientProxy.gunEnhancedRenderer.controller.INSPECT_EMPTY = 0;
                            }else {
                                ClientProxy.gunEnhancedRenderer.controller.INSPECT = 0;
                            }
                        }else {
                            ClientProxy.gunEnhancedRenderer.controller.INSPECT = 0;
                        }
                    }
                }
            }
        }if(KeyBinding.GunReload.isPressed()) {
            ItemStack reloadStack = entityPlayer.getHeldItemMainhand();
            if (reloadStack != null && (reloadStack.getItem() instanceof ItemGun || reloadStack.getItem() instanceof ItemAmmo)) {
                if (ClientProxy.gunEnhancedRenderer.controller == null
                        || ClientProxy.gunEnhancedRenderer.controller.isCouldReload()) {
                    ModularWarfare.NETWORK.sendToServer(new PacketGunReload());
                }
            }
        }if(KeyBinding.GunUnload.isPressed()) {
            ItemStack unloadStack = entityPlayer.getHeldItemMainhand();
            if (ClientRenderHooks.getAnimMachine(entityPlayer).attachmentMode) {
                ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(ClientProxy.attachmentUI.selectedAttachEnum.getName(), false));
            } else {
                if (unloadStack != null && (unloadStack.getItem() instanceof ItemGun || unloadStack.getItem() instanceof ItemAmmo)) {
                    ModularWarfare.NETWORK.sendToServer(new PacketGunReload(true));
                }
            }
        }if(KeyBinding.AddAttachment.isPressed()) {
            if(!entityPlayer.isSpectator()) {
                if (entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemGun) {
                    if(((ItemGun)entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem()).type.animationType == WeaponAnimationType.BASIC) {
                        AnimStateMachine stateMachine = ClientRenderHooks.getAnimMachine(entityPlayer);
                        stateMachine.attachmentMode = !stateMachine.attachmentMode;
                        ModularWarfare.PROXY.playSound(new MWSound(entityPlayer.getPosition(), "attachment.open", 1f, 1f));
                    }else if (((ItemGun)entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem()).type.animationType == WeaponAnimationType.ENHANCED) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiAttachmentModified());
                        ModularWarfare.PROXY.playSound(new MWSound(entityPlayer.getPosition(), "attachment.open", 1f, 1f));
                    }
                }
            }
        }if(KeyBinding.Flashlight.isPressed()) {
            if (entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
                if (entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemGun) {
                    final ItemStack gunStack = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                    if (GunType.getAttachment(gunStack, AttachmentPresetEnum.Flashlight) != null) {
                        final ItemAttachment itemAttachment = (ItemAttachment) GunType.getAttachment(gunStack, AttachmentPresetEnum.Flashlight).getItem();
                        if (itemAttachment != null) {
                            RenderGunStatic.isLightOn = !RenderGunStatic.isLightOn;
                        }
                        ModularWarfare.PROXY.playSound(new MWSound(entityPlayer.getPosition(), "attachment.apply", 1f, 1f));
                    }
                }
            }
        }if(KeyBinding.CustomInventory.isPressed()) {
            ModularWarfare.NETWORK.sendToServer(new PacketOpenGui(0));
        }if(KeyBinding.SwitchLaser.isPressed()) {
            ModularWarfare.NETWORK.sendToServer(new PacketSwitchLaser());
        }if(KeyBinding.FovZoomAdd.isPressed()) {
            final ItemStack gunStack = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            final ItemAttachment itemAttachment = (ItemAttachment) GunType.getAttachment(gunStack, AttachmentPresetEnum.Sight).getItem();

            ScopeUtils.FovZoomAdd(itemAttachment);
        }if(KeyBinding.FovZoomDes.isPressed()) {
            final ItemStack gunStack = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            final ItemAttachment itemAttachment = (ItemAttachment) GunType.getAttachment(gunStack, AttachmentPresetEnum.Sight).getItem();

            ScopeUtils.FovZoomDes(itemAttachment);
        }
    }
}