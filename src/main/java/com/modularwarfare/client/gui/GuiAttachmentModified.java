package com.modularwarfare.client.gui;

import com.modularwarfare.ModConfig;
import com.modularwarfare.ModularWarfare;

import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.gui.button.TextureButton;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.common.network.PacketGunAddAttachment;
import com.modularwarfare.common.network.PacketGunUnloadAttachment;
import com.modularwarfare.common.type.BaseItem;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.utility.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiAttachmentModified extends GuiScreen {
    public ItemStack currentModify;
    private List<Integer> attachmentSlotList = null;
    private TextureButton selectedSideButton = null;
    private final int sideButtonIdOffset = 20;
    private final int subSlotIdOffset = 100;
    public static final ResourceLocation arrow_down = new ResourceLocation("modularwarfare", "textures/modifygui/arrow_down.png");
    public static final ResourceLocation arrow_left = new ResourceLocation("modularwarfare", "textures/modifygui/arrow_left.png");
    public static final ResourceLocation arrow_right = new ResourceLocation("modularwarfare", "textures/modifygui/arrow_right.png");
    public static final ResourceLocation arrow_up = new ResourceLocation("modularwarfare", "textures/modifygui/arrow_up.png");
    public static final ResourceLocation leftarrow = new ResourceLocation("modularwarfare", "textures/modifygui/leftarrow.png");
    public static final ResourceLocation rigihtarrow = new ResourceLocation("modularwarfare", "textures/modifygui/rigihtarrow.png");
    public static final ResourceLocation arrow_fon = new ResourceLocation("modularwarfare", "textures/modifygui/arrow_fon.png");
    public static final ResourceLocation block_slot = new ResourceLocation("modularwarfare", "textures/modifygui/block_slot.png");
    public static final ResourceLocation slot = new ResourceLocation("modularwarfare", "textures/modifygui/slot.png");
    public static final ResourceLocation null_slot = new ResourceLocation("modularwarfare", "textures/modifygui/null_slot.png");
    public static final ResourceLocation slot_list = new ResourceLocation("modularwarfare", "textures/modifygui/slot_list.png");
    public static final ResourceLocation attachmentmode = new ResourceLocation("modularwarfare", "textures/modifygui/attachslot.png");
    public static final ResourceLocation attachslot_yelow = new ResourceLocation("modularwarfare", "textures/modifygui/attachslot_yelow.png");
    public static final ResourceLocation quit = new ResourceLocation("modularwarfare", "textures/modifygui/quit.png");
    public static final ResourceLocation name_attachment_fon = new ResourceLocation("modularwarfare", "textures/modifygui/name_attachment_fon.png");
    public static final ResourceLocation decal_1 = new ResourceLocation("modularwarfare", "textures/modifygui/decal_1.png");
    public static TextureButton QUITBUTTON,ATTACHMENTMODE,MODIFICATIONMODE,CUSTOMIZATIONMODE,
            FALSE_BARREL,FALSE_CHARM,FALSE_FLASHLIGHT,FALSE_GRIP,FALSE_SIGHT,FALSE_SKIN,FALSE_STOCK,FALSE_HANDGUARD,FALSE_LASER,FALSE_RALING;
    public int currentPage = 0;
    public int totalPages = 6;
    public int currentPageModifi = 0;
    public boolean on = false;
    public static boolean clickOnce = false;
    public AttachmentType itemAttachment;

    public GuiAttachmentModified() {
        updateItemModifying();

        initGui();
    }

    public void updateItemModifying() {
        if (Minecraft.getMinecraft().player == null)
            return;

        ItemStack itemMainhand=Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        boolean refresh=false;
        if(this.currentModify!=null&&!this.currentModify.equals(itemMainhand)) {
            refresh=true;
        }
        this.currentModify = itemMainhand;
        if(refresh) {
            initGui();
        }
    }
    @Override
    public void initGui() {
        GlStateManager.disableAlpha();
        if(currentModify == null)
            return;

        mc=Minecraft.getMinecraft();

        double sFactor = mc.displayWidth / 1920d;

        ScaledResolution scaledresolution = new ScaledResolution(mc);

        this.buttonList.clear();

        int buttonSize = (int) (sFactor/scaledresolution.getScaleFactor() * 98);
        int buttonSize2 = (int) (sFactor/scaledresolution.getScaleFactor() * 32);
        int buttonSize3 = (int) (sFactor/scaledresolution.getScaleFactor() * 32);

        GlStateManager.enableBlend();
        QUITBUTTON = new TextureButton(1000, scaledresolution.getScaleFactor() - 0 * 0.0f, scaledresolution.getScaledHeight() - 0 * 1.0f, 0, 0, quit,"").setType(TextureButton.TypeEnum.Button);

        FALSE_BARREL = new TextureButton(1007, scaledresolution.getScaledWidth() - buttonSize * 14,scaledresolution.getScaledWidth() - buttonSize * 17.4, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_CHARM= new TextureButton(1008, scaledresolution.getScaledWidth() - buttonSize * 13,scaledresolution.getScaledWidth() - buttonSize * 13, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_FLASHLIGHT = new TextureButton(1009, scaledresolution.getScaledWidth() - buttonSize * 13,scaledresolution.getScaledWidth() - buttonSize * 13, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_GRIP = new TextureButton(1010, scaledresolution.getScaledWidth() - buttonSize * 13,scaledresolution.getScaledWidth() - buttonSize * 13, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_SIGHT = new TextureButton(1011, scaledresolution.getScaledWidth() - buttonSize * 6,scaledresolution.getScaledWidth() - buttonSize * 16, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_SKIN = new TextureButton(1012, scaledresolution.getScaledWidth() - buttonSize * 8,scaledresolution.getScaledWidth() - buttonSize * 17.4, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_STOCK = new TextureButton(1013, scaledresolution.getScaledWidth() - buttonSize * 6,scaledresolution.getScaledWidth() - buttonSize * 16, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_HANDGUARD = new TextureButton(1014, scaledresolution.getScaledWidth() - buttonSize * 11,scaledresolution.getScaledWidth() - buttonSize * 19, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_LASER = new TextureButton(1015, scaledresolution.getScaledWidth() - buttonSize * 13,scaledresolution.getScaledWidth() - buttonSize * 13, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);
        FALSE_RALING = new TextureButton(1016, scaledresolution.getScaledWidth() - buttonSize * 13,scaledresolution.getScaledWidth() - buttonSize * 13, buttonSize,buttonSize, block_slot,"").setType(TextureButton.TypeEnum.AirSlot);

        this.buttonList.add(QUITBUTTON);
        this.buttonList.add(FALSE_BARREL);
        this.buttonList.add(FALSE_LASER);
        this.buttonList.add(FALSE_GRIP);
        this.buttonList.add(FALSE_SIGHT);
        this.buttonList.add(FALSE_FLASHLIGHT);
        this.buttonList.add(FALSE_STOCK);
        this.buttonList.add(FALSE_HANDGUARD);
        this.buttonList.add(FALSE_RALING);
        this.buttonList.add(FALSE_CHARM);
        this.buttonList.add(FALSE_SKIN);
        this.buttonList.add(ATTACHMENTMODE);
        this.buttonList.add(MODIFICATIONMODE);
        this.buttonList.add(CUSTOMIZATIONMODE);

        this.buttonList.add(new TextureButton(0,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Barrel).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(3,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Grip).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(4,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Sight).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(8,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Laser).setType(TextureButton.TypeEnum.Slot));

        this.buttonList.add(new TextureButton(0+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(3+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(4+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(8+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));

        this.buttonList.add(new TextureButton(2,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Flashlight).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(6,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Stock).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(7,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Handguard).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(9,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Raling).setType(TextureButton.TypeEnum.Slot));

        this.buttonList.add(new TextureButton(2+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(6+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(7+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(9+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));

        this.buttonList.add(new TextureButton(1,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Charm).setType(TextureButton.TypeEnum.Slot));
        this.buttonList.add(new TextureButton(5,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize,slot,"").setAttachment(AttachmentPresetEnum.Skin).setType(TextureButton.TypeEnum.Slot));

        this.buttonList.add(new TextureButton(1+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));
        this.buttonList.add(new TextureButton(5+sideButtonIdOffset,scaledresolution.getScaleFactor() - buttonSize,scaledresolution.getScaleFactor() - buttonSize,
                buttonSize,buttonSize/3,decal_1,"").setType(TextureButton.TypeEnum.SideButton));

        if (currentPageModifi == 0){
            ATTACHMENTMODE = new TextureButton(1201, sFactor/scaledresolution.getScaleFactor() * 20, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachslot_yelow,"").setType(TextureButton.TypeEnum.Button);
            MODIFICATIONMODE = new TextureButton(1202, sFactor/scaledresolution.getScaleFactor() * 76, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
            CUSTOMIZATIONMODE = new TextureButton(1203, sFactor/scaledresolution.getScaleFactor() * 136, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
        }else if (currentPageModifi == 1){
            ATTACHMENTMODE = new TextureButton(1201, sFactor/scaledresolution.getScaleFactor() * 20, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
            MODIFICATIONMODE = new TextureButton(1202, sFactor/scaledresolution.getScaleFactor() * 76, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachslot_yelow,"").setType(TextureButton.TypeEnum.Button);
            CUSTOMIZATIONMODE = new TextureButton(1203, sFactor/scaledresolution.getScaleFactor() * 136, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
        }else if (currentPageModifi == 2){
            ATTACHMENTMODE = new TextureButton(1201, sFactor/scaledresolution.getScaleFactor() * 20, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
            MODIFICATIONMODE = new TextureButton(1202, sFactor/scaledresolution.getScaleFactor() * 76, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachmentmode,"").setType(TextureButton.TypeEnum.Button);
            CUSTOMIZATIONMODE = new TextureButton(1203, sFactor/scaledresolution.getScaleFactor() * 136, sFactor/scaledresolution.getScaleFactor() * 128, buttonSize2, buttonSize3, attachslot_yelow,"").setType(TextureButton.TypeEnum.Button);
        }


        updateButtonItem();
    }

    public List<Integer> checkAttach(EntityPlayer player, GunType gunType, AttachmentPresetEnum attachmentEnum) {
        List<Integer> attachments = new ArrayList<>();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = player.inventory.getStackInSlot(i);
            if (attachmentEnum != AttachmentPresetEnum.Skin) {
                if (itemStack != null && itemStack.getItem() instanceof ItemAttachment) {
                    ItemAttachment itemAttachment = (ItemAttachment) itemStack.getItem();
                    AttachmentType attachType = itemAttachment.type;
                    if (attachType.attachmentType.equals(attachmentEnum)) {
                        if (gunType.acceptedAttachments.get(attachType.attachmentType) != null && gunType.acceptedAttachments.get(attachType.attachmentType).size() >= 1) {
                            if (gunType.acceptedAttachments.get(attachType.attachmentType).contains(attachType.internalName)) {
                                attachments.add(i);
                            }
                        }
                    }
                }
            } else {
                if (itemStack != null && itemStack.getItem() instanceof ItemSpray) {
                    ItemSpray itemSpray = (ItemSpray) itemStack.getItem();
                    SprayType attachType = itemSpray.type;
                    for (int j = 0; j < gunType.modelSkins.length; j++) {
                        if (gunType.modelSkins[j].internalName.equalsIgnoreCase(attachType.skinName)) {
                            attachments.add(i);
                        }
                    }
                }
            }
        }
        return attachments;
    }

    public void joinSubPageButtons(EntityPlayer player,ScaledResolution scaledresolution, double sFactor ) {
        TextureButton currentButton=this.getButton(selectedSideButton.id-this.sideButtonIdOffset);
        BaseType type = ((BaseItem) this.currentModify.getItem()).baseType;
        GunType gunType = (GunType) type;
        attachmentSlotList=checkAttach(mc.player,gunType,currentButton.getAttachmentType());
        int buttonSize= (int) (95 * sFactor/scaledresolution.getScaleFactor());
        int buttonSizeArrowWidht= (int) (15 * sFactor/scaledresolution.getScaleFactor());
        int buttonSizeArrowHeight= (int) (18 * sFactor/scaledresolution.getScaleFactor());

        this.buttonList.add(new TextureButton(1195, 9999, 9999, buttonSizeArrowWidht, buttonSizeArrowHeight, rigihtarrow,"").setType(TextureButton.TypeEnum.NameAttachSlot));
        this.buttonList.add(new TextureButton(1196, 9999, 9999, buttonSizeArrowWidht, buttonSizeArrowHeight, leftarrow,"").setType(TextureButton.TypeEnum.NameAttachSlot1));

        for(Integer inventoryId:attachmentSlotList) {
            this.buttonList.add(new TextureButton(subSlotIdOffset+inventoryId,9999,9999,buttonSize,buttonSize, null_slot,"").setAttachment(currentButton.getAttachmentType()).setType(TextureButton.TypeEnum.SubSlot).setItemStack(player.inventory.getStackInSlot(inventoryId)));
        }
    }

    public boolean gunHasMultiSkins() {
        BaseType type = ((BaseItem) this.currentModify.getItem()).baseType;
        GunType gunType = (GunType) type;
        return gunType.modelSkins!=null&&gunType.modelSkins.length>1;
    }

    public void updateButtonItem() {
        BaseType type = ((BaseItem) this.currentModify.getItem()).baseType;
        GunType gunType = (GunType) type;
        List<AttachmentPresetEnum> keys = new ArrayList<>(gunType.acceptedAttachments.keySet());
        if (gunType.modelSkins.length > 1) {
            keys.add(AttachmentPresetEnum.Skin);
        }
        for(GuiButton button:this.buttonList) {
            if(button instanceof TextureButton) {
                TextureButton tb=(TextureButton) button;
                if(tb.getAttachmentType()==AttachmentPresetEnum.Skin&&gunHasMultiSkins()) {
                    int skinId = 0;
                    if (this.currentModify.hasTagCompound()) {
                        if (this.currentModify.getTagCompound().hasKey("skinId")) {
                            skinId = this.currentModify.getTagCompound().getInteger("skinId");
                        }
                    }
                    ItemSpray itemSpray=null;
                    for(Item item:Item.REGISTRY) {
                        if (item instanceof ItemSpray) {
                            ItemSpray itemS = (ItemSpray) item;
                            SprayType attachType = itemS.type;
                            if(gunType.modelSkins[skinId].internalName.equalsIgnoreCase(attachType.skinName)) {
                                itemSpray=itemS;
                                break;
                            }
                        }
                    }
                    if(itemSpray!=null) {
                        ItemStack skinItem=new ItemStack(itemSpray,1);
                        tb.setItemStack(skinItem);
                    }
                }
            }
        }
        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
            ItemStack itemStack = GunType.getAttachment(this.currentModify, attachment);
            if (keys.contains(attachment)&&itemStack != null && !itemStack.isEmpty()) {
                for(GuiButton button:this.buttonList) {
                    if(button instanceof TextureButton) {
                        TextureButton tb=(TextureButton) button;
                        if(tb.getType().equals(TextureButton.TypeEnum.Slot)&&tb.getAttachmentType()==attachment) {
                            tb.setItemStack(itemStack);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        clickOnce=false;
        updateItemModifying();
        double sFactor = mc.displayWidth / 1920d;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        EntityLivingBase entitylivingbaseIn = Minecraft.getMinecraft().player;
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(),0.0D, 100.0D, 4000.0D);
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        int color = ColorUtils.getARGB(0, 0, 0, 0);
        this.drawGradientRect(0, 0, width, height, color, color);

        GlStateManager.disableLighting();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        BaseType type = ((BaseItem) this.currentModify.getItem()).baseType;
        ItemStack itemstack = this.currentModify;
        renderGui(sFactor,type,scaledresolution);
        renderNameAttachmentSlot(sFactor,scaledresolution);
        renderSlotStuff(mouseX,mouseY,entitylivingbaseIn, type,1);

        QUITBUTTON.drawButton(mc, mouseX, mouseY, partialTicks);
        MODIFICATIONMODE.drawButton(mc, mouseX, mouseY, partialTicks);
        ATTACHMENTMODE.drawButton(mc, mouseX, mouseY, partialTicks);
        CUSTOMIZATIONMODE.drawButton(mc, mouseX, mouseY, partialTicks);

        if (currentPageModifi == 0){
            FALSE_GRIP.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_BARREL.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_SIGHT.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_LASER.drawButton(mc,mouseX,mouseY,partialTicks);
        }else if (currentPageModifi == 1){
            FALSE_FLASHLIGHT.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_STOCK.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_HANDGUARD.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_RALING.drawButton(mc,mouseX,mouseY,partialTicks);
        }else if (currentPageModifi == 2){
            FALSE_CHARM.drawButton(mc, mouseX, mouseY, partialTicks);
            FALSE_SKIN.drawButton(mc, mouseX, mouseY, partialTicks);
        }

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

    }

    public void renderSlotStuff(int mouseX, int mouseY,EntityLivingBase entitylivingbaseIn,BaseType type,float partialTicks) {
        GL11.glTranslatef(0, 0, 0);
        GlStateManager.disableTexture2D();
        GunType gunType = (GunType) type;

        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton && ((TextureButton) button).getType().equals(TextureButton.TypeEnum.SideButton)) {
                button.visible = false;
            }
        }

        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Sight) {
                    if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null &&
                            GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) == null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        if (handguardAttachment.type.handguard.disableButtonSight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_SIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotSigint == true) {
                                FALSE_SIGHT.visible = false;
                                tb.visible = gunType.slotSigint;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotSigint;
                            } else {
                                FALSE_SIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null &&
                            GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (handguardAttachment.type.handguard.disableButtonSight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_SIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (railingAttachment.type.railing.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotSigint == true) {
                                FALSE_SIGHT.visible = false;
                                tb.visible = gunType.slotSigint;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotSigint;
                            } else {
                                FALSE_SIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null &&
                            GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) == null) {
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonSight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_SIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else {
                            if (gunType.slotSigint == true) {
                                FALSE_SIGHT.visible = false;
                                tb.visible = gunType.slotSigint;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotSigint;
                            } else {
                                FALSE_SIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null &&
                            GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonSight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_SIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (handguardAttachment.type.handguard.showButtonSight == true){
                            FALSE_SIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else {
                            if (gunType.slotSigint == true) {
                                FALSE_SIGHT.visible = false;
                                tb.visible = gunType.slotSigint;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotSigint;
                            } else {
                                FALSE_SIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else {
                        if (gunType.slotSigint == true) {
                            FALSE_SIGHT.visible = false;
                            tb.visible = gunType.slotSigint;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = gunType.slotSigint;
                        } else {
                            FALSE_SIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Barrel) {
                    if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        if (handguardAttachment.type.handguard.disableButtonBarrel == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_BARREL.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonBarrel == true){
                            FALSE_BARREL.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotBarel == true) {
                                FALSE_BARREL.visible = false;
                                tb.visible = gunType.slotBarel;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotBarel;
                            } else {
                                FALSE_BARREL.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else {
                        if (gunType.slotBarel == true) {
                            FALSE_BARREL.visible = false;
                            tb.visible = gunType.slotBarel;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = gunType.slotBarel;
                        } else {
                            FALSE_BARREL.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Grip) {
                    if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) == null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        if (handguardAttachment.type.handguard.disableButtonGrip == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_GRIP.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotGrip == true) {
                                FALSE_GRIP.visible = false;
                                tb.visible = gunType.slotGrip;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotGrip;
                            } else {
                                FALSE_GRIP.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (handguardAttachment.type.handguard.disableButtonGrip == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_GRIP.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (railingAttachment.type.railing.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotGrip == true) {
                                FALSE_GRIP.visible = false;
                                tb.visible = gunType.slotGrip;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotGrip;
                            } else {
                                FALSE_GRIP.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                    && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) == null) {
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonGrip == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_GRIP.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotGrip == true) {
                                FALSE_GRIP.visible = false;
                                tb.visible = gunType.slotGrip;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotGrip;
                            } else {
                                FALSE_GRIP.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonGrip == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_GRIP.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (handguardAttachment.type.handguard.showButtonGrip == true){
                            FALSE_GRIP.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotGrip == true) {
                                FALSE_GRIP.visible = false;
                                tb.visible = gunType.slotGrip;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotGrip;
                            } else {
                                FALSE_GRIP.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else {
                        if (gunType.slotGrip == true) {
                            FALSE_GRIP.visible = false;
                            tb.visible = gunType.slotGrip;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = gunType.slotGrip;
                        } else {
                            FALSE_GRIP.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Laser) {
                    if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) == null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        if (handguardAttachment.type.handguard.disableButtonLaser == true) {
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_LASER.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotLaser == true) {
                                FALSE_LASER.visible = false;
                                tb.visible = gunType.slotRaling;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotRaling;
                            } else {
                                FALSE_LASER.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (handguardAttachment.type.handguard.disableButtonLaser == true) {
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_LASER.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else if (railingAttachment.type.railing.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotLaser == true) {
                                FALSE_LASER.visible = false;
                                tb.visible = gunType.slotRaling;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotRaling;
                            } else {
                                FALSE_LASER.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) == null) {
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonLaser == true) {
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_LASER.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        } else if (railingAttachment.type.railing.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotLaser == true) {
                                FALSE_LASER.visible = false;
                                tb.visible = gunType.slotRaling;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotRaling;
                            } else {
                                FALSE_LASER.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonLaser == true) {
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_LASER.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        } else if (railingAttachment.type.railing.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (handguardAttachment.type.handguard.showButtonLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotLaser == true) {
                                FALSE_LASER.visible = false;
                                tb.visible = gunType.slotRaling;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotRaling;
                            } else {
                                FALSE_LASER.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else {
                        if (gunType.slotLaser == true) {
                            FALSE_LASER.visible = false;
                            tb.visible = gunType.slotRaling;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = gunType.slotRaling;
                        } else {
                            FALSE_LASER.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }
                    }
                }

                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Flashlight) {
                    if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                    && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) == null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();

                        if (handguardAttachment.type.handguard.disableButtonFlashlight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_FLASHLIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotFlashlight == true) {
                                FALSE_FLASHLIGHT.visible = false;
                                tb.visible = gunType.slotFlashlight;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotFlashlight;
                            } else {
                                FALSE_FLASHLIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (handguardAttachment.type.handguard.disableButtonFlashlight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_FLASHLIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (handguardAttachment.type.handguard.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (railingAttachment.type.railing.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotFlashlight == true) {
                                FALSE_FLASHLIGHT.visible = false;
                                tb.visible = gunType.slotFlashlight;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotFlashlight;
                            } else {
                                FALSE_FLASHLIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                    && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) == null) {
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonFlashlight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_FLASHLIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotFlashlight == true) {
                                FALSE_FLASHLIGHT.visible = false;
                                tb.visible = gunType.slotFlashlight;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotFlashlight;
                            } else {
                                FALSE_FLASHLIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    }else if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling) != null
                            && GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard) != null) {
                        ItemAttachment handguardAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Handguard).getItem();
                        ItemAttachment railingAttachment = (ItemAttachment) GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Raling).getItem();
                        if (railingAttachment.type.railing.disableButtonFlashlight == true){
                            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tb.getAttachmentType().getName(), false));
                            FALSE_FLASHLIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }else if (railingAttachment.type.railing.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        }else if (handguardAttachment.type.handguard.showButtonFlashlight == true){
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = true;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = true;
                        } else {
                            if (gunType.slotFlashlight == true) {
                                FALSE_FLASHLIGHT.visible = false;
                                tb.visible = gunType.slotFlashlight;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = gunType.slotFlashlight;
                            } else {
                                FALSE_FLASHLIGHT.visible = true;
                                tb.visible = false;
                                TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                                sideButton.hidden = false;
                            }
                        }
                    } else {
                        if (gunType.slotFlashlight == true) {
                            FALSE_FLASHLIGHT.visible = false;
                            tb.visible = gunType.slotFlashlight;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = gunType.slotFlashlight;
                        } else {
                            FALSE_FLASHLIGHT.visible = true;
                            tb.visible = false;
                            TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                            sideButton.hidden = false;
                        }
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Handguard) {
                    if (gunType.slotHandguard == true) {
                        FALSE_HANDGUARD.visible = false;
                        tb.visible = gunType.slotHandguard;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = gunType.slotHandguard;
                    } else {
                        FALSE_HANDGUARD.visible = true;
                        tb.visible = false;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = false;
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Stock) {
                    if (gunType.slotStock == true) {
                        FALSE_STOCK.visible = false;
                        tb.visible = gunType.slotStock;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = gunType.slotStock;
                    } else {
                        FALSE_STOCK.visible = true;
                        tb.visible = false;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = false;
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Raling) {
                    if (gunType.slotRaling == true) {
                        FALSE_RALING.visible = false;
                        tb.visible = gunType.slotRaling;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = gunType.slotRaling;
                    } else {
                        FALSE_RALING.visible = true;
                        tb.visible = false;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = false;
                    }
                }

                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Charm) {
                    if (gunType.slotCharm == true) {
                        FALSE_CHARM.visible = false;
                        tb.visible = gunType.slotCharm;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = gunType.slotCharm;
                    } else {
                        FALSE_CHARM.visible = true;
                        tb.visible = false;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = false;
                    }
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Skin) {
                    if (gunType.slotSkin == true) {
                        FALSE_SKIN.visible = false;
                        tb.visible = gunType.slotSkin;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = gunType.slotSkin;
                    } else {
                        FALSE_SKIN.visible = true;
                        tb.visible = false;
                        TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                        sideButton.hidden = false;
                    }
                }
            }
        }
        GlStateManager.enableTexture2D();
        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.SideButton)) {
                    button.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }
        }
        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.Slot)) {
                    button.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }
        }

        drawButtonSubPage(mouseX, mouseY, partialTicks);
    }

    public void renderNameAttachmentSlot(double sFactor, ScaledResolution scaledResolution){
        GL11.glTranslatef(0, 0, 0);
        GlStateManager.color(1,1,1,1);

        //attachmentPoint
        float[] barellPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] charmPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] flashlightPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] gripPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] sigingPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] skinPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] stockPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] handguardPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] laserdPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };
        float[] ralingdPoint = new float[]{ 9999/scaledResolution.getScaleFactor(), 9999/scaledResolution.getScaleFactor() };

        if (currentPageModifi == 0){
            barellPoint = new float[]{(float) (650f * sFactor/scaledResolution.getScaleFactor()), (float) (266f * sFactor/scaledResolution.getScaleFactor())};
            gripPoint = new float[]{(float) (731f * sFactor/scaledResolution.getScaleFactor()), (float) (691f * sFactor/scaledResolution.getScaleFactor())};
            sigingPoint = new float[]{(float) (1322f * sFactor/scaledResolution.getScaleFactor()), (float) (494 * sFactor/scaledResolution.getScaleFactor())};
            laserdPoint = new float[]{(float) (1090 * sFactor/scaledResolution.getScaleFactor()), (float) (220 * sFactor/scaledResolution.getScaleFactor())};
        }
        else if (currentPageModifi == 1){
            flashlightPoint = new float[]{(float) (560 * sFactor/scaledResolution.getScaleFactor()), (float) (480 * sFactor/scaledResolution.getScaleFactor())};
            stockPoint = new float[]{(float) (1375 * sFactor/scaledResolution.getScaleFactor()), (float) (680 * sFactor/scaledResolution.getScaleFactor())};
            handguardPoint = new float[]{(float) (960 * sFactor/scaledResolution.getScaleFactor()), (float) (220 * sFactor/scaledResolution.getScaleFactor())};
            ralingdPoint = new float[]{(float) (1260 * sFactor/scaledResolution.getScaleFactor()), (float) (340 * sFactor/scaledResolution.getScaleFactor())};
        }
        else if (currentPageModifi == 2){
            charmPoint = new float[]{(float) (1400 * sFactor/scaledResolution.getScaleFactor()), (float) (600 * sFactor/scaledResolution.getScaleFactor())};
            skinPoint = new float[]{(float) (700 * sFactor/scaledResolution.getScaleFactor()), (float) (350 * sFactor/scaledResolution.getScaleFactor())};
        }

        //renderNameAttachmentSlot
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        double topBGsize=40 * sFactor/scaledResolution.getScaleFactor();
        if (currentPageModifi == 0){
            GlStateManager.pushMatrix();
            GlStateManager.translate(0,0,100);
            RenderHelperMW.renderCenteredTextScaledWithShadow("Barel", (int) (barellPoint[0]-(-196f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (barellPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Laser", (int) (laserdPoint[0]-(-204f * sFactor/scaledResolution.getScaleFactor())), (
                    int) (laserdPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Grip", (int) (gripPoint[0]-(-196f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (gripPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Sight", (int) (sigingPoint[0]-(-196f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (sigingPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            GlStateManager.popMatrix();


            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(barellPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), barellPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.5, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(laserdPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), laserdPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.5, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(gripPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), gripPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.5, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(sigingPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), sigingPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.5, topBGsize);
        }
        else if (currentPageModifi == 1){
            GlStateManager.pushMatrix();
            GlStateManager.translate(0,0,100);
            RenderHelperMW.renderCenteredTextScaledWithShadow("Flashlight", (int) (flashlightPoint[0]-(-228f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (flashlightPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Stock", (int) (stockPoint[0]-(-196f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (stockPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Handguard", (int) (handguardPoint[0]-(-236f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (handguardPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Raling", (int) (ralingdPoint[0]-(-196f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (ralingdPoint[1]-(34f * sFactor/scaledResolution.getScaleFactor())),0xffffff,3 * sFactor/scaledResolution.getScaleFactor());
            GlStateManager.popMatrix();

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(flashlightPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), flashlightPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*4.85, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(stockPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), stockPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.15, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(handguardPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), handguardPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*5.45, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(ralingdPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), ralingdPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.15, topBGsize);
        }
        else if (currentPageModifi == 2) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 100);
            RenderHelperMW.renderCenteredTextScaledWithShadow("Charm", (int) (charmPoint[0] - (-200f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (charmPoint[1] - (34f * sFactor/scaledResolution.getScaleFactor())), 0xffffff, 3 * sFactor / scaledResolution.getScaleFactor());
            RenderHelperMW.renderCenteredTextScaledWithShadow("Skin", (int) (skinPoint[0] - (-186f * sFactor/scaledResolution.getScaleFactor())),
                    (int) (skinPoint[1] - (34f * sFactor/scaledResolution.getScaleFactor())), 0xffffff, 3 * sFactor / scaledResolution.getScaleFactor());
            GlStateManager.popMatrix();

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(charmPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), charmPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*3.3, topBGsize);

            mc.renderEngine.bindTexture(name_attachment_fon);
            RenderHelperMW.drawTexturedRect(skinPoint[0]-(-148f * sFactor/scaledResolution.getScaleFactor()), skinPoint[1]-(44f * sFactor/scaledResolution.getScaleFactor()), topBGsize*2.5, topBGsize);
        }

        for (GuiButton button : this.buttonList){
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                //Иконки слотов атачей
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Sight) {
                    tb.visible = true;
                    tb.x = sigingPoint[0] + (tb.width) / 2d;
                    tb.y = sigingPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = sigingPoint[0] + (tb.width) / 2d;
                    sideButton.y = sigingPoint[1] + (tb.height / 1.8);

                    FALSE_SIGHT.x = sigingPoint[0] + (tb.width) / 2d;
                    FALSE_SIGHT.y = sigingPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Barrel) {
                    tb.visible = true;
                    tb.x = barellPoint[0] + (tb.width) / 2d;
                    tb.y = barellPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = barellPoint[0] + (tb.width) / 2d;
                    sideButton.y = barellPoint[1] + (tb.height / 1.8);

                    FALSE_BARREL.x = barellPoint[0] + (tb.width) / 2d;
                    FALSE_BARREL.y = barellPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Laser) {
                    tb.visible = true;
                    tb.x = laserdPoint[0] + (tb.width) / 2d;
                    tb.y = laserdPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = laserdPoint[0] + (tb.width) / 2d;
                    sideButton.y = laserdPoint[1] + (tb.height / 1.8);

                    FALSE_LASER.x = laserdPoint[0] + (tb.width) / 2d;
                    FALSE_LASER.y = laserdPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Grip) {
                    tb.visible = true;
                    tb.x = gripPoint[0] + (tb.width) / 2d;
                    tb.y = gripPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = gripPoint[0] + (tb.width) / 2d;
                    sideButton.y = gripPoint[1] + (tb.height / 1.8);

                    FALSE_GRIP.x = gripPoint[0] + (tb.width) / 2d;
                    FALSE_GRIP.y = gripPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Flashlight) {
                    tb.visible = true;
                    tb.x = flashlightPoint[0] + (tb.width) / 2d;
                    tb.y = flashlightPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = flashlightPoint[0] + (tb.width) / 2d;
                    sideButton.y = flashlightPoint[1] + (tb.height / 1.8);

                    FALSE_FLASHLIGHT.x = flashlightPoint[0] + (tb.width) / 2d;
                    FALSE_FLASHLIGHT.y = flashlightPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Stock) {
                    tb.visible = true;
                    tb.x = stockPoint[0] + (tb.width) / 2d;
                    tb.y = stockPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = stockPoint[0] + (tb.width) / 2d;
                    sideButton.y = stockPoint[1] + (tb.height / 1.8);

                    FALSE_STOCK.x = stockPoint[0] + (tb.width) / 2d;
                    FALSE_STOCK.y = stockPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Handguard) {
                    tb.visible = true;
                    tb.x = handguardPoint[0] + (tb.width) / 2d;
                    tb.y = handguardPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = handguardPoint[0] + (tb.width) / 2d;
                    sideButton.y = handguardPoint[1] + (tb.height / 1.8);

                    FALSE_HANDGUARD.x = handguardPoint[0] + (tb.width) / 2d;
                    FALSE_HANDGUARD.y = handguardPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Raling) {
                    tb.visible = true;
                    tb.x = ralingdPoint[0] + (tb.width) / 2d;
                    tb.y = ralingdPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = ralingdPoint[0] + (tb.width) / 2d;
                    sideButton.y = ralingdPoint[1] + (tb.height / 1.8);

                    FALSE_RALING.x = ralingdPoint[0] + (tb.width) / 2d;
                    FALSE_RALING.y = ralingdPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Charm) {
                    tb.visible = true;
                    tb.x = charmPoint[0] + (tb.width) / 2d;
                    tb.y = charmPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = charmPoint[0] + (tb.width) / 2d;
                    sideButton.y = charmPoint[1] + (tb.height / 1.8);

                    FALSE_CHARM.x = charmPoint[0] + (tb.width) / 2d;
                    FALSE_CHARM.y = charmPoint[1] - (tb.height / 2);
                }
                if (tb.getType().equals(TextureButton.TypeEnum.Slot) && tb.getAttachmentType() == AttachmentPresetEnum.Skin) {
                    tb.visible = true;
                    tb.x = skinPoint[0] + (tb.width) / 2d;
                    tb.y = skinPoint[1] - (tb.height / 2);

                    TextureButton sideButton = getButton(tb.id + sideButtonIdOffset);
                    sideButton.visible = true;
                    sideButton.x = skinPoint[0] + (tb.width) / 2d;
                    sideButton.y = skinPoint[1] + (tb.height / 1.8);

                    FALSE_SKIN.x = skinPoint[0] + (tb.width) / 2d;
                    FALSE_SKIN.y = skinPoint[1] - (tb.height / 2);
                }
            }
        }
    }

    public void renderGui(double sFactor,BaseType type, ScaledResolution scaledResolution){
        Minecraft mc = Minecraft.getMinecraft();
        GunType gunType = (GunType) type;

        String displayName = gunType.displayName.toString();
        String weaponType = gunType.weaponType.toString();

        GlStateManager.pushMatrix();
        GlStateManager.scale(5 * sFactor/scaledResolution.getScaleFactor(),5 * sFactor/scaledResolution.getScaleFactor(),5 * sFactor/scaledResolution.getScaleFactor());
        mc.fontRenderer.drawString(displayName, 4, 4,0xffffff,true);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(4 * sFactor/scaledResolution.getScaleFactor(),4 * sFactor/scaledResolution.getScaleFactor(),4 * sFactor/scaledResolution.getScaleFactor());
        mc.fontRenderer.drawString(weaponType, 5, 18,0xffffff,true);
        GlStateManager.popMatrix();
    }

    public void drawButtonSubPage(int mouseX, int mouseY,float partialTicks) {
        int indexX = 0;
        int indexY = 0;
        int maxX = 0;
        int buttonsPerPage = 3;

        TextureButton parentButton = null;
        if (selectedSideButton != null) {
            parentButton = this.getButton(selectedSideButton.id - this.sideButtonIdOffset);
            boolean isSkin = parentButton.getAttachmentType() == AttachmentPresetEnum.Skin;
            indexY = isSkin ? 2 : 2;

            GlStateManager.color(1,1,1);
            int buttonCount = 0;
            for (GuiButton button2 : this.buttonList) {
                if (button2 instanceof TextureButton) {
                    TextureButton tb2 = (TextureButton) button2;
                    if (tb2.getType().equals(TextureButton.TypeEnum.NameAttachSlot)) {
                        if (tb2.getItemStack().isEmpty()) {
                            tb2.x = parentButton.x + parentButton.width * 1.98D;
                            tb2.y = parentButton.y + parentButton.height * 2.58D;
                        }
                    }
                }
            }
            for (GuiButton button2 : this.buttonList) {
                if (button2 instanceof TextureButton) {
                    TextureButton tb2 = (TextureButton) button2;
                    if (tb2.getType().equals(TextureButton.TypeEnum.NameAttachSlot1)) {
                        if (tb2.getItemStack().isEmpty()) {
                            tb2.x = parentButton.x + parentButton.width * 0.98D;
                            tb2.y = parentButton.y + parentButton.height * 2.58D;

                            mc.renderEngine.bindTexture(slot_list);
                            RenderHelperMW.drawTexturedRect(parentButton.x, parentButton.y + parentButton.height * 1.41, parentButton.width * 3.12D, parentButton.height * 1.07D);

                            mc.renderEngine.bindTexture(arrow_fon);
                            RenderHelperMW.drawTexturedRect(parentButton.x, parentButton.y + parentButton.height * 2.52, parentButton.width * 3.12D, parentButton.height * 0.3D);
                            RenderHelperMW.renderCenteredTextScaled(String.valueOf("pg " + currentPage),(int) ((int) parentButton.x + parentButton.width * 1.575), (int) ((int) parentButton.y + parentButton.height * 2.6f), 0xffffff, parentButton.height * 0.0275);
                        }
                    }
                }
            }
            for (GuiButton button2 : this.buttonList) {
                if (button2 instanceof TextureButton) {
                    TextureButton tb2 = (TextureButton) button2;
                    if (tb2.getType().equals(TextureButton.TypeEnum.SubSlot)) {
                        if (buttonCount >= currentPage * buttonsPerPage && buttonCount < (currentPage + 1) * buttonsPerPage) {
                            tb2.x = parentButton.x + (parentButton.width * 1.05D * indexX);
                            tb2.y = parentButton.y + (parentButton.height * 0.74D * indexY);

                            indexX++;
                            if (maxX < indexX) {
                                maxX = indexX;
                            }
                            if (indexX % buttonsPerPage == 0) {
                                indexX = 0;
                                indexY++;
                            }
                        } else {
                            tb2.x = -1000;
                        }
                        buttonCount++;
                    }
                }
            }

            totalPages = (int) Math.ceil((double) buttonCount / buttonsPerPage);
        }

        if (indexX % buttonsPerPage == 0) {
            indexY--;
        }

        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.SubSlot)) {
                    tb.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }
        }
        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.NameAttachSlot)) {
                    tb.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }
        }
        for (GuiButton button : this.buttonList) {
            if (button instanceof TextureButton) {
                TextureButton tb = (TextureButton) button;
                if (tb.getType().equals(TextureButton.TypeEnum.NameAttachSlot1)) {
                    tb.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }
        }

        GlStateManager.enableDepth();
    }

    public TextureButton getButton(int id) {
        for(GuiButton button:this.buttonList) {
            if(button.id==id)
                return (TextureButton) button;
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        EntityLivingBase entitylivingbaseIn = Minecraft.getMinecraft().player;
        double sFactor = mc.displayWidth / 1920d;
        if (clickOnce)
            return;
        clickOnce = true;
        if (button.id == 1195) {
            currentPage++;
            if (currentPage >= totalPages) {
                currentPage = 0;
            }
        }else if (button.id == 1196) {
            currentPage--;
            if (currentPage == -1) {
                currentPage = 0;
            }
        }else if (button.id == 1201) {
            currentPageModifi = 0;
            selectedSideButton = null;
            this.initGui();
        } else if (button.id == 1202) {
            currentPageModifi = 1;
            selectedSideButton = null;
            this.initGui();
        } else if (button.id == 1203) {
            currentPageModifi = 2;
            selectedSideButton = null;
            this.initGui();
        }
        TextureButton tButton = (TextureButton) button;
        if (tButton.getType().equals(TextureButton.TypeEnum.SideButton)) {
            if (tButton.state == 0) {
                selectedSideButton = null;
                tButton.state = -1;
                this.initGui();
            } else {
                if (selectedSideButton != null) {
                    this.initGui();
                }
                selectedSideButton = this.getButton(tButton.id);
                selectedSideButton.state = 0;
                this.joinSubPageButtons(mc.player, scaledresolution,sFactor);
            }
        }else if (tButton.getType().equals(TextureButton.TypeEnum.SubSlot)) {
            if (tButton.id == -1) {
                ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tButton.getAttachmentType().getName(), false));
                this.actionPerformed(this.selectedSideButton);
            } else {
                int inventoryID = tButton.id - this.subSlotIdOffset;
                if (GunType.getAttachment(this.currentModify, tButton.getAttachmentType()) !=
                        mc.player.inventory.getStackInSlot(inventoryID)) {
                    ModularWarfare.NETWORK.sendToServer(new PacketGunAddAttachment(inventoryID));
                    this.actionPerformed(this.selectedSideButton);
                }
            }
        }else if (button == QUITBUTTON) {
            ModularWarfare.PROXY.playSound(new MWSound(mc.player.getPosition(), "attachment.open", 1f, 1f));
            Minecraft.getMinecraft().displayGuiScreen(null);
            ModConfig.INSTANCE.hud.typeweapon = true;
            ModConfig.INSTANCE.hud.ammo_count = true;
        }if (tButton.id >= 0 && tButton.id <= 8) {
            ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tButton.getAttachmentType().getName(), false));
        }else if (tButton.id == 9){
            if (GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Sight) != null ||
                    GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Laser) != null ||
                    GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Grip) != null ||
                    GunType.getAttachment(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Flashlight) != null){
            }else {
                ModularWarfare.NETWORK.sendToServer(new PacketGunUnloadAttachment(tButton.getAttachmentType().getName(), false));
            }
        }
    }

    @Override
    public void onGuiClosed() {
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int x, int y, int mouseEvent) throws IOException {
        super.mouseClicked(x, y, mouseEvent);
    }

    @Override
    protected void mouseReleased(int x, int y, int mouseEvent) {
        super.mouseReleased(x, y, mouseEvent);
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey) {
        if (eventKey == 1||eventKey==50) {
            try {
                actionPerformed(QUITBUTTON);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
