package com.modularwarfare.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class PacketDestroyBlock extends PacketBase {
    private BlockPos blockPos;

    public PacketDestroyBlock() {
    }

    public PacketDestroyBlock(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        data.writeInt(blockPos.getX());
        data.writeInt(blockPos.getY());
        data.writeInt(blockPos.getZ());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();
        blockPos = new BlockPos(x, y, z);
    }

    @Override
    public void handleServerSide(EntityPlayerMP playerEntity) {
        playerEntity.world.destroyBlock(blockPos, false);
    }

    @Override
    public void handleClientSide(EntityPlayer clientPlayer) {
        // Client side handling not needed for block destruction
    }
}
