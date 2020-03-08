package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.container.ContainerVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.MouseHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Author: MrCrayfish
 */
public class GuiEditVehicle extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation("vehicle:textures/gui/edit_vehicle.png");

    private final IInventory playerInventory;
    private final IInventory vehicleInventory;
    private final EntityPoweredVehicle vehicle;

    private boolean showHelp = true;
    private int windowZoom = 10;
    private int windowX, windowY;
    private float windowRotationX, windowRotationY;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX, mouseClickedY;

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

        if(vehicle.getEngineType() != EngineType.NONE)
        {
            if(vehicleInventory.getStackInSlot(0).isEmpty())
            {
                this.drawTexturedModalRect(i + 8, j + 17, 176, 0, 16, 16);
            }
        }
        else if(vehicleInventory.getStackInSlot(0).isEmpty())
        {
            this.drawTexturedModalRect(i + 8, j + 17, 176, 32, 16, 16);
        }

        if(vehicle.canChangeWheels())
        {
            if(vehicleInventory.getStackInSlot(1).isEmpty())
            {
                this.drawTexturedModalRect(i + 8, j + 35, 176, 16, 16, 16);
            }
        }
        else if(vehicleInventory.getStackInSlot(1).isEmpty())
        {
            this.drawTexturedModalRect(i + 8, j + 35, 176, 32, 16, 16);
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
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(i + 26, j + 17, 142, 70);
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(96, 78, 100);
                GlStateManager.translate(windowX + (mouseGrabbed && mouseGrabbedButton == 0 ? mouseX - mouseClickedX : 0), 0, 0);
                GlStateManager.translate(0, windowY + (mouseGrabbed && mouseGrabbedButton == 0 ? mouseY - mouseClickedY : 0), 0);
                GlStateManager.rotate(-10F, 1, 0, 0);
                GlStateManager.rotate(windowRotationY - (mouseGrabbed && mouseGrabbedButton == 1 ? mouseY - mouseClickedY : 0), 1, 0, 0);
                GlStateManager.rotate(windowRotationX + (mouseGrabbed && mouseGrabbedButton == 1 ? mouseX - mouseClickedX : 0), 0, 1, 0);
                GlStateManager.rotate(135F, 0, 1, 0);
                GlStateManager.scale(windowZoom / 10F, windowZoom / 10F, windowZoom / 10F);
                GlStateManager.scale(-22, -22, -22);
                PartPosition position = VehicleProperties.getProperties(vehicle.getClass()).getDisplayPosition();
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
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if(showHelp)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 1000);
            GlStateManager.scale(0.5, 0.5, 0.5);
            this.fontRenderer.drawString(I18n.format("container.edit_vehicle.window_help"), 56, 38, Color.WHITE.getRGB());
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        if(MouseHelper.isMouseWithin(mouseX, mouseY, startX + 26, startY + 17, 142, 70))
        {
            int mouseWheelDelta = Mouse.getDWheel();
            if(mouseWheelDelta < 0 && windowZoom > 0)
            {
                showHelp = false;
                windowZoom--;
            }
            else if(mouseWheelDelta > 0)
            {
                showHelp = false;
                windowZoom++;
            }

            if(!mouseGrabbed && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)))
            {
                mouseGrabbed = true;
                mouseGrabbedButton = Mouse.isButtonDown(1) ? 1 : 0;
                mouseClickedX = mouseX;
                mouseClickedY = mouseY;
                showHelp = false;
            }
        }

        if(mouseGrabbed)
        {
            if(mouseGrabbedButton == 0 && !Mouse.isButtonDown(0))
            {
                mouseGrabbed = false;
                windowX += (mouseX - mouseClickedX);
                windowY += (mouseY - mouseClickedY);
            }
            else if(mouseGrabbedButton == 1 && !Mouse.isButtonDown(1))
            {
                mouseGrabbed = false;
                windowRotationX += (mouseX - mouseClickedX);
                windowRotationY -= (mouseY - mouseClickedY);
            }
        }

        if(this.vehicleInventory.getStackInSlot(0).isEmpty())
        {
            if(MouseHelper.isMouseWithin(mouseX, mouseY, startX + 7, startY + 16, 18, 18))
            {
                if(vehicle.getEngineType() != EngineType.NONE)
                {
                    this.drawHoveringText(Collections.singletonList("Engine"), mouseX, mouseY, this.mc.fontRenderer);
                }
                else
                {
                    this.drawHoveringText(Arrays.asList("Engine", TextFormatting.GRAY + "Not applicable"), mouseX, mouseY, this.mc.fontRenderer);
                }
            }
        }

        if(this.vehicleInventory.getStackInSlot(1).isEmpty())
        {
            if(MouseHelper.isMouseWithin(mouseX, mouseY, startX + 7, startY + 34, 18, 18))
            {
                if(vehicle.canChangeWheels())
                {
                    this.drawHoveringText(Collections.singletonList("Wheels"), mouseX, mouseY, this.mc.fontRenderer);
                }
                else
                {
                    this.drawHoveringText(Arrays.asList("Wheels", TextFormatting.GRAY + "Not applicable"), mouseX, mouseY, this.mc.fontRenderer);
                }
            }
        }
    }
}
