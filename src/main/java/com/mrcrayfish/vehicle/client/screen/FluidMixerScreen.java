package com.mrcrayfish.vehicle.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;

/**
 * Author: MrCrayfish
 */
public class FluidMixerScreen extends ContainerScreen<FluidMixerContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/fluid_mixer.png");

    private PlayerInventory playerInventory;
    private FluidMixerTileEntity fluidMixerTileEntity;

    public FluidMixerScreen(FluidMixerContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.fluidMixerTileEntity = container.getFluidExtractor();
        this.xSize = 176;
        this.ySize = 180;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(this.fluidMixerTileEntity.getBlazeFluidStack() != null)
        {
            FluidStack stack = this.fluidMixerTileEntity.getBlazeFluidStack();
            if(this.isMouseWithinRegion(startX + 33, startY + 17, 16, 29, mouseX, mouseY))
            {
                if(stack.getAmount() > 0)
                {
                    this.renderTooltip(Arrays.asList(stack.getDisplayName().getFormattedText(), TextFormatting.GRAY.toString() + this.fluidMixerTileEntity.getBlazeLevel() + "/" + this.fluidMixerTileEntity.getBlazeTank().getCapacity() + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.renderTooltip(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        if(this.fluidMixerTileEntity.getEnderSapFluidStack() != null)
        {
            FluidStack stack = this.fluidMixerTileEntity.getEnderSapFluidStack();
            if(this.isMouseWithinRegion(startX + 33, startY + 52, 16, 29, mouseX, mouseY))
            {
                if(stack.getAmount() > 0)
                {
                    this.renderTooltip(Arrays.asList(stack.getDisplayName().getFormattedText(), TextFormatting.GRAY.toString() + this.fluidMixerTileEntity.getEnderSapLevel() + "/" + this.fluidMixerTileEntity.getEnderSapTank().getCapacity() + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.renderTooltip(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        if(this.fluidMixerTileEntity.getFueliumFluidStack() != null)
        {
            FluidStack stack = this.fluidMixerTileEntity.getFueliumFluidStack();
            if(this.isMouseWithinRegion(startX + 151, startY + 20, 16, 59, mouseX, mouseY))
            {
                if(stack.getAmount() > 0)
                {
                    this.renderTooltip(Arrays.asList(stack.getDisplayName().getFormattedText(), TextFormatting.GRAY.toString() + this.fluidMixerTileEntity.getFueliumLevel() + "/" + this.fluidMixerTileEntity.getFueliumTank().getCapacity() + " mB"), mouseX, mouseY);
                }
                else
                {
                    this.renderTooltip(Collections.singletonList("No Fluid"), mouseX, mouseY);
                }
            }
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.minecraft.fontRenderer.drawString(this.fluidMixerTileEntity.getDisplayName().getFormattedText(), 8, 6, 4210752);
        this.minecraft.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(startX, startY, 0, 0, this.xSize, this.ySize);

        if(this.fluidMixerTileEntity.getRemainingFuel() >= 0)
        {
            int remainingFuel = (int) (14 * (this.fluidMixerTileEntity.getRemainingFuel() / (double) this.fluidMixerTileEntity.getFuelMaxProgress()));
            this.blit(startX + 9, startY + 31 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }

        if(this.fluidMixerTileEntity.canMix())
        {
            int blazeColorRGB = FluidUtils.getAverageFluidColor(this.fluidMixerTileEntity.getBlazeFluidStack().getFluid());
            int sapColorRGB = FluidUtils.getAverageFluidColor(this.fluidMixerTileEntity.getEnderSapFluidStack().getFluid());
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
            int fluidColor = (130 << 24) | FluidUtils.getAverageFluidColor(ModFluids.FUELIUM.get()); //TODO change to recipe
            double extractionPercentage = this.fluidMixerTileEntity.getExtractionProgress() / (double) Config.SERVER.mixerMixTime.get();

            double lenghtItem = 76;
            double lenghtHorizontal = 12;
            double lenghtVerticle = 8;
            double lenghtNode = 10;
            double lenghtTotal = lenghtItem + lenghtHorizontal + lenghtVerticle + lenghtNode * 2;
            double percentageStart = 0;

            double percentageHorizontal = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtHorizontal / lenghtTotal), 0, 1);
            int left = startX + 51;
            int top = startY + 27;
            RenderUtil.drawGradientRectHorizontal(left, top, (int) (left + 12 * percentageHorizontal), top + 8, blazeColor, blazeColor);
            top += 36;
            RenderUtil.drawGradientRectHorizontal(left, top, (int) (left + 12 * percentageHorizontal), top + 8, sapColor, sapColor);
            percentageStart += lenghtHorizontal / lenghtTotal;

            left += 12;
            top -= 37;
            int colorFade;
            if (extractionPercentage >= percentageStart)
            {
                int alpha = (int) (130 * MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal), 0, 1));
                colorFade = (alpha << 24) | blazeColorRGB;
                RenderUtil.drawGradientRectHorizontal(left, top, left + 10, top + 10, colorFade, colorFade);
                colorFade = (alpha << 24) | sapColorRGB;
                top += 36;
                RenderUtil.drawGradientRectHorizontal(left, top, left + 10, top + 10, colorFade, colorFade);
            }
            percentageStart += lenghtNode / lenghtTotal;

            left += 1;
            top -= 26;
            if (extractionPercentage >= percentageStart)
            {
                double percentageVerticle = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtVerticle / lenghtTotal), 0, 1);
                RenderUtil.drawGradientRectHorizontal(left, top, left + 8, (int) (top + 8 * percentageVerticle), blazeColor, blazeColor);
                top += 26;
                RenderUtil.drawGradientRectHorizontal(left, (int) (top - 8 * percentageVerticle), left + 8, top, sapColor, sapColor);
            }
            percentageStart += lenghtVerticle / lenghtTotal;

            left -= 1;
            top -= 18;
            if (extractionPercentage >= percentageStart)
            {
                int alpha = (int) (130 * MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal), 0, 1));
                colorFade = (alpha << 24) | statrColorRGB;
                RenderUtil.drawGradientRectHorizontal(left, top, left + 10, top + 10, colorFade, colorFade);
            }
            percentageStart += lenghtNode / lenghtTotal;

            if (extractionPercentage >= percentageStart)
            {
                left = startX + 73;
                top = startY + 36;
                int right = left + 76;
                int bottom = top + 26;
                double percentageItem = MathHelper.clamp((extractionPercentage - percentageStart) / (lenghtItem / lenghtTotal), 0, 1);
                RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, statrColor, fluidColor);
                this.blit(left, top, 176, 14, 76, 26);
                int extractionProgress = (int) (76 * percentageItem + 1);
                this.blit(left + extractionProgress, top, 73 + extractionProgress, 36, 76 - extractionProgress, 26);
            }
        }

        this.drawSmallFluidTank(this.fluidMixerTileEntity.getBlazeFluidStack(), startX + 33, startY + 17, this.fluidMixerTileEntity.getBlazeLevel() / (double) this.fluidMixerTileEntity.getBlazeTank().getCapacity());
        this.drawSmallFluidTank(this.fluidMixerTileEntity.getEnderSapFluidStack(), startX + 33, startY + 52, this.fluidMixerTileEntity.getEnderSapLevel() / (double) this.fluidMixerTileEntity.getEnderSapTank().getCapacity());
        this.drawFluidTank(this.fluidMixerTileEntity.getFueliumFluidStack(), startX + 151, startY + 20, this.fluidMixerTileEntity.getFueliumLevel() / (double) this.fluidMixerTileEntity.getFueliumTank().getCapacity());
    }

    private void drawFluidTank(FluidStack fluid, int x, int y, double level)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 59);
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(x, y, 176, 44, 16, 59);
    }

    private void drawSmallFluidTank(FluidStack fluid, int x, int y, double level)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 29);
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(x, y, 176, 44, 16, 29);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
