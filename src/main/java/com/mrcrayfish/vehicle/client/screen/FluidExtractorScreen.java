package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.inventory.container.FluidExtractorContainer;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorScreen extends ContainerScreen<FluidExtractorContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/fluid_extractor.png");

    private PlayerInventory playerInventory;
    private FluidExtractorTileEntity fluidExtractorTileEntity;

    public FluidExtractorScreen(FluidExtractorContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.fluidExtractorTileEntity = container.getFluidExtractor();
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack); //TODO do I need this?
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(this.fluidExtractorTileEntity.getFluidStackTank() != null)
        {
            FluidStack stack = this.fluidExtractorTileEntity.getFluidStackTank();
            if(this.isMouseWithinRegion(startX + 127, startY + 14, 16, 59, mouseX, mouseY))
            {
                if(stack.getAmount() > 0)
                {
                    this.renderTooltip(matrixStack, Lists.transform(Arrays.asList(new StringTextComponent(stack.getDisplayName().getString()), new StringTextComponent(TextFormatting.GRAY.toString() + this.fluidExtractorTileEntity.getFluidLevel() + "/" + this.fluidExtractorTileEntity.getCapacity() + " mB")), ITextComponent::func_241878_f), mouseX, mouseY);
                }
                else
                {
                    this.renderTooltip(matrixStack, Lists.transform(Collections.singletonList(new StringTextComponent("No Fluid")), ITextComponent::func_241878_f), mouseX, mouseY);
                }
            }
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.minecraft.fontRenderer.drawString(matrixStack, this.fluidExtractorTileEntity.getDisplayName().getString(), 8, 6, 4210752);
        this.minecraft.fontRenderer.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, startX, startY, 0, 0, this.xSize, this.ySize);

        if(this.fluidExtractorTileEntity.getRemainingFuel() >= 0)
        {
            int remainingFuel = (int) (14 * (this.fluidExtractorTileEntity.getRemainingFuel() / (double) this.fluidExtractorTileEntity.getFuelMaxProgress()));
            this.blit(matrixStack, startX + 64, startY + 53 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }

        if(this.fluidExtractorTileEntity.canExtract())
        {
            int left = startX + 93 + 1;
            int top = startY + 34;
            int right = left + 23 - 1;
            int bottom = top + 16;
            FluidEntry fluidExtract = this.fluidExtractorTileEntity.getCurrentRecipe().getResult();
            int fluidColor = -1;
            if(fluidExtract != null)
            {
                fluidColor = (255 << 24) | FluidUtils.getAverageFluidColor(fluidExtract.getFluid());
            }
            RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, -1, fluidColor);
            this.blit(matrixStack, startX + 93, startY + 34, 176, 14, 23, 16);
            double extractionPercentage = this.fluidExtractorTileEntity.getExtractionProgress() / (double) Config.SERVER.extractorExtractTime.get();
            int extractionProgress = (int) (22 * extractionPercentage + 1);
            this.blit(matrixStack, startX + 93 + extractionProgress, startY + 34, 93 + extractionProgress, 34, 23 - extractionProgress, 17);
        }

        this.drawFluidTank(this.fluidExtractorTileEntity.getFluidStackTank(), matrixStack, startX + 127, startY + 14, this.fluidExtractorTileEntity.getFluidLevel() / (double) this.fluidExtractorTileEntity.getCapacity(), 59);
    }

    private void drawFluidTank(FluidStack fluid, MatrixStack matrixStack, int x, int y, double level, int height)
    {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, height);
        Minecraft.getInstance().getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, x, y, 176, 44, 16, 59);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
