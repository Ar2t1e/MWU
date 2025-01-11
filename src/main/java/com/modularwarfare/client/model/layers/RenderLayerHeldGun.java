package com.modularwarfare.client.model.layers;

import com.modularwarfare.ModConfig;
import com.modularwarfare.ModularWarfare;
import com.modularwarfare.api.RenderHeldItemLayerEvent;
import com.modularwarfare.client.ClientProxy;
import com.modularwarfare.client.ClientRenderHooks;
import com.modularwarfare.client.fpp.basic.animations.StateEntry;
import com.modularwarfare.client.fpp.basic.models.objects.CustomItemRenderType;
import com.modularwarfare.client.fpp.basic.renderers.RenderParameters;
import com.modularwarfare.client.fpp.enhanced.AnimationType;
import com.modularwarfare.client.fpp.enhanced.animation.EnhancedStateMachine;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.fpp.enhanced.models.EnhancedModel;
import com.modularwarfare.client.model.ModelAttachment;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.common.handler.data.VarBoolean;
import com.modularwarfare.common.textures.TextureType;
import com.modularwarfare.common.type.BaseItem;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.loader.api.model.ObjModelRenderer;
import com.modularwarfare.utility.ReloadHelper;
import com.modularwarfare.utility.maths.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;


public class RenderLayerHeldGun extends LayerHeldItem {

    public RenderLayerHeldGun(RenderLivingBase<?> livingEntityRendererIn) {
        super(livingEntityRendererIn);
    }
    public static final int BULLET_MAX_RENDER=256;
    public static final HashSet<String> DEFAULT_EXCEPT =new HashSet<String>();

    static {
        for(String str : ModConfig.INSTANCE.guns.anim_guns_show_default_objects) {
            DEFAULT_EXCEPT.add(str);
        }
        for(int i=0;i<BULLET_MAX_RENDER;i++) {
            DEFAULT_EXCEPT.add("bulletModel_"+i);
        }
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean glowMode=ObjModelRenderer.glowTxtureMode;
        ObjModelRenderer.glowTxtureMode=true;
        ItemStack itemstack = entitylivingbaseIn.getHeldItemMainhand();
        if (itemstack != ItemStack.EMPTY && !itemstack.isEmpty()) {
            if (!((ModelBiped) this.livingEntityRenderer.getMainModel()).bipedRightArm.isHidden) {

                RenderHeldItemLayerEvent event = new RenderHeldItemLayerEvent(itemstack, this, entitylivingbaseIn, partialTicks);
                MinecraftForge.EVENT_BUS.post(event);

                if (!(itemstack.getItem() instanceof ItemGun)) {
                    return;
                }
                BaseType type = ((BaseItem) itemstack.getItem()).baseType;
                if (!type.hasModel()) {
                    return;
                }

                GlStateManager.pushMatrix();
                if (entitylivingbaseIn.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                if (((GunType) type).animationType == WeaponAnimationType.BASIC) {
                    this.translateToHand(EnumHandSide.RIGHT);
                    GlStateManager.translate(-0.06, 0.38, -0.02);
                    if (ClientRenderHooks.customRenderers[type.id] != null) {
                        ClientRenderHooks.customRenderers[type.id].renderItem(CustomItemRenderType.EQUIPPED, null, itemstack,
                                entitylivingbaseIn.world, entitylivingbaseIn, partialTicks);
                    }
                } else if (((GunType) type).animationType == WeaponAnimationType.ENHANCED) {

                    GunType gunType = ((ItemGun) itemstack.getItem()).type;
                    EnhancedModel model = type.enhancedModel;

                    GunEnhancedRenderConfig config = (GunEnhancedRenderConfig) gunType.enhancedModel.config;

                    if (!ItemGun.hasNextShot(itemstack) && ((GunEnhancedRenderConfig)model.config).animations.containsKey(AnimationType.DEFAULT_EMPTY)){
                        model.updateAnimation((float) config.animations.get(AnimationType.DEFAULT_EMPTY).getStartTime(config.FPS));
                    }else {
                        model.updateAnimation((float) config.animations.get(AnimationType.DEFAULT).getStartTime(config.FPS));
                    }

                    HashSet<String> exceptParts=new HashSet<String>();
                    {
                        exceptParts.addAll(config.defaultHidePart);
                        exceptParts.addAll(DEFAULT_EXCEPT);

                        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                            ItemStack itemStack = GunType.getAttachment(itemstack, attachment);
                            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                                if(config.attachmentGroup.containsKey(attachment.typeName)) {
                                    if (config.attachmentGroup.get(attachment.typeName).hidePart != null) {
                                        exceptParts.addAll(config.attachmentGroup.get(attachment.typeName).hidePart);
                                    }
                                }
                                if (config.attachment.containsKey(attachmentType.internalName)) {
                                    if (config.attachment.get(attachmentType.internalName).hidePart != null) {
                                        exceptParts.addAll(config.attachment.get(attachmentType.internalName).hidePart);
                                    }
                                }
                            }
                        }

                        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                            ItemStack itemStack = GunType.getAttachment(itemstack, attachment);
                            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                                if(config.attachmentGroup.containsKey(attachment.typeName)) {
                                    if (config.attachmentGroup.get(attachment.typeName).showPart != null) {
                                        exceptParts.removeAll(config.attachmentGroup.get(attachment.typeName).showPart);
                                    }
                                }
                                if (config.attachment.containsKey(attachmentType.internalName)) {
                                    if (config.attachment.get(attachmentType.internalName).showPart != null) {
                                        exceptParts.removeAll(config.attachment.get(attachmentType.internalName).showPart);
                                    }
                                }
                            }

                        }

                        exceptParts.addAll(DEFAULT_EXCEPT);
                    }

                    HashSet<String> exceptPartsRendering=exceptParts;

                    this.translateToHand(EnumHandSide.RIGHT);
                    GlStateManager.translate(-0.06, 0.38, -0.02);

                    GL11.glRotatef(-90F, 0F, 1F, 0F);
                    GL11.glRotatef(90F, 0F, 0F, 1F);
                    GL11.glTranslatef(0.25F, 0.2F, -0.05F);
                    GL11.glScalef(1 / 16F, 1 / 16F, 1 / 16F);

                    GL11.glTranslatef(config.thirdPerson.thirdPersonOffset.x, config.thirdPerson.thirdPersonOffset.y, config.thirdPerson.thirdPersonOffset.z);
                    GL11.glScalef(config.thirdPerson.thirdPersonScale, config.thirdPerson.thirdPersonScale, config.thirdPerson.thirdPersonScale);

                    int skinId = 0;
                    if (itemstack.hasTagCompound()) {
                        if (itemstack.getTagCompound().hasKey("skinId")) {
                            skinId = itemstack.getTagCompound().getInteger("skinId");
                        }
                    }
                    String gunPath = skinId > 0 ? gunType.modelSkins[skinId].getSkin() : gunType.modelSkins[0].getSkin();
                    ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);

                    HashSet<String> partsToIgnore = new HashSet<>(DEFAULT_EXCEPT);
                    partsToIgnore.addAll(config.defaultHidePart);

                    for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                        ItemStack itemStack = GunType.getAttachment(itemstack, attachment);
                        if (itemStack != null && itemStack.getItem() != Items.AIR) {
                            AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                            ModelAttachment attachmentModel = (ModelAttachment) attachmentType.model;

                            if (attachmentModel != null) {
                                String binding = "gunModel";
                                if (config.attachment.containsKey(attachmentType.internalName)) {
                                    binding = config.attachment.get(attachmentType.internalName).binding;
                                }

                                model.applyGlobalTransformToOther(binding, () -> {
                                    if (attachmentType.sameTextureAsGun) {
                                        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                                    } else {
                                        int attachmentsSkinId = 0;
                                        if (itemStack.hasTagCompound()) {
                                            if (itemStack.getTagCompound().hasKey("skinId")) {
                                                attachmentsSkinId = itemStack.getTagCompound().getInteger("skinId");
                                            }
                                        }
                                        String attachmentsPath = attachmentsSkinId > 0 ? attachmentType.modelSkins[attachmentsSkinId].getSkin()
                                                : attachmentType.modelSkins[0].getSkin();
                                        ClientProxy.gunEnhancedRenderer.bindTexture("attachments", attachmentsPath);
                                    }
                                    ClientProxy.gunEnhancedRenderer.renderAttachment(config, attachment.typeName, attachmentType.internalName, () -> {
                                        attachmentModel.renderAttachment(1);
                                    });

                                    if (attachment == AttachmentPresetEnum.Laser){
                                        if (itemstack.getTagCompound().hasKey("laserEnabled")){
                                            boolean enabled = itemstack.getTagCompound().getBoolean("laserEnabled");
                                            if (enabled){
                                                attachmentModel.renderLaser(1,false);
                                            }
                                        }
                                    }
                                });

                            }

                        }

                    }

                    /**
                     * ammo and bullet
                     * */
                    if (itemstack.hasTagCompound()) {
                        ItemStack stackAmmo = new ItemStack(itemstack.getTagCompound().getCompoundTag("ammo"));
                        ItemStack renderAmmo = stackAmmo;

                        ItemStack bulletStack = ItemStack.EMPTY;
                        int currentAmmoCount = 0;

                        VarBoolean defaultBulletFlag = new VarBoolean();
                        defaultBulletFlag.b = true;
                        boolean defaultAmmoFlag = true;

                        if (stackAmmo.getItem() != Items.AIR && stackAmmo.getTagCompound() != null) {
                            if (gunType.acceptedBullets != null) {
                                currentAmmoCount = itemstack.getTagCompound().getInteger("ammocount");
                                bulletStack = new ItemStack(stackAmmo.getTagCompound().getCompoundTag("bullet"));
                            } else {
                                Integer currentMagcount = null;
                                if (stackAmmo != null && !stackAmmo.isEmpty() && stackAmmo.hasTagCompound()) {
                                    if (stackAmmo.getTagCompound().hasKey("magcount")) {
                                        currentMagcount = stackAmmo.getTagCompound().getInteger("magcount");
                                    }
                                    currentAmmoCount = ReloadHelper.getBulletOnMag(stackAmmo, currentMagcount);
                                    bulletStack = new ItemStack(stackAmmo.getTagCompound().getCompoundTag("bullet"));
                                }
                            }
                        }
                        int currentAmmoCountRendering = currentAmmoCount;

                        if (bulletStack != null) {
                            if (bulletStack.getItem() instanceof ItemBullet) {
                                BulletType bulletType = ((ItemBullet) bulletStack.getItem()).type;
                                if (bulletType.isDynamicBullet && bulletType.model != null) {
                                    int skinIdBullet = 0;
                                    if (bulletStack.hasTagCompound()) {
                                        if (bulletStack.getTagCompound().hasKey("skinId")) {
                                            skinIdBullet = bulletStack.getTagCompound().getInteger("skinId");
                                        }
                                    }
                                    if (bulletType.sameTextureAsGun) {
                                        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                                    } else {
                                        String pathAmmo = skinIdBullet > 0 ? bulletType.modelSkins[skinIdBullet].getSkin()
                                                : bulletType.modelSkins[0].getSkin();
                                        ClientProxy.gunEnhancedRenderer.bindTexture("bullets", pathAmmo);
                                    }
                                    for (int bullet = 0; bullet < currentAmmoCount && bullet < ClientProxy.gunEnhancedRenderer.BULLET_MAX_RENDER; bullet++) {
                                        int renderBullet = bullet;
                                        model.applyGlobalTransformToOther("bulletModel_" + bullet, () -> {
                                            ClientProxy.gunEnhancedRenderer.renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                                bulletType.model.renderPart("bulletModel", 1);
                                            });
                                        });
                                    }
                                    model.applyGlobalTransformToOther("bulletModel", () -> {
                                        ClientProxy.gunEnhancedRenderer.renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                            bulletType.model.renderPart("bulletModel", 1);
                                        });
                                    });
                                    defaultBulletFlag.b = false;
                                }
                            }
                        }

                        String binding = "ammoModel";
                        if (!(stackAmmo.isEmpty() || model.getPart(binding) == null)) {
                            if (stackAmmo.getItem() instanceof ItemAmmo) {
                                ItemAmmo itemAmmo = (ItemAmmo) stackAmmo.getItem();
                                AmmoType ammoType = itemAmmo.type;
                                if (ammoType.isDynamicAmmo && ammoType.model != null) {
                                    int skinIdAmmo = 0;
                                    int baseAmmoCount = 0;

                                    if (stackAmmo.hasTagCompound()) {
                                        if (stackAmmo.getTagCompound().hasKey("skinId")) {
                                            skinIdAmmo = stackAmmo.getTagCompound().getInteger("skinId");
                                        }
                                        if (stackAmmo.getTagCompound().hasKey("magcount")) {
                                            baseAmmoCount = (stackAmmo.getTagCompound().getInteger("magcount") - 1) * ammoType.ammoCapacity;
                                        }
                                    }
                                    int baseAmmoCountRendering = baseAmmoCount;

                                    if (ammoType.sameTextureAsGun) {
                                        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                                    } else {
                                        String pathAmmo = skinIdAmmo > 0 ? ammoType.modelSkins[skinIdAmmo].getSkin() : ammoType.modelSkins[0].getSkin();
                                        ClientProxy.gunEnhancedRenderer.bindTexture("ammo", pathAmmo);
                                    }

                                    if (ItemGun.hasAmmoLoaded(itemstack)) {
                                        model.applyGlobalTransformToOther("ammoModel", () -> {
                                            GlStateManager.pushMatrix();
                                            if (renderAmmo.getTagCompound().hasKey("magcount")) {
                                                if (config.attachment.containsKey(itemAmmo.type.internalName)) {
                                                    if (config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform != null) {
                                                        if (renderAmmo.getTagCompound().getInteger("magcount") <= config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.size()) {
                                                            //be careful, don't mod the config
                                                            GunEnhancedRenderConfig.Transform ammoTransform = config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.get(renderAmmo.getTagCompound().getInteger("magcount") - 1);
                                                            GunEnhancedRenderConfig.Transform renderTransform = ammoTransform;
                                                            GlStateManager.translate(renderTransform.translate.x,
                                                                    renderTransform.translate.y, renderTransform.translate.z);
                                                            GlStateManager.scale(renderTransform.scale.x, renderTransform.scale.y,
                                                                    renderTransform.scale.z);
                                                            GlStateManager.rotate(renderTransform.rotate.y, 0, 1, 0);
                                                            GlStateManager.rotate(renderTransform.rotate.x, 1, 0, 0);
                                                            GlStateManager.rotate(renderTransform.rotate.z, 0, 0, 1);
                                                        }
                                                    }
                                                }
                                            }
                                            ClientProxy.gunEnhancedRenderer.renderAttachment(config, "ammo", ammoType.internalName, () -> {
                                                ammoType.model.renderPart("ammoModel", 1);
                                                if (defaultBulletFlag.b) {
                                                    if (renderAmmo.getTagCompound().hasKey("magcount")) {
                                                        for (int i = 1; i <= ammoType.magazineCount; i++) {
                                                            int count = ReloadHelper.getBulletOnMag(renderAmmo, i);
                                                            for (int bullet = 0; bullet < count && bullet < ClientProxy.gunEnhancedRenderer.BULLET_MAX_RENDER; bullet++) {
                                                                //System.out.println((ammoType.ammoCapacity*(i-1))+bullet);
                                                                ammoType.model.renderPart("bulletModel_" + ((ammoType.ammoCapacity * (i - 1)) + bullet), 1);
                                                            }
                                                        }
                                                    } else {
                                                        for (int bullet = 0; bullet < currentAmmoCountRendering && bullet < ClientProxy.gunEnhancedRenderer.BULLET_MAX_RENDER; bullet++) {
                                                            ammoType.model.renderPart("bulletModel_" + (baseAmmoCountRendering + bullet), 1);
                                                        }
                                                    }

                                                    defaultBulletFlag.b = false;
                                                }
                                            });
                                            GlStateManager.popMatrix();
                                        });
                                        model.applyGlobalTransformToOther("bulletModel", () -> {
                                            ClientProxy.gunEnhancedRenderer.renderAttachment(config, "bullet", ammoType.internalName, () -> {
                                                ammoType.model.renderPart("bulletModel", 1);
                                            });
                                        });
                                        defaultAmmoFlag = false;
                                    }
                                }
                            }

                        }


                        /**
                         * default bullet and ammo
                         * */
                        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);

                        if (ItemGun.hasAmmoLoaded(itemstack) && defaultBulletFlag.b) {
                            for (int bullet = 0; bullet < currentAmmoCount && bullet < ClientProxy.gunEnhancedRenderer.BULLET_MAX_RENDER; bullet++) {
                                model.renderPart("bulletModel_" + bullet);
                            }
                            model.renderPart("bulletModel");
                        }

                        if (ItemGun.hasAmmoLoaded(itemstack) && defaultAmmoFlag) {
                            model.renderPart("ammoModel");
                        }

                    }

                    WeaponFireMode fireMode = GunType.getFireMode(itemstack);
                    if (fireMode == WeaponFireMode.SEMI) {
                        model.renderPart("selector_semi");
                    } else if (fireMode == WeaponFireMode.FULL) {
                        model.renderPart("selector_full");
                    } else if (fireMode == WeaponFireMode.BURST) {
                        model.renderPart("selector_brust");
                    }else if (fireMode == WeaponFireMode.FUSE) {
                        model.renderPart("selector_fuse");
                    }

                    ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                    model.renderPartExcept(exceptPartsRendering);


                    /**
                     *  flashmodel
                     *  */
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);
                    ObjModelRenderer.glowTxtureMode=false;
                    boolean shouldRenderFlash=true;
                    if ((GunType.getAttachment(itemstack, AttachmentPresetEnum.Barrel) != null)) {
                        AttachmentType attachmentType = ((ItemAttachment) GunType.getAttachment(itemstack, AttachmentPresetEnum.Barrel).getItem()).type;
                        if (attachmentType.attachmentType == AttachmentPresetEnum.Barrel) {
                            shouldRenderFlash = !attachmentType.barrel.hideFlash;
                        }
                    }


                    if (shouldRenderFlash) {
                        if (itemstack.getTagCompound().hasKey("gunfire")) {
                            float flash = itemstack.getTagCompound().getFloat("gunfire");
                            if (flash > 0) {
                                GlStateManager.pushMatrix();
                                {
                                    GL11.glEnable(3042);
                                    GL11.glEnable(2832);
                                    GL11.glHint(3153, 4353);
                                    TextureType flashType = gunType.flashType;
                                    ClientProxy.gunEnhancedRenderer.bindTexture(flashType.resourceLocations.get(anim.flashCount % flashType.resourceLocations.size()));

                                    float lastBrightnessX = OpenGlHelper.lastBrightnessX;
                                    float lastBrightnessY = OpenGlHelper.lastBrightnessY;
                                    GlStateManager.depthMask(false);
                                    GlStateManager.disableLighting();
                                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

                                    ObjModelRenderer.glowTxtureMode = false;
                                    model.renderPart("flashModel");
                                    ObjModelRenderer.glowTxtureMode = glowMode;

                                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
                                    GlStateManager.enableLighting();
                                    GlStateManager.depthMask(true);

                                    GL11.glDisable(3042);
                                    GL11.glDisable(2832);
                                }
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        ObjModelRenderer.glowTxtureMode=glowMode;
    }
}