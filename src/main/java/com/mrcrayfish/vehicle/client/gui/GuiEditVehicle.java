package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.container.ContainerVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class GuiEditVehicle extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation("vehicle:textures/gui/edit_vehicle.png");

    private final IInventory playerInventory;
    private final IInventory vehicleInventory;
    private final EntityPoweredVehicle vehicle;

    public GuiEditVehicle(IInventory vehicleInventory, EntityPoweredVehicle vehicle, EntityPlayer player)
    {
        super(new ContainerVehicle(vehicleInventory, vehicle, player));
        this.playerInventory = player.inventory;
        this.vehicleInventory = vehicleInventory;
        this.vehicle = vehicle;
        this.ySize = 184;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if(vehicleInventory.getStackInSlot(0).isEmpty())
        {
            this.drawTexturedModalRect(i + 8, j + 17, 176, 0, 16, 16);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.vehicleInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper(vehicle.getClass());
        if(wrapper != null)
        {
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(96, 78, 100);
                GlStateManager.rotate(-10F, 1, 0, 0);
                GlStateManager.rotate(-(vehicle.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks()) / 2F, 0, 1, 0);
                GlStateManager.scale(-22, -22, -22);
                PartPosition position = GuiWorkstation.DISPLAY_PROPERTIES.get(vehicle.getClass());
                if(position != null)
                {
                    //Apply vehicle rotations, translations, and scale
                    GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
                    GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
                    GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
                    GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
                    GlStateManager.translate(position.getX(), position.getY(), position.getZ());
                }
                wrapper.render(vehicle, Minecraft.getMinecraft().getRenderPartialTicks());
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
