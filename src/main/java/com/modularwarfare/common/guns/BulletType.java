package com.modularwarfare.common.guns;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.client.fpp.basic.configs.BulletRenderConfig;
import com.modularwarfare.client.model.ModelBullet;
import com.modularwarfare.client.model.ModelShell;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.loader.MWModelBase;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class BulletType extends BaseType {

    // Bullet Type
    public HashMap<String, BulletProperty> bulletProperties;

    //Bullet Configs
    public ExplosionBullet explosionBullet = new ExplosionBullet();
    public Flamethrowern flamethrowern = new Flamethrowern();

    public float bulletDamageFactor = 1.0f;
    public float bulletAccuracyFactor = 1.0f;
    public boolean isSlug = false;
    
    public boolean isFireDamage=false;
    public boolean isAbsoluteDamage=false;
    public boolean isBypassesArmorDamage=false;
    public boolean isExplosionDamage=false;
    public boolean isMagicDamage=false;
    public float velocity = 0.1f;

    /**
     * If the ammo model need to be rendered on guns
     */
    public boolean renderBulletModel = false;

    public transient String defaultModel = "default.obj";
    public String shellModelFileName = defaultModel;
    public transient MWModelBase shell;

    public String shellSound = "casing";

    public static class ExplosionBullet  {
        public boolean isExplosionBullets = false;
        public float explosionStrength = 4f;
        public float gravityY = 0.1f;
        public boolean damageWorld = false;
        public boolean flaming = false;
        public boolean isGravidy = false;
        public boolean isLeavesMark = false;
        public LeavesMarkConfig leavesMarkConfig = new LeavesMarkConfig();
        public ExplodeConfig explodeConfig = new ExplodeConfig();
    }

    public static class LeavesMarkConfig{
        public String setLeavesMarkTexture = "";//название текстуры с спрайтами
        public int length = 0; //общие количество спрайтов
        public int unit = 0; //количество спрайтов по горизонтале
        public int fps = 24; //fps - это скорость проигрывания анимацыи обычное значение 24
        public double size = 0; //размер партикла
        public int delay = 0; //Задержка поевления партикла нуливое значение озночяет что задержки нет
    }

    public static class ExplodeConfig{
        public String setExplodeTexture = "";
        public int length = 25;
        public int unit = 5;
        public int fps = 24;
        public double size = 0;
        public int delay = 0;
    }

    public static class Flamethrowern  {
        public boolean isFlamethrowern = false;
        public float explosionStrength = 4f;
        public float gravityY = 0.1f;
        public boolean isGravidy = false;
        public boolean isLeavesMark = false;
    }

    /**
     * Custom Bullet Trail
     * */
    public String trailModel;
    public String trailTex;
    public boolean trailGlow=false;
    
    public boolean isDynamicBullet=false;
    public boolean sameTextureAsGun=false;

    @Override
    public void loadExtraValues() {
        if (maxStackSize == null)
            maxStackSize = 64;

        loadBaseValues();
    }

    @Override
    public void reloadModel() {
        if (renderBulletModel) {
            model = new ModelBullet(ModularWarfare.getRenderConfig(this, BulletRenderConfig.class), this);
        }
        if (!shellModelFileName.equals(defaultModel)) {
            shell = new ModelShell(this, false);
        } else {
            shell = new ModelShell(this, true);
        }
    }

    @Override
    public String getAssetDir() {
        return "bullets";
    }

}
