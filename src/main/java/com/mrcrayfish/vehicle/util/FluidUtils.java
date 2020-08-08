package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FluidUtils
{
    private static final Map<ResourceLocation, Integer> CACHE_FLUID_COLOR = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void clearCacheFluidColor()
    {
        CACHE_FLUID_COLOR.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static int getAverageFluidColor(Fluid fluid)
    {
        int fluidColor = -1;
        Integer colorCashed = CACHE_FLUID_COLOR.get(fluid.getRegistryName());
        if (colorCashed != null )
        {
            fluidColor = colorCashed;
        }
        else
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getFluid().getAttributes().getStillTexture());
            if(sprite != null)
            {
                long totalRed = 0;
                long totalGreen = 0;
                long totalBlue = 0;
                int pixelCount = sprite.getWidth() * sprite.getHeight();
                int red, green, blue;
                for(int i = 0; i < sprite.getHeight(); i++)
                {
                    for(int j = 0; j < sprite.getWidth(); j++)
                    {
                        int color = sprite.getPixelRGBA(0, j, i);
                        red = color & 255;
                        green = color >> 8 & 255;
                        blue = color >> 16 & 255;
                        totalRed += red * red;
                        totalGreen += green * green;
                        totalBlue += blue * blue;
                    }
                }
                fluidColor = (((int) Math.sqrt(totalRed / pixelCount) & 255) << 16)
                        | (((int) Math.sqrt(totalGreen / pixelCount) & 255) << 8) | (((int) Math.sqrt(totalBlue / pixelCount) & 255));
            }
            CACHE_FLUID_COLOR.put(fluid.getRegistryName(), fluidColor);
        }
        return fluidColor;
    }

    public static int transferFluid(IFluidHandler source, IFluidHandler target, int maxAmount)
    {
        FluidStack drained = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if(drained.getAmount() > 0)
        {
            int filled = target.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if(filled > 0)
            {
                drained = source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                return target.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidTankInGUI(FluidStack fluid, double x, double y, double percent, int height)
    {
        if(fluid == null || fluid.isEmpty())
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();
            float deltaV = maxV - minV;
            double tankLevel = percent * height;

            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            RenderSystem.enableBlend();
            int count = 1 + ((int) Math.ceil(tankLevel)) / 16;
            for(int i = 0; i < count; i++)
            {
                double subHeight = Math.min(16.0, tankLevel - (16.0 * i));
                double offsetY = height - 16.0 * i - subHeight;
                drawQuad(x, y + offsetY, 16, subHeight, minU, (float) (maxV - deltaV * (subHeight / 16.0)), maxU, maxV);
            }
            RenderSystem.disableBlend();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawQuad(double x, double y, double width, double height, float minU, float minV, float maxU, float maxV)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
        buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}
