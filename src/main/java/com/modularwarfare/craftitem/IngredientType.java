package com.modularwarfare.craftitem;

import com.modularwarfare.common.type.BaseType;

public class IngredientType extends BaseType {
    @Override
    public void loadExtraValues() {
        if (maxStackSize == null)
            maxStackSize = 64;

        loadBaseValues();

    }

    @Override
    public void reloadModel() {
    }

    @Override
    public String getAssetDir() {
        return "ingredients";
    }

}
