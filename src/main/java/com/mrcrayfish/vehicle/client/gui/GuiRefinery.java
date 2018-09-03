package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerRefinery;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

/**
 * Author: MrCrayfish
 */
public class GuiRefinery extends GuiContainer
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/refinery.png");

    private IInventory playerInventory;
    private TileEntityRefinery tileEntityRefinery;

    public GuiRefinery(IInventory playerInventory, TileEntityRefinery tileEntityRefinery)
    {
        super(new ContainerRefinery(playerInventory, tileEntityRefinery));
        this.playerInventory = playerInventory;
        this.tileEntityRefinery = tileEntityRefinery;
        this.xSize = 176;
        this.ySize = 189;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        if(this.isMouseWithinRegion(startX + 8, startY + 29, 16, 59, mouseX, mouseY))
        {
            this.drawHoveringText(Arrays.asList("Water", TextFormatting.BLUE.toString() + tileEntityRefinery.getWaterLevel() + "/" + TileEntityRefinery.TANK_CAPACITY + " mB"), mouseX, mouseY);
        }

        if(this.isMouseWithinRegion(startX + 80, startY + 29, 16, 59, mouseX, mouseY))
        {
            this.drawHoveringText(Arrays.asList("Ethanol", TextFormatting.YELLOW.toString() + tileEntityRefinery.getEthanolLevel() + "/" + TileEntityRefinery.TANK_CAPACITY + " mB"), mouseX, mouseY);
        }

        if(this.isMouseWithinRegion(startX + 152, startY + 29, 16, 59, mouseX, mouseY))
        {
            this.drawHoveringText(Arrays.asList("Fuelium", TextFormatting.GREEN.toString() + tileEntityRefinery.getFueliumLevel() + "/" + TileEntityRefinery.TANK_CAPACITY + " mB"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileEntityRefinery.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(startX, startY, 0, 0, this.xSize, this.ySize);

        if(tileEntityRefinery.getEthanolMaxProgress() > 0)
        {
            int ethanolProgress = (int) Math.ceil(54 * (tileEntityRefinery.getEthanolProgress() / (double) tileEntityRefinery.getEthanolMaxProgress()));
            this.drawTexturedModalRect(startX + 25, startY + 25, 176, 0, ethanolProgress, 24);
        }

        int fueliumProgress = (int) (54 * (tileEntityRefinery.getFueliumProgress() / (double) TileEntityRefinery.FUELIUM_MAX_PROGRESS));
        this.drawTexturedModalRect(startX + 97, startY + 32, 176, 24, fueliumProgress, 10);

        this.drawFluidTank(FluidRegistry.WATER, startX + 8, startY + 29, tileEntityRefinery.getWaterLevel() / (double) TileEntityRefinery.TANK_CAPACITY, 59, 16);
        this.drawFluidTank(ModFluids.FUELIUM, startX + 80, startY + 29, tileEntityRefinery.getEthanolLevel() / (double) TileEntityRefinery.TANK_CAPACITY, 59, 16);
        this.drawFluidTank(ModFluids.FUELIUM, startX + 152, startY + 29, tileEntityRefinery.getFueliumLevel() / (double) TileEntityRefinery.TANK_CAPACITY, 59, 16);
    }

    private void drawFluidTank(Fluid fluid, int x, int y, double level, double height, int width)
    {
        ResourceLocation resource = fluid.getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = sprite.getMaxU();
            double minV = sprite.getMinV();
            double maxV = sprite.getMaxV();
            double tankLevel = height * level;
            double offsetY = y + (height - tankLevel);


            GlStateManager.enableBlend();

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x, offsetY + tankLevel, 0).tex(minU, maxV).endVertex();
            buffer.pos(x + width, offsetY + tankLevel, 0).tex(maxU, maxV).endVertex();
            buffer.pos(x + width, offsetY, 0).tex(maxU, minV).endVertex();
            buffer.pos(x, offsetY, 0).tex(minU, minV).endVertex();
            tessellator.draw();

            GlStateManager.disableBlend();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(x, y, 176, 34, 16, 59);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
