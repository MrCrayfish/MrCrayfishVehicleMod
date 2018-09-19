package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerFluidMixer;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidMixer;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;

/**
 * Author: MrCrayfish
 */
public class GuiFluidMixer extends GuiContainer
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/fluid_mixer.png");

    private IInventory playerInventory;
    private TileEntityFluidMixer tileEntityFluidMixer;

    public GuiFluidMixer(IInventory playerInventory, TileEntityFluidMixer tileEntityFluidMixer)
    {
        super(new ContainerFluidMixer(playerInventory, tileEntityFluidMixer));
        this.playerInventory = playerInventory;
        this.tileEntityFluidMixer = tileEntityFluidMixer;
        this.xSize = 176;
        this.ySize = 180;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(tileEntityFluidMixer.getBlazeFluidStack() != null)
        {
            FluidStack stack = tileEntityFluidMixer.getBlazeFluidStack();
            if(this.isMouseWithinRegion(startX + 33, startY + 17, 16, 29, mouseX, mouseY))
            {
                if(stack.amount > 0)
                {
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GOLD.toString() + tileEntityFluidMixer.getBlazeLevel() + "/" + 5000 + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.drawHoveringText(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        if(tileEntityFluidMixer.getEnderSapFluidStack() != null)
        {
            FluidStack stack = tileEntityFluidMixer.getEnderSapFluidStack();
            if(this.isMouseWithinRegion(startX + 33, startY + 52, 16, 29, mouseX, mouseY))
            {
                if(stack.amount > 0)
                {
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.DARK_GREEN.toString() + tileEntityFluidMixer.getEnderSapLevel() + "/" + 5000 + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.drawHoveringText(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        if(tileEntityFluidMixer.getFueliumFluidStack() != null)
        {
            FluidStack stack = tileEntityFluidMixer.getFueliumFluidStack();
            if(this.isMouseWithinRegion(startX + 151, startY + 20, 16, 59, mouseX, mouseY))
            {
                if(stack.amount > 0)
                {
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GREEN.toString() + tileEntityFluidMixer.getFueliumLevel() + "/" + 10000 + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.drawHoveringText(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileEntityFluidMixer.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(startX, startY, 0, 0, this.xSize, this.ySize);

        if(tileEntityFluidMixer.getRemainingFuel() > 0)
        {
            int remainingFuel = (int) (14 * (tileEntityFluidMixer.getRemainingFuel() / (double) tileEntityFluidMixer.getFuelMaxProgress()));
            this.drawTexturedModalRect(startX + 9, startY + 31 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }

        if(tileEntityFluidMixer.getExtractionProgress() > 0)
        {
            double extractionPercentage = tileEntityFluidMixer.getExtractionProgress() / (double) TileEntityFluidMixer.FLUID_MAX_PROGRESS;
            int extractionProgress = (int) (22 * extractionPercentage);
            int left = startX + 100 + 1;
            int top = startY + 13;
            int right = left + 23 - 1;
            int bottom = top + 16;
            int fluidColor = FluidUtils.getAverageFluidColor(ModFluids.FUELIUM); //TODO change to recipe
            RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, -1, fluidColor, zLevel);
            this.drawTexturedModalRect(startX + 100, startY + 13, 176, 14, extractionProgress, 16);
            this.drawTexturedModalRect(startX + 100 + extractionProgress, startY + 13, 100 + extractionProgress, 13, 23 - extractionProgress, 17);
        }

        drawSmallFluidTank(tileEntityFluidMixer.getBlazeFluidStack(), startX + 33, startY + 17, tileEntityFluidMixer.getBlazeLevel() / 5000.0);
        drawSmallFluidTank(tileEntityFluidMixer.getEnderSapFluidStack(), startX + 33, startY + 52, tileEntityFluidMixer.getEnderSapLevel() / 5000.0);
        drawFluidTank(tileEntityFluidMixer.getFueliumFluidStack(), startX + 151, startY + 20, tileEntityFluidMixer.getFueliumLevel() / 10000.0);
    }

    private void drawFluidTank(FluidStack fluid, int x, int y, double level)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 59);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(x, y, 176, 44, 16, 59);
    }

    private void drawSmallFluidTank(FluidStack fluid, int x, int y, double level)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 29);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(x, y, 176, 44, 16, 29);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
