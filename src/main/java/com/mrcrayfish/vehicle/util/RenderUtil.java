package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

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
        bufferbuilder.pos(x, y + height, 0).tex(((float) textureX * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).tex(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y, 0).tex(((float) (textureX + width) * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + 0, y, 0).tex(((float) textureX * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format).
     */
    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor)
    {
        float redStart = (float)(leftColor >> 24 & 255) / 255.0F;
        float greenStart = (float)(leftColor >> 16 & 255) / 255.0F;
        float blueStart = (float)(leftColor >> 8 & 255) / 255.0F;
        float alphaStart = (float)(leftColor & 255) / 255.0F;
        float redEnd = (float)(rightColor >> 24 & 255) / 255.0F;
        float greenEnd = (float)(rightColor >> 16 & 255) / 255.0F;
        float blueEnd = (float)(rightColor >> 8 & 255) / 255.0F;
        float alphaEnd = (float)(rightColor & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0).color(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0).color(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0).color(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0).color(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public static void scissor(int x, int y, int width, int height) //TODO might need fixing. I believe I rewrote this in a another mod
    {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.mainWindow.getGuiScaleFactor();
        GL11.glScissor(x * scale, mc.mainWindow.getHeight() - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    public static IBakedModel getModel(ItemStack stack)
    {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(stack);
    }

    public static void renderColoredModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, int color)
    {
        GlStateManager.pushMatrix();
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transformType, leftHanded);
        if(!model.isBuiltInRenderer())
        {
            renderModel(model, ItemStack.EMPTY, color);
        }
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
    }

    public static void renderDamagedVehicleModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, int stage, int color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transformType, leftHanded);
        if(!model.isBuiltInRenderer())
        {
            Minecraft mc = Minecraft.getInstance(); //TODO figure out how to bind damaged texture
            renderModel(model, ItemStack.EMPTY, color);
        }
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderModel(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHanded, IBakedModel model)
    {
        if(!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            boolean isGui = transformType == ItemCameraTransforms.TransformType.GUI;
            boolean tridentFlag = isGui || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
            if(stack.getItem() == Items.TRIDENT && tridentFlag)
            {
                model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transformType, leftHanded);
            GlStateManager.translated(-0.5, -0.5, -0.5);
            if(!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || tridentFlag))
            {
                renderModel(model, stack, -1);
            }
            else
            {
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            }

            GlStateManager.popMatrix();
        }
    }

    private static void renderModel(IBakedModel model, ItemStack stack, int color)
    {
        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
        Random random = new Random();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.ITEM);
        for(Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderQuads(buffer, model.getQuads(null, direction, random), stack, color);
        }
        random.setSeed(42L);
        renderQuads(buffer, model.getQuads(null, null, random), stack, color);
        tessellator.draw();
    }

    private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, ItemStack stack, int color)
    {
        boolean useItemColor = !stack.isEmpty() && color != -1;
        for(BakedQuad quad : quads)
        {
            int tintColor = 0xFFFFFFFF;
            if(quad.hasTintIndex())
            {
                if(useItemColor)
                {
                    tintColor = Minecraft.getInstance().getItemColors().getColor(stack, quad.getTintIndex());
                }
                else
                {
                    tintColor = color;
                }
                tintColor = tintColor | -16777216;
            }
            LightUtil.renderQuadColor(renderer, quad, tintColor);
        }
    }

    /**
     * Gets an IBakedModel of the wheel currently on a powered vehicle.
     * If there are no wheels installed on the vehicle, a null model will be returned.
     *
     * @param entity the powered vehicle to get the wheel model from
     * @return an IBakedModel of the wheel or null if wheels are not present
     */
    @Nullable
    public static IBakedModel getWheelModel(PoweredVehicleEntity entity)
    {
        ItemStack stack = ItemLookup.getWheel(entity);
        if(!stack.isEmpty())
        {
            return RenderUtil.getModel(stack);
        }
        return null;
    }

    /**
     * Gets an IBakedModel of the engine currently on a powered vehicle.
     * If there is no engine installed in the vehicle, a null model will be returned.
     *
     * @param entity the powered vehicle to get the engine model from
     * @return an IBakedModel of the engine or null if the engine is not present
     */
    @Nullable
    public static IBakedModel getEngineModel(PoweredVehicleEntity entity)
    {
        ItemStack stack = ItemLookup.getEngine(entity);
        if(!stack.isEmpty())
        {
            return RenderUtil.getModel(stack);
        }
        return null;
    }
}
