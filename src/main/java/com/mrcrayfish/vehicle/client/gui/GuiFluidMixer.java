package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerFluidMixer;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidMixer;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GRAY.toString() + tileEntityFluidMixer.getBlazeLevel() + "/" + 5000 + " mB"), mouseX, mouseY);
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
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GRAY.toString() + tileEntityFluidMixer.getEnderSapLevel() + "/" + 5000 + " mB"), mouseX, mouseY);
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
                    this.drawHoveringText(Arrays.asList(stack.getLocalizedName(), TextFormatting.GRAY.toString() + tileEntityFluidMixer.getFueliumLevel() + "/" + 10000 + " mB"), mouseX, mouseY);
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
            int blazeColorRGB = FluidUtils.getAverageFluidColor(tileEntityFluidMixer.getBlazeFluidStack().getFluid());
            int sapColorRGB = FluidUtils.getAverageFluidColor(tileEntityFluidMixer.getEnderSapFluidStack().getFluid());
            int blazeColor = (130 << 24) | blazeColorRGB;
            int sapColor = (130 << 24) | sapColorRGB;
            int redBlaze = blazeColor >> 16 & 255;
            int greenBlaze = blazeColor >> 8 & 255;
            int blueBlaze = blazeColor & 255;
            int redSap = sapColor >> 16 & 255;
            int greenSap = sapColor >> 8 & 255;
            int blueSap = sapColor & 255;
            int statrColorRGB = ((((redBlaze + redSap) / 2) & 255) << 16)
                    | ((((greenBlaze + greenSap) / 2) & 255) << 8) | ((((blueBlaze + blueSap) / 2) & 255));
            int statrColor = (130 << 24) | statrColorRGB;
            int fluidColor = (130 << 24) | FluidUtils.getAverageFluidColor(ModFluids.FUELIUM); //TODO change to recipe
            double extractionPercentage = tileEntityFluidMixer.getExtractionProgress() / (double) TileEntityFluidMixer.FLUID_MAX_PROGRESS;

            double lenghtItem = 76;
            double lenghtHorizontal = 12;
            double lenghtVerticle = 8;
            double lenghtNode = 10;
            double lenghtTotal = lenghtItem + lenghtHorizontal + lenghtVerticle + lenghtNode * 2;
            double percentageStart = 0;

            double percentageHorizontal = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtHorizontal / lenghtTotal), 0, 1);
            int left = startX + 51;
            int top = startY + 27;
            drawGradientRect(left, top, (int) (left + 12 * percentageHorizontal), top + 8, blazeColor, blazeColor);
            top += 36;
            drawGradientRect(left, top, (int) (left + 12 * percentageHorizontal), top + 8, sapColor, sapColor);
            percentageStart += lenghtHorizontal / lenghtTotal;

            left += 12;
            top -= 37;
            int colorFade;
            if (extractionPercentage >= percentageStart)
            {
                int alpha = (int) (130 * MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal), 0, 1));
                colorFade = (alpha << 24) | blazeColorRGB;
                drawGradientRect(left, top, left + 10, top + 10, colorFade, colorFade);
                colorFade = (alpha << 24) | sapColorRGB;
                top += 36;
                drawGradientRect(left, top, left + 10, top + 10, colorFade, colorFade);
            }
            percentageStart += lenghtNode / lenghtTotal;

            left += 1;
            top -= 26;
            if (extractionPercentage >= percentageStart)
            {
                double percentageVerticle = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtVerticle / lenghtTotal), 0, 1);
                drawGradientRect(left, top, left + 8, (int) (top + 8 * percentageVerticle), blazeColor, blazeColor);
                top += 26;
                drawGradientRect(left, (int) (top - 8 * percentageVerticle), left + 8, top, sapColor, sapColor);
            }
            percentageStart += lenghtVerticle / lenghtTotal;

            left -= 1;
            top -= 18;
            if (extractionPercentage >= percentageStart)
            {
                int alpha = (int) (130 * MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal), 0, 1));
                colorFade = (alpha << 24) | statrColorRGB;
                drawGradientRect(left, top, left + 10, top + 10, colorFade, colorFade);
            }
            percentageStart += lenghtNode / lenghtTotal;

            if (extractionPercentage >= percentageStart)
            {
                left = startX + 73;
                top = startY + 36;
                int right = left + 76;
                int bottom = top + 26;
                double percentageItem = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtItem / lenghtTotal), 0, 1);
                RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, statrColor, fluidColor, zLevel);
                this.drawTexturedModalRect(left, top, 176, 14, 76, 26);
                int extractionProgress = (int) (76 * percentageItem + 1);
                this.drawTexturedModalRect(left + extractionProgress, top, 73 + extractionProgress, 36, 76 - extractionProgress, 26);
            }
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
