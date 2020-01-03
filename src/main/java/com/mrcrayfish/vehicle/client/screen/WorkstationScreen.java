package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.trailer.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class WorkstationScreen extends ContainerScreen<WorkstationContainer>
{
    private static final ImmutableList<Class<? extends VehicleEntity>> VEHICLES;
    public static final ImmutableMap<Class<? extends VehicleEntity>, PartPosition> DISPLAY_PROPERTIES;

    static
    {
        ImmutableMap.Builder<Class<? extends VehicleEntity>, PartPosition> builder = ImmutableMap.builder();
        builder.put(AluminumBoatEntity.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F));
        builder.put(ATVEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(BumperCarEntity.class, new PartPosition(0.0F, 0.0F, -0.4F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(DuneBuggyEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.75F));
        builder.put(GoKartEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(GolfCartEntity.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F));
        builder.put(JetSkiEntity.class, new PartPosition(0.0F, 0.0F, -0.45F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(LawnMowerEntity.class, new PartPosition(0.0F, 0.0F, -0.7F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(MiniBikeEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(MopedEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(OffRoaderEntity.class, new PartPosition(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 1.0F));
        builder.put(ShoppingCartEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.45F));
        builder.put(SmartCarEntity.class, new PartPosition(0.0F, 0.0F, -0.2F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(SpeedBoatEntity.class, new PartPosition(0.0F, 0.0F, -0.65F, 0.0F, 0.0F, 0.0F, 1.25F));
        builder.put(SportsPlaneEntity.class, new PartPosition(0.0F, 0.0F, 0.35F, 0.0F, 0.0F, 0.0F, 0.85F));
        builder.put(TractorEntity.class, new PartPosition(0.0F, 0.0F, -0.2F, 0.0F, 0.0F, 0.0F, 1.25F));
        builder.put(VehicleEntityTrailer.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(StorageTrailerEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(SeederTrailerEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(FertilizerTrailerEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(FluidTrailerEntity.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));

        if(ModList.get().isLoaded("cfm"))
        {
            builder.put(BathEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
            builder.put(CouchEntity.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
            builder.put(SofacopterEntity.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F));
        }

        DISPLAY_PROPERTIES = builder.build();
        VEHICLES = DISPLAY_PROPERTIES.keySet().asList();
    }

    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/workstation.png");

    private List<MaterialItem> materials;
    private List<MaterialItem> filteredMaterials;
    private static int currentVehicle = 0;
    private static int prevCurrentVehicle = 0;
    private static boolean showRemaining = false;
    private VehicleEntity[] cachedVehicle;
    private PlayerInventory playerInventory;
    private WorkstationTileEntity workstation;
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
        this.xSize = 289;
        this.ySize = 202;
        this.materials = new ArrayList<>();
        this.cachedVehicle = new VehicleEntity[VEHICLES.size()];
    }

    @Override
    public void init()
    {
        super.init();
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        this.addButton(new Button(startX, startY, 15, 20, "<", button ->
        {
            if(currentVehicle - 1 < 0)
            {
                this.loadVehicle(VEHICLES.size() - 1);
            }
            else
            {
                this.loadVehicle(currentVehicle - 1);
            }
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));
        this.addButton(new Button(startX + 161, startY, 15, 20, ">", button ->
        {
            if(currentVehicle + 1 >= VEHICLES.size())
            {
                this.loadVehicle(0);
            }
            else
            {
                this.loadVehicle(currentVehicle + 1);
            }
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));
        this.btnCraft = this.addButton(new Button(startX + 186, startY + 6, 97, 20, "Craft", button ->
        {
            ResourceLocation registryName = this.cachedVehicle[currentVehicle].getType().getRegistryName();
            if(registryName != null)
            {
                PacketHandler.instance.sendToServer(new MessageCraftVehicle(registryName.toString(), this.workstation.getPos()));
            }
        }));
        this.btnCraft.active = false;
        this.checkBoxMaterials = this.addButton(new CheckBox(186, 51, "Show Remaining"));
        this.checkBoxMaterials.setToggled(WorkstationScreen.showRemaining);
        this.loadVehicle(currentVehicle);
    }

    @Override
    public void tick()
    {
        super.tick();

        validEngine = true;

        for(MaterialItem material : materials)
        {
            material.update();
        }

        boolean canCraft = true;
        for(MaterialItem material : materials)
        {
            if(!material.isEnabled())
            {
                canCraft = false;
                break;
            }
        }

        if(cachedVehicle[currentVehicle] instanceof PoweredVehicleEntity)
        {
            PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) cachedVehicle[currentVehicle];
            if(entityPoweredVehicle.getEngineType() != EngineType.NONE)
            {
                ItemStack engine = workstation.getStackInSlot(1);
                if(!engine.isEmpty() && engine.getItem() instanceof EngineItem)
                {
                    EngineItem engineItem = (EngineItem) engine.getItem();
                    EngineType engineType = engineItem.getEngineType();
                    if(entityPoweredVehicle.getEngineType() != engineType)
                    {
                        canCraft = false;
                        validEngine = false;
                        entityPoweredVehicle.setEngine(false);
                    }
                    else
                    {
                        entityPoweredVehicle.setEngineTier(engineItem.getEngineTier());
                        entityPoweredVehicle.setEngine(true);
                        entityPoweredVehicle.notifyDataManagerChange(PoweredVehicleEntity.ENGINE_TIER);
                    }
                }
                else
                {
                    canCraft = false;
                    validEngine = false;
                    entityPoweredVehicle.setEngine(false);
                }
            }

            if(entityPoweredVehicle.canChangeWheels())
            {
                ItemStack wheels = workstation.getStackInSlot(2);
                if(!wheels.isEmpty() && wheels.getItem() instanceof WheelItem)
                {
                    if(wheels.getTag() != null)
                    {
                        CompoundNBT tagCompound = wheels.getTag();
                        if(tagCompound.contains("Color", Constants.NBT.TAG_INT))
                        {
                            entityPoweredVehicle.setWheelColor(tagCompound.getInt("Color"));
                        }
                    }
                    WheelItem wheelItem = (WheelItem) wheels.getItem();
                    entityPoweredVehicle.setWheelType(wheelItem.getWheelType());
                    entityPoweredVehicle.setWheels(true);
                    entityPoweredVehicle.notifyDataManagerChange(PoweredVehicleEntity.WHEEL_COLOR);
                }
                else
                {
                    entityPoweredVehicle.setWheels(false);
                    canCraft = false;
                }
            }
        }
        btnCraft.active = canCraft;

        prevVehicleScale = vehicleScale;
        if(transitioning)
        {
            if(vehicleScale > 0)
            {
                vehicleScale = Math.max(0, vehicleScale - 6);
            }
            else
            {
                transitioning = false;
            }
        }
        else if(vehicleScale < 30)
        {
            vehicleScale = Math.min(30, vehicleScale + 6);
        }

        if(cachedVehicle[currentVehicle].canBeColored())
        {
            if(!workstation.getStackInSlot(0).isEmpty())
            {
                ItemStack stack = workstation.getStackInSlot(0);
                if(stack.getItem() instanceof DyeItem)
                {
                    DyeItem dyeItem = (DyeItem) stack.getItem();
                    cachedVehicle[currentVehicle].setColor(dyeItem.getDyeColor().getColorValue());
                }
                else
                {
                    cachedVehicle[currentVehicle].setColor(VehicleEntity.DYE_TO_COLOR[0]);
                }
            }
            else
            {
                cachedVehicle[currentVehicle].setColor(VehicleEntity.DYE_TO_COLOR[0]);
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

    private void loadVehicle(int index)
    {
        prevCurrentVehicle = currentVehicle;

        try
        {
            if(this.cachedVehicle[index] == null)
            {
                VehicleEntity vehicle = VEHICLES.get(index).getDeclaredConstructor(World.class).newInstance(Minecraft.getInstance().world);
                java.util.List<EntityDataManager.DataEntry<?>> entryList = vehicle.getDataManager().getAll();
                if(entryList != null)
                {
                    entryList.forEach(dataEntry -> vehicle.notifyDataManagerChange(dataEntry.getKey()));
                }

                if(vehicle instanceof PoweredVehicleEntity)
                {
                    ((PoweredVehicleEntity) vehicle).setEngine(false);
                    ((PoweredVehicleEntity) vehicle).setWheels(false);
                }
                this.cachedVehicle[index] = vehicle;
            }
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }

        this.materials.clear();
        VehicleRecipes.VehicleRecipe recipe = VehicleRecipes.getRecipe(this.cachedVehicle[index].getType());
        for(int i = 0; i < recipe.getMaterials().size(); i++)
        {
            MaterialItem item = new MaterialItem(recipe.getMaterials().get(i));
            item.update();
            materials.add(item);
        }

        currentVehicle = index;

        if(Config.CLIENT.workstationAnimation.get() && prevCurrentVehicle != currentVehicle)
        {
            this.transitioning = true;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        for(int i = 0; i < filteredMaterials.size(); i++)
        {
            int itemX = startX + 186;
            int itemY = startY + i * 19 + 6 + 57;
            if(CommonUtils.isMouseWithin(mouseX, mouseY, itemX, itemY, 80, 19))
            {
                MaterialItem materialItem = filteredMaterials.get(i);
                if(!materialItem.getStack().isEmpty())
                {
                    this.renderTooltip(materialItem.getStack(), mouseX, mouseY);
                }
            }
        }

        VehicleEntity vehicle = cachedVehicle[currentVehicle];
        if(vehicle.canBeColored())
        {
            this.drawSlotTooltip(Lists.newArrayList(TextFormatting.AQUA + I18n.format("vehicle.tooltip.optional"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.paint_color")), startX, startY, 186, 29, mouseX, mouseY, 0);
        }
        else
        {
            this.drawSlotTooltip(Lists.newArrayList(I18n.format("vehicle.tooltip.paint_color"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.not_applicable")), startX, startY, 186, 29, mouseX, mouseY, 0);
        }

        if(vehicle instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) vehicle).getEngineType() != EngineType.NONE)
        {
            String engineName = ((PoweredVehicleEntity) vehicle).getEngineType().getEngineName();
            this.drawSlotTooltip(Lists.newArrayList(TextFormatting.RED + I18n.format("vehicle.tooltip.required"), TextFormatting.GRAY + engineName), startX, startY, 206, 29, mouseX, mouseY, 1);
        }
        else
        {
            this.drawSlotTooltip(Lists.newArrayList(I18n.format("vehicle.tooltip.engine"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.not_applicable")), startX, startY, 206, 29, mouseX, mouseY, 1);
        }

        if(vehicle instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) vehicle).canChangeWheels())
        {
            this.drawSlotTooltip(Lists.newArrayList(TextFormatting.RED + I18n.format("vehicle.tooltip.required"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.wheels")), startX, startY, 226, 29, mouseX, mouseY, 2);
        }
        else
        {
            this.drawSlotTooltip(Lists.newArrayList(I18n.format("vehicle.tooltip.wheels"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.not_applicable")), startX, startY, 226, 29, mouseX, mouseY, 2);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        /* Fixes partial ticks to use percentage from 0 to 1 */
        partialTicks = this.minecraft.getRenderPartialTicks();

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        RenderSystem.enableBlend();

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(startX, startY + 80, 0, 134, 176, 122);
        this.blit(startX + 180, startY, 176, 54, 6, 208);
        this.blit(startX + 186, startY, 182, 54, 57, 208);
        this.blit(startX + 186 + 57, startY, 220, 54, 23, 208);
        this.blit(startX + 186 + 57 + 23, startY, 220, 54, 3, 208);
        this.blit(startX + 186 + 57 + 23 + 3, startY, 236, 54, 20, 208);

        /* Slots */
        this.drawSlot(startX, startY, 186, 29, 80, 0, 0, false, cachedVehicle[currentVehicle].canBeColored());
        boolean needsEngine = cachedVehicle[currentVehicle] instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) cachedVehicle[currentVehicle]).getEngineType() != EngineType.NONE;
        this.drawSlot(startX, startY, 206, 29, 80, 16, 1, !validEngine, needsEngine);
        boolean needsWheels = cachedVehicle[currentVehicle] instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) cachedVehicle[currentVehicle]).canChangeWheels();
        this.drawSlot(startX, startY, 226, 29, 80, 32, 2, needsWheels && workstation.getStackInSlot(2).isEmpty(), needsWheels);

        //this.checkBoxMaterials.draw(mc, guiLeft, guiTop); TODO I dont need this?

        this.drawCenteredString(this.font, this.cachedVehicle[currentVehicle].getName().getFormattedText(), startX + 88, startY + 6, Color.WHITE.getRGB());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.func_227861_a_(startX + 88, startY + 90, 100);

        float scale = this.prevVehicleScale + (this.vehicleScale - this.prevVehicleScale) * partialTicks;
        matrixStack.func_227862_a_(scale, -scale, scale);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(5F));
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(this.minecraft.player.ticksExisted + partialTicks));

        int vehicleIndex = transitioning ? prevCurrentVehicle : currentVehicle;
        Class<? extends VehicleEntity> clazz = cachedVehicle[vehicleIndex].getClass();
        PartPosition position = DISPLAY_PROPERTIES.get(clazz);
        if(position != null)
        {
            //Apply vehicle rotations, translations, and scale
            matrixStack.func_227862_a_((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_((float) position.getRotX()));
            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_((float) position.getRotY()));
            matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_((float) position.getRotZ()));
            matrixStack.func_227861_a_(position.getX(), position.getY(), position.getZ());
        }
        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper((EntityType<? extends VehicleEntity>) this.cachedVehicle[vehicleIndex].getType());
        if(wrapper != null)
        {
            IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().func_228019_au_().func_228487_b_();
            wrapper.render(this.cachedVehicle[vehicleIndex], matrixStack, renderTypeBuffer, Minecraft.getInstance().getRenderPartialTicks());
        }

        this.filteredMaterials = this.getMaterials();
        for(int i = 0; i < this.filteredMaterials.size(); i++)
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(GUI);

            MaterialItem materialItem = filteredMaterials.get(i);
            ItemStack stack = materialItem.stack;
            if(stack.isEmpty())
            {
                RenderHelper.disableStandardItemLighting();
                this.blit(startX + 186, startY + i * 19 + 6 + 57, 0, 19, 80, 19);
            }
            else
            {
                RenderHelper.disableStandardItemLighting();
                if(materialItem.isEnabled())
                {
                    this.blit(startX + 186, startY + i * 19 + 6 + 57, 0, 0, 80, 19);
                }
                else
                {
                    this.blit(startX + 186, startY + i * 19 + 6 + 57, 0, 38, 80, 19);
                }

                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                String name = stack.getDisplayName().getUnformattedComponentText();
                if(this.font.getStringWidth(name) > 55)
                {
                    name = this.font.trimStringToWidth(stack.getDisplayName().getUnformattedComponentText(), 50).trim() + "...";
                }
                this.font.drawString(name, startX + 186 + 22, startY + i * 19 + 6 + 6 + 57, Color.WHITE.getRGB());

                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57);

                if(checkBoxMaterials.isToggled())
                {
                    int count = InventoryUtil.getItemStackAmount(this.minecraft.player, stack);
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }

                Minecraft.getInstance().getItemRenderer().renderItemOverlayIntoGUI(this.font, stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57, null);
            }
        }
    }

    private void drawSlot(int startX, int startY, int x, int y, int iconX, int iconY, int slot, boolean required, boolean applicable)
    {
        int textureOffset = required ? 18 : 0;
        this.blit(startX + x, startY + y, 128 + textureOffset, 0, 18, 18);
        if(workstation.getStackInSlot(slot).isEmpty())
        {
            if(applicable)
            {
                this.blit(startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), iconY, 16, 16);
            }
            else
            {
                this.blit(startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), 48, 16, 16);
            }
        }
    }

    private void drawSlotTooltip(List<String> text, int startX, int startY, int x, int y, int mouseX, int mouseY, int slot)
    {
        if(workstation.getStackInSlot(slot).isEmpty())
        {
            if(CommonUtils.isMouseWithin(mouseX, mouseY, startX + x, startY + y, 18, 18))
            {
                this.renderTooltip(text, mouseX, mouseY, this.minecraft.fontRenderer);
            }
        }
    }

    private List<MaterialItem> getMaterials()
    {
        List<MaterialItem> materials = NonNullList.withSize(7, new MaterialItem(ItemStack.EMPTY));
        List<MaterialItem> filteredMaterials = this.materials.stream().filter(materialItem -> checkBoxMaterials.isToggled() ? !materialItem.isEnabled() : !materialItem.stack.isEmpty()).collect(Collectors.toList());
        for(int i = 0; i < filteredMaterials.size() && i < materials.size(); i++)
        {
            materials.set(i, filteredMaterials.get(i));
        }
        return materials;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, 109, 4210752);
    }

    public static class MaterialItem
    {
        public static final MaterialItem EMPTY = new MaterialItem();

        private boolean enabled = false;
        private ItemStack stack = ItemStack.EMPTY;

        public MaterialItem() {}

        public MaterialItem(ItemStack stack)
        {
            this.stack = stack;
        }

        public ItemStack getStack()
        {
            return stack;
        }
        
        public void update()
        {
            if(!stack.isEmpty())
            {
                enabled = InventoryUtil.hasItemStack(Minecraft.getInstance().player, stack);
            }
        }

        public boolean isEnabled()
        {
            return stack.isEmpty() || enabled;
        }
    }
}
