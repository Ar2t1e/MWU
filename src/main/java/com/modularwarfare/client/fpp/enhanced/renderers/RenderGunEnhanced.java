package com.modularwarfare.client.fpp.enhanced.renderers;

import com.modularwarfare.ModConfig;
import com.modularwarfare.ModularWarfare;
import com.modularwarfare.api.RenderHandSleeveEventEnhanced;
import com.modularwarfare.client.ClientProxy;
import com.modularwarfare.client.ClientRenderHooks;
import com.modularwarfare.client.fpp.basic.configs.AttachmentRenderConfig;
import com.modularwarfare.client.gui.GuiAttachmentModified;
import com.modularwarfare.client.model.ModelAttachment;
import com.modularwarfare.client.fpp.basic.models.objects.CustomItemRenderType;
import com.modularwarfare.client.fpp.basic.models.objects.CustomItemRenderer;
import com.modularwarfare.client.fpp.basic.renderers.RenderParameters;
import com.modularwarfare.client.fpp.enhanced.AnimationType;
import com.modularwarfare.client.fpp.enhanced.animation.AnimationController;
import com.modularwarfare.client.fpp.enhanced.animation.EnhancedStateMachine;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig.Attachment;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig.Transform;
import com.modularwarfare.client.fpp.enhanced.models.EnhancedModel;
import com.modularwarfare.client.handler.ClientTickHandler;
import com.modularwarfare.client.model.ModelCustomArmor;
import com.modularwarfare.client.scope.ScopeUtils;
import com.modularwarfare.client.shader.Programs;
import com.modularwarfare.common.armor.ItemMWArmor;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.common.handler.data.VarBoolean;
import com.modularwarfare.common.network.PacketAimingRequest;
import com.modularwarfare.common.textures.TextureType;
import com.modularwarfare.loader.api.model.ObjModelRenderer;
import com.modularwarfare.utility.OptifineHelper;
import com.modularwarfare.utility.ReloadHelper;
import com.modularwarfare.utility.RenderHelperMW;
import com.modularwarfare.utility.maths.Interpolation;

import de.javagl.jgltf.model.NodeModel;

import mchhui.hegltf.DataNode;
import mchhui.hegltf.GltfRenderModel;
import mchhui.modularmovements.tactical.client.ClientLitener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.optifine.shaders.Shaders;

import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static com.modularwarfare.client.fpp.basic.renderers.RenderParameters.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class RenderGunEnhanced extends CustomItemRenderer {
    public static boolean debug=false;
    public static boolean debug1=false;
    public HashMap<String, AnimationController> otherControllers=new HashMap<String, AnimationController>();

    public static final float PI = 3.14159265f;
    private Timer timer;
    public AnimationController controller;
    public FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    public static final int BULLET_MAX_RENDER=256;
    private static float theata90=(float) Math.toRadians(90);
    public static final HashSet<String> DEFAULT_EXCEPT =new HashSet<String>();
    private static final String[] LEFT_HAND_PART=new String[]{
            "leftArmModel", "leftArmLayerModel"
    };
    private static final String[] LEFT_SLIM_HAND_PART=new String[]{
            "leftArmSlimModel", "leftArmLayerSlimModel"
    };
    private static final String[] RIGHT_HAND_PART=new String[]{
            "rightArmModel", "rightArmLayerModel"
    };
    private static final String[] RIGHT_SLIM_HAND_PART=new String[]{
            "rightArmSlimModel", "rightArmLayerSlimModel"
    };
    static {
        for(String str : ModConfig.INSTANCE.guns.anim_guns_show_default_objects) {
            DEFAULT_EXCEPT.add(str);
        }
        for(int i=0;i<BULLET_MAX_RENDER;i++) {
            DEFAULT_EXCEPT.add("bulletModel_"+i);
        }
    }

    public AnimationController getController(EntityLivingBase player,GunEnhancedRenderConfig config) {
        if(player==Minecraft.getMinecraft().player) {
            if(controller.player!=player||controller.getConfig()!=config) {
                controller=new AnimationController(player, config);
            }
            return controller;
        }
        String name=player.getName();
        if(config==null&&!otherControllers.containsKey(name)) {
            return null;
        }
        if(!otherControllers.containsKey(name)) {
            otherControllers.put(name, new AnimationController(player,config));
        }
        if(config!=null&&otherControllers.get(name).getConfig()!=config) {
            otherControllers.put(name, new AnimationController(player,config));
        }
        return otherControllers.get(name);
    }

    public void renderItem(CustomItemRenderType type, EnumHand hand, ItemStack item, Object... data) {
        if (!(item.getItem() instanceof ItemGun))
            return;

        GunType gunType = ((ItemGun) item.getItem()).type;
        if (gunType == null)
            return;

        EnhancedModel model = gunType.enhancedModel;

        if(!(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayerSP)) {
            return;
        }

        if (model == null)
            return;

        GunEnhancedRenderConfig config = (GunEnhancedRenderConfig) model.config;
        if(this.controller == null || this.controller.getConfig() != config||this.controller.player!=Minecraft.getMinecraft().player){
            this.controller = new AnimationController(Minecraft.getMinecraft().player,config);
        }


        if (this.timer == null) {
            this.timer = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
        }

        if(!item.hasTagCompound())
            return;

        float partialTicks = this.timer.renderPartialTicks;

        EntityPlayerSP player = (EntityPlayerSP) Minecraft.getMinecraft().player;

        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);

        Matrix4f mat = new Matrix4f();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();

        float bx = OpenGlHelper.lastBrightnessX;
        float by = OpenGlHelper.lastBrightnessY;

        /**
         * INITIAL BLENDER POSITION
         * nonono this is minecrfat hand transform
         */
        Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(Minecraft.getMinecraft().player);
        RenderPlayer renderplayer = (RenderPlayer) render;
        renderplayer.getMainModel().setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        renderplayer.getMainModel().bipedLeftArm.offsetX = 0F;

        /**
         * DEFAULT TRANSFORM
         * */
        float zFar = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16F*2;
        mat.rotate(toRadians(90.0F), new Vector3f(0,1,0));
        mat.scale(new Vector3f(1/zFar, 1/zFar, 1/zFar));
        //Do hand rotations
        float f5 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTicks;
        float f6 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTicks;
        mat.rotate(toRadians((player.rotationPitch - f5) * 0.1F), new Vector3f(1, 0, 0));
        mat.rotate(toRadians((player.rotationYaw - f6) * 0.1F), new Vector3f(0, 1, 0));

        float rotateX=0;
        float adsModifier = 0.8F;

        /**
         *  global
         * */
        mat.rotate(toRadians(90), new Vector3f(0, 1, 0));
        mat.translate(new Vector3f(config.global.globalTranslate.x, config.global.globalTranslate.y, config.global.globalTranslate.z));
        mat.scale(new Vector3f(config.global.globalScale.x,config.global.globalScale.y,config.global.globalScale.z));
        mat.rotate(toRadians(-90), new Vector3f(0, 1, 0));
        mat.rotate(config.global.globalRotate.y/180*3.14f, new Vector3f(0, 1, 0));
        mat.rotate(config.global.globalRotate.x/180*3.14f, new Vector3f(1, 0, 0));
        mat.rotate(config.global.globalRotate.z/180*3.14f, new Vector3f(0, 0, 1));

        /**
         * ACTION GUN MOTION
         */
        float gunRotX = RenderParameters.GUN_ROT_X_LAST
                + (RenderParameters.GUN_ROT_X - RenderParameters.GUN_ROT_X_LAST) * ClientProxy.renderHooks.partialTicks;
        float gunRotY = RenderParameters.GUN_ROT_Y_LAST
                + (RenderParameters.GUN_ROT_Y - RenderParameters.GUN_ROT_Y_LAST) * ClientProxy.renderHooks.partialTicks;
        mat.rotate(toRadians(gunRotX), new Vector3f(0, -1, 0));
        mat.rotate(toRadians(gunRotY), new Vector3f(0, 0, -1));

        /**
         * ACTION FORWARD
         */
        float f1 = (player.distanceWalkedModified - player.prevDistanceWalkedModified);
        float f2 = -(player.distanceWalkedModified + f1 * partialTicks);
        float f3 = (player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks);
        float f4 = (player.prevCameraPitch + (player.cameraPitch - player.prevCameraPitch) * partialTicks);

        mat.translate(new Vector3f(0, adsModifier * Interpolation.SINE_IN.interpolate(0F, (-0.2f * (1F - (float)controller.ADS)), GUN_BALANCING_Y),0));
        mat.translate(new Vector3f(0, adsModifier * ((float) (0.05f * (Math.sin(SMOOTH_SWING/10) * GUN_BALANCING_Y))),0));

        mat.rotate(toRadians(adsModifier * 0.1f * Interpolation.SINE_OUT.interpolate(-GUN_BALANCING_Y, GUN_BALANCING_Y, adsModifier * MathHelper.sin(f2 * (float) Math.PI))), new Vector3f(0f,1f, 0f));
        mat.rotate(toRadians(adsModifier * 0.1f * Interpolation.SINE_OUT.interpolate(-GUN_BALANCING_X, GUN_BALANCING_X, adsModifier * MathHelper.sin(f2 * (float) Math.PI))), new Vector3f(0f,1f, 0f));

        mat.translate(new Vector3f(adsModifier * MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F, adsModifier * -Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3), 0.0F));
        mat.rotate(toRadians(adsModifier * MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F), new Vector3f(0.0F, 0.0F, 1.0F));
        mat.rotate(toRadians(adsModifier * Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F), new Vector3f(1.0F, 0.0F, 0.0F));
        mat.rotate(toRadians(adsModifier * f4), new Vector3f(1.0F, 0.0F, 0.0F));

        /**
         * ACTION GUN COLLIDE
         */
        float rotateZ = -(35F * collideFrontDistance);
        float translateY = -(0.7F * collideFrontDistance);
        mat.rotate(toRadians(rotateZ), new Vector3f(0, 0, 1));
        mat.translate(new Vector3f(translateY,0,0));

        /**
         * ACTION GUN SWAY
         */
        RenderParameters.VAL = (float) (Math.sin(RenderParameters.SMOOTH_SWING / 100) * 8);
        RenderParameters.VAL2 = (float) (Math.sin(RenderParameters.SMOOTH_SWING / 80) * 8);
        RenderParameters.VALROT = (float) (Math.sin(RenderParameters.SMOOTH_SWING / 90) * 1.2f);
        mat.translate(new Vector3f(0f, ((VAL / 500) * (0.95f -  (float)controller.ADS)),  ((VAL2 / 500 * (0.95f -  (float)controller.ADS)))));
        mat.rotate(toRadians(adsModifier * VALROT), new Vector3f(1F, 0F, 0F));

        /**
         * ACTION GUN BALANCING X / Y
         */
        mat.translate(new Vector3f((float) (0.1f*GUN_BALANCING_X*Math.cos(Math.PI * RenderParameters.SMOOTH_SWING / 50)) * (1F -  (float)controller.ADS),0,0));
        rotateX-=(GUN_BALANCING_X * 4F) + (float) (GUN_BALANCING_X * Math.sin(Math.PI * RenderParameters.SMOOTH_SWING / 35));
        rotateX-=(float) Math.sin(Math.PI * GUN_BALANCING_X);
        rotateX-=(GUN_BALANCING_X) * 0.4F;

        /**
         * ACTION PROBE
         */
        if(ModularWarfare.isLoadedModularMovements) {
            rotateX+=15F * ClientLitener.cameraProbeOffset;
        }
        mat.rotate(toRadians(rotateX),  new Vector3f(1f, 0f, 0f));

        /**
         * ACTION SPRINT
         */
        RenderParameters.VALSPRINT = (float) (Math.cos(controller.SPRINT_RANDOM * config.springConfig.VALSPRINT * Math.PI)) * gunType.moveSpeedModifier;
        RenderParameters.VALSPRINT2 = (float)(Math.sin(controller.SPRINT_RANDOM * config.springConfig.VALSPRINT1 * Math.PI)) * gunType.moveSpeedModifier;

        Vector3f customSprintRotation = new Vector3f();
        Vector3f customSprintTranslate = new Vector3f();

        float springModifier = (float) (config.springConfig.springModifier.x - controller.ADS);
        float springModifier1 = (float) (config.springConfig.springModifier.y - controller.ADS);
        float springModifier2 = (float) (config.springConfig.springModifier.z - controller.ADS);

        mat.rotate(toRadians(config.springConfig.sprintRandomRotate.x * VALSPRINT * springModifier), new Vector3f(1, 0, 0));
        mat.rotate(toRadians(config.springConfig.sprintRandomRotate.y * VALSPRINT * springModifier1), new Vector3f(0, 1, 0));
        mat.rotate(toRadians(config.springConfig.sprintRandomRotate.z * VALSPRINT2 * springModifier1), new Vector3f(0, 0, 1));
        mat.translate(new Vector3f(VALSPRINT * config.springConfig.sprintRandomTranslate.x * springModifier, VALSPRINT2 * config.springConfig.sprintRandomTranslate.y * springModifier2, VALSPRINT2 * config.springConfig.sprintRandomTranslate.z * springModifier));

        customSprintRotation = new Vector3f((config.sprint.sprintRotate.x * (float) controller.SPRINT), (config.sprint.sprintRotate.y * (float) controller.SPRINT), (config.sprint.sprintRotate.z * (float) controller.SPRINT));
        customSprintTranslate = new Vector3f((config.sprint.sprintTranslate.x * (float) controller.SPRINT), (config.sprint.sprintTranslate.y * (float) controller.SPRINT), (config.sprint.sprintTranslate.z * (float) controller.SPRINT));

        customSprintRotation.scale((1F - (float) controller.ADS));
        customSprintTranslate.scale((1F - (float) controller.ADS));
        /**
         * CUSTOM HIP POSITION
         */

        Vector3f customHipRotation = new Vector3f(config.aim.rotateHipPosition.x, config.aim.rotateHipPosition.y, config.aim.rotateHipPosition.z);
        Vector3f customHipTranslate = new Vector3f(config.aim.translateHipPosition.x, (config.aim.translateHipPosition.y), (config.aim.translateHipPosition.z));

        Vector3f customAimRotation = new Vector3f((config.aim.rotateAimPosition.x *  (float)controller.ADS), (config.aim.rotateAimPosition.y *  (float)controller.ADS), (config.aim.rotateAimPosition.z *  (float)controller.ADS));
        Vector3f customAimTranslate = new Vector3f((config.aim.translateAimPosition.x *  (float)controller.ADS), (config.aim.translateAimPosition.y *  (float)controller.ADS), (config.aim.translateAimPosition.z *  (float)controller.ADS));

        mat.rotate(toRadians(customHipRotation.x + customSprintRotation.x+customAimRotation.x), new Vector3f(1f,0f,0f));
        mat.rotate(toRadians(customHipRotation.y + customSprintRotation.y+customAimRotation.y), new Vector3f(0f,1f,0f));
        mat.rotate(toRadians(customHipRotation.z + customSprintRotation.z+customAimRotation.z), new Vector3f(0f,0f,1f));
        mat.translate(new Vector3f(customHipTranslate.x + customSprintTranslate.x+customAimTranslate.x, customHipTranslate.y + customSprintTranslate.y+customAimTranslate.y, customHipTranslate.z + customSprintTranslate.z+customAimTranslate.z));

        float renderInsideGunOffset=5;

        /**
         * ATTACHMENT AIM
         * */
        ItemAttachment sight = null;
        if(GunType.getAttachment(item, AttachmentPresetEnum.Sight)!=null) {
            sight = (ItemAttachment) GunType.getAttachment(item, AttachmentPresetEnum.Sight).getItem();
            Attachment sightConfig=config.attachment.get(sight.type.internalName);
            if(sightConfig!=null) {
                //System.out.println("test");
                float ads = (float) controller.ADS;
                mat.translate((Vector3f) new Vector3f(sightConfig.sightAimPosOffset).scale(ads));
                mat.rotate(ads * sightConfig.sightAimRotOffset.y * 3.14f / 180, new Vector3f(0, 1, 0));
                mat.rotate(ads * sightConfig.sightAimRotOffset.x * 3.14f / 180, new Vector3f(1, 0, 0));
                mat.rotate(ads * sightConfig.sightAimRotOffset.z * 3.14f / 180, new Vector3f(0, 0, 1));
                renderInsideGunOffset=sightConfig.renderInsideGunOffset;
            }
        }

        /**
         * RECOIL
         */

        /** Random Shake */
        float min = -1.5f;
        float max = 1.5f;
        float randomNum = new Random().nextFloat();
        float randomShake = min + (randomNum * (max - min));

        final float alpha = anim.lastGunRecoil + (anim.gunRecoil - anim.lastGunRecoil) * partialTicks;
        float bounce = Interpolation.BOUNCE_INOUT.interpolate(0F, 1F, alpha);
        float elastic = Interpolation.ELASTIC_OUT.interpolate(0F, 1F, alpha);

        float sin = MathHelper.sin((float) (2 * Math.PI * alpha));

        float sin10 = MathHelper.sin((float) (2 * Math.PI * alpha)) * 0.05f;

        mat.translate(new Vector3f(-(bounce) * config.extra.modelRecoilBackwards, 0F, 0F));
        mat.translate(new Vector3f(0F, (-(elastic) * config.extra.modelRecoilBackwards) * 0.05F, 0F));

        mat.translate(new Vector3f(0F, 0F, sin10 * anim.recoilSide * config.extra.modelRecoilUpwards));
        mat.rotate(toRadians(sin * anim.recoilSide * config.extra.modelRecoilUpwards), new Vector3f(0F, 0F, 1F));
        mat.rotate(toRadians(5F * sin10 * anim.recoilSide * config.extra.modelRecoilUpwards), new Vector3f(0F, 0F, 1F));

        mat.rotate(toRadians((bounce) * config.extra.modelRecoilUpwards), new Vector3f(0F, 0F, 1F));

        mat.rotate(toRadians(((-alpha) * randomShake * config.extra.modelRecoilShake)), new Vector3f(0.0f, 1.0f, 0.0f));
        mat.rotate(toRadians(((-alpha) * randomShake * config.extra.modelRecoilShake)), new Vector3f(1.0f, 0.0f, 0.0f));

        if(ScopeUtils.isIndsideGunRendering) {
            mat.translate(new Vector3f(-renderInsideGunOffset, 0, 0));
        }

        floatBuffer.clear();
        mat.store(floatBuffer);
        floatBuffer.rewind();

        GL11.glMultMatrix(floatBuffer);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

        float worldScale = 1;
        float rotateXRendering=rotateX;
        CROSS_ROTATE=rotateXRendering;
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        color(1, 1, 1, 1f);

        boolean applySprint = controller.SPRINT > 0.1 && controller.INSPECT >= 1;
        boolean isRenderHand0 = ScopeUtils.isRenderHand0||!OptifineHelper.isShadersEnabled();
        HashSet<String> exceptParts=new HashSet<String>();
        if(isRenderHand0) {
            exceptParts.addAll(config.defaultHidePart);
            exceptParts.addAll(DEFAULT_EXCEPT);

            for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                ItemStack itemStack = GunType.getAttachment(item, attachment);
                if (itemStack != null && itemStack.getItem() != Items.AIR) {
                    AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                    String binding = "gunModel";
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
                ItemStack itemStack = GunType.getAttachment(item, attachment);
                if (itemStack != null && itemStack.getItem() != Items.AIR) {
                    AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                    String binding = "gunModel";
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

//        model.updateAnimation(controller.getTime(),true);

        /**
         * RIGHT HAND GROUP
         * */

        final ItemAttachment sightRendering=sight;

        boolean glowMode=ObjModelRenderer.glowTxtureMode;
        ObjModelRenderer.glowTxtureMode=true;
        blendTransform(model,item, !config.animations.containsKey(AnimationType.SPRINT), controller.getTime(), controller.getSprintTime(),(float)controller.SPRINT, "sprint_righthand", applySprint, true, () -> {
            if(isRenderHand0) {
                if(sightRendering!=null) {
                    String binding = "gunModel";
                    if (config.attachment.containsKey(sightRendering.type.internalName)) {
                        binding = config.attachment.get(sightRendering.type.internalName).binding;
                    }
                    model.applyGlobalTransformToOther(binding, () -> {
                        renderAttachment(config, AttachmentPresetEnum.Sight.typeName, sightRendering.type.internalName, () -> {
                            writeScopeGlassDepth(sightRendering.type, (ModelAttachment)sightRendering.type.model, controller.ADS > 0, worldScale, sightRendering.type.sight.modeType.isPIP);
                        });
                    });
                }
                /**
                 * player right hand
                 * */
                if(Minecraft.getMinecraft().currentScreen instanceof GuiAttachmentModified) {
                    controller.attachmentMode = true;
                    GL11.glTranslatef(config.attachmentMode.attachmentRightArmTranslate.x * controller.GetAtachModeProgress(), config.attachmentMode.attachmentRightArmTranslate.y * controller.GetAtachModeProgress(), config.attachmentMode.attachmentRightArmTranslate.z * controller.GetAtachModeProgress());
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.x * controller.GetAtachModeProgress(), 1, 0, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.y * controller.GetAtachModeProgress(), 0, 1, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.z * controller.GetAtachModeProgress(), 0, 0, 1);

                    {
                        if (gunType.handsTextureType != null) {
                            bindCustomHands(gunType.handsTextureType);
                        } else {
                            bindPlayerSkin();
                        }
                        if(!Minecraft.getMinecraft().player.getSkinType().equals("slim")) {
                            model.renderPart(RIGHT_HAND_PART);
                            model.applyGlobalTransformToOther("rightArmModel", () -> {
                                renderRightArm(player, renderplayer.getMainModel(),config);
                            });
                        }else {
                            model.renderPart(RIGHT_SLIM_HAND_PART);
                            model.applyGlobalTransformToOther("rightArmSlimModel", () -> {
                                renderRightArm(player, renderplayer.getMainModel(),config);
                            });
                        }
                    }
                }else {
                    controller.attachmentMode = false;
                    GL11.glTranslatef(config.attachmentMode.attachmentRightArmTranslate.x * controller.GetAtachModeProgress(), config.attachmentMode.attachmentRightArmTranslate.y * controller.GetAtachModeProgress(), config.attachmentMode.attachmentRightArmTranslate.z * controller.GetAtachModeProgress());
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.x * controller.GetAtachModeProgress(), 1, 0, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.y * controller.GetAtachModeProgress(), 0, 1, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentRightArmRotate.z * controller.GetAtachModeProgress(), 0, 0, 1);
                    if(gunType.handsTextureType != null){
                        bindCustomHands(gunType.handsTextureType);
                    } else {
                        bindPlayerSkin();
                    }
                    if(!Minecraft.getMinecraft().player.getSkinType().equals("slim")) {
                        model.renderPart(RIGHT_HAND_PART);
                        model.applyGlobalTransformToOther("rightArmModel", () -> {
                            renderRightArm(player, renderplayer.getMainModel(),config);
                        });
                    }else {
                        model.renderPart(RIGHT_SLIM_HAND_PART);
                        model.applyGlobalTransformToOther("rightArmSlimModel", () -> {
                            renderRightArm(player, renderplayer.getMainModel(),config);
                        });
                    }
                }
                ObjModelRenderer.glowTxtureMode=true;

                /**
                 * gun
                 * */
                int skinId = 0;
                if (item.hasTagCompound()) {
                    if (item.getTagCompound().hasKey("skinId")) {
                        skinId = item.getTagCompound().getInteger("skinId");
                    }
                }
                String gunPath = skinId > 0 ? gunType.modelSkins[skinId].getSkin() : gunType.modelSkins[0].getSkin();
                bindTexture("guns", gunPath);
                model.renderPartExcept(exceptPartsRendering);

                /**
                 * selecotr
                 * */
                WeaponFireMode fireMode = GunType.getFireMode(item);
                if(fireMode==WeaponFireMode.SEMI) {
                    model.renderPart("selector_semi");
                }else if(fireMode==WeaponFireMode.FULL) {
                    model.renderPart("selector_full");
                }else if(fireMode==WeaponFireMode.BURST){
                    model.renderPart("selector_brust");
                }else if(fireMode==WeaponFireMode.FUSE){
                    model.renderPart("selector_fuse");
                }

                /**
                 * ammo and bullet
                 * */
                boolean flagDynamicAmmoRendered=false;
                ItemStack stackAmmo = new ItemStack(item.getTagCompound().getCompoundTag("ammo"));
                ItemStack orignalAmmo = stackAmmo;
                stackAmmo=controller.getRenderAmmo(stackAmmo);
                ItemStack renderAmmo=stackAmmo;
                ItemStack prognosisAmmo=ClientTickHandler.reloadEnhancedPrognosisAmmoRendering;

                ItemStack bulletStack=ItemStack.EMPTY;
                int currentAmmoCount=0;

                VarBoolean defaultBulletFlag=new VarBoolean();
                defaultBulletFlag.b=true;
                boolean defaultAmmoFlag=true;

                if (gunType.acceptedBullets != null) {
                    currentAmmoCount= item.getTagCompound().getInteger("ammocount");
                    if (anim.reloading) {
                        currentAmmoCount += anim.getAmmoCountOffset(true);
                    }
                    bulletStack= new ItemStack(item.getTagCompound().getCompoundTag("bullet"));
                    if (anim.reloading) {
                        bulletStack = ClientProxy.gunEnhancedRenderer.controller.getRenderAmmo(bulletStack);
                    }
                }else {
                    Integer currentMagcount=null;
                    if(stackAmmo!=null&&!stackAmmo.isEmpty()&&stackAmmo.hasTagCompound()) {
                        if(stackAmmo.getTagCompound().hasKey("magcount")) {
                            currentMagcount=stackAmmo.getTagCompound().getInteger("magcount");
                        }
                        currentAmmoCount=ReloadHelper.getBulletOnMag(stackAmmo, currentMagcount);
                        bulletStack= new ItemStack(stackAmmo.getTagCompound().getCompoundTag("bullet"));
                    }
                }
                int currentAmmoCountRendering=currentAmmoCount;

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
                                bindTexture("guns", gunPath);
                            } else {
                                String pathAmmo = skinIdBullet > 0 ? bulletType.modelSkins[skinIdBullet].getSkin()
                                        : bulletType.modelSkins[0].getSkin();
                                bindTexture("bullets", pathAmmo);
                            }
                            if (gunType.weaponType == WeaponType.Grenadelauncher) {
                                for (int bullet = 0; bullet < gunType.internalAmmoStorage && bullet < BULLET_MAX_RENDER; bullet++) {
                                    int renderBullet=bullet;
                                    model.applyGlobalTransformToOther("bulletModel_" + bullet, () -> {
                                        renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                            bulletType.model.renderPart("bulletModel", worldScale);
                                        });
                                    });
                                }
                            } else {
                                for (int bullet = 0; bullet < currentAmmoCount && bullet < BULLET_MAX_RENDER; bullet++) {
                                    int renderBullet = bullet;
                                    model.applyGlobalTransformToOther("bulletModel_" + bullet, () -> {
                                        renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                            bulletType.model.renderPart("bulletModel", worldScale);
                                        });
                                    });
                                }
                            }
                            model.applyGlobalTransformToOther("bulletModel", () -> {
                                renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                    bulletType.model.renderPart("bulletModel", worldScale);
                                });
                            });
                            defaultBulletFlag.b=false;
                        }
                    }
                }

                ItemStack[] ammoList=new ItemStack[] {stackAmmo,orignalAmmo,prognosisAmmo};
                String[] binddings=new String[] {"ammoModel","ammoModelPre","ammoModelPost"};
                for(int x=0;x<3;x++) {
                    ItemStack stackAmmoX = ammoList[x];
                    if (stackAmmoX == null || stackAmmoX.isEmpty()) {
                        continue;
                    }
                    if (!model.existPart(binddings[x])) {
                        continue;
                    }
                    if (stackAmmoX.getItem() instanceof ItemAmmo) {
                        ItemAmmo itemAmmo = (ItemAmmo) stackAmmoX.getItem();
                        AmmoType ammoType = itemAmmo.type;
                        if (ammoType.isDynamicAmmo && ammoType.model != null) {
                            int skinIdAmmo = 0;
                            int baseAmmoCount = 0;

                            if (stackAmmoX.hasTagCompound()) {
                                if (stackAmmoX.getTagCompound().hasKey("skinId")) {
                                    skinIdAmmo = stackAmmoX.getTagCompound().getInteger("skinId");
                                }
                                if (stackAmmoX.getTagCompound().hasKey("magcount")) {
                                    baseAmmoCount = (stackAmmoX.getTagCompound().getInteger("magcount") - 1) * ammoType.ammoCapacity;
                                }
                            }
                            int baseAmmoCountRendering = baseAmmoCount;

                            if (ammoType.sameTextureAsGun) {
                                bindTexture("guns", gunPath);
                            } else {
                                String pathAmmo = skinIdAmmo > 0 ? ammoType.modelSkins[skinIdAmmo].getSkin() : ammoType.modelSkins[0].getSkin();
                                bindTexture("ammo", pathAmmo);
                            }

                            if (controller.shouldRenderAmmo()) {
                                model.applyGlobalTransformToOther("ammoModel", () -> {
                                    GlStateManager.pushMatrix();
                                    if (renderAmmo.getTagCompound().hasKey("magcount")) {
                                        if (config.attachment.containsKey(itemAmmo.type.internalName)) {
                                            if (config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform != null) {
                                                if (renderAmmo.getTagCompound().getInteger("magcount") <= config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.size()) {
                                                    //be careful, don't mod the config
                                                    Transform ammoTransform = config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.get(renderAmmo.getTagCompound().getInteger("magcount") - 1);
                                                    Transform renderTransform = ammoTransform;
                                                    if (anim.reloading && (anim
                                                            .getReloadAnimationType() == AnimationType.RELOAD_FIRST_QUICKLY)) {
                                                        float magAlpha = (float) controller.RELOAD;
                                                        renderTransform = new Transform();
                                                        ammoTransform = config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.get(prognosisAmmo.getTagCompound().getInteger("magcount") - 1);
                                                        Transform beginTransform = config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform.get(orignalAmmo.getTagCompound().getInteger("magcount") - 1);

                                                        renderTransform.translate.x = beginTransform.translate.x
                                                                + (ammoTransform.translate.x - beginTransform.translate.x)
                                                                * magAlpha;
                                                        renderTransform.translate.y = beginTransform.translate.y
                                                                + (ammoTransform.translate.y - beginTransform.translate.y)
                                                                * magAlpha;
                                                        renderTransform.translate.z = beginTransform.translate.z
                                                                + (ammoTransform.translate.z - beginTransform.translate.z)
                                                                * magAlpha;

                                                        renderTransform.rotate.x = beginTransform.rotate.x
                                                                + (ammoTransform.rotate.x - beginTransform.rotate.x)
                                                                * magAlpha;
                                                        renderTransform.rotate.y = beginTransform.rotate.y
                                                                + (ammoTransform.rotate.y - beginTransform.rotate.y)
                                                                * magAlpha;
                                                        renderTransform.rotate.z = beginTransform.rotate.z
                                                                + (ammoTransform.rotate.z - beginTransform.rotate.z)
                                                                * magAlpha;

                                                        renderTransform.scale.x = beginTransform.scale.x
                                                                + (ammoTransform.scale.x - beginTransform.scale.x)
                                                                * magAlpha;
                                                        renderTransform.scale.y = beginTransform.scale.y
                                                                + (ammoTransform.scale.y - beginTransform.scale.y)
                                                                * magAlpha;
                                                        renderTransform.scale.z = beginTransform.scale.z
                                                                + (ammoTransform.scale.z - beginTransform.scale.z)
                                                                * magAlpha;
                                                    }
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
                                    renderAttachment(config, "ammo", ammoType.internalName, () -> {
                                        ammoType.model.renderPart("ammoModel", worldScale);
                                        if (defaultBulletFlag.b) {
                                            if (renderAmmo.getTagCompound().hasKey("magcount")) {
                                                for (int i = 1; i <= ammoType.magazineCount; i++) {
                                                    int count = ReloadHelper.getBulletOnMag(renderAmmo, i);
                                                    for (int bullet = 0; bullet < count && bullet < BULLET_MAX_RENDER; bullet++) {
                                                        //System.out.println((ammoType.ammoCapacity*(i-1))+bullet);
                                                        ammoType.model.renderPart("bulletModel_" + ((ammoType.ammoCapacity * (i - 1)) + bullet), worldScale);
                                                    }
                                                }
                                            } else {
                                                for (int bullet = 0; bullet < currentAmmoCountRendering && bullet < BULLET_MAX_RENDER; bullet++) {
                                                    ammoType.model.renderPart("bulletModel_" + (baseAmmoCountRendering + bullet), worldScale);
                                                }
                                            }

                                            defaultBulletFlag.b = false;
                                        }
                                    });
                                    GlStateManager.popMatrix();
                                });
                                model.applyGlobalTransformToOther("bulletModel", () -> {
                                    renderAttachment(config, "bullet", ammoType.internalName, () -> {
                                        ammoType.model.renderPart("bulletModel", worldScale);
                                    });
                                });
                                flagDynamicAmmoRendered = true;
                                defaultAmmoFlag = false;
                            }
                        }
                    }
                }

                /**
                 * default bullet and ammo
                 * */

                bindTexture("guns", gunPath);

                if(defaultBulletFlag.b) {
                    if (gunType.weaponType == WeaponType.Grenadelauncher){
                        for (int bullet = 0; bullet < gunType.internalAmmoStorage && bullet < BULLET_MAX_RENDER; bullet++) {
                            model.renderPart("bulletModel_" + bullet);
                        }
                        model.renderPart("bulletModel");
                    }else {
                        for (int bullet = 0; bullet < currentAmmoCount && bullet < BULLET_MAX_RENDER; bullet++) {
                            model.renderPart("bulletModel_" + bullet);
                        }
                        model.renderPart("bulletModel");
                    }
                }

                if (controller.shouldRenderAmmo() && defaultAmmoFlag) {
                    model.renderPart("ammoModel");
                }


                /**
                 * attachment
                 * */

                for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                    ItemStack itemStack = GunType.getAttachment(item, attachment);
                    if (itemStack != null && itemStack.getItem() != Items.AIR) {
                        AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                        ModelAttachment attachmentModel = (ModelAttachment) attachmentType.model;

                        if(ScopeUtils.isIndsideGunRendering) {
                            if (attachment == AttachmentPresetEnum.Sight) {
                                if (config.attachment.containsKey(attachmentType.internalName)) {
                                    if(!config.attachment.get(attachmentType.internalName).renderInsideSightModel) {
                                        continue;
                                    }
                                }else {
                                    continue;
                                }
                            }
                        }

                        if (attachmentModel != null) {
                            String binding = "gunModel";
                            if (config.attachment.containsKey(attachmentType.internalName)) {
                                binding = config.attachment.get(attachmentType.internalName).binding;
                            }
                            model.applyGlobalTransformToOther(binding, () -> {
                                if (attachmentType.sameTextureAsGun) {
                                    bindTexture("guns", gunPath);
                                } else {
                                    int attachmentsSkinId = 0;
                                    if (itemStack.hasTagCompound()) {
                                        if (itemStack.getTagCompound().hasKey("skinId")) {
                                            attachmentsSkinId = itemStack.getTagCompound().getInteger("skinId");
                                        }
                                    }
                                    String attachmentsPath = attachmentsSkinId > 0 ? attachmentType.modelSkins[attachmentsSkinId].getSkin()
                                            : attachmentType.modelSkins[0].getSkin();
                                    bindTexture("attachments", attachmentsPath);
                                }
                                renderAttachment(config, attachment.typeName, attachmentType.internalName, () -> {
                                    attachmentModel.renderAttachment(worldScale);
                                    if(attachment==AttachmentPresetEnum.Sight) {
                                        renderScopeGlass(attachmentType, attachmentModel, controller.ADS > 0, worldScale);
                                    }
                                });
                                if (attachment == AttachmentPresetEnum.Laser){
                                    if (item.getTagCompound().hasKey("laserEnabled")){
                                        boolean enabled = item.getTagCompound().getBoolean("laserEnabled");
                                        if (enabled){
                                            attachmentModel.renderLaser(worldScale,true);
                                        }
                                    }
                                }
                            });
                        }

                        if (attachment == AttachmentPresetEnum.Sight) {
                            WeaponScopeModeType modeType = attachmentType.sight.modeType;
                            if (modeType.isMirror) {
                                if (controller.ADS == 1) {
                                    if (!ClientRenderHooks.isAimingScope) {
                                        ClientRenderHooks.isAimingScope = true;
                                        ModularWarfare.NETWORK
                                                .sendToServer(new PacketAimingRequest(player.getDisplayNameString(), true));
                                    }
                                } else {
                                    if (ClientRenderHooks.isAimingScope) {
                                        ClientRenderHooks.isAimingScope = false;
                                        ModularWarfare.NETWORK.sendToServer(
                                                new PacketAimingRequest(player.getDisplayNameString(), false));
                                    }
                                }
                            } else {
                                if (adsSwitch == 1.0F) {
                                    if (!ClientRenderHooks.isAiming) {
                                        ClientRenderHooks.isAiming = true;
                                        ModularWarfare.NETWORK
                                                .sendToServer(new PacketAimingRequest(player.getDisplayNameString(), true));
                                    }
                                } else {
                                    if (ClientRenderHooks.isAiming) {
                                        ClientRenderHooks.isAiming = false;
                                        ModularWarfare.NETWORK.sendToServer(
                                                new PacketAimingRequest(player.getDisplayNameString(), false));
                                    }
                                }
                            }
                        }
                    }
                }
                /**
                 *  flashmodel
                 *  */
                ObjModelRenderer.glowTxtureMode=false;
                boolean shouldRenderFlash=true;
                if ((GunType.getAttachment(item, AttachmentPresetEnum.Barrel) != null)) {
                    AttachmentType attachmentType = ((ItemAttachment) GunType.getAttachment(item, AttachmentPresetEnum.Barrel).getItem()).type;
                    if (attachmentType.attachmentType == AttachmentPresetEnum.Barrel) {
                        shouldRenderFlash = !attachmentType.barrel.hideFlash;
                    }
                }


                if (shouldRenderFlash && anim.shooting && !player.isInWater()) {
                    if (anim.getShootingAnimationType() == AnimationType.FIRE || anim.getShootingAnimationType() == AnimationType.FIRE_SECOND ||
                            anim.getShootingAnimationType() == AnimationType.FIRE_THIRD || anim.getShootingAnimationType() == AnimationType.FIRE_EMPTY){
                        GlStateManager.pushMatrix();
                        {
                            GL11.glEnable(3042);
                            GL11.glEnable(2832);
                            GL11.glHint(3153, 4353);
                            TextureType flashType = gunType.flashType;
                            bindTexture(flashType.resourceLocations.get(anim.flashCount % flashType.resourceLocations.size()));

                            GlStateManager.depthMask(false);
                            GlStateManager.disableLighting();
                            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

                            ObjModelRenderer.glowTxtureMode=false;
                            model.renderPart("flashModel");
                            ObjModelRenderer.glowTxtureMode=glowMode;

                            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bx, by);
                            GlStateManager.enableLighting();
                            GlStateManager.depthMask(true);

                            GL11.glDisable(3042);
                            GL11.glDisable(2832);
                        }
                        GlStateManager.popMatrix();
                    }
                }

                GlStateManager.depthMask(true);
                ObjModelRenderer.glowTxtureMode=true;
            }
        });

        /**
         * LEFT HAND GROUP
         * */
        blendTransform(model, item, !config.animations.containsKey(AnimationType.SPRINT), controller.getTime(), controller.getSprintTime(),(float)controller.SPRINT, "sprint_lefthand", applySprint,true, () -> {
            if (isRenderHand0) {
                /**
                 * player left hand
                 * */
                if(Minecraft.getMinecraft().currentScreen instanceof GuiAttachmentModified) {
                    controller.attachmentMode = true;
                    controller.INSPECT = 1;
                    controller.INSPECT_EMPTY = 1;
                    controller.DRAW = 1;
                    GL11.glTranslatef(config.attachmentMode.attachmentLeftArmTranslate.x * controller.GetAtachModeProgress(), config.attachmentMode.attachmentLeftArmTranslate.y * controller.GetAtachModeProgress(), config.attachmentMode.attachmentLeftArmTranslate.z*  controller.GetAtachModeProgress());
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.x * controller.GetAtachModeProgress(), 1, 0, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.y * controller.GetAtachModeProgress(), 0, 1, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.z * controller.GetAtachModeProgress(), 0, 0, 1);

                    if (gunType.handsTextureType != null) {
                        bindCustomHands(gunType.handsTextureType);
                    } else {
                        bindPlayerSkin();
                    }
                    if (!Minecraft.getMinecraft().player.getSkinType().equals("slim")) {
                        model.renderPart(LEFT_HAND_PART);
                        model.applyGlobalTransformToOther("leftArmModel", () -> {
                            renderLeftArm(player, renderplayer.getMainModel(), config);
                        });
                    } else {
                        model.renderPart(LEFT_SLIM_HAND_PART);
                        model.applyGlobalTransformToOther("leftArmSlimModel", () -> {
                            renderLeftArm(player, renderplayer.getMainModel(), config);
                        });
                    }
                }else {
                    controller.attachmentMode = false;
                    GL11.glTranslatef(config.attachmentMode.attachmentLeftArmTranslate.x * controller.GetAtachModeProgress(), config.attachmentMode.attachmentLeftArmTranslate.y * controller.GetAtachModeProgress(), config.attachmentMode.attachmentLeftArmTranslate.z*  controller.GetAtachModeProgress());
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.x * controller.GetAtachModeProgress(), 1, 0, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.y * controller.GetAtachModeProgress(), 0, 1, 0);
                    GL11.glRotatef(config.attachmentMode.attachmentLeftArmRotate.z * controller.GetAtachModeProgress(), 0, 0, 1);
                    if (gunType.handsTextureType != null) {
                        bindCustomHands(gunType.handsTextureType);
                    } else {
                        bindPlayerSkin();
                    }if (!Minecraft.getMinecraft().player.getSkinType().equals("slim")) {
                        model.renderPart(LEFT_HAND_PART);
                        model.applyGlobalTransformToOther("leftArmModel", () -> {
                            renderLeftArm(player, renderplayer.getMainModel(), config);
                        });
                    } else {
                        model.renderPart(LEFT_SLIM_HAND_PART);
                        model.applyGlobalTransformToOther("leftArmSlimModel", () -> {
                            renderLeftArm(player, renderplayer.getMainModel(), config);
                        });
                    }
                }
            }
        });

        if(sightRendering!=null) {
            if (!ScopeUtils.isIndsideGunRendering) {
                if (!OptifineHelper.isShadersEnabled()) {
                    copyMirrorTexture();
                    ClientProxy.scopeUtils.renderPostScope(partialTicks, false, true, true, 1);
                    eraseScopeGlassDepth(sightRendering.type, (ModelAttachment) sightRendering.type.model,controller.ADS > 0, worldScale);
                } else if (sightRendering.type.sight.modeType.isPIP) {
                    if (isRenderHand0) {
                        //nothing to do
                        ClientProxy.scopeUtils.renderPostScope(partialTicks, false, true, false, 1);
                    }
                } else {
                    if (isRenderHand0) {
                        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);

                        GL11.glDepthRange(0,1);
                        copyMirrorTexture();
                        ClientProxy.scopeUtils.renderPostScope(partialTicks, true, false, true, 1);
                        eraseScopeGlassDepth(sightRendering.type, (ModelAttachment) sightRendering.type.model,controller.ADS > 0, worldScale);
                        writeScopeSoildDepth(controller.ADS > 0);

                        GL11.glPopAttrib();
                    } else {
                        ClientProxy.scopeUtils.renderPostScope(partialTicks, false, true, true, 1);
                    }
                }
            }
        }
        ObjModelRenderer.glowTxtureMode=glowMode;
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.disableBlend();
    }

    public void renderLeftArm(EntityPlayer player, ModelBiped modelplayer, GunEnhancedRenderConfig config) {
        if (!MinecraftForge.EVENT_BUS.post(new RenderHandSleeveEventEnhanced.Pre(this, EnumHandSide.LEFT, modelplayer))) {
            if (player.inventory.armorItemInSlot(2) != null) {
                ItemStack armorStack = player.inventory.armorItemInSlot(2);
                if (armorStack.getItem() instanceof ItemMWArmor) {
                    int skinId = 0;
                    String path = skinId > 0 ? ((ItemMWArmor) armorStack.getItem()).type.modelSkins[skinId].getSkin()
                            : ((ItemMWArmor) armorStack.getItem()).type.modelSkins[0].getSkin();

                    if (!Minecraft.getMinecraft().player.getSkinType().equals("slim")) {
                        ModelCustomArmor modelArmor = ((ModelCustomArmor) ((ItemMWArmor) armorStack
                                .getItem()).type.bipedModel);

                        bindTexture("armor", path);
                        GL11.glPushMatrix();
                        {
                            GL11.glScalef(config.RenderArmorArms.leftArmModelScale.x,config.RenderArmorArms.leftArmModelScale.y,config.RenderArmorArms.leftArmModelScale.z);
                            GL11.glTranslatef(config.RenderArmorArms.leftArmModelTranslate.x,config.RenderArmorArms.leftArmModelTranslate.y,config.RenderArmorArms.leftArmModelTranslate.z);
                            GL11.glScalef(17F, 10.4F, 11F);
                            GL11.glTranslatef(-0.31F,-0.11F,0.0F);
                            modelArmor.showChest(true);
                            modelplayer.bipedLeftArm.rotateAngleX = 0.0030F;
                            modelplayer.bipedLeftArm.rotateAngleY = 0F;
                            modelplayer.bipedLeftArm.rotateAngleZ = config.RenderArmorArms.LeftArmRotate;
                            modelArmor.renderLeftArm((AbstractClientPlayer) player, modelplayer);
                        }
                        GL11.glPopMatrix();
                    } else {
                        ModelCustomArmor modelArmor = ((ModelCustomArmor) ((ItemMWArmor) armorStack
                                .getItem()).type.bipedModel);

                        bindTexture("armor", path);
                        GL11.glPushMatrix();
                        {
                            GL11.glScalef(config.RenderArmorArms.leftArmModelSlimScale.x,config.RenderArmorArms.leftArmModelSlimScale.y,
                                    config.RenderArmorArms.leftArmModelSlimScale.z);
                            GL11.glTranslatef(config.RenderArmorArms.leftArmModelSlimTranslate.x,config.RenderArmorArms.leftArmModelSlimTranslate.y,
                                    config.RenderArmorArms.leftArmModelSlimTranslate.z);
                            GL11.glScalef(11F, 10.4F, 11F);
                            GL11.glTranslatef(0.16F,-0.11F,0.34F);
                            modelArmor.showChest(true);
                            modelplayer.bipedLeftArm.rotateAngleX = 0.0030F;
                            modelplayer.bipedLeftArm.rotateAngleY = 0F;
                            modelplayer.bipedLeftArm.rotateAngleZ = config.RenderArmorArms.LeftArmRotate;
                            modelArmor.renderLeftArm((AbstractClientPlayer) player, modelplayer);
                        }
                        GL11.glPopMatrix();
                    }
                }
            }
            MinecraftForge.EVENT_BUS.post(new RenderHandSleeveEventEnhanced.Post(this, EnumHandSide.LEFT, modelplayer));
        }
    }

    public void renderRightArm(EntityPlayer player, ModelBiped modelplayer,GunEnhancedRenderConfig config) {
        if (!MinecraftForge.EVENT_BUS.post(new RenderHandSleeveEventEnhanced.Pre(this, EnumHandSide.RIGHT, modelplayer))) {
            if (player.inventory.armorItemInSlot(2) != null) {
                ItemStack armorStack = player.inventory.armorItemInSlot(2);
                if (armorStack.getItem() instanceof ItemMWArmor) {
                    int skinId = 0;
                    String path = skinId > 0 ? ((ItemMWArmor) armorStack.getItem()).type.modelSkins[skinId].getSkin()
                            : ((ItemMWArmor) armorStack.getItem()).type.modelSkins[0].getSkin();
                    if (!((ItemMWArmor) armorStack.getItem()).type.simpleArmor) {
                        ModelCustomArmor modelArmor = ((ModelCustomArmor) ((ItemMWArmor) armorStack
                                .getItem()).type.bipedModel);

                        bindTexture("armor", path);
                        GL11.glPushMatrix();
                        {
                            GL11.glScalef(config.RenderArmorArms.rightArmModelScale.x,config.RenderArmorArms.rightArmModelScale.y,config.RenderArmorArms.rightArmModelScale.z);
                            GL11.glTranslatef(config.RenderArmorArms.rightArmModelTranslate.x,config.RenderArmorArms.rightArmModelTranslate.y,config.RenderArmorArms.rightArmModelTranslate.z);
                            GL11.glScalef(17F, 10.4F, 11F);
                            GL11.glTranslatef(0.3F,-0.117F,0.0025F);
                            modelArmor.showChest(true);
                            modelplayer.bipedRightArm.rotateAngleX = 0.0030F;
                            modelplayer.bipedRightArm.rotateAngleY= -0.01F;
                            modelplayer.bipedRightArm.rotateAngleZ= config.RenderArmorArms.RightArmRotate;
                            modelArmor.renderRightArm((AbstractClientPlayer) player, modelplayer);
                        }
                        GL11.glPopMatrix();
                    } else {
                        ModelCustomArmor modelArmor = ((ModelCustomArmor) ((ItemMWArmor) armorStack
                                .getItem()).type.bipedModel);

                        bindTexture("armor", path);
                        GL11.glPushMatrix();
                        {
                            GL11.glScalef(config.RenderArmorArms.rightArmModelSlimScale.x,config.RenderArmorArms.rightArmModelSlimScale.y,
                                    config.RenderArmorArms.rightArmModelSlimScale.z);
                            GL11.glTranslatef(config.RenderArmorArms.rightArmModelSlimTranslate.x,config.RenderArmorArms.rightArmModelSlimTranslate.y,
                                    config.RenderArmorArms.rightArmModelSlimTranslate.z);
                            GL11.glScalef(17F, 10.4F, 11F);
                            GL11.glTranslatef(0.3F,-0.117F,0.0025F);
                            modelArmor.showChest(true);
                            modelplayer.bipedRightArm.rotateAngleX = 0.0030F;
                            modelplayer.bipedRightArm.rotateAngleY= -0.01F;
                            modelplayer.bipedRightArm.rotateAngleZ= config.RenderArmorArms.RightArmRotate;
                            modelArmor.renderRightArm((AbstractClientPlayer) player, modelplayer);
                        }
                        GL11.glPopMatrix();
                    }
                }
            }
            MinecraftForge.EVENT_BUS.post(new RenderHandSleeveEventEnhanced.Post(this, EnumHandSide.RIGHT, modelplayer));
        }
    }

    public void drawThirdGun(RenderLivingBase renderPlayer, RenderType renderType, EntityLivingBase player, ItemStack demoStack) {
        boolean sneakFlag = false;
        if (player != null && player.isSneaking()) {
            sneakFlag = true;
        }
        drawThirdGun(renderPlayer, renderType, player, demoStack, sneakFlag);
    }

    public void drawThirdGun(RenderLivingBase renderPlayer, RenderType renderType, EntityLivingBase player, ItemStack demoStack, boolean sneakFlag) {
        if (!(demoStack.getItem() instanceof ItemGun))
            return;
        GunType gunType = ((ItemGun) demoStack.getItem()).type;
        if (gunType == null)
            return;
        EnhancedModel model = gunType.enhancedModel;
        GunEnhancedRenderConfig config = (GunEnhancedRenderConfig) model.config;
        EnhancedStateMachine anim = ClientRenderHooks.getEnhancedAnimMachine(player);

        GlStateManager.scale(0.1,0.1,0.1);

        if (!ItemGun.hasNextShot(demoStack) && ((GunEnhancedRenderConfig)model.config).animations.containsKey(AnimationType.DEFAULT_EMPTY)){
            model.updateAnimation((float) config.animations.get(AnimationType.DEFAULT_EMPTY).getStartTime(config.FPS),true);
        }else {
            model.updateAnimation((float) config.animations.get(AnimationType.DEFAULT).getStartTime(config.FPS),true);
        }

        HashSet<String> exceptParts = new HashSet<String>();
        exceptParts.addAll(config.defaultHidePart);

        boolean glowTxtureMode=ObjModelRenderer.glowTxtureMode;
        ObjModelRenderer.glowTxtureMode=true;
        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
            ItemStack itemStack = GunType.getAttachment(demoStack, attachment);
            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                if (config.attachmentGroup.containsKey(attachment.typeName)) {
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
            ItemStack itemStack = GunType.getAttachment(demoStack, attachment);
            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                if (config.attachmentGroup.containsKey(attachment.typeName)) {
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

        exceptParts.addAll(RenderGunEnhanced.DEFAULT_EXCEPT);

        float worldScale = 1;

        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        ClientProxy.gunEnhancedRenderer.color(1, 1, 1, 1f);

        if (player!=null&&sneakFlag) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        if(renderPlayer!=null&&renderPlayer.getMainModel() instanceof ModelBiped) {
            ((ModelBiped)renderPlayer.getMainModel()).bipedRightArm.postRender(0.0625F);
        }

        /**
         * gun
         * */
        int skinId = 0;
        if (demoStack.hasTagCompound()) {
            if (demoStack.getTagCompound().hasKey("skinId")) {
                skinId = demoStack.getTagCompound().getInteger("skinId");
            }
        }
        String gunPath = skinId > 0 ? gunType.modelSkins[skinId].getSkin() : gunType.modelSkins[0].getSkin();
        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
        model.renderPartExcept(exceptParts);

        /**
         * ammo and bullet
         * */
        ItemStack stackAmmo = ItemStack.EMPTY;
        ItemStack bulletStack = ItemStack.EMPTY;
        if (demoStack.hasTagCompound()) {
            stackAmmo = new ItemStack(demoStack.getTagCompound().getCompoundTag("ammo"));
        }
        ItemStack renderAmmo = stackAmmo;
        boolean defaultAmmoFlag = true;

        VarBoolean defaultBulletFlag=new VarBoolean();
        defaultBulletFlag.b=true;
        int currentAmmoCount=0;
        int costAmmoCount=0;

        if (gunType.acceptedBullets != null && demoStack.hasTagCompound()) {
            currentAmmoCount= demoStack.getTagCompound().getInteger("ammocount");
            bulletStack = new ItemStack(demoStack.getTagCompound().getCompoundTag("bullet"));
            costAmmoCount=gunType.internalAmmoStorage-currentAmmoCount;
        }

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
                    for (int bullet = 0; bullet < currentAmmoCount && bullet < RenderGunEnhanced.BULLET_MAX_RENDER; bullet++) {
                        model.applyGlobalTransformToOther("bulletModel_" + bullet, () -> {
                            ClientProxy.gunEnhancedRenderer.renderAttachment(config, "bullet", bulletType.internalName, () -> {
                                bulletType.model.renderPart("bulletModel", worldScale);
                            });
                        });
                    }
                    for (int bullet = 0; bullet < costAmmoCount && bullet < RenderGunEnhanced.BULLET_MAX_RENDER; bullet++) {
                        model.applyGlobalTransformToOther("shellModel_" + bullet, () -> {
                            ClientProxy.gunEnhancedRenderer.renderAttachment(config, "shell", bulletType.internalName, () -> {
                                bulletType.model.renderPart("shellModel", worldScale);
                            });
                        });
                    }
                    model.applyGlobalTransformToOther("bulletModel", () -> {
                        ClientProxy.gunEnhancedRenderer.renderAttachment(config, "bullet", bulletType.internalName, () -> {
                            bulletType.model.renderPart("bulletModel", worldScale);
                        });
                    });
                    defaultBulletFlag.b = false;
                }
            }
        }
        ItemStack[] ammoList = new ItemStack[] { stackAmmo };
        String[] binddings = new String[] { "ammoModel" };
        for (int x = 0; x < 1; x++) {
            ItemStack stackAmmoX = ammoList[x];
            if (stackAmmoX == null || stackAmmoX.isEmpty()) {
                continue;
            }
            if (stackAmmoX.getItem() instanceof ItemAmmo) {
                ItemAmmo itemAmmo = (ItemAmmo) stackAmmoX.getItem();
                AmmoType ammoType = itemAmmo.type;
                if (ammoType.isDynamicAmmo && ammoType.model != null) {
                    int skinIdAmmo = 0;

                    if (ammoType.sameTextureAsGun) {
                        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                    } else {
                        String pathAmmo = skinIdAmmo > 0 ? ammoType.modelSkins[skinIdAmmo].getSkin()
                                : ammoType.modelSkins[0].getSkin();
                        ClientProxy.gunEnhancedRenderer.bindTexture("ammo", pathAmmo);
                    }

                    model.applyGlobalTransformToOther("ammoModel", () -> {
                        GlStateManager.pushMatrix();
                        if (renderAmmo.getTagCompound().hasKey("magcount")) {
                            if (config.attachment.containsKey(itemAmmo.type.internalName)) {
                                if (config.attachment.get(itemAmmo.type.internalName).multiMagazineTransform != null) {
                                    if (renderAmmo.getTagCompound().getInteger("magcount") <= config.attachment
                                            .get(itemAmmo.type.internalName).multiMagazineTransform.size()) {
                                        //be careful, don't mod the config
                                        Transform ammoTransform = config.attachment
                                                .get(itemAmmo.type.internalName).multiMagazineTransform
                                                .get(renderAmmo.getTagCompound().getInteger("magcount") - 1);
                                        Transform renderTransform = ammoTransform;

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
                            ammoType.model.renderPart("ammoModel", worldScale);
                        });
                        GlStateManager.popMatrix();
                    });
                    defaultAmmoFlag = false;
                }
            }
        }

        /**
         * default bullet and ammo
         * */

        ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);

        if(defaultBulletFlag.b) {
            for (int bullet = 0; bullet < currentAmmoCount && bullet < RenderGunEnhanced.BULLET_MAX_RENDER; bullet++) {
                model.renderPart("bulletModel_" + bullet);
            }
            for (int bullet = 0; bullet < costAmmoCount && bullet < RenderGunEnhanced.BULLET_MAX_RENDER; bullet++) {
                model.renderPart("shellModel_" + bullet);
            }
        }

        if (!renderAmmo.isEmpty() && defaultAmmoFlag) {
            model.renderPart("ammoModel");
        }

        /**
         * attachment
         * */

        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
            ItemStack itemStack = GunType.getAttachment(demoStack, attachment);
            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                ModelAttachment attachmentModel = (ModelAttachment) attachmentType.model;
                if (ScopeUtils.isIndsideGunRendering) {
                    if (attachment == AttachmentPresetEnum.Sight) {
                        if (config.attachment.containsKey(attachmentType.internalName)) {
                            if (!config.attachment.get(attachmentType.internalName).renderInsideSightModel) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                }
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
                            String attachmentsPath = attachmentsSkinId > 0
                                    ? attachmentType.modelSkins[attachmentsSkinId].getSkin()
                                    : attachmentType.modelSkins[0].getSkin();
                            ClientProxy.gunEnhancedRenderer.bindTexture("attachments", attachmentsPath);
                        }
                        ClientProxy.gunEnhancedRenderer.renderAttachment(config, attachment.typeName, attachmentType.internalName, () -> {
                            attachmentModel.renderAttachment(worldScale);
                            if (attachment == AttachmentPresetEnum.Sight) {
                                ObjModelRenderer.glowTxtureMode=false;
                                ClientProxy.gunEnhancedRenderer.renderScopeGlass(attachmentType,
                                        attachmentModel, false, worldScale);
                                ObjModelRenderer.glowTxtureMode=true;
                            }
                        });
                    });
                }
            }
        }

        ObjModelRenderer.glowTxtureMode=glowTxtureMode;

        /**
         *  flashmodel
         *  */
        boolean shouldRenderFlash=true;
        if ((GunType.getAttachment(demoStack, AttachmentPresetEnum.Barrel) != null)) {
            AttachmentType attachmentType = ((ItemAttachment) GunType.getAttachment(demoStack, AttachmentPresetEnum.Barrel).getItem()).type;
            if (attachmentType.attachmentType == AttachmentPresetEnum.Barrel) {
                shouldRenderFlash = !attachmentType.barrel.hideFlash;
            }
        }

        float bx=OpenGlHelper.lastBrightnessX;
        float by=OpenGlHelper.lastBrightnessY;

        if (shouldRenderFlash && anim.shooting && anim.getShootingAnimationType() == AnimationType.FIRE && !player.isInWater()) {
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            TextureType flashType = gunType.flashType;
            bindTexture(flashType.resourceLocations.get(anim.flashCount % flashType.resourceLocations.size()));
            model.renderPart("flashModel");
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bx, by);
            GlStateManager.enableLighting();
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void writeScopeGlassDepth(AttachmentType attachmentType, ModelAttachment modelAttachment, boolean isAiming,float worldScale,boolean mask) {
        if(ScopeUtils.isIndsideGunRendering) {
            return;
        }
        if (Minecraft.getMinecraft().world !=  null) {
            if (isAiming) {
                GlStateManager.colorMask(mask, mask, mask, mask);
                renderWorldOntoScope(attachmentType, modelAttachment,worldScale,false);
                GlStateManager.colorMask(true, true, true, true);
            }
        }
        
    }
    /**
     * 将blurFramebuffer图案保存到SCOPE_MASK_TEX
     * */
    public void copyMirrorTexture() {
        if(ScopeUtils.isIndsideGunRendering) {
            return;
        }
        if(!OptifineHelper.isShadersEnabled()) {
            return;
        }
        Minecraft mc=Minecraft.getMinecraft();
        GL43.glCopyImageSubData(ClientProxy.scopeUtils.blurFramebuffer.framebufferTexture, GL_TEXTURE_2D, 0, 0, 0, 0, ScopeUtils.SCOPE_MASK_TEX, GL_TEXTURE_2D, 0, 0, 0, 0, mc.displayWidth, mc.displayHeight, 1);
    }

    /**
     *以SCOPE_MASK_TEX为遮罩  将深度改为cofnig.eraseScopeDepth
     ***/
    @SideOnly(Side.CLIENT)
    public void eraseScopeGlassDepth(AttachmentType attachmentType, ModelAttachment modelAttachment, boolean isAiming,float worldScale) {
        if(ScopeUtils.isIndsideGunRendering) {
            return;
        }
        if(!OptifineHelper.isShadersEnabled()) {
            return;
        }
        if (Minecraft.getMinecraft().world != null) {
            if (isAiming) {
                GlStateManager.colorMask(false, false, false, false);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.pushMatrix();
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.pushMatrix();
                ClientProxy.scopeUtils.setupOverlayRendering();
                ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.pushProgram();
                    Shaders.useProgram(Shaders.ProgramNone);
                }

                GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);

                GL11.glDepthRange(ModConfig.INSTANCE.hud.eraseScopeDepth,ModConfig.INSTANCE.hud.eraseScopeDepth);
                GlStateManager.alphaFunc(GL11.GL_GREATER,0f);
                GlStateManager.depthFunc(GL11.GL_ALWAYS);
                GlStateManager.bindTexture(ScopeUtils.SCOPE_MASK_TEX);
                ClientProxy.scopeUtils.drawScaledCustomSizeModalRectFlipY(0, 0, 0, 0, 1, 1, resolution.getScaledWidth(), resolution.getScaledHeight(), 1, 1);
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0.1f);
                GL11.glPopAttrib();

                if(ScopeUtils.isRenderHand0) {
                    GL20.glUseProgram(Programs.depthProgram);
                    GlStateManager.bindTexture(ClientProxy.scopeUtils.DEPTH_ERASE_TEX);
                    ClientProxy.scopeUtils.drawScaledCustomSizeModalRectFlipY(0, 0, 0, 0, 1, 1, resolution.getScaledWidth(), resolution.getScaledHeight(), 1, 1);
                    GL20.glUseProgram(0);
                }

                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.popProgram();
                }

                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.popMatrix();
                GlStateManager.colorMask(true, true, true, true);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public void writeScopeSoildDepth(boolean isAiming) {
        if(ScopeUtils.isIndsideGunRendering) {
            return;
        }
        if(!OptifineHelper.isShadersEnabled()) {
            return;
        }
        if (Minecraft.getMinecraft().world != null) {
            if (isAiming) {
                GlStateManager.colorMask(false, false, false, false);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.pushMatrix();
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.pushMatrix();
                ClientProxy.scopeUtils.setupOverlayRendering();
                ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.pushProgram();
                    Shaders.useProgram(Shaders.ProgramNone);
                }

                GL20.glUseProgram(Programs.alphaDepthProgram);
                GlStateManager.setActiveTexture(GL13.GL_TEXTURE3);
                int tex3=GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                GlStateManager.bindTexture(ClientProxy.scopeUtils.blurFramebuffer.framebufferTexture);
                GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
                GlStateManager.bindTexture(ClientProxy.scopeUtils.DEPTH_TEX);
                ClientProxy.scopeUtils.drawScaledCustomSizeModalRectFlipY(0, 0, 0, 0, 1, 1, resolution.getScaledWidth(), resolution.getScaledHeight(), 1, 1);
                GlStateManager.setActiveTexture(GL13.GL_TEXTURE3);
                GlStateManager.bindTexture(tex3);
                GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
                GL20.glUseProgram(0);

                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.popProgram();
                }

                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.popMatrix();
                GlStateManager.colorMask(true, true, true, true);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public void renderScopeGlass(AttachmentType attachmentType, ModelAttachment modelAttachment, boolean isAiming,float worldScale) {
        if(ScopeUtils.isIndsideGunRendering) {
            return;
        }
        if (Minecraft.getMinecraft().world != null) {
            if (isAiming) {
                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.pushProgram();
                    Shaders.useProgram(Shaders.ProgramNone);
                }
                GL20.glUseProgram(Programs.normalProgram);
                Minecraft mc=Minecraft.getMinecraft();

                GL11.glPushMatrix();
                int tex=ClientProxy.scopeUtils.blurFramebuffer.framebufferTexture;

                ClientProxy.scopeUtils.blurFramebuffer.bindFramebuffer(false);

                GL30.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ScopeUtils.OVERLAY_TEX, 0);
                GlStateManager.clearColor(0, 0, 0, 0);
                GL11.glClearColor(0, 0, 0, 0);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.depthMask(true);
                GlStateManager.clear (GL11.GL_DEPTH_BUFFER_BIT);
                copyDepthBuffer();
                ClientProxy.scopeUtils.blurFramebuffer.bindFramebuffer(false);
                GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

                //GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                modelAttachment.renderOverlaySolid(worldScale);

                GL20.glUseProgram(0);
                if(OptifineHelper.isShadersEnabled()) {
                    Shaders.popProgram();
                }

                float alpha = 1 - adsSwitch;
                GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                if(attachmentType.sight.usedDefaultOverlayModelTexture) {
                    renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/skins/black.png"));
                }
                //必要的colormask
                GlStateManager.colorMask(true, true, true, false);
                modelAttachment.renderOverlay(worldScale);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.disableBlend();

                ClientProxy.scopeUtils.blurFramebuffer.bindFramebuffer(false);
                GL30.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);
                GlStateManager.clear (GL11.GL_DEPTH_BUFFER_BIT);
                copyDepthBuffer();
                ClientProxy.scopeUtils.blurFramebuffer.bindFramebuffer(false);
                GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

                GlStateManager.disableBlend();
                renderWorldOntoScope(attachmentType, modelAttachment,worldScale,false);
                GlStateManager.enableBlend();

                ContextCapabilities contextCapabilities = GLContext.getCapabilities();
                if (contextCapabilities.OpenGL43) {
                    GL43.glCopyImageSubData(tex, GL_TEXTURE_2D, 0, 0, 0, 0, ScopeUtils.SCOPE_LIGHTMAP_TEX, GL_TEXTURE_2D, 0, 0, 0, 0, mc.displayWidth, mc.displayHeight, 1);

                } else {
                    GL11.glBindTexture(GL_TEXTURE_2D, tex);
                    GL11.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0,  0, 0,0,  mc.displayWidth, mc.displayHeight);
                }
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, OptifineHelper.getDrawFrameBuffer());
                GL11.glPopMatrix();

            } else {
                GL11.glPushMatrix();
                renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/skins/black.png"));
                modelAttachment.renderOverlay(worldScale);
                GL11.glPopMatrix();
            }
        }
    }

    /**
     * 把世界深度写入blurFramebuffer
     * */
    public void copyDepthBuffer() {
        Minecraft mc=Minecraft.getMinecraft();
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, OptifineHelper.getDrawFrameBuffer());
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, ClientProxy.scopeUtils.blurFramebuffer.framebufferObject);
        GlStateManager.colorMask(false,false,false,false);
        GL30.glBlitFramebuffer(0, 0, mc.displayWidth, mc.displayHeight, 0, 0, mc.displayWidth, mc.displayHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GlStateManager.colorMask(true,true,true,true);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, GL11.GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, GL11.GL_NONE);
    }

    @SideOnly(Side.CLIENT)
    private void renderWorldOntoScope(AttachmentType type, ModelAttachment modelAttachment,float worldScale,boolean isLightOn) {
        GL11.glPushMatrix();

        if (isLightOn) {
            renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/skins/white.png"));
            GL11.glDisable(2896);
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
            //ModelGun.glowOn(1);
            modelAttachment.renderScope(worldScale);
            //ModelGun.glowOff();
            GL11.glEnable(2896);
            Minecraft.getMinecraft().entityRenderer.enableLightmap();
        } else {
            if(debug) {
                renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/skins/white.png"));
            }else {
                renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/skins/black.png"));
            }
            modelAttachment.renderScope(worldScale);
        }
        /*
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && mc.gameSettings.thirdPersonView == 0) {
            if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemGun) {
                final ItemStack gunStack = mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                if (GunType.getAttachment(gunStack, AttachmentPresetEnum.Flashlight) != null) {
                    if (isLightOn) {
                        GL11.glDisable(2896);
                        Minecraft.getMinecraft().entityRenderer.disableLightmap();
                        GL11.glDisable(3042);
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(16384);
                        GL11.glEnable(3042);
                        GL11.glDepthMask(false);
                        GL11.glBlendFunc(774, 770);

                        renderEngine.bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, "textures/gui/light.png"));
                        modelAttachment.renderOverlay(worldScale);

                        GL11.glBlendFunc(770, 771);
                        GL11.glDepthMask(true);
                        GL11.glDisable(3042);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                        GL11.glEnable(2896);
                        Minecraft.getMinecraft().entityRenderer.enableLightmap();
                    }
                }
            }
        }
         */
        GL11.glPopMatrix();
    }

    public static void renderAttachment(GunEnhancedRenderConfig config, String type, String name, Runnable run) {
        if (config.attachmentGroup.containsKey(type)) {
            applyTransform(config.attachmentGroup.get(type));
        }
        if (config.attachment.containsKey(name)) {
            applyTransform(config.attachment.get(name));
        }
        run.run();
    }
    
    public static void applyTransform(Transform transform) {
        GlStateManager.translate(transform.translate.x,transform.translate.y,transform.translate.z);
        GlStateManager.scale(transform.scale.x,transform.scale.y,transform.scale.z);
        GlStateManager.rotate(transform.rotate.y, 0,1,0);
        GlStateManager.rotate(transform.rotate.x, 1,0,0);
        GlStateManager.rotate(transform.rotate.z, 0,0,1);
    }

    public void blendTransform(EnhancedModel model,ItemStack gunStack, boolean basicSprint, float time, float sprintTime,
                               float alpha, String hand, boolean applySprint,boolean skin, Runnable runnable) {
        float ammoPer=0;
        if (gunStack.getTagCompound() != null) {
            if (ItemGun.hasAmmoLoaded(gunStack)) {
                ItemStack ammoStack = new ItemStack(gunStack.getTagCompound().getCompoundTag("ammo"));
                if (ammoStack.getTagCompound() != null && ammoStack.getItem() instanceof ItemAmmo) {

                    ItemAmmo itemAmmo = (ItemAmmo)ammoStack.getItem();
                    Integer currentMagcount = null;
                    if (ammoStack.getTagCompound().hasKey("magcount")) {
                        currentMagcount = ammoStack.getTagCompound().getInteger("magcount");
                    }
                    int currentAmmoCount = ReloadHelper.getBulletOnMag(ammoStack, currentMagcount);
                    ammoPer = currentAmmoCount / (float)itemAmmo.type.ammoCapacity;
                }
            }
            if (ItemGun.getUsedBullet(gunStack, ((ItemGun)(gunStack.getItem())).type) != null) {

            }
        }
        float ammoPerParam=ammoPer;

        model.setAnimationCalBlender(new GltfRenderModel.NodeAnimationBlender("FirstPersonBlender") {

            @Override
            public void handle(DataNode node, org.joml.Matrix4f mat) {
                if(!basicSprint) {
                    if(alpha!=0) {
                        sprint:{
                            org.joml.Matrix4f begin_transform = mat;
                            mchhui.hegltf.DataAnimation.Transform end_transform = model.findLocalTransform(node.name, sprintTime);
                            if (end_transform == null) {
                                break sprint;
                            }
                            if (!node.name.equals("root") && !node.name.equals("sprint_lefthand")
                                    && !node.name.equals("sprint_righthand") && !node.name.equals("root_bone")
                                    && !node.name.equals("sprint_lefthand_bone") && !node.name.equals("sprint_righthand_bone")) {
                                break sprint;
                            }
                            Quaternionf quat = new Quaternionf();
                            quat.setFromUnnormalized(begin_transform);
                            quat.normalize().slerp(end_transform.rot.normalize(), alpha);
                            org.joml.Vector3f pos = new org.joml.Vector3f();
                            begin_transform.getTranslation(pos);
                            pos.set(pos.x + (end_transform.pos.x - pos.x) * alpha, pos.y + (end_transform.pos.y - pos.y) * alpha,
                                    pos.z + (end_transform.pos.z - pos.z) * alpha);
                            org.joml.Vector3f size = new org.joml.Vector3f();
                            begin_transform.getScale(size);
                            size.set(size.x + (end_transform.size.x - size.x) * alpha,
                                    size.y + (end_transform.size.y - size.y) * alpha, size.z + (end_transform.size.z - size.z) * alpha);
                            mat.identity();
                            mat.translate(pos);
                            mat.scale(size);
                            mat.rotate(quat);
                        }
                    }
                }
                GunEnhancedRenderConfig.ObjectControl cfg=((GunEnhancedRenderConfig)model.config).objectControl.get(node.name);
                if(cfg!=null) {
                    float per = ammoPerParam;
                    if (!cfg.progress) {
                        per = 1 - per;
                    }
                    //System.out.println(per);
                    mat.translate(cfg.translate.x * per, cfg.translate.y * per, cfg.translate.z * per);
                    mat.rotate(cfg.rotate.y * per * 3.14f / 180, 0, 1, 0);
                    mat.rotate(cfg.rotate.x * per * 3.14f / 180, 1, 0, 0);
                    mat.rotate(cfg.rotate.z * per * 3.14f / 180, 0, 0, 1);
                }
            }
        });
        model.updateAnimation(time, skin);
        runnable.run();
        model.setAnimationCalBlender(null);
    }

    public Quaternionf interpolationRot(Quaternionf q0, Quaternionf q1, float t) {
        float theata = (float)Math.acos(q0.dot(q1));
        if (theata >= theata90 || -theata >= theata90) {
            q1.set(-q1.x, -q1.y, -q1.z, -q1.w);
            theata = q0.dot(q1);
        }
        float sinTheata = MathHelper.sin(theata);
        if (sinTheata == 0) {
            return new Quaternionf(q0.x + (q1.x - q0.x) * t, q0.y + (q1.y - q0.y) * t, q0.z + (q1.z - q0.z) * t,
                    q0.w + (q1.w - q0.w) * t).normalize();
        }
        float c1 = (float)(MathHelper.sin(theata * (1 - t)) / sinTheata);
        float c2 = (float)(MathHelper.sin(theata * t) / sinTheata);
        return new Quaternionf(c1 * q0.x + c2 * q1.x, c1 * q0.y + c2 * q1.y, c1 * q0.z + c2 * q1.z,
                c1 * q0.w + c2 * q1.w).normalize();
    }

    public org.joml.Matrix4f getGlobalTransform(EnhancedModel model,String name) {
        return model.getGlobalTransform(name);
    }

    @Deprecated
    private Matrix3f genMatrixFromQuaternion(Quaternion quaternion) {
        Matrix3f matrix3f=new Matrix3f();
        matrix3f.m00 = 1 - 2 * quaternion.y * quaternion.y - 2 * quaternion.z * quaternion.z;
        matrix3f.m01 = 2 * quaternion.x * quaternion.y + 2 * quaternion.w * quaternion.z;
        matrix3f.m02 = 2 * quaternion.x * quaternion.z - 2 * quaternion.w * quaternion.y;

        matrix3f.m10 = 2 * quaternion.x * quaternion.y - 2 * quaternion.w * quaternion.z;
        matrix3f.m11 = 1 - 2 * quaternion.x * quaternion.x - 2 * quaternion.z * quaternion.z;
        matrix3f.m12 = 2 * quaternion.y * quaternion.z + 2 * quaternion.w * quaternion.x;

        matrix3f.m20 = 2 * quaternion.x * quaternion.z + 2 * quaternion.w * quaternion.y;
        matrix3f.m21 = 2 * quaternion.y * quaternion.z - 2 * quaternion.w * quaternion.x;
        matrix3f.m22 = 1 - 2 * quaternion.x * quaternion.x - 2 * quaternion.y * quaternion.y;
        return matrix3f;
    }

    //4x4 floats
    @Deprecated
    private void genMatrix(Matrix3f m,float[] floats) {
        m.m00=floats[0];
        m.m01=floats[4];
        m.m02=floats[8];

        m.m10=floats[1];
        m.m11=floats[5];
        m.m12=floats[9];

        m.m20=floats[2];
        m.m21=floats[6];
        m.m22=floats[10];
    }

    public static float toRadians(float angdeg) {
        return angdeg / 180.0f * PI;
    }
    
    public void color(float r,float g,float b,float a) {
        this.r=r;
        this.g=g;
        this.b=b;
        this.a=a;
        GlStateManager.color(r, g, b,a);
    }

    @Override
    public void bindTexture(String type, String fileName) {
        super.bindTexture(type, fileName);
        String pathFormat = "skins/%s/%s.png";
        bindTexture(new ResourceLocation(ModularWarfare.MOD_ID, String.format(pathFormat, type, fileName)));
    }

    public void bindTexture(ResourceLocation location) {
        bindingTexture = location;
        Minecraft.getMinecraft().renderEngine.bindTexture(bindingTexture);
    }

    public void bindPlayerSkin() {
        bindingTexture = Minecraft.getMinecraft().player.getLocationSkin();
        Minecraft.getMinecraft().renderEngine.bindTexture(bindingTexture);
    }

    public void bindCustomHands(TextureType handTextureType){
        if(handTextureType.resourceLocations != null) {
            bindingTexture = handTextureType.resourceLocations.get(0);
            Minecraft.getMinecraft().renderEngine.bindTexture(bindingTexture);
        }
    }
}
