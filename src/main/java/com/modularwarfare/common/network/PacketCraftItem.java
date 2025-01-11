package com.modularwarfare.common.network;

import com.modularwarfare.craft.guicraft.WorkbenchGuns;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.oredict.OreDictionary;

import static com.modularwarfare.craft.guicraft.WorkbenchGuns.currentRecipe;

/**
 * Хехехе если хочешь можешь брать данный код (спойлер он не работает коректно)🤣🤣
 */
public class PacketCraftItem extends PacketBase {
    private ItemStack craftedItem;

    public PacketCraftItem() {
    }

    public PacketCraftItem(ItemStack craftedItem) {
        this.craftedItem = craftedItem;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        NBTTagCompound tag = new NBTTagCompound();
        craftedItem.writeToNBT(tag);
        ByteBufUtils.writeTag(data, tag);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
        NBTTagCompound tag = ByteBufUtils.readTag(data);
        craftedItem = new ItemStack(tag);
    }

    @Override
    public void handleServerSide(EntityPlayerMP playerEntity) {
        IInventory inventory = playerEntity.inventory;

        for (ItemStack ingredient : currentRecipe.getInputItems()) {
            int remainingAmount = ingredient.getCount() * WorkbenchGuns.getCount();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null && OreDictionary.itemMatches(ingredient, stack, false)) {
                    int amountToRemove = Math.min(remainingAmount, stack.getCount());
                    stack.shrink(amountToRemove);
                    remainingAmount -= amountToRemove;

                    if (stack.getCount() <= 0) {
                        inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                    } else {
                        inventory.setInventorySlotContents(i, stack);
                    }

                    if (remainingAmount <= 0) {
                        break;
                    }
                }
            }
            playerEntity.inventoryContainer.detectAndSendChanges();
        }

        playerEntity.getServerWorld().addScheduledTask(() -> {
            ItemStack doubleItem = craftedItem.copy();
            doubleItem.setCount(WorkbenchGuns.getCount());
            if (!playerEntity.inventory.addItemStackToInventory(doubleItem)) {
                playerEntity.dropItem(doubleItem, false);
            }
            playerEntity.inventoryContainer.detectAndSendChanges();
        });
    }

    @Override
    public void handleClientSide(EntityPlayer clientPlayer) {
        // Client side handling not needed
    }
}
