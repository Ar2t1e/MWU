package com.modularwarfare.craft.guicraft;

import com.modularwarfare.ModularWarfare;
import com.modularwarfare.api.MWArmorModel;
import com.modularwarfare.api.MWArmorType;
import com.modularwarfare.client.ClientProxy;
import com.modularwarfare.client.ClientRenderHooks;
import com.modularwarfare.client.fpp.basic.configs.ArmorRenderConfig;
import com.modularwarfare.client.fpp.basic.configs.AttachmentRenderConfig;
import com.modularwarfare.client.fpp.basic.configs.GunRenderConfig;
import com.modularwarfare.client.fpp.basic.models.objects.CustomItemRenderType;
import com.modularwarfare.client.fpp.enhanced.AnimationType;
import com.modularwarfare.client.fpp.enhanced.configs.GunEnhancedRenderConfig;
import com.modularwarfare.client.fpp.enhanced.models.EnhancedModel;
import com.modularwarfare.client.gui.button.TextureButton;
import com.modularwarfare.client.model.ModelAttachment;
import com.modularwarfare.client.model.ModelGun;
import com.modularwarfare.common.armor.ArmorType;
import com.modularwarfare.common.armor.ItemSpecialArmor;
import com.modularwarfare.common.guns.*;
import com.modularwarfare.common.network.PacketBase;
import com.modularwarfare.common.network.PacketCraftItem;
import com.modularwarfare.common.type.BaseItem;
import com.modularwarfare.common.type.BaseType;
import com.modularwarfare.craft.jsonloader.JsonLoderCraft;
import com.modularwarfare.craft.container.ContainerWorkbenchGuns;
import com.modularwarfare.craft.recipe.Recipe;
import com.modularwarfare.utility.RenderHelperMW;
import io.netty.util.internal.MathUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WorkbenchGuns extends GuiContainer {
    private static final int MAX_RECIPE_DIRECTORIES = 30;
    public static Recipe currentRecipe;
    private EntityPlayer player;
    private List<Recipe> recipes;
    private InventoryPlayer inventoryPlayer;
    private boolean isCrafting = false;
    private int craftingProgress = 0;
    private int currentPage = 1;
    private static final int RECIPES_PER_PAGE = 5;
    private int maxPages = 0;
    public static GuiTextField textField;

    private int ratioMaxPages = 0;

    private int scrollBarX = 0;
    private int scrollBarY = 0;
    private int scrollBarWidth = 5;
    private int scrollBarHeight = 0;
    private int lastMouseY = 0;
    private boolean isScrolling = false;

    public static final ResourceLocation CraftGui = new ResourceLocation("modularwarfare", "textures/modifacation_craft/craft_gui_dev.png");
    public static final ResourceLocation CraftButton = new ResourceLocation("modularwarfare", "textures/modifacation_craft/craftbutton.png");
    public static final ResourceLocation Icon = new ResourceLocation("modularwarfare", "textures/modifacation_craft/icon.png");
    public static final ResourceLocation SlotGun = new ResourceLocation("modularwarfare", "textures/modifacation_craft/slot_gun.png");
    public static final ResourceLocation SlotAmmo = new ResourceLocation("modularwarfare", "textures/modifacation_craft/slot_ammo.png");
    public static final ResourceLocation SlotArmor = new ResourceLocation("modularwarfare", "textures/modifacation_craft/slot_armor.png");
    public static final ResourceLocation SlotAttachment = new ResourceLocation("modularwarfare", "textures/modifacation_craft/slot_attachment.png");
    public static final ResourceLocation SlotIngredients = new ResourceLocation("modularwarfare", "textures/modifacation_craft/slot_ingredients.png");

    public WorkbenchGuns(EntityPlayer player) {
        super(new ContainerWorkbenchGuns(player.inventory));
        this.player = player;
        this.inventoryPlayer = player.inventory;
        this.recipes = JsonLoderCraft.loadRecipesFromJson(ModularWarfare.baseTypes);
        cycleDirectory(0);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.recipes = JsonLoderCraft.loadRecipesFromJson(ModularWarfare.baseTypes);
        if (!recipes.isEmpty()) {
            this.currentRecipe = recipes.get(0);

            currentPage();
        }

        scrollBarX = guiLeft + 237;
        scrollBarY = guiTop + 56;
        scrollBarWidth = 5;

        // Add the text field
        textField = new GuiTextField(0, fontRenderer, guiLeft + 63, guiTop + 82, 50, 17);
        textField.setMaxStringLength(3); // Set maximum length for the text field
        textField.setText(String.valueOf(getCount()));
        textField.setTextColor(Color.WHITE.getRGB());

        refreshButtons();
    }

    public void currentPage (){
        this.maxPages = (int)Math.ceil((double)recipes.size() / RECIPES_PER_PAGE);

        if (maxPages >= 7){
            if (maxPages == 7){
                ratioMaxPages = 2;
            }else {
                this.ratioMaxPages = (int)Math.ceil(maxPages / 2);
            }
        }else {
            this.ratioMaxPages = 1;
        }
    }

    private void refreshButtons(){
        addButton(new TextureButton(2001, this.guiLeft + 119, this.guiTop + 38.5, 48, 16, CraftButton,"Craft"));

        addButton(new TextureButton(2002, this.guiLeft + 251, this.guiTop - 16, 18, 18, SlotGun,""));
        addButton(new TextureButton(2003, this.guiLeft + 251, this.guiTop - -3, 18, 18, SlotAmmo,""));
        addButton(new TextureButton(2004, this.guiLeft + 251, this.guiTop - -22, 18, 18, SlotArmor,""));
        addButton(new TextureButton(2005, this.guiLeft + 251, this.guiTop - -41, 18, 18, SlotAttachment,""));
        addButton(new TextureButton(2006, this.guiLeft + 251, this.guiTop - -60, 18, 18, SlotIngredients,""));

        for (int i = 0; i < RECIPES_PER_PAGE * 6; i++) {
            int recipeIndex = (currentPage - 1) * RECIPES_PER_PAGE + i;
            int maxButtonsPerLine = 5;

            if (recipeIndex >= recipes.size()) {
                break;
            }

            Recipe recipe = recipes.get(recipeIndex);
            ItemStack stack = recipe.getOutput();
            int line = i / maxButtonsPerLine;
            int column = i % maxButtonsPerLine;
            int x = this.guiLeft + 124 + (column * 22);
            int y = this.guiTop + 58 + (line * 20);

            addButton(new TextureButton(i, x, y, 18, 18, Icon,"",true).setItemStack(stack));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 2001) {
            if (!recipes.isEmpty()){
                if (currentRecipe != null && !isCrafting) {
                    boolean canCraft = checkCanCraft();
                    if (canCraft) {
                        startCrafting();
                    }
                }
            }
        } else if (button.id >= 0 && button.id <= recipes.size()) {
            if (isCrafting == false){
                int recipeIndex = (currentPage - 1) * RECIPES_PER_PAGE + button.id;
                currentRecipe = recipes.get(recipeIndex);
            }
        }else if (button.id == 2002){
            if (isCrafting == false){
                cycleDirectory(0);
                initGui();
                currentPage();
                currentPage = 1;
                if (!recipes.isEmpty()){
                    cycleRecipe(0);
                }
                buttonList.clear();
                refreshButtons();
            }
        }else if (button.id == 2003){
            if (isCrafting == false){
                cycleDirectory(1);
                initGui();
                currentPage();
                currentPage = 1;
                if (!recipes.isEmpty()){
                    cycleRecipe(0);
                }
                buttonList.clear();
                refreshButtons();
            }
        }else if (button.id == 2004){
            if (isCrafting == false){
                cycleDirectory(2);
                initGui();
                currentPage();
                currentPage = 1;
                if (!recipes.isEmpty()){
                    cycleRecipe(0);
                }
                buttonList.clear();
                refreshButtons();
            }
        }else if (button.id == 2005){
            if (isCrafting == false){
                cycleDirectory(3);
                initGui();
                currentPage();
                currentPage = 1;
                if (!recipes.isEmpty()){
                    cycleRecipe(0);
                }
                buttonList.clear();
                refreshButtons();
            }
        }else if (button.id == 2006) {
            if (isCrafting == false) {
                cycleDirectory(4);
                initGui();
                currentPage();
                currentPage = 1;
                if (!recipes.isEmpty()) {
                    cycleRecipe(0);
                }
                buttonList.clear();
                refreshButtons();
            }
        }
    }

    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        this.drawDefaultBackground();
        //прочёл значит ты гей 😁
        GlStateManager.color(1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(CraftGui);
        RenderHelperMW.drawTexturedRect(this.guiLeft + -55, this.guiTop + -18, 512, 256);
        RenderHelperMW.renderCenteredTextScaledWithShadow("Ingredients", this.guiLeft + 140,this.guiTop - 13, Color.WHITE.getRGB(), 0.75);
        RenderHelperMW.renderCenteredTextScaled("Craft Workbench", this.guiLeft - 7,this.guiTop - 13, Color.DARK_GRAY.getRGB(), 1);
        RenderHelperMW.renderCenteredTextScaled("Inventory", this.guiLeft - 23,this.guiTop + 93,Color.DARK_GRAY.getRGB(), 1);
        if (recipes.isEmpty()){
            RenderHelperMW.renderCenteredTextScaledWithShadow("<" + "There Are No Crafts" + ">",this.guiLeft + 32, this.guiTop + 0, Color.WHITE.getRGB(),0.9);
        } else {
            RenderHelperMW.renderCenteredTextScaledWithShadow("<" + currentRecipe.getRecipeId() + ">",this.guiLeft + 32, this.guiTop + 0, Color.WHITE.getRGB(),0.9);
        }

        textField.drawTextBox();

        if (isCrafting) {
            int progressBarX = this.guiLeft + 190;
            int progressBarY =  this.guiTop + 40;
            float fillSpeed = (float) (1.06 / (float) currentRecipe.getCraftingTime());
            float currentProgress = (float) craftingProgress * fillSpeed;
            currentProgress = Math.max(0, Math.min(1, currentProgress));
            int fillWidth = (int) (45 * currentProgress);

            drawRect(progressBarX, progressBarY, progressBarX + 45, progressBarY + 10, Color.DARK_GRAY.getRGB());
            drawRect(progressBarX, progressBarY, progressBarX + fillWidth, progressBarY + 10, Color.GRAY.getRGB());
        } else {
            int progressBarX = this.guiLeft + 190;
            int progressBarY =  this.guiTop + 40;

            drawRect(progressBarX, progressBarY, progressBarX + 45, progressBarY + 10, Color.DARK_GRAY.getRGB());
        }

        if (!recipes.isEmpty()){
            renderScrollBar(mouseX,mouseY);
            Render3DItemGui(mouseX,mouseY);
        }
    }

    private boolean checkCanCraft() {
        for (ItemStack ingredient : currentRecipe.getInputItems()) {
            boolean hasIngredient = false;
            int totalAmount = ingredient.getCount() * getCount();
            for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
                ItemStack stack = inventoryPlayer.getStackInSlot(i);
                if (stack != null && OreDictionary.itemMatches(ingredient, stack, false)) {
                    totalAmount -= stack.getCount();
                    if (totalAmount <= 0) {
                        hasIngredient = true;
                        break;
                    }
                }
            }
            if (!hasIngredient) {
                return false;
            }
        }
        return true;
    }

    private void startCrafting() {
        isCrafting = true;
        craftingProgress = 0;
    }

    private void updateCrafting() {
        if (isCrafting) {
            craftingProgress++;

            if (craftingProgress >= currentRecipe.getCraftingTime()) {
                craftItem();
                isCrafting = false;
                craftingProgress = 0;
            }
        }
    }

    private void craftItem() {
        ItemStack craftedItem = currentRecipe.getOutput().copy();

        ModularWarfare.NETWORK.sendToServer(new PacketCraftItem(craftedItem));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        updateCrafting();
    }

    private void cycleRecipe(int increment) {
        if (!recipes.isEmpty()){
            int currentIndex = recipes.indexOf(currentRecipe);
            int newIndex = (currentIndex + increment + recipes.size()) % recipes.size();
            currentRecipe = recipes.get(newIndex);
        }
    }

    private void cycleDirectory(int direction) {
        JsonLoderCraft.currentDirectory = direction;

        if (JsonLoderCraft.currentDirectory < 0) {
            JsonLoderCraft.currentDirectory = MAX_RECIPE_DIRECTORIES - 1;
        } else if (JsonLoderCraft.currentDirectory >= MAX_RECIPE_DIRECTORIES) {
            JsonLoderCraft.currentDirectory = 0;
        }

        this.recipes = JsonLoderCraft.loadRecipesFromJson(ModularWarfare.baseTypes);
        cycleRecipe(0);
    }

    private void renderScrollBar(int mouseX, int mouseY) {
        scrollBarHeight = 122 / ratioMaxPages;

        drawRect(scrollBarX, scrollBarY, scrollBarX + scrollBarWidth, scrollBarY + scrollBarHeight, Color.DARK_GRAY.getRGB());

        if (isScrolling) {
            int deltaY = mouseY - lastMouseY;

            scrollBarY += deltaY;

            int maxY = height - scrollBarHeight + 11 - guiTop;
            scrollBarY = Math.min(Math.max(scrollBarY, guiTop + 56), maxY);

            lastMouseY = mouseY;

            int currentScrollPage = (int)Math.round((double)(scrollBarY - guiTop - 56) / scrollBarHeight) + 1;
            if (currentScrollPage != currentPage) {
                currentPage = currentScrollPage;

                int start = (currentPage - 1) * RECIPES_PER_PAGE;
                int end = Math.min(start + RECIPES_PER_PAGE, recipes.size());

                if (start < recipes.size()) {
                    currentRecipe = recipes.get(start);
                    buttonList.clear();
                    refreshButtons();
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && mouseX >= scrollBarX && mouseX < scrollBarX + scrollBarWidth && mouseY >= scrollBarY && mouseY < scrollBarY + scrollBarHeight) {
            isScrolling = true;
            lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX,mouseY,state);
        if (state == 0) {
            isScrolling = false;
        }
    }

    public static int getCount() {
        if(textField.getText().length() == 0)
            return 1;

        int quantity = Integer.parseInt(WorkbenchGuns.textField.getText());

        return quantity;
    }

    private void Render3DItemGui(final int mouseX, final int mouseY){
        if (currentRecipe != null) {
            // Render ingredient (2D icon)
            int startX = guiLeft + 105;
            for (ItemStack ingredient : currentRecipe.getInputItems()) {
                startX += 17;

                GlStateManager.pushMatrix();
                GlStateManager.translate(startX, this.guiTop - 4, 300);

                // не ну ты опять это читаешь так и быть ты не гей ТЫ УЛЬТРО СУПЕР ГЕЙ 🤩
                int ingredientCountInInventory = 0;
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.isItemEqual(ingredient)) {
                        ingredientCountInInventory += stack.getCount();
                    }
                }
                
                if (ingredientCountInInventory >= ingredient.getCount() * getCount()) {
                    RenderHelperMW.renderCenteredTextScaledWithShadow(String.valueOf(ingredient.getCount() * getCount()), 12, 8, Color.WHITE.getRGB(), 0.8);
                }else {
                    RenderHelperMW.renderCenteredTextScaledWithShadow(String.valueOf(ingredient.getCount() * getCount()), 12, 8, Color.RED.getRGB(), 0.8);
                }

                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(startX + -0.3, this.guiTop - 4, 0);
                GlStateManager.scale(0.9, 0.9, 1);
                RenderHelperMW.renderItemStack(ingredient, 0, 0, 0, true);
                GlStateManager.popMatrix();
            }

            ItemStack recipeOutput = currentRecipe.getOutput();
            //Render 3d Items || Render 2d items
            if (!recipeOutput.isEmpty() && recipeOutput.getItem() instanceof ItemGun) {
                GL11.glPushMatrix();
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glTranslatef((this.guiLeft + 31), (this.guiTop + 36), 100);

                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(mouseY * 0.015F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(mouseX * 0.015F, 0.0F, 1.0F, 0.0F);

                GL11.glTranslatef(0,-20,0);

                GL11.glRotatef(0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(-80.0F, 80.0F, 80.0F);

                BaseType type = ((BaseItem) recipeOutput.getItem()).baseType;
                if (((GunType) type).animationType == WeaponAnimationType.BASIC) {
                    if (ClientRenderHooks.customRenderers[type.id] != null) {
                        GunType gunType = (GunType) type;
                        ModelGun model = (ModelGun) gunType.model;
                        GunRenderConfig config = model.config;
                        GL11.glPushMatrix();
                        GL11.glTranslatef(config.extra.craftTranslate.x, config.extra.craftTranslate.y, config.extra.craftTranslate.z);
                        GL11.glScalef(config.extra.scaleCraft, config.extra.scaleCraft, config.extra.scaleCraft);
                        GlStateManager.translate(0F,0F,0F);
                        ClientProxy.gunStaticRenderer.renderItem(CustomItemRenderType.ENTITY, EnumHand.MAIN_HAND, recipeOutput, ((ItemGun) recipeOutput.getItem()).type, new Object[0]);
                        GL11.glPopMatrix();
                    }
                } else if (((GunType) type).animationType == WeaponAnimationType.ENHANCED) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableRescaleNormal();
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();

                    GunType gunType = (GunType) type;
                    EnhancedModel model = type.enhancedModel;

                    GunEnhancedRenderConfig config = (GunEnhancedRenderConfig) gunType.enhancedModel.config;
                    //тут есть проблема при открытие гуи крафтов с простым оружием происходит краш null
                    model.updateAnimation((float) config.animations.get(AnimationType.DEFAULT).getStartTime(config.FPS));

                    ItemStack bulletStack = recipeOutput;
                    HashSet<String> exceptParts = new HashSet<String>();
                    {
                        exceptParts.addAll(config.defaultHidePart);
                        exceptParts.addAll(ClientProxy.gunEnhancedRenderer.DEFAULT_EXCEPT);

                        for (AttachmentPresetEnum attachment : AttachmentPresetEnum.values()) {
                            ItemStack itemStack = GunType.getAttachment(bulletStack, attachment);
                            if (itemStack != null && itemStack.getItem() != Items.AIR) {
                                AttachmentType attachmentType = ((ItemAttachment) itemStack.getItem()).type;
                                String binding = "gunModel";
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
                            ItemStack itemStack = GunType.getAttachment(bulletStack, attachment);
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

                        exceptParts.addAll(ClientProxy.gunEnhancedRenderer.DEFAULT_EXCEPT);
                    }

                    HashSet<String> exceptPartsRendering=exceptParts;

                    GlStateManager.translate(-0.06, 0.38, -0.02);

                    GL11.glRotatef(0F, 0F, 1F, 0F);
                    GL11.glRotatef(0F, 0F, 0F, 1F);
                    GL11.glTranslatef(0F, 0F, 0F);
                    GL11.glScalef(1 / 14F, 1 / 14F, 1 / 14F);

                    GL11.glTranslatef(config.extra.craftTranslate.x, config.extra.craftTranslate.y, config.extra.craftTranslate.z);
                    GL11.glScalef(config.extra.scaleCraft, config.extra.scaleCraft, config.extra.scaleCraft);

                    int skinId = 0;
                    if (bulletStack.hasTagCompound()) {
                        if (bulletStack.getTagCompound().hasKey("skinId")) {
                            skinId = bulletStack.getTagCompound().getInteger("skinId");
                        }
                    }
                    String gunPath = skinId > 0 ? gunType.modelSkins[skinId].getSkin() : gunType.modelSkins[0].getSkin();
                    ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);

                    HashSet<String> partsToIgnore = new HashSet<>(ClientProxy.gunEnhancedRenderer.DEFAULT_EXCEPT);
                    partsToIgnore.addAll(config.defaultHidePart);

                    //тут был код но теперь его нет так как я еврей 🎅

                    WeaponFireMode fireMode = GunType.getFireMode(bulletStack);
                    if (fireMode == WeaponFireMode.SEMI) {
                        model.renderPart("selector_semi");
                    } else if (fireMode == WeaponFireMode.FULL) {
                        model.renderPart("selector_full");
                    } else if (fireMode == WeaponFireMode.BURST) {
                        model.renderPart("selector_brust");
                    } else if (fireMode == WeaponFireMode.FUSE) {
                        model.renderPart("selector_fuse");
                    }

                    ClientProxy.gunEnhancedRenderer.bindTexture("guns", gunPath);
                    model.renderPartExcept(exceptPartsRendering);

                    GlStateManager.popMatrix();

                }
                GL11.glPopMatrix();
            }else if (!recipeOutput.isEmpty() && recipeOutput.getItem() instanceof ItemAttachment){
                AttachmentType type = ((ItemAttachment) recipeOutput.getItem()).type;
                ModelAttachment model = (ModelAttachment) type.model;
                AttachmentRenderConfig renderConfig = model.config;

                GL11.glPushMatrix();
                //освещение
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();

                //первоночялная настройка позицыи
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glTranslatef((this.guiLeft + 0), (this.guiTop + 48), 100);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);

                GL11.glRotatef(mouseY * 0.015F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(mouseX * 0.015F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(-160.0F, 160.0F, 160.0F);

                //не ну ты уже супер ультра гей хватит с тебя
                GL11.glTranslatef(renderConfig.extra.craftTranslate.x, renderConfig.extra.craftTranslate.y, renderConfig.extra.craftTranslate.z);
                GL11.glScalef(renderConfig.extra.craftScale, renderConfig.extra.craftScale, renderConfig.extra.craftScale);

                float f = 1F / 16F;
                int skinId = 0;
                String path = skinId > 0 ? "skins/" + type.modelSkins[skinId].getSkin() : type.modelSkins[0].getSkin();
                ClientProxy.attachmentRenderer.bindTexture("attachments", path);
                model.renderAttachment(f);
                GL11.glPopMatrix();
            } else if (!recipeOutput.isEmpty() && recipeOutput.getItem() instanceof Item) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.guiLeft + 20, this.guiTop + 28, 0);
                GlStateManager.scale(1.5, 1.5, 1);
                RenderHelperMW.renderItemStack(recipeOutput, 0, 0, 0, true);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.textField.textboxKeyTyped(typedChar, keyCode); //повернулся *** надулся 🤡
        try {
            Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            // Handle the case where the input is not a valid number
        }
    }
}