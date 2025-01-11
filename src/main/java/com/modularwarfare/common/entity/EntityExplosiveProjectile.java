package com.modularwarfare.common.entity;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.client.ClientProxy;
import com.modularwarfare.common.guns.GunType;
import com.modularwarfare.common.guns.ItemBullet;
import com.modularwarfare.common.guns.ItemGun;
import com.modularwarfare.common.guns.WeaponType;
import com.modularwarfare.common.init.ModSounds;
import mchhui.easyeffect.EasyEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityExplosiveProjectile extends EntityBullet implements IProjectile {

    public EntityExplosiveProjectile(World world) {
        super(world);
        setSize(0.2F, 0.2F);
    }

    public EntityExplosiveProjectile(World par1World, EntityPlayer par2EntityPlayer, float damage, float accuracy, float velocity, String bulletName) {
        super(par1World, par2EntityPlayer, damage, accuracy, velocity, bulletName);
    }

    public void onUpdate() {
        super.onUpdate();
        ItemBullet itemBullet = ModularWarfare.bulletTypes.get(this.getBulletName());
        Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);

        if (itemBullet != null){
            if (itemBullet.type.explosionBullet.isGravidy == true){
                this.motionY -= itemBullet.type.explosionBullet.gravityY;
            }
            if (itemBullet.type.explosionBullet.isLeavesMark == true){
               ModularWarfare.PROXY.spawnParticleLeavesMark(world, posX, posY, posZ);
            }
        }

        if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            explode();
            this.setDead();
        }
    }

    public void explode(){
        if (!this.world.isRemote) {
            if (ModularWarfare.bulletTypes.containsKey(this.getBulletName())) {
                ItemBullet itemBullet = ModularWarfare.bulletTypes.get(this.getBulletName());

                ModularWarfare.PROXY.spawnParticleExplosionRocket(world, posX, posY, posZ);
                Explosion explosion = new Explosion(this.world,this.player, this.posX, this.posY, this.posZ, itemBullet.type.explosionBullet.explosionStrength, itemBullet.type.explosionBullet.flaming, itemBullet.type.explosionBullet.damageWorld);
                explosion.doExplosionA();
                explosion.doExplosionB(true);

            }
        }
        this.setDead();
    }
}