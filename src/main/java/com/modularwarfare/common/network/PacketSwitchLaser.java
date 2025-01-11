package com.modularwarfare.common.network;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.guns.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketSwitchLaser extends PacketBase {


    public PacketSwitchLaser() {
    }

    public PacketSwitchLaser(String attachmentType, boolean unloadAll) {
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
    }

    @Override
    public void handleServerSide(EntityPlayerMP entityPlayer) {
        if (entityPlayer.getHeldItemMainhand() != null) {
            if (entityPlayer.getHeldItemMainhand().getItem() instanceof ItemGun) {
                ItemStack gunStack = entityPlayer.getHeldItemMainhand();
                GunType gunType = ((ItemGun)gunStack.getItem()).type;

                if (!gunStack.hasTagCompound())
                    return;

                if (GunType.getAttachment(gunStack,AttachmentPresetEnum.Laser) == null)
                    return;

                boolean laserEnabled = false;
                if (gunStack.getTagCompound().hasKey("laserEnabled"))
                    laserEnabled = gunStack.getTagCompound().getBoolean("laserEnabled");

                gunStack.getTagCompound().setBoolean("laserEnabled", !laserEnabled);
                gunType.playSound(entityPlayer,WeaponSoundType.ModeSwitch,gunStack);

            }
        }
    }


    @Override
    public void handleClientSide(EntityPlayer entityPlayer) {
        // UNUSED
    }

}