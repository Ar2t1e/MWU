package com.modularwarfare.common.network;

import com.modularwarfare.common.particle.customParticle.customParticle;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.List;

public class PacketEXPEffect extends PacketBase {
    //Позицыя партикла в мире
    private double posX;
    private double posY;
    private double posZ;

    public PacketEXPEffect() {}

    public PacketEXPEffect(double posX, double posY, double posZ) {
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
            customParticle.customExplosionParticle(playerEntity, posX, posY, posZ);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer clientPlayer) {}
}
