package com.modularwarfare.common.particle.customParticle;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.guns.BulletType;
import com.modularwarfare.common.guns.GunType;
import com.modularwarfare.common.guns.ItemBullet;
import com.modularwarfare.common.guns.ItemGun;
import mchhui.easyeffect.EasyEffect;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;

public class customParticle {
    public static void customExplosionParticle(EntityPlayerMP playerEntity, double posX, double posY, double posZ){
        ItemStack stack = playerEntity.getHeldItemMainhand();
        ItemGun itemGun = (ItemGun) stack.getItem();
        GunType gunType = itemGun.type;
        ItemBullet itemBullet = ItemGun.getUsedBullet(stack, gunType);

        if (itemBullet == null)
            return;
        BulletType bulletType = itemBullet.type;

        EasyEffect.sendEffect(playerEntity, posX,
                posY + .4, posZ,
                0, 0, 0, 0, 0, 0, bulletType.explosionBullet.explodeConfig.delay, bulletType.explosionBullet.explodeConfig.fps, bulletType.explosionBullet.explodeConfig.length,
                bulletType.explosionBullet.explodeConfig.unit, bulletType.explosionBullet.explodeConfig.size,
                String.valueOf(new ResourceLocation(ModularWarfare.MOD_ID, "textures/particle/" + bulletType.explosionBullet.explodeConfig.setExplodeTexture + ".png")));
        EasyEffect.sendEffect(playerEntity, posX,
                posY + .8, posZ + 1,
                0, 0, 0, 0, 0, 0, bulletType.explosionBullet.explodeConfig.delay, bulletType.explosionBullet.explodeConfig.fps, bulletType.explosionBullet.explodeConfig.length,
                bulletType.explosionBullet.explodeConfig.unit, bulletType.explosionBullet.explodeConfig.size,
                String.valueOf(new ResourceLocation(ModularWarfare.MOD_ID, "textures/particle/" + bulletType.explosionBullet.explodeConfig.setExplodeTexture + ".png")));
        EasyEffect.sendEffect(playerEntity, posX,
                posY + .8, posZ - 1,
                0, 0, 0, 0, 0, 0, bulletType.explosionBullet.explodeConfig.delay, bulletType.explosionBullet.explodeConfig.fps, bulletType.explosionBullet.explodeConfig.length,
                bulletType.explosionBullet.explodeConfig.unit, bulletType.explosionBullet.explodeConfig.size,
                String.valueOf(new ResourceLocation(ModularWarfare.MOD_ID, "textures/particle/" + bulletType.explosionBullet.explodeConfig.setExplodeTexture + ".png")));
    }

    public static void customLeavesMarkParticle(EntityPlayerMP playerEntity, double posX, double posY, double posZ){
        ItemStack stack = playerEntity.getHeldItemMainhand();
        ItemGun itemGun = (ItemGun) stack.getItem();
        GunType gunType = itemGun.type;
        ItemBullet itemBullet = ItemGun.getUsedBullet(stack, gunType);

        if (itemBullet == null)
            return;
        BulletType bulletType = itemBullet.type;

        EasyEffect.sendEffect(playerEntity, posX,
                posY, posZ,
                0, 0, 0, 0, 0, 0, bulletType.explosionBullet.leavesMarkConfig.delay, bulletType.explosionBullet.leavesMarkConfig.fps, bulletType.explosionBullet.leavesMarkConfig.length,
                bulletType.explosionBullet.leavesMarkConfig.unit, bulletType.explosionBullet.leavesMarkConfig.size,
                String.valueOf(new ResourceLocation(ModularWarfare.MOD_ID, "textures/particle/" + bulletType.explosionBullet.leavesMarkConfig.setLeavesMarkTexture + ".png")));
    }
}
