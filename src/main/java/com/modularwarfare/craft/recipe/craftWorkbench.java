package com.modularwarfare.craft.recipe;

import com.modularwarfare.ModularWarfare;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class craftWorkbench {
    public static void registerCraftingRecipesWorkbench() {
        IRecipe myRecipe = new ShapedOreRecipe(new ResourceLocation("modularwarfare", "gun_workbench"), new ItemStack(ModularWarfare.YOUR_CUSTOM_BLOCK_INSTANCE),
                "ZZZ", "YXY", "YYY",
                'X', new ItemStack(Blocks.CRAFTING_TABLE),
                'Y', new ItemStack(Blocks.IRON_BLOCK),
                'Z', new ItemStack(Items.IRON_INGOT));

        myRecipe.setRegistryName(new ResourceLocation("modularwarfare", "gun_workbench"));
        ForgeRegistries.RECIPES.register(myRecipe);
    }
}
