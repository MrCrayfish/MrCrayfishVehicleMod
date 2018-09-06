package com.mrcrayfish.vehicle.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FluidUtils
{
    public static int transferFluid(IFluidHandler source, IFluidHandler target, int maxAmount)
    {
        FluidStack drained = source.drain(maxAmount, false);
        if(drained != null && drained.amount > 0)
        {
            int filled = target.fill(drained, false);
            if(filled > 0)
            {
                drained = source.drain(filled, true);
                return target.fill(drained, true);
            }
        }
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public static void drawFluidTankInGUI(FluidStack fluid, double x, double y, double percent, int height)
    {
        if(fluid == null)
            return;

        ResourceLocation resource = fluid.getFluid().getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = sprite.getMaxU();
            double minV = sprite.getMinV();
            double maxV = sprite.getMaxV();
            double deltaV = maxV - minV;
            double tankLevel = percent * height;

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.enableBlend();

            int count = 1 + ((int) Math.ceil(tankLevel)) / 16;
            for(int i = 0; i < count; i++)
            {
                double subHeight = Math.min(16.0, tankLevel - (16.0 * i));
                double offsetY = height - 16.0 * i - subHeight;
                drawQuad(x, y + offsetY, 16, subHeight, minU, maxV - deltaV * (subHeight / 16.0), maxU, maxV);
            }

            GlStateManager.disableBlend();
        }
    }

    private static void drawQuad(double x, double y, double width, double height, double minU, double minV, double maxU, double maxV)
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

    public static void fixEmptyTag(NBTTagCompound tag)
    {
        if(tag.hasKey("FluidName", Constants.NBT.TAG_STRING) && tag.hasKey("Amount", Constants.NBT.TAG_INT))
        {
            tag.removeTag("Empty");
        }
    }
}
