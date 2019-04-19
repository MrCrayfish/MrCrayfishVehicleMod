package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerFluidExtractor;
import com.mrcrayfish.vehicle.crafting.FluidExtract;
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
import java.util.Collections;

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

        if(tileEntityFluidExtractor.getFluidStackTank() != null)
        {
            FluidStack stack = tileEntityFluidExtractor.getFluidStackTank();
            if(this.isMouseWithinRegion(startX + 127, startY + 14, 16, 59, mouseX, mouseY))
            {
                if(stack.amount > 0)
                {
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GRAY.toString() + tileEntityFluidExtractor.getFluidLevel() + "/" + TileEntityFluidExtractor.TANK_CAPACITY + " mB"), mouseX, mouseY);
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
            this.drawTexturedModalRect(startX + 64, startY + 53 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }

        if(tileEntityFluidExtractor.getExtractionProgress() > 0)
        {
            int left = startX + 93 + 1;
            int top = startY + 34;
            int right = left + 23 - 1;
            int bottom = top + 16;
            FluidExtract fluidExtract = tileEntityFluidExtractor.getFluidExtractSource();
            int fluidColor = -1;
            if(fluidExtract != null)
            {
                fluidColor = (255 << 24) | FluidUtils.getAverageFluidColor(fluidExtract.getFluid());
            }
            RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, -1, fluidColor, zLevel);
            this.drawTexturedModalRect(startX + 93, startY + 34, 176, 14, 23, 16);
            double extractionPercentage = tileEntityFluidExtractor.getExtractionProgress() / (double) TileEntityFluidExtractor.FLUID_MAX_PROGRESS;
            int extractionProgress = (int) (22 * extractionPercentage + 1);
            this.drawTexturedModalRect(startX + 93 + extractionProgress, startY + 34, 93 + extractionProgress, 34, 23 - extractionProgress, 17);
        }

        this.drawFluidTank(tileEntityFluidExtractor.getFluidStackTank(), startX + 127, startY + 14, tileEntityFluidExtractor.getFluidLevel() / (double) TileEntityFluidExtractor.TANK_CAPACITY, 59);
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
