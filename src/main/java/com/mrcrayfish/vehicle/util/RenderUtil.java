package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

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
        bufferbuilder.pos(x, y + height, 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, y, 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) textureY * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + 0, y, 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float) textureY * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format).
     */
    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor, double zLevel)
    {
        float f = (float) (leftColor >> 24 & 255) / 255.0F;
        float f1 = (float) (leftColor >> 16 & 255) / 255.0F;
        float f2 = (float) (leftColor >> 8 & 255) / 255.0F;
        float f3 = (float) (leftColor & 255) / 255.0F;
        float f4 = (float) (rightColor >> 24 & 255) / 255.0F;
        float f5 = (float) (rightColor >> 16 & 255) / 255.0F;
        float f6 = (float) (rightColor >> 8 & 255) / 255.0F;
        float f7 = (float) (rightColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) right, (double) top, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) left, (double) top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void renderItemModel(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType transform)
    {
        if(!stack.isEmpty())
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

    public static void renderColoredModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, int color)
    {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transformType, false);
        if(!model.isBuiltInRenderer())
        {
            renderModel(model, color);
        }
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
    }

    public static void renderModel(IBakedModel model, ItemCameraTransforms.TransformType transformType)
    {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transformType, false);
        if(!model.isBuiltInRenderer())
        {
            renderModel(model, -1);
        }
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
    }

    public static void renderModel(IBakedModel model, int color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        if(!model.isBuiltInRenderer())
        {
            renderModel(model, color, ItemStack.EMPTY);
        }
        GlStateManager.popMatrix();
    }

    private static void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.ITEM);
        for(EnumFacing face : EnumFacing.values())
        {
            renderQuads(buffer, model.getQuads(null, face, 0L), color, stack);
        }
        renderQuads(buffer, model.getQuads(null, null, 0L), color, stack);
        tessellator.draw();
    }

    private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        boolean useItemColor = color == -1 && !stack.isEmpty();
        for(BakedQuad quad : quads)
        {
            int tintColor = 0xFFFFFFFF;
            if(quad.hasTintIndex())
            {
                if(useItemColor)
                {
                    tintColor = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, quad.getTintIndex());
                }
                else
                {
                    tintColor = color;
                }

                if(EntityRenderer.anaglyphEnable)
                {
                    tintColor = TextureUtil.anaglyphColor(tintColor);
                }

                tintColor = tintColor | -16777216;
            }
            LightUtil.renderQuadColor(renderer, quad, tintColor);
        }
    }

    public static void scissor(int x, int y, int width, int height)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int scale = resolution.getScaleFactor();
        GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    /**
     * Gets a ModelManager instance
     */
    public static ModelManager getModelManager()
    {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
    }

    public static IBakedModel getModel(ItemStack stack)
    {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
    }

    /**
     * Gets an IBakedModel of the wheel currently on a powered vehicle.
     * If there are no wheels installed on the vehicle, a null model will be returned.
     *
     * @param entity the powered vehicle to get the wheel model from
     * @return an IBakedModel of the wheel or null if wheels are not present
     */
    @Nullable
    public static IBakedModel getWheelModel(EntityPoweredVehicle entity)
    {
        if(entity.hasWheels())
        {
            ItemStack stack = new ItemStack(ModItems.WHEEL, 1, entity.getWheelType().ordinal());
            if(!stack.isEmpty())
            {
                return RenderUtil.getModel(stack);
            }
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
    public static IBakedModel getEngineModel(EntityPoweredVehicle entity)
    {
        if(entity.hasEngine())
        {
            ItemStack stack = ItemStack.EMPTY;
            switch(entity.getEngineType())
            {
                case SMALL_MOTOR:
                    stack = new ItemStack(ModItems.SMALL_ENGINE, 1, entity.getEngineTier().ordinal());
                    break;
                case LARGE_MOTOR:
                    stack = new ItemStack(ModItems.LARGE_ENGINE, 1, entity.getEngineTier().ordinal());
                    break;
                case ELECTRIC_MOTOR:
                    stack = new ItemStack(ModItems.ELECTRIC_ENGINE, 1, entity.getEngineTier().ordinal());
                    break;
            }
            if(!stack.isEmpty())
            {
                return RenderUtil.getModel(stack);
            }
        }
        return null;
    }
}
