package com.modularwarfare.client.fpp.enhanced.configs;

import com.modularwarfare.client.fpp.enhanced.AnimationType;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GunEnhancedRenderConfig extends EnhancedRenderConfig {

    public HashMap<AnimationType, Animation> animations = new HashMap<>();
    public HashMap<String, ObjectControl> objectControl = new HashMap<>();

    public GunEnhancedRenderConfig.Global global = new GunEnhancedRenderConfig.Global();
    public GunEnhancedRenderConfig.Sprint sprint = new GunEnhancedRenderConfig.Sprint();
    public GunEnhancedRenderConfig.Aim aim = new GunEnhancedRenderConfig.Aim();
    public GunEnhancedRenderConfig.Extra extra = new GunEnhancedRenderConfig.Extra();
    public GunEnhancedRenderConfig.SpringConfig springConfig = new GunEnhancedRenderConfig.SpringConfig();
    public GunEnhancedRenderConfig.AttachmentMode attachmentMode = new GunEnhancedRenderConfig.AttachmentMode();
    public GunEnhancedRenderConfig.RenderArmorArms RenderArmorArms = new GunEnhancedRenderConfig.RenderArmorArms();
    public GunEnhancedRenderConfig.ThirdPerson thirdPerson = new GunEnhancedRenderConfig.ThirdPerson();
    public HashMap<String, Attachment> attachment=new HashMap<String, GunEnhancedRenderConfig.Attachment>();
    public HashMap<String, AttachmentGroup> attachmentGroup=new HashMap<String, GunEnhancedRenderConfig.AttachmentGroup>();
    public HashSet<String> defaultHidePart=new HashSet<String>();
    public GunEnhancedRenderConfig.ItemLoot itemLoot = new GunEnhancedRenderConfig.ItemLoot();

    public static class Transform{
        public Vector3f translate = new Vector3f(0, 0, 0);
        public Vector3f scale = new Vector3f(1, 1, 1);
        public Vector3f rotate = new Vector3f(0, 0, 0);
    }

    public static class Animation {
        public double startTime = 0;
        public double endTime = 1;
        public double speed = 1;

        public double getStartTime(double FPS) {
            return startTime * 1/FPS;
        }

        public double getEndTime(double FPS) {
            return endTime * 1/FPS;
        }

        public double getSpeed(double FPS) {
            double a=(getEndTime(FPS)-getStartTime(FPS));
            if(a<=0) {
                a=1;
            }
            return speed/a;
        }
    }

    public static class ObjectControl extends Transform{
        public boolean progress;
    }

    //1.translate 3.scale 2.rotate(yxz)
    public static class Global {
        public Vector3f globalTranslate = new Vector3f(0, 0, 0);
        //注:这并不会让你的枪看起来变大或变小 但是会影响剪裁空间
        public Vector3f globalScale = new Vector3f(1, 1, 1);
        public Vector3f globalRotate = new Vector3f(0, 0, 0);
    }

    public static class Sprint {
        public boolean basicSprint = false;
        public Vector3f sprintRotate = new Vector3f(-20.0F, 30.0F, -0.0F);
        public Vector3f sprintTranslate = new Vector3f(0.5F, -0.10F, -0.65F);
    }

    public static class Aim {
        //Advanced configuration - Allows you to change how the gun is held without effecting the sight alignment
        public Vector3f rotateHipPosition = new Vector3f(0F, 0F, 0F);
        //Advanced configuration - Allows you to change how the gun is held without effecting the sight alignment
        public Vector3f translateHipPosition = new Vector3f(0F, 0F, 0F);
        //Advanced configuration - Allows you to change how the gun is held while aiming
        public Vector3f rotateAimPosition = new Vector3f(0F, 0F, 0F);
        //Advanced configuration - Allows you to change how the gun is held while aiming
        public Vector3f translateAimPosition = new Vector3f(0F, 0F, 0F);
    }

    public static class Attachment extends Transform {
        public String binding = "gunModel";
        public Vector3f sightAimPosOffset = new Vector3f(0F, 0F, 0F);
        public Vector3f sightAimRotOffset = new Vector3f(0F, 0F, 0F);
        public ArrayList<Transform> multiMagazineTransform;
        public HashSet<String> hidePart=new HashSet<String>();
        public HashSet<String> showPart=new HashSet<String>();
        public boolean renderInsideSightModel=false;
        public float renderInsideGunOffset=5;
    }

    public static class AttachmentGroup extends Transform {
        public HashSet<String> hidePart=new HashSet<String>();
        public HashSet<String> showPart=new HashSet<String>();
    }

    public static class ThirdPerson {
        public Vector3f thirdPersonOffset = new Vector3f(0.0F, -0.1F, 0.0F);
        public Vector3f backPersonOffset = new Vector3f(0.0F, 0.0F, 0.0F);
        public float thirdPersonScale = 0.8F;
    }

    public static class ItemLoot {
        public Vector3f itemLootOffset = new Vector3f(0.0F, 0.0F, 0.0F);
        public Vector3f itemLootTranslate = new Vector3f(0.0F, 0.0F, 0.0F);
        public Vector3f itemLootScale = new Vector3f(1.0F, 1.0F, 1.0F);
    }

    public static class Extra {
        /**
         * Adds backwards recoil translations to the gun staticModel when firing
         */
        public float modelRecoilBackwards = 0.15F;
        /**
         * Adds upwards/downwards recoil translations to the gun staticModel when firing
         */
        public float modelRecoilUpwards = 1.0F;
        /**
         * Adds a left-right staticModel shaking motion when firing, default 0.5
         */
        public float modelRecoilShake = 1.5F;
        public float scaleCraft = 0.0F;
        public Vector3f craftTranslate = new Vector3f(0.0F, -0.1F, 0.0F);
    }

    public static class AttachmentMode{
        public Vector3f attachmentRightArmTranslate = new Vector3f(0F, 0F, 0F);
        public Vector3f attachmentRightArmRotate = new Vector3f(0F, 0F, 0F);
        public Vector3f attachmentLeftArmTranslate = new Vector3f(0F, 0F, 0F);
        public Vector3f attachmentLeftArmRotate = new Vector3f(0F, 0F, 0F);
    }

    public static class RenderArmorArms {
        public Vector3f leftArmModelTranslate = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f leftArmModelScale = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f leftArmModelSlimTranslate = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f leftArmModelSlimScale = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f rightArmModelTranslate = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f rightArmModelScale = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f rightArmModelSlimTranslate = new Vector3f(1.0F, 1.0F, 1.0F);
        public Vector3f rightArmModelSlimScale = new Vector3f(1.0F, 1.0F, 1.0F);
        public float LeftArmRotate = -89.539F;
        public float RightArmRotate = -114.67F;
    }

    public static class SpringConfig{
        public float VALSPRINT = 2.0F;
        public float VALSPRINT1 = 4.0F;
        public Vector3f springModifier = new Vector3f(0.8F, 1.8F, 2F);
        public Vector3f sprintRandomRotate = new Vector3f(0.8F, 0.6F, 0.6F);
        public Vector3f sprintRandomTranslate = new Vector3f(0.4F, 0.01F, 0.4F);
    }
}
