package com.modularwarfare.craftitem;

import com.modularwarfare.common.type.BaseItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class IngredientItem extends BaseItem {

    public static final Function<IngredientType, IngredientItem> factory = type -> {
        return new IngredientItem(type);
    };
    public IngredientType type;

    public IngredientItem(IngredientType type) {
        super(type);
        this.type = type;
        this.render3d = false;
        this.maxStackSize = 64;
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

}
