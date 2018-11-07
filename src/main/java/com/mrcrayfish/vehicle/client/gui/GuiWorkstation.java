package com.mrcrayfish.vehicle.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCraftVehicle;
import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Author: MrCrayfish
 */
public class GuiWorkstation extends GuiContainer
{
    private static final ImmutableList<Class<? extends EntityVehicle>> VEHICLES;
    private static final ImmutableMap<Class<? extends EntityVehicle>, PartPosition> DISPLAY_PROPERTIES;

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

    private MaterialItem[] materials;
    private static int currentVehicle = 0;
    private static int prevCurrentVehicle = 0;
    private EntityVehicle[] cachedVehicle;
    private IInventory playerInventory;
    private TileEntityWorkstation workstation;
    private GuiButton btnCraft;

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
        this.materials = new MaterialItem[7];
        Arrays.fill(materials, MaterialItem.EMPTY);
        this.cachedVehicle = new EntityVehicle[VEHICLES.size()];
        this.loadVehicle(currentVehicle);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(1, startX, startY + 40, 20, 20, "<"));
        this.buttonList.add(new GuiButton(2, startX + 156, startY + 40, 20, 20, ">"));
        this.buttonList.add(btnCraft = new GuiButton(3, startX + 186, startY + 6, 97, 20, "Craft"));
        this.btnCraft.enabled = false;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        for(MaterialItem material : materials)
        {
            material.update();
        }
        boolean allEnabled = true;
        for(MaterialItem material : materials)
        {
            if(!material.isEnabled())
            {
                allEnabled = false;
                break;
            }
        }
        btnCraft.enabled = allEnabled;

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

        Arrays.fill(materials, MaterialItem.EMPTY);
        VehicleRecipes.VehicleRecipe recipe = VehicleRecipes.getRecipe(cachedVehicle[index].getClass());
        for(int i = 0; i < recipe.getMaterials().size(); i++)
        {
            materials[i] = new MaterialItem(recipe.getMaterials().get(i));
        }

        currentVehicle = index;

        if(VehicleConfig.CLIENT.display.workstationAnimation && prevCurrentVehicle != currentVehicle)
        {
            transitioning = true;
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
        this.drawTexturedModalRect(startX + 186, startY, 179, 54, 57, 208);
        this.drawTexturedModalRect(startX + 186 + 57, startY, 179, 54, 26, 208);
        this.drawTexturedModalRect(startX + 186 + 57 + 26, startY, 236, 54, 20, 208);

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

        for(int i = 0; i < materials.length; i++)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(GUI);

            ItemStack stack = materials[i].stack;
            if(stack.isEmpty())
            {
                RenderHelper.disableStandardItemLighting();
                this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 19, 80, 19);
            }
            else
            {
                RenderHelper.disableStandardItemLighting();
                if(materials[i].isEnabled())
                {
                    this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 0, 80, 19);
                }
                else
                {
                    this.drawTexturedModalRect(startX + 186, startY + i * 19 + 6 + 57, 0, 38, 80, 19);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                String name = materials[i].stack.getDisplayName();
                if(fontRenderer.getStringWidth(name) > 55)
                {
                    name = fontRenderer.trimStringToWidth(materials[i].stack.getDisplayName(), 50).trim() + "...";
                }
                fontRenderer.drawString(name, startX + 186 + 22, startY + i * 19 + 6 + 6 + 57, Color.WHITE.getRGB());

                RenderHelper.enableGUIStandardItemLighting();
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(materials[i].stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57);
                Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(fontRenderer, materials[i].stack, startX + 186 + 2, startY + i * 19 + 6 + 1 + 57, null);
            }
        }
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
                enabled = InventoryUtil.hasItemAndAmount(Minecraft.getMinecraft().player, stack.getItem(), stack.getCount());
            }
        }

        public boolean isEnabled()
        {
            return stack.isEmpty() || enabled;
        }
    }
}
