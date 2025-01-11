package com.modularwarfare.common.network;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.guns.GunType;
import com.modularwarfare.common.guns.ItemBullet;
import com.modularwarfare.common.guns.ItemGun;
import com.modularwarfare.common.particle.customParticle.customParticle;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mchhui.easyeffect.EasyEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class PacketCustomEffect extends PacketBase {
    //Позицыя партикла в мире
    private double posX;
    private double posY;
    private double posZ;

    public PacketCustomEffect() {}

    public PacketCustomEffect(double posX, double posY, double posZ) {
        //устанавливаем позицыю
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        //кодруем позицыю в байты для хранения в пакете
        data.writeDouble(posX);
        data.writeDouble(posY);
        data.writeDouble(posZ);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        //расжифровываем позицыю из байт
        posX = data.readDouble();
        posY = data.readDouble();
        posZ = data.readDouble();
    }

    @Override
    public void handleServerSide(EntityPlayerMP playerEntity) {
        //определяем мобов в мире
        World world = playerEntity.getEntityWorld();
        //создаём список из всех игроков на сервере
        List<EntityPlayerMP> players = world.getPlayers(EntityPlayerMP.class, player -> true);
        //рендерим партикл у всех игроков на сервере
        for (EntityPlayerMP player : players) {
            customParticle.customLeavesMarkParticle(playerEntity, posX, posY, posZ);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer clientPlayer) {}
}
