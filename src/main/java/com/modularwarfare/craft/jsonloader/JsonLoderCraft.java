package com.modularwarfare.craft.jsonloader;

import com.google.gson.*;
import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.CommonProxy;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.craft.recipe.Recipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JsonLoderCraft extends BaseType {
    public static int currentDirectory = 0;

    public static List loadRecipesFromJson(ArrayList<BaseType> types) {
        List recipeList = new ArrayList<>();
        Set<String> loadedRecipeIds = new HashSet<>();

        for (BaseType type : types) {
            if (type.contentPack == null)
                continue;

            File contentPackDir = new File(ModularWarfare.MOD_DIR, type.contentPack);

            if (CommonProxy.zipJar.matcher(contentPackDir.getName()).matches()) {
                try {
                    ZipFile zipFile = new ZipFile(contentPackDir);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().startsWith("crafting/") && entry.getName().endsWith(".json")) {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                            JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                            JsonArray jsonArray = jsonObject.getAsJsonArray("recipes");

                            receps(jsonArray, loadedRecipeIds, recipeList);

                            reader.close();
                            inputStream.close();
                        }
                    }

                    zipFile.close();
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                File recipesDirectory = new File(contentPackDir, "crafting");

                if (!recipesDirectory.exists()) {
                    recipesDirectory.mkdirs();
                    continue;
                }

                File[] recipeFiles = recipesDirectory.listFiles();

                if (recipeFiles != null) {
                    for (File recipeFile : recipeFiles) {
                        if (recipeFile.isFile() && recipeFile.getName().endsWith(".json") && recipeFile.length() > 0) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(recipeFile));

                                JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                                JsonArray jsonArray = jsonObject.getAsJsonArray("recipes");

                                receps(jsonArray, loadedRecipeIds, recipeList);

                                reader.close();
                            } catch (IOException | JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return recipeList;
    }

    public static void receps(JsonArray jsonArray, Set<String> loadedRecipeIds, List<Recipe> recipeList){
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonRecipe = jsonArray.get(i).getAsJsonObject();
            String recipeId = jsonRecipe.get("id").getAsString();
            String idDirect = jsonRecipe.get("idDir").getAsString();

            //НОВАЯ СИСТЕМА ДЕРИКТОРИЙ КРАФТОВ НАКОНЕЦТО!
            if (currentDirectory == 0 && !idDirect.equals("GUN")) {
                continue;
            }else if (currentDirectory == 1 && !idDirect.equals("AMMO")) {
                continue;
            }else if (currentDirectory == 2 && !idDirect.equals("ARMOR")) {
                continue;
            }else if (currentDirectory == 3 && !idDirect.equals("ATTACHMENT")) {
                continue;
            }else if (currentDirectory == 4 && !idDirect.equals("INGREDIENT")) {
                continue;
            }

            if (loadedRecipeIds.contains(recipeId)) {
                continue;
            } else {
                loadedRecipeIds.add(recipeId);
            }

            JsonArray jsonInput = jsonRecipe.getAsJsonArray("input");
            List<ItemStack> inputItems = new ArrayList<>();

            for (int j = 0; j < jsonInput.size(); j++) {
                JsonObject jsonItem = jsonInput.get(j).getAsJsonObject();
                String itemName = jsonItem.get("item").getAsString();
                int itemCount = jsonItem.get("count").getAsInt();
                int data = 0;

                if (jsonItem.has("data")) {
                    data = jsonItem.get("data").getAsInt();
                }

                inputItems.add(new ItemStack(Item.getByNameOrId(itemName), itemCount, data));
            }

            JsonObject jsonOutput = jsonRecipe.getAsJsonObject("output");
            String outputItem = jsonOutput.get("item").getAsString();
            int outputCount = jsonOutput.get("count").getAsInt();
            int craftingTime = jsonOutput.get("craftingTime").getAsInt();
            int data = 0;

            if (jsonOutput.has("data")) {
                data = jsonOutput.get("data").getAsInt();
            }

            ItemStack output = new ItemStack(Item.getByNameOrId(outputItem), outputCount, data);

            recipeList.add(new Recipe(recipeId, idDirect, inputItems, output, craftingTime));
        }
    }
}