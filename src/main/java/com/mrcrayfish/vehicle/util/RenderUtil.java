package com.mrcrayfish.vehicle.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Author: MrCrayfish
 */
public class RenderUtil
{
    /**
     * Draws a textured modal rectangle with more precision than GuiScreen's methods. This will only
     * work correctly if the bound texture is 256x256.
     */
    public static void drawTexturedModalRect(double x, double y, int textureX, int textureY, double width, double height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y, 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) textureY * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + 0, y, 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float)textureY * 0.00390625F)).endVertex();
        tessellator.draw();
    }
}
