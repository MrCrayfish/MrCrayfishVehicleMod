package com.mrcrayfish.vehicle.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.trailer.EntityTrailer;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.item.ItemEngine;
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
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
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
    private static final ImmutableList<Class<? extends EntityVehicle>> VEHICLES;
    public static final ImmutableMap<Class<? extends EntityVehicle>, PartPosition> DISPLAY_PROPERTIES;

    static
    {
        ImmutableMap.Builder<Class<? extends EntityVehicle>, PartPosition> builder = ImmutableMap.builder();
        builder.put(EntityAluminumBoat.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F));
        builder.put(EntityATV.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityBumperCar.class, new PartPosition(0.0F, 0.0F, -0.4F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityDuneBuggy.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.75F));
        builder.put(EntityGoKart.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityGolfCart.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F));
        builder.put(EntityJetSki.class, new PartPosition(0.0F, 0.0F, -0.45F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityLawnMower.class, new PartPosition(0.0F, 0.0F, -0.7F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityMiniBike.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityMoped.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
        builder.put(EntityOffRoader.class, new PartPosition(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 1.0F));
        builder.put(EntityShoppingCart.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.45F));
        builder.put(EntitySmartCar.class, new PartPosition(0.0F, 0.0F, -0.2F, 0.0F, 0.0F, 0.0F, 1.35F));
        builder.put(EntitySpeedBoat.class, new PartPosition(0.0F, 0.0F, -0.65F, 0.0F, 0.0F, 0.0F, 1.25F));
        builder.put(EntitySportsPlane.class, new PartPosition(0.0F, 0.0F, 0.35F, 0.0F, 0.0F, 0.0F, 0.85F));
        builder.put(EntityTrailer.class, new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F));

        if(Loader.isModLoaded("cfm"))
        {
            builder.put(EntityBath.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
            builder.put(EntityCouch.class, new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F));
            builder.put(EntitySofacopter.class, new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F));
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
        this.cachedVehicle = new EntityVehicle[VEHICLES.size()];
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
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == 1)
        {
            if(currentVehicle + 1 >= VEHICLES.size())
            {
                this.loadVehicle(0);
            }
            else
            {
                this.loadVehicle(currentVehicle + 1);
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if(button.id == 2)
        {
            if(currentVehicle - 1 < 0)
            {
                this.loadVehicle(VEHICLES.size() - 1);
            }
            else
            {
                this.loadVehicle(currentVehicle - 1);
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
                EntityVehicle vehicle = VEHICLES.get(index).getDeclaredConstructor(World.class).newInstance(Minecraft.getMinecraft().world);
                java.util.List<EntityDataManager.DataEntry<?>> entryList = vehicle.getDataManager().getAll();
                if(entryList != null)
                {
                    entryList.forEach(dataEntry -> vehicle.notifyDataManagerChange(dataEntry.getKey()));
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
        this.drawTexturedModalRect(startX + 180, startY, 176, 54, 6, 208);
        this.drawTexturedModalRect(startX + 186, startY, 182, 54, 57, 208);
        this.drawTexturedModalRect(startX + 186 + 57, startY, 220, 54, 23, 208);
        this.drawTexturedModalRect(startX + 186 + 57 + 23, startY, 220, 54, 3, 208);
        this.drawTexturedModalRect(startX + 186 + 57 + 23 + 3, startY, 236, 54, 20, 208);

        if(workstation.getStackInSlot(0).isEmpty())
        {
            this.drawTexturedModalRect(startX + 187, startY + 30, 80, 0, 16, 16);
        }

        if(!validEngine)
        {
            this.drawTexturedModalRect(startX + 206, startY + 29, 80, 16, 18, 18);
            if(workstation.getStackInSlot(1).isEmpty())
            {
                this.drawTexturedModalRect(startX + 207, startY + 30, 112, 0, 16, 16);
            }
        }
        else if(workstation.getStackInSlot(1).isEmpty())
        {
            this.drawTexturedModalRect(startX + 207, startY + 30, 96, 0, 16, 16);
        }

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
            PartPosition position = DISPLAY_PROPERTIES.get(clazz);
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
