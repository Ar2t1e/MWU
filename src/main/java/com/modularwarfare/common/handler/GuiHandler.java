package com.modularwarfare.common.handler;

import com.modularwarfare.client.gui.GuiInventoryModified;
import com.modularwarfare.common.container.ContainerInventoryModified;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.craft.container.ContainerWorkbenchGuns;
import com.modularwarfare.craft.guicraft.WorkbenchGuns;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    public Container getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (id) {
            case 0: {
                return new ContainerInventoryModified(player.inventory, !world.isRemote, player);
            }case 1: {
                return new ContainerWorkbenchGuns(player.inventory);
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public Gui getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (id) {
            case 0: {
                return new GuiInventoryModified(player);
            } case 1: {
                return new WorkbenchGuns(player);
            }
            default: {
                return null;
            }
        }
    }
}
