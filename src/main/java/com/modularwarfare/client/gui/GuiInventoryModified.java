package com.modularwarfare.client.gui;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.backpacks.ItemBackpack;
import com.modularwarfare.common.container.ContainerInventoryModified;
import com.modularwarfare.utility.RenderHelperMW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class GuiInventoryModified extends InventoryEffectRenderer {
    public static final ResourceLocation ICONS;
    public static final ResourceLocation INVENTORY_BG;
    public static final ResourceLocation INVENTORY_X8;
    public static final ResourceLocation INVENTORY_X16;
    public static final ResourceLocation INVENTORY_X24;
    public static final ResourceLocation INVENTORY_X32;
    public static final ResourceLocation INVENTORY_X48;

    static {
        ICONS = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/icons.png");
        INVENTORY_BG = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory.png");
        INVENTORY_X8 = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory_x8.png");
        INVENTORY_X16 = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory_x16.png");
        INVENTORY_X24 = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory_x24.png");
        INVENTORY_X32 = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory_x32.png");
        INVENTORY_X48 = new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/inventory_x48.png");
    }

    private float oldMouseX;
    private float oldMouseY;

    public GuiInventoryModified(final EntityPlayer player) {
        super(new ContainerInventoryModified(player.inventory, !player.getEntityWorld().isRemote, player));
        this.allowUserInput = true;
        this.xSize = 176;
        /** The Y size of the inventory window in pixels. */
        this.ySize = 185;
    }

    private void resetGuiLeft() {
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    public void updateScreen() {
        this.updateActivePotionEffects();
        this.resetGuiLeft();
    }

    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        this.resetGuiLeft();
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (!(Minecraft.getMinecraft().player.openContainer instanceof ContainerInventoryModified)) {
            return;
        }
        this.drawDefaultBackground();
        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        final int k = this.guiLeft;
        final int l = this.guiTop + 9;

        final ContainerInventoryModified containter = (ContainerInventoryModified) Minecraft.getMinecraft().player.openContainer;
        final IItemHandler backpack = (IItemHandler) containter.extra;
        if (backpack.getStackInSlot(0).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,(EnumFacing)null)){
            ItemBackpack itemBackpack = ((ItemBackpack) containter.extra.getStackInSlot(0).getItem());
            final ItemStack stack = backpack.getStackInSlot(0);

            if (((ItemBackpack) stack.getItem()).type.size == 8){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X8);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }else if (((ItemBackpack) stack.getItem()).type.size == 16){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X16);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }else if (((ItemBackpack) stack.getItem()).type.size == 24){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X24);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }else if (((ItemBackpack) stack.getItem()).type.size == 32){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X32);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }else if (((ItemBackpack) stack.getItem()).type.size == 48){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X48);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }else if (((ItemBackpack) stack.getItem()).type.size == 64){
                this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_X16);
                RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop, 320, 256);
            }
        }else {
            this.mc.getTextureManager().bindTexture(GuiInventoryModified.INVENTORY_BG);
            RenderHelperMW.drawTexturedRect(this.guiLeft, this.guiTop + 9, 320, 256);
        }

        if (backpack.getStackInSlot(0).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing) null)) {
            final ItemStack stack = backpack.getStackInSlot(0);
            final IItemHandler backpackInv = (IItemHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing) null);
            int xP = 0;
            int yP = 0;
            final int x = k + 120;
            final int y = l + 18;
            this.mc.getTextureManager().bindTexture(GuiInventoryModified.ICONS);
            this.drawTexturedModalRect(x - 5, y - 18, 18, 0, 82, 18);
            this.drawTexturedModalRect(x - 5, y, 18, 5, 82, 18);
            for (int i = 0; i < backpackInv.getSlots(); ++i) {
                this.drawSlotBackground(x + xP * 18, -1 + y + yP * 18);
                if (++xP % 4 == 0) {
                    xP = 0;
                    ++yP;
                    if (i + 1 < backpackInv.getSlots()) {
                        this.drawTexturedModalRect(x - 5, y + yP * 18, 18, 5, 82, 18);
                    }
                } else if (i + 1 >= backpackInv.getSlots()) {
                    ++yP;
                }
            }
            this.drawTexturedModalRect(x - 5, -1 + y + yP * 18, 18, 33, 82, 5);

            if (stack != null) {
                ItemBackpack backpackItem = (ItemBackpack) stack.getItem();
                //this.fontRenderer.drawString(backpackItem.type.displayName, x, y - 12, 16777215);
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.color(1,1,1,1);
        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float)(k + 51) - this.oldMouseX, (float)(l + 75 - 50) - this.oldMouseY, this.mc.player);
        GlStateManager.popMatrix();
    }

    protected void actionPerformed(final GuiButton button) {
    }

    public void drawSlotBackground(final int x, final int y) {
        this.mc.getTextureManager().bindTexture(GuiInventoryModified.ICONS);
        this.drawTexturedModalRect(x, y, 0, 0, 18, 18);
    }
}
