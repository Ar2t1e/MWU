package com.modularwarfare.craft.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

//От гея геям 👆
public class Recipe {
    private String recipeId;
    private String idDirect;
    private List<ItemStack> inputItems;
    private ItemStack output;
    public int craftingTime;

    public Recipe(String recipeId, String idDirect, List<ItemStack> inputItems, ItemStack output, int craftingTime) {
        this.recipeId = recipeId;
        this.inputItems = inputItems;
        this.output = output;
        this.craftingTime = craftingTime;
        this.idDirect = idDirect;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getIdDirect() {
        return idDirect;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public List<ItemStack> getInputItems() {
        return inputItems;
    }

    public void setInputItems(List<ItemStack> inputItems) {
        this.inputItems = inputItems;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }
}