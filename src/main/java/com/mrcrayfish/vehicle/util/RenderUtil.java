package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
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
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0).color(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0).color(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0).color(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0).color(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public static void scissor(int x, int y, int width, int height) //TODO might need fixing. I believe I rewrote this in a another mod
    {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.getMainWindow().getGuiScaleFactor();
        GL11.glScissor(x * scale, mc.getMainWindow().getHeight() - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    public static IBakedModel getModel(ItemStack stack)
    {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(stack);
    }

    public static void renderColoredModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int color, int lightTexture, int overlayTexture)
    {
        matrixStack.push();
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        matrixStack.translate(-0.5, -0.5, -0.5);
        if(!model.isBuiltInRenderer())
        {
            IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(Atlases.getCutoutBlockType());
            renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.pop();
    }

    public static void renderDamagedVehicleModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, int stage, int color, int lightTexture, int overlayTexture)
    {
        matrixStack.push();
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        matrixStack.translate(-0.5, -0.5, -0.5);
        if(!model.isBuiltInRenderer())
        {
            Minecraft mc = Minecraft.getInstance();
            MatrixStack.Entry entry = matrixStack.getLast();
            IVertexBuilder vertexBuilder = new MatrixApplyingVertexBuilder(mc.getRenderTypeBuffers().getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(stage)), entry.getMatrix(), entry.getNormal());
            renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.pop();
    }

    public static void renderModel(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int lightTexture, int overlayTexture, IBakedModel model)
    {
        if(!stack.isEmpty())
        {
            matrixStack.push();
            boolean isGui = transformType == ItemCameraTransforms.TransformType.GUI;
            boolean tridentFlag = isGui || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
            if(stack.getItem() == Items.TRIDENT && tridentFlag)
            {
                model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
            matrixStack.translate(-0.5, -0.5, -0.5);
            if(!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || tridentFlag))
            {
                RenderType renderType = RenderTypeLookup.func_239219_a_(stack, false); //TODO test what this flag does
                if(isGui && Objects.equals(renderType, Atlases.getTranslucentCullBlockType()))
                {
                    renderType = Atlases.getTranslucentCullBlockType();
                }
                IVertexBuilder vertexBuilder = ItemRenderer.getBuffer(renderTypeBuffer, renderType, true, stack.hasEffect());
                renderModel(model, stack, -1, lightTexture, overlayTexture, matrixStack, vertexBuilder);
            }
            else
            {
                stack.getItem().getItemStackTileEntityRenderer().func_239207_a_(stack, transformType, matrixStack, renderTypeBuffer, lightTexture, overlayTexture);
            }

            matrixStack.pop();
        }
    }

    private static void renderModel(IBakedModel model, ItemStack stack, int color, int lightTexture, int overlayTexture, MatrixStack matrixStack, IVertexBuilder vertexBuilder)
    {
        Random random = new Random();
        for(Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderQuads(matrixStack, vertexBuilder, model.getQuads(null, direction, random), stack, color, lightTexture, overlayTexture);
        }
        random.setSeed(42L);
        renderQuads(matrixStack, vertexBuilder, model.getQuads(null, null, random), stack, color, lightTexture, overlayTexture);
    }

    private static void renderQuads(MatrixStack matrixStack, IVertexBuilder vertexBuilder, List<BakedQuad> quads, ItemStack stack, int color, int lightTexture, int overlayTexture)
    {
        boolean useItemColor = !stack.isEmpty() && color == -1;
        MatrixStack.Entry entry = matrixStack.getLast();
        for(BakedQuad quad : quads)
        {
            int tintColor = 0xFFFFFF;
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
            }
            float red = (float) (tintColor >> 16 & 255) / 255.0F;
            float green = (float) (tintColor >> 8 & 255) / 255.0F;
            float blue = (float) (tintColor & 255) / 255.0F;
            vertexBuilder.addVertexData(entry, quad, red, green, blue, lightTexture, overlayTexture, true);
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
