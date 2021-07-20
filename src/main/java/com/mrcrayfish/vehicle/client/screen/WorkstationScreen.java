package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractPoweredRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.render.CachedVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.RecipeType;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCraftVehicle;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkstationScreen extends ContainerScreen<WorkstationContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/workstation.png");
    private static CachedVehicle cachedVehicle;
    private static CachedVehicle prevCachedVehicle;
    private static int currentVehicle = 0;
    private static boolean showRemaining = false;

    private final List<EntityType<?>> vehicleTypes;
    private final List<MaterialItem> materials;
    private List<MaterialItem> filteredMaterials;
    private final PlayerInventory playerInventory;
    private final WorkstationTileEntity workstation;
    private Button btnCraft;
    private CheckBox checkBoxMaterials;
    private boolean validEngine;
    private boolean transitioning;
    private int vehicleScale = 30;
    private int prevVehicleScale = 30;

    public WorkstationScreen(WorkstationContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.workstation = container.getTileEntity();
        this.imageWidth = 256;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 93;
        this.materials = new ArrayList<>();
        this.vehicleTypes = this.getVehicleTypes(playerInventory.player.level);
        this.vehicleTypes.sort(Comparator.comparing(type -> type.getRegistryName().getPath()));
    }

    private List<EntityType<?>> getVehicleTypes(World world)
    {
        return world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == RecipeType.WORKSTATION).map(recipe -> (WorkstationRecipe) recipe).map(WorkstationRecipe::getVehicle).filter(entityType -> !Config.SERVER.disabledVehicles.get().contains(Objects.requireNonNull(entityType.getRegistryName()).toString())).collect(Collectors.toList());
    }

    @Override
    public void init()
    {
        super.init();

        this.addButton(new Button(this.leftPos + 9, this.topPos + 18, 15, 20, new StringTextComponent("<"), button -> {
            this.loadVehicle(Math.floorMod(currentVehicle - 1,  this.vehicleTypes.size()));
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));

        this.addButton(new Button(this.leftPos + 153, this.topPos + 18, 15, 20, new StringTextComponent(">"), button -> {
            this.loadVehicle(Math.floorMod(currentVehicle + 1,  this.vehicleTypes.size()));
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));

        this.btnCraft = this.addButton(new Button(this.leftPos + 172, this.topPos + 6, 97, 20, new TranslationTextComponent("gui.vehicle.craft"), button -> {
            ResourceLocation registryName = this.vehicleTypes.get(currentVehicle).getRegistryName();
            Objects.requireNonNull(registryName, "Vehicle registry name must not be null!");
            PacketHandler.instance.sendToServer(new MessageCraftVehicle(registryName.toString(), this.workstation.getBlockPos()));
        }));

        this.btnCraft.active = false;
        this.checkBoxMaterials = this.addButton(new CheckBox(this.leftPos + 172, this.topPos + 51,  new TranslationTextComponent("gui.vehicle.show_remaining")));
        this.checkBoxMaterials.setToggled(WorkstationScreen.showRemaining);
        this.loadVehicle(currentVehicle);
    }

    @Override
    public void tick()
    {
        super.tick();

        this.validEngine = true;

        for(MaterialItem material : this.materials)
        {
            material.tick();
        }

        boolean canCraft = true;
        for(MaterialItem material : this.materials)
        {
            if(!material.isEnabled())
            {
                canCraft = false;
                break;
            }
        }

        if(cachedVehicle.getRenderer() instanceof AbstractPoweredRenderer)
        {
            AbstractPoweredRenderer<?> poweredRenderer = (AbstractPoweredRenderer<?>) cachedVehicle.getRenderer();
            VehicleProperties properties = cachedVehicle.getProperties();
            if(properties.getEngineType() != EngineType.NONE)
            {
                ItemStack engine = this.workstation.getItem(1);
                if(!engine.isEmpty() && engine.getItem() instanceof EngineItem)
                {
                    EngineItem engineItem = (EngineItem) engine.getItem();
                    IEngineType engineType = engineItem.getEngineType();
                    if(properties.getEngineType() == engineType)
                    {
                        poweredRenderer.setEngineStack(engine);
                    }
                    else
                    {
                        canCraft = false;
                        this.validEngine = false;
                        poweredRenderer.setEngineStack(ItemStack.EMPTY);
                    }
                }
                else
                {
                    canCraft = false;
                    this.validEngine = false;
                    poweredRenderer.setEngineStack(ItemStack.EMPTY);
                }
            }

            if(cachedVehicle.getProperties().canChangeWheels())
            {
                ItemStack wheels = this.workstation.getItem(2);
                if(!wheels.isEmpty() && wheels.getItem() instanceof WheelItem)
                {
                    poweredRenderer.setWheelStack(wheels);
                }
                else
                {
                    poweredRenderer.setWheelStack(ItemStack.EMPTY);
                    canCraft = false;
                }
            }
        }
        this.btnCraft.active = canCraft;

        this.prevVehicleScale = this.vehicleScale;
        if(this.transitioning)
        {
            if(this.vehicleScale > 0)
            {
                this.vehicleScale = Math.max(0, this.vehicleScale - 6);
            }
            else
            {
                this.transitioning = false;
            }
        }
        else if(this.vehicleScale < 30)
        {
            this.vehicleScale = Math.min(30, this.vehicleScale + 6);
        }

        this.updateVehicleColor();
    }

    private void updateVehicleColor()
    {
        if(cachedVehicle.getProperties().isColored())
        {
            AbstractVehicleRenderer<?> renderer = cachedVehicle.getRenderer();
            ItemStack dyeStack = this.workstation.getItem(0);
            if(dyeStack.getItem() instanceof DyeItem)
            {
                renderer.setColor(((DyeItem) dyeStack.getItem()).getDyeColor().getColorValue());
            }
            else
            {
                renderer.setColor(VehicleEntity.DYE_TO_COLOR[0]);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
        WorkstationScreen.showRemaining = this.checkBoxMaterials.isToggled();
        return result;
    }

    @SuppressWarnings({"unchecked"})
    private void loadVehicle(int index)
    {
        prevCachedVehicle = cachedVehicle;
        cachedVehicle = new CachedVehicle(this.vehicleTypes.get(index));
        currentVehicle = index;

        AbstractVehicleRenderer<?> renderer = cachedVehicle.getRenderer();
        if(renderer instanceof AbstractLandVehicleRenderer<?>)
        {
            ((AbstractLandVehicleRenderer<?>) renderer).setEngineStack(ItemStack.EMPTY);
            ((AbstractLandVehicleRenderer<?>) renderer).setWheelStack(ItemStack.EMPTY);
        }

        this.materials.clear();

        WorkstationRecipe recipe = WorkstationRecipes.getRecipe(cachedVehicle.getType(), this.minecraft.level);
        if(recipe != null)
        {
            for(int i = 0; i < recipe.getMaterials().size(); i++)
            {
                MaterialItem item = new MaterialItem(recipe.getMaterials().get(i));
                item.updateEnabledState();
                this.materials.add(item);
            }
        }

        if(Config.CLIENT.workstationAnimation.get() && prevCachedVehicle != null && prevCachedVehicle.getType() != cachedVehicle.getType())
        {
            this.transitioning = true;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        for(int i = 0; i < filteredMaterials.size(); i++)
        {
            int itemX = startX + 172;
            int itemY = startY + i * 19 + 63;
            if(CommonUtils.isMouseWithin(mouseX, mouseY, itemX, itemY, 80, 19))
            {
                MaterialItem materialItem = this.filteredMaterials.get(i);
                if(materialItem != MaterialItem.EMPTY)
                {
                    this.renderTooltip(matrixStack, materialItem.getDisplayStack(), mouseX, mouseY);
                }
            }
        }

        VehicleProperties properties = cachedVehicle.getProperties();
        if(properties.isColored())
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.optional").withStyle(TextFormatting.AQUA), new TranslationTextComponent("vehicle.tooltip.paint_color").withStyle(TextFormatting.GRAY)), startX, startY, 172, 29, mouseX, mouseY, 0);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.paint_color"), new TranslationTextComponent("vehicle.tooltip.not_applicable").withStyle(TextFormatting.GRAY)), startX, startY, 172, 29, mouseX, mouseY, 0);
        }

        if(properties.getEngineType() != EngineType.NONE)
        {
            TranslationTextComponent engineName = properties.getEngineType().getEngineName();
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.required").withStyle(TextFormatting.RED), engineName), startX, startY, 192, 29, mouseX, mouseY, 1);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.engine"), new TranslationTextComponent("vehicle.tooltip.not_applicable").withStyle(TextFormatting.GRAY)), startX, startY, 192, 29, mouseX, mouseY, 1);
        }

        if(properties.canChangeWheels())
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.required").withStyle(TextFormatting.RED), new TranslationTextComponent("vehicle.tooltip.wheels")), startX, startY, 212, 29, mouseX, mouseY, 2);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(new TranslationTextComponent("vehicle.tooltip.wheels"), new TranslationTextComponent("vehicle.tooltip.not_applicable").withStyle(TextFormatting.GRAY)), startX, startY, 212, 29, mouseX, mouseY, 2);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        /* Fixes partial ticks to use percentage from 0 to 1 */
        partialTicks = this.minecraft.getFrameTime();

        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;

        RenderSystem.enableBlend();

        this.minecraft.getTextureManager().bind(GUI);
        this.blit(matrixStack, startX, startY, 0, 0, 173, 184);
        blit(matrixStack, startX + 173, startY, 78, 184, 173, 0, 1, 184, 256, 256);
        this.blit(matrixStack, startX + 251, startY, 174, 0, 24, 184);
        this.blit(matrixStack, startX + 256, startY + 64, 12, 241, 12, 15);

        /* Slots */
        VehicleProperties properties = cachedVehicle.getProperties();
        this.drawSlot(matrixStack, startX, startY, 172, 29, 164, 184, 0, false, properties.isColored());
        boolean needsEngine = properties.getEngineType() != EngineType.NONE;
        this.drawSlot(matrixStack, startX, startY, 192, 29, 164, 200, 1, !this.validEngine, needsEngine);
        boolean needsWheels = properties.canChangeWheels();
        this.drawSlot(matrixStack, startX, startY, 212, 29, 164, 216, 2, needsWheels && this.workstation.getItem(2).isEmpty(), needsWheels);

        drawCenteredString(matrixStack, this.font, cachedVehicle.getType().getDescription(), startX + 88, startY + 22, Color.WHITE.getRGB());

        this.filteredMaterials = this.getMaterials();
        for(int i = 0; i < this.filteredMaterials.size(); i++)
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(GUI);

            MaterialItem materialItem = this.filteredMaterials.get(i);
            ItemStack stack = materialItem.getDisplayStack();
            if(!stack.isEmpty())
            {
                RenderHelper.turnOff();
                if(materialItem.isEnabled())
                {
                    this.blit(matrixStack, startX + 172, startY + i * 19 + 63, 0, 184, 80, 19);
                }
                else
                {
                    this.blit(matrixStack, startX + 172, startY + i * 19 + 63, 0, 222, 80, 19);
                }

                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                String name = stack.getHoverName().getString();
                if(this.font.width(name) > 55)
                {
                    name = this.font.plainSubstrByWidth(stack.getHoverName().getString(), 50).trim() + "...";
                }
                this.font.draw(matrixStack, name, startX + 172 + 22, startY + i * 19 + 6 + 63, Color.WHITE.getRGB());

                Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, startX + 172 + 2, startY + i * 19 + 1 + 63);

                if(this.checkBoxMaterials.isToggled())
                {
                    int count = InventoryUtil.getItemStackAmount(this.minecraft.player, stack);
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }

                Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(this.font, stack, startX + 172 + 2, startY + i * 19 + 1 + 63, null);
            }
        }

        this.drawVehicle(startX + 88, startY + 90, partialTicks);
    }

    private void drawVehicle(int x, int y, float partialTicks)
    {
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, 1050.0F);
        RenderSystem.scalef(-1.0F, -1.0F, -1.0F);

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);

        float scale = this.prevVehicleScale + (this.vehicleScale - this.prevVehicleScale) * partialTicks;
        matrixStack.scale(scale, scale, scale);

        Quaternion quaternion = Axis.POSITIVE_X.rotationDegrees(-5F);
        Quaternion quaternion1 = Axis.POSITIVE_Y.rotationDegrees(-(this.minecraft.player.tickCount + partialTicks));
        quaternion.mul(quaternion1);
        matrixStack.mulPose(quaternion);

        CachedVehicle transitionVehicle = this.transitioning ? prevCachedVehicle : cachedVehicle;

        PartPosition position = transitionVehicle.getProperties().getDisplayPosition();
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees((float) position.getRotZ()));
        matrixStack.translate(position.getX(), position.getY(), position.getZ());

        EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        renderManager.setRenderShadow(false);
        renderManager.overrideCameraOrientation(quaternion);
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> transitionVehicle.getRenderer().setupTransformsAndRender(null, matrixStack, renderTypeBuffer, partialTicks, 15728880));
        renderTypeBuffer.endBatch();
        renderManager.setRenderShadow(true);

        matrixStack.popPose();

        RenderSystem.popMatrix();
    }

    private void drawSlot(MatrixStack matrixStack, int startX, int startY, int x, int y, int iconX, int iconY, int slot, boolean required, boolean applicable)
    {
        int textureOffset = required ? 18 : 0;
        this.blit(matrixStack, startX + x, startY + y, 198, 20 + textureOffset, 18, 18);
        if(this.workstation.getItem(slot).isEmpty())
        {
            if(applicable)
            {
                this.blit(matrixStack, startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), iconY, 16, 16);
            }
            else
            {
                this.blit(matrixStack, startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), 232, 16, 16);
            }
        }
    }

    private void drawSlotTooltip(MatrixStack matrixStack, List<ITextComponent> text, int startX, int startY, int x, int y, int mouseX, int mouseY, int slot)
    {
        if(this.workstation.getItem(slot).isEmpty())
        {
            if(CommonUtils.isMouseWithin(mouseX, mouseY, startX + x, startY + y, 18, 18))
            {
                this.renderTooltip(matrixStack, Lists.transform(text, ITextComponent::getVisualOrderText), mouseX, mouseY);
            }
        }
    }

    private List<MaterialItem> getMaterials()
    {
        List<MaterialItem> materials = NonNullList.withSize(7, MaterialItem.EMPTY);
        List<MaterialItem> filteredMaterials = this.materials.stream().filter(materialItem -> this.checkBoxMaterials.isToggled() ? !materialItem.isEnabled() : materialItem != MaterialItem.EMPTY).collect(Collectors.toList());
        for(int i = 0; i < filteredMaterials.size() && i < materials.size(); i++)
        {
            materials.set(i, filteredMaterials.get(i));
        }
        return materials;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(matrixStack, this.playerInventory.getDisplayName().getString(), this.inventoryLabelX, this.inventoryLabelY, 4210752);
    }

    public static class MaterialItem
    {
        public static final MaterialItem EMPTY = new MaterialItem();

        private long lastTime = System.currentTimeMillis();
        private int displayIndex;
        private boolean enabled = false;
        private WorkstationIngredient ingredient = null;
        private final List<ItemStack> displayStacks = new ArrayList<>();

        public MaterialItem() {}

        public MaterialItem(WorkstationIngredient ingredient)
        {
            this.ingredient = ingredient;
            Stream.of(ingredient.getItems()).forEach(stack -> {
                ItemStack displayStack = stack.copy();
                displayStack.setCount(ingredient.getCount());
                this.displayStacks.add(displayStack);
            });
        }

        public WorkstationIngredient getIngredient()
        {
            return this.ingredient;
        }

        public void tick()
        {
            if(this.ingredient == null)
                return;

            this.updateEnabledState();
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastTime >= 1000)
            {
                this.displayIndex = (this.displayIndex + 1) % this.displayStacks.size();
                this.lastTime = currentTime;
            }
        }

        public ItemStack getDisplayStack()
        {
            return this.ingredient != null ? this.displayStacks.get(this.displayIndex) : ItemStack.EMPTY;
        }

        public void updateEnabledState()
        {
            if(this.ingredient != null)
            {
                this.enabled = InventoryUtil.hasWorkstationIngredient(Minecraft.getInstance().player, this.ingredient);
            }
        }

        public boolean isEnabled()
        {
            return this.ingredient == null || this.enabled;
        }
    }
}
