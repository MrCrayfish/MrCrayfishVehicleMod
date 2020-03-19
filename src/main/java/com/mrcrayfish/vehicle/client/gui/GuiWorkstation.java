package com.mrcrayfish.vehicle.client.gui;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.item.ItemWheel;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCraftVehicle;
import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import com.mrcrayfish.vehicle.util.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class GuiWorkstation extends GuiContainer
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/workstation.png");

    private List<MaterialItem> materials;
    private List<MaterialItem> filteredMaterials;
    private static int currentVehicle = 0;
    private static int prevCurrentVehicle = 0;
    private static boolean showRemaining = false;
    private EntityVehicle[] cachedVehicle;
    private IInventory playerInventory;
    private TileEntityWorkstation workstation;
    private GuiButton btnCraft;
    private GuiCheckBox checkBoxMaterials;
    private boolean validEngine;
    private boolean transitioning;
    private int vehicleScale = 30;
    private int prevVehicleScale = 30;

    public GuiWorkstation(IInventory playerInventory, TileEntityWorkstation workstation)
    {
        super(new ContainerWorkstation(playerInventory, workstation));
        this.playerInventory = playerInventory;
        this.workstation = workstation;
        this.xSize = 289;
        this.ySize = 202;
        this.materials = new ArrayList<>();
        this.cachedVehicle = new EntityVehicle[VehicleRecipes.getVehicleCount()];
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(1, startX, startY, 15, 20, "<"));
        this.buttonList.add(new GuiButton(2, startX + 161, startY, 15, 20, ">"));
        this.buttonList.add(btnCraft = new GuiButton(3, startX + 186, startY + 6, 97, 20, "Craft"));
        this.btnCraft.enabled = false;
        this.checkBoxMaterials = new GuiCheckBox(186, 51, "Show Remaining");
        this.checkBoxMaterials.setToggled(GuiWorkstation.showRemaining);
        this.loadVehicle(currentVehicle);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

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

        if(cachedVehicle[currentVehicle] instanceof EntityPoweredVehicle)
        {
            EntityPoweredVehicle entityPoweredVehicle = (EntityPoweredVehicle) cachedVehicle[currentVehicle];
            if(entityPoweredVehicle.getEngineType() != EngineType.NONE)
            {
                ItemStack engine = workstation.getStackInSlot(1);
                if(!engine.isEmpty() && engine.getItem() instanceof ItemEngine)
                {
                    EngineType engineType = ((ItemEngine) engine.getItem()).getEngineType();
                    if(entityPoweredVehicle.getEngineType() != engineType)
                    {
                        canCraft = false;
                        validEngine = false;
                        entityPoweredVehicle.setEngine(false);
                    }
                    else
                    {
                        entityPoweredVehicle.setEngineTier(EngineTier.getType(engine.getItemDamage()));
                        entityPoweredVehicle.setEngine(true);
                        entityPoweredVehicle.notifyDataManagerChange(EntityPoweredVehicle.ENGINE_TIER);
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
                if(!wheels.isEmpty() && wheels.getItem() instanceof ItemWheel)
                {
                    if(wheels.getTagCompound() != null)
                    {
                        NBTTagCompound tagCompound = wheels.getTagCompound();
                        if(tagCompound.hasKey("color", Constants.NBT.TAG_INT))
                        {
                            entityPoweredVehicle.setWheelColor(tagCompound.getInteger("color"));
                        }
                    }
                    entityPoweredVehicle.setWheelType(WheelType.values()[wheels.getItemDamage()]);
                    entityPoweredVehicle.setWheels(true);
                    entityPoweredVehicle.notifyDataManagerChange(EntityPoweredVehicle.WHEEL_COLOR);
                }
                else
                {
                    entityPoweredVehicle.setWheels(false);
                    canCraft = false;
                }
            }
        }
        btnCraft.enabled = canCraft;

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
                if(stack.getItem() == Items.DYE)
                {
                    cachedVehicle[currentVehicle].setColor(EntityVehicle.DYE_TO_COLOR[15 - stack.getMetadata()]);
                }
                else
                {
                    cachedVehicle[currentVehicle].setColor(EntityVehicle.DYE_TO_COLOR[0]);
                }
            }
            else
            {
                cachedVehicle[currentVehicle].setColor(EntityVehicle.DYE_TO_COLOR[0]);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        this.checkBoxMaterials.handleClick(startX, startY, mouseX, mouseY, mouseButton);
        GuiWorkstation.showRemaining = this.checkBoxMaterials.isToggled();
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button.id == 1)
        {
            if(currentVehicle - 1 < 0)
            {
                this.loadVehicle(VehicleRecipes.getVehicleCount() - 1);
            }
            else
            {
                this.loadVehicle(currentVehicle - 1);
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if(button.id == 2)
        {
            if(currentVehicle + 1 >= VehicleRecipes.getVehicleCount())
            {
                this.loadVehicle(0);
            }
            else
            {
                this.loadVehicle(currentVehicle + 1);
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if(button.id == 3)
        {
            EntityEntry entry = EntityRegistry.getEntry(cachedVehicle[currentVehicle].getClass());
            if(entry != null)
            {
                ResourceLocation registryName = entry.getRegistryName();
                if(registryName != null)
                {
                    IMessage message = new MessageCraftVehicle(registryName.toString(), workstation.getPos());
                    PacketHandler.INSTANCE.sendToServer(message);
                    //TODO make confirm GUI if engine and wheels are not present.
                }
            }
        }
    }

    private void loadVehicle(int index)
    {
        prevCurrentVehicle = currentVehicle;

        try
        {
            if(cachedVehicle[index] == null)
            {
                EntityVehicle vehicle = VehicleRecipes.getVehicleClasses().get(index).getDeclaredConstructor(World.class).newInstance(Minecraft.getMinecraft().world);
                java.util.List<EntityDataManager.DataEntry<?>> entryList = vehicle.getDataManager().getAll();
                if(entryList != null)
                {
                    entryList.forEach(dataEntry -> vehicle.notifyDataManagerChange(dataEntry.getKey()));
                }

                if(vehicle instanceof EntityPoweredVehicle)
                {
                    ((EntityPoweredVehicle) vehicle).setEngine(false);
                    ((EntityPoweredVehicle) vehicle).setWheels(false);
                }

                cachedVehicle[index] = vehicle;
            }
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }

        materials.clear();
        VehicleRecipes.VehicleRecipe recipe = VehicleRecipes.getRecipe(cachedVehicle[index].getClass());
        for(int i = 0; i < recipe.getMaterials().size(); i++)
        {
            MaterialItem item = new MaterialItem(recipe.getMaterials().get(i));
            item.update();
            materials.add(item);
        }

        currentVehicle = index;

        if(VehicleConfig.CLIENT.display.workstationAnimation && prevCurrentVehicle != currentVehicle)
        {
            transitioning = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        for(int i = 0; i < filteredMaterials.size(); i++)
        {
            int itemX = startX + 186;
            int itemY = startY + i * 19 + 6 + 57;
            if(MouseHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, 80, 19))
            {
                MaterialItem materialItem = filteredMaterials.get(i);
                if(!materialItem.getStack().isEmpty())
                {
                    this.renderToolTip(materialItem.getStack(), mouseX, mouseY);
                }
            }
        }

        EntityVehicle vehicle = cachedVehicle[currentVehicle];
        if(vehicle.canBeColored())
        {
            this.drawSlotTooltip(Lists.newArrayList(TextFormatting.AQUA + I18n.format("vehicle.tooltip.optional"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.paint_color")), startX, startY, 186, 29, mouseX, mouseY, 0);
        }
        else
        {
            this.drawSlotTooltip(Lists.newArrayList(I18n.format("vehicle.tooltip.paint_color"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.not_applicable")), startX, startY, 186, 29, mouseX, mouseY, 0);
        }

        if(vehicle instanceof EntityPoweredVehicle && ((EntityPoweredVehicle) vehicle).getEngineType() != EngineType.NONE)
        {
            String engineName = ((EntityPoweredVehicle) vehicle).getEngineType().getEngineName();
            this.drawSlotTooltip(Lists.newArrayList(TextFormatting.RED + I18n.format("vehicle.tooltip.required"), TextFormatting.GRAY + engineName), startX, startY, 206, 29, mouseX, mouseY, 1);
        }
        else
        {
            this.drawSlotTooltip(Lists.newArrayList(I18n.format("vehicle.tooltip.engine"), TextFormatting.GRAY + I18n.format("vehicle.tooltip.not_applicable")), startX, startY, 206, 29, mouseX, mouseY, 1);
        }

        if(vehicle instanceof EntityPoweredVehicle && ((EntityPoweredVehicle) vehicle).canChangeWheels())
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
        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        this.drawDefaultBackground();

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        GlStateManager.enableBlend();

        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(startX, startY + 80, 0, 134, 176, 122);
        this.drawTexturedModalRect(startX + 180, startY, 176, 54, 6, 202);
        this.drawTexturedModalRect(startX + 186, startY, 182, 54, 57, 202);
        this.drawTexturedModalRect(startX + 186 + 57, startY, 220, 54, 23, 202);
        this.drawTexturedModalRect(startX + 186 + 57 + 23, startY, 220, 54, 3, 202);
        this.drawTexturedModalRect(startX + 186 + 57 + 23 + 3, startY, 236, 54, 20, 202);

        /* Slots */
        this.drawSlot(startX, startY, 186, 29, 80, 0, 0, false, cachedVehicle[currentVehicle].canBeColored());
        boolean needsEngine = cachedVehicle[currentVehicle] instanceof EntityPoweredVehicle && ((EntityPoweredVehicle) cachedVehicle[currentVehicle]).getEngineType() != EngineType.NONE;
        this.drawSlot(startX, startY, 206, 29, 80, 16, 1, !validEngine, needsEngine);
        boolean needsWheels = cachedVehicle[currentVehicle] instanceof EntityPoweredVehicle && ((EntityPoweredVehicle) cachedVehicle[currentVehicle]).canChangeWheels();
        this.drawSlot(startX, startY, 226, 29, 80, 32, 2, needsWheels && workstation.getStackInSlot(2).isEmpty(), needsWheels);

        this.checkBoxMaterials.draw(mc, guiLeft, guiTop);

        this.drawCenteredString(fontRenderer, cachedVehicle[currentVehicle].getName(), startX + 88, startY + 6, Color.WHITE.getRGB());

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(startX + 88, startY + 90, 100);

            float scale = prevVehicleScale + (vehicleScale - prevVehicleScale) * partialTicks;
            GlStateManager.scale(scale, -scale, scale);

            GlStateManager.rotate(5F, 1, 0, 0);
            GlStateManager.rotate(Minecraft.getMinecraft().player.ticksExisted + partialTicks, 0, 1, 0);

            int vehicleIndex = transitioning ? prevCurrentVehicle : currentVehicle;
            Class<? extends EntityVehicle>  clazz = cachedVehicle[vehicleIndex].getClass();
            PartPosition position = VehicleProperties.getProperties(VehicleRecipes.getVehicleClasses().get(vehicleIndex)).getDisplayPosition();
            if(position != null)
            {
                //Apply vehicle rotations, translations, and scale
                GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
                GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
                GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
                GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
                GlStateManager.translate(position.getX(), position.getY(), position.getZ());
            }
            Render<EntityVehicle> render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(clazz);
            render.doRender(cachedVehicle[vehicleIndex], 0F, 0F, 0F, 0F, 0F);
        }
        GlStateManager.popMatrix();

        filteredMaterials = this.getMaterials();
        for(int i = 0; i < filteredMaterials.size(); i++)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(GUI);

            MaterialItem materialItem = filteredMaterials.get(i);
            ItemStack stack = materialItem.stack;
            if(stack.isEmpty())
            {
                RenderHelper.disableStandardItemLighting();
                this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 19, 80, 19);
            }
            else
            {
                RenderHelper.disableStandardItemLighting();
                if(materialItem.isEnabled())
                {
                    this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 0, 80, 19);
                }
                else
                {
                    this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 38, 80, 19);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                String name = stack.getDisplayName();
                if(fontRenderer.getStringWidth(name) > 55)
                {
                    name = fontRenderer.trimStringToWidth(stack.getDisplayName(), 50).trim() + "...";
                }
                fontRenderer.drawString(name, startX + 186 + 22, startY + i * 19 + 6 + 6 + 57, Color.WHITE.getRGB());

                RenderHelper.enableGUIStandardItemLighting();
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57);

                if(checkBoxMaterials.isToggled())
                {
                    int count = InventoryUtil.getItemStackAmount(Minecraft.getMinecraft().player, stack);
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }

                Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(fontRenderer, stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57, null);
            }
        }
    }

    private void drawSlot(int startX, int startY, int x, int y, int iconX, int iconY, int slot, boolean required, boolean applicable)
    {
        int textureOffset = required ? 18 : 0;
        this.drawTexturedModalRect(startX + x, startY + y, 128 + textureOffset, 0, 18, 18);
        if(workstation.getStackInSlot(slot).isEmpty())
        {
            if(applicable)
            {
                this.drawTexturedModalRect(startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), iconY, 16, 16);
            }
            else
            {
                this.drawTexturedModalRect(startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), 48, 16, 16);
            }
        }
    }

    private void drawSlotTooltip(List<String> text, int startX, int startY, int x, int y, int mouseX, int mouseY, int slot)
    {
        if(workstation.getStackInSlot(slot).isEmpty())
        {
            if(MouseHelper.isMouseWithin(mouseX, mouseY, startX + x, startY + y, 18, 18))
            {
                this.drawHoveringText(text, mouseX, mouseY, this.mc.fontRenderer);
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
        this.fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, 109, 4210752);
    }

    public static class MaterialItem extends Gui
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
                enabled = InventoryUtil.hasItemStack(Minecraft.getMinecraft().player, stack);
            }
        }

        public boolean isEnabled()
        {
            return stack.isEmpty() || enabled;
        }
    }
}
