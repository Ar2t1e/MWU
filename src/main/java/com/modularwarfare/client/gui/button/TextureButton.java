package com.modularwarfare.client.gui.button;

import com.modularwarfare.client.gui.GuiAttachmentModified;
import com.modularwarfare.client.gui.api.GuiUtils;
import com.modularwarfare.common.guns.AttachmentPresetEnum;
import com.modularwarfare.utility.RenderHelperMW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TextureButton extends GuiButton {
    private ResourceLocation iconTexture = null;
    public boolean isCraftingItem = false;
    public int isOver = 2;
    public int colorText = 0xFFFFFFFF;
    private String buttonText;
    private FontRenderer fontRenderer;
    private int xMovement;
    private AttachmentPresetEnum attachment;
    private ItemStack itemStack=ItemStack.EMPTY;
    private TypeEnum type;
    public TypeEnum getType() {
        return type;
    }
    public boolean hidden=false;
    public int state=-1;
    private double z = 0;
    public ItemStack getItemStack() {
        return itemStack;
    }
    public TextureButton setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }
    public double x;
    public double y;
    public AttachmentPresetEnum getAttachmentType() {
        return attachment;
    }
    public TextureButton(int buttonId, double x, double y, String givenText) {
        super(buttonId, (int) x, (int) y, givenText);
        this.buttonText = givenText;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    public TextureButton setZAxis(double zAxis) {
        this.z = zAxis;
        return this;
    }

    public TextureButton(int buttonId, double x, double y, int givenWidth, int givenHeight, String givenText) {
        this(buttonId, x, y, givenText);
        this.width = givenWidth;
        this.height = givenHeight;
    }

    public TextureButton(int id, double x, double y, int width, int height, ResourceLocation iconTexture, String buttonText) {
        this(id, x, y, width, height, "");
        this.x = x;
        this.y = y;
        this.iconTexture = iconTexture;
        this.buttonText = buttonText;
    }

    public TextureButton(int id, double x, double y, int width, int height, ResourceLocation iconTexture, String buttonText,boolean isCraftingItem) {
        this(id, x, y, width, height, "");
        this.x = x;
        this.y = y;
        this.iconTexture = iconTexture;
        this.buttonText = buttonText;
        this.isCraftingItem = isCraftingItem;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public TextureButton setAttachment(AttachmentPresetEnum attachment) {
        this.attachment=attachment;
        return this;
    }

    public TextureButton setType(TypeEnum type) {
        this.type=type;
        return this;
    }

    public enum TypeEnum {
        Button,
        NameAttachSlot,
        NameAttachSlot1,
        Slot,
        AirSlot,
        SubSlot,
        SideButton,
        SideButtonNew,
        SideButtonVert;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        double sFactor = mc.displayWidth / 1920d;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if(hidden) {
            if(hovered||this.state==0) {
                visible=true;
            }else {
                visible=false;
            }
        }
        if (visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            isOver = getHoverState(hovered);
            mouseDragged(mc, mouseX, mouseY);
            String displayText = displayString;
            if (isOver == 2) {
                GlStateManager.pushMatrix();
                GlStateManager.popMatrix();
            }
            if (iconTexture != null) {
                if (isOver == 2) {
                    GuiUtils.renderColor(0x999999);
                } else {
                    GuiUtils.renderColor(0xFFFFFF);
                }
                Minecraft.getMinecraft().renderEngine.bindTexture(iconTexture);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                RenderHelperMW.drawTexturedRect(x , y , width+0.05d, height);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                if(isOver == 2) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F,0.4f);
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    RenderHelperMW.drawTexturedRect(x , y , width+0.05d, height);//,0XFFFFFF
                    GlStateManager.enableTexture2D();
                }
            }
            if(!this.itemStack.isEmpty()) {
                if (!isCraftingItem){
                    GlStateManager.pushMatrix();
                    double scale=5.2d * sFactor/scaledresolution.getScaleFactor()*0.9D;
                    GlStateManager.translate(0, 0, -135);
                    GlStateManager.scale(scale, scale,scale);

                    RenderHelperMW.renderItemStack(itemStack, (int)((x+3)/scale), (int)((y+2.5d)/scale), 1, false);
                    GlStateManager.popMatrix();

                    GlStateManager.disableDepth();
                    GlStateManager.pushMatrix();
                    scale=6.0d/scaledresolution.getScaleFactor()*0.4D;
                    GlStateManager.translate(0, 0, 1);
                    GlStateManager.scale(scale, scale,scale);
                    GlStateManager.popMatrix();
                    GlStateManager.enableDepth();
                }else {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0, 0, -135);

                    RenderHelperMW.renderItemStack(itemStack, (int)((x + 1)), (int)((y + 1)), 1, true);
                    GlStateManager.popMatrix();

                    GlStateManager.disableDepth();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0, 0, 1);
                    GlStateManager.popMatrix();
                    GlStateManager.enableDepth();
                }
            }else if(this.attachment!=null) {
                GlStateManager.pushMatrix();
                double scale=6.0d/scaledresolution.getScaleFactor()*0.5D;
                GlStateManager.scale(scale, scale,scale);
                GlStateManager.popMatrix();
            }
            if(this.type==TypeEnum.SideButton) {
                Minecraft.getMinecraft().renderEngine.bindTexture(this.state==0? GuiAttachmentModified.arrow_down: GuiAttachmentModified.arrow_up);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                RenderHelperMW.drawTexturedRect(x + 3, y - 2.4, width -6, height + 5);
            }
            if(this.type==TypeEnum.SideButtonVert) {
                Minecraft.getMinecraft().renderEngine.bindTexture(this.state==0? GuiAttachmentModified.arrow_right: GuiAttachmentModified.arrow_left);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                RenderHelperMW.drawTexturedRect(x + 2, y - 3, width -6, height + 5);
            }
            GuiUtils.renderCenteredTextWithShadow(buttonText, ((int)x + width / 2) + xMovement, (int)y + (height - 8) / 2, isOver == 2 ? Color.white.getRGB() : colorText, 0x000000);
        }
        GlStateManager.popMatrix();
    }
}
