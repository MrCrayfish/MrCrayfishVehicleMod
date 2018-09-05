package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
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

/**
 * Author: MrCrayfish
 */
public class GuiFluidExtractor extends GuiContainer
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/fluid_extractor.png");

    private IInventory playerInventory;
    private TileEntityFluidExtractor tileEntityFluidExtractor;

    public GuiFluidExtractor(IInventory playerInventory, TileEntityFluidExtractor tileEntityFluidExtractor)
    {
        super(new ContainerFluidExtractor(playerInventory, tileEntityFluidExtractor));
        this.playerInventory = playerInventory;
        this.tileEntityFluidExtractor = tileEntityFluidExtractor;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(tileEntityFluidExtractor.getFluidStack() != null)
        {
            FluidStack stack = tileEntityFluidExtractor.getFluidStack();
            if(this.isMouseWithinRegion(startX + 127, startY + 14, 16, 59, mouseX, mouseY))
            {
                this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GREEN.toString() + tileEntityFluidExtractor.getFluidLevel() + "/" + TileEntityFluidExtractor.TANK_CAPACITY + " mB"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileEntityFluidExtractor.getDisplayName().getUnformattedText();
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

        if(tileEntityFluidExtractor.getRemainingFuel() > 0)
        {
            int remainingFuel = (int) (14 * (tileEntityFluidExtractor.getRemainingFuel() / (double) tileEntityFluidExtractor.getFuelMaxProgress()));
            RenderUtil.drawTexturedModalRect(startX + 64, startY + 53 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }

        if(tileEntityFluidExtractor.getExtractionProgress() > 0)
        {
            int extractionProgress = (int) (22 * (tileEntityFluidExtractor.getExtractionProgress() / (double) TileEntityFluidExtractor.FLUID_MAX_PROGRESS) + 1);
            RenderUtil.drawTexturedModalRect(startX + 93, startY + 34, 176, 14, extractionProgress, 16);
        }

        this.drawFluidTank(tileEntityFluidExtractor.getFluidStack(), startX + 127, startY + 14, tileEntityFluidExtractor.getFluidLevel() / (double) TileEntityFluidExtractor.TANK_CAPACITY, 59);
    }

    private void drawFluidTank(FluidStack fluid, int x, int y, double level, int height)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, height);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(x, y, 176, 44, 16, 59);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
