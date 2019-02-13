package com.mrcrayfish.vehicle.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

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

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format).
     */
    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor, double zLevel)
    {
        float f = (float)(leftColor >> 24 & 255) / 255.0F;
        float f1 = (float)(leftColor >> 16 & 255) / 255.0F;
        float f2 = (float)(leftColor >> 8 & 255) / 255.0F;
        float f3 = (float)(leftColor & 255) / 255.0F;
        float f4 = (float)(rightColor >> 24 & 255) / 255.0F;
        float f5 = (float)(rightColor >> 16 & 255) / 255.0F;
        float f6 = (float)(rightColor >> 8 & 255) / 255.0F;
        float f7 = (float)(rightColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)left, (double)top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void renderItemModel(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType transform)
    {
        if (!stack.isEmpty())
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transform, false);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        }
    }

    public static void scissor(int x, int y, int width, int height)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int scale = resolution.getScaleFactor();
        GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }
}
