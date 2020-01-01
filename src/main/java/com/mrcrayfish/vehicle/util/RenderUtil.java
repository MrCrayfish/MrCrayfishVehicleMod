package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import org.lwjgl.opengl.GL11;

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
        bufferbuilder.func_225582_a_(x, y + height, 0).func_225583_a_(((float) textureX * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.func_225582_a_(x + width, y + height, 0).func_225583_a_(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.func_225582_a_(x + width, y, 0).func_225583_a_(((float) (textureX + width) * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        bufferbuilder.func_225582_a_(x + 0, y, 0).func_225583_a_(((float) textureX * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format).
     */
    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor, double zLevel)
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
        bufferbuilder.func_225582_a_((double)right, (double)top, zLevel).func_227885_a_(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        bufferbuilder.func_225582_a_((double)left, (double)top, zLevel).func_227885_a_(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.func_225582_a_((double)left, (double)bottom, zLevel).func_227885_a_(greenStart, blueStart, alphaStart, redStart).endVertex();
        bufferbuilder.func_225582_a_((double)right, (double)bottom, zLevel).func_227885_a_(greenEnd, blueEnd, alphaEnd, redEnd).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public static void scissor(int x, int y, int width, int height) //TODO might need fixing. I believe I rewrote this in a another mod
    {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.func_228018_at_().getGuiScaleFactor();
        GL11.glScissor(x * scale, mc.func_228018_at_().getHeight() - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    public static IBakedModel getModel(ItemStack stack)
    {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(stack);
    }

    public static void renderColoredModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int color, int lightTexture, int overlayTexture)
    {
        matrixStack.func_227860_a_();
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        matrixStack.func_227861_a_(-0.5, -0.5, -0.5);
        if(!model.isBuiltInRenderer())
        {
            IVertexBuilder vertexBuilder = ItemRenderer.func_229113_a_(renderTypeBuffer, Atlases.func_228784_i_(), true, false);
            renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.func_227865_b_();
    }

    public static void renderDamagedVehicleModel(IBakedModel model, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, int stage, int color, int lightTexture, int overlayTexture)
    {
        matrixStack.func_227860_a_();
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        matrixStack.func_227861_a_(-0.5, -0.5, -0.5);
        if(!model.isBuiltInRenderer())
        {
            Minecraft mc = Minecraft.getInstance();
            IVertexBuilder vertexBuilder = new MatrixApplyingVertexBuilder(mc.func_228019_au_().func_228489_c_().getBuffer(ModelBakery.field_229320_k_.get(stage)), matrixStack.func_227866_c_());
            renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.func_227865_b_();
    }

    public static void renderModel(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHanded, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int lightTexture, int overlayTexture, IBakedModel model)
    {
        if(!stack.isEmpty())
        {
            matrixStack.func_227860_a_();
            boolean isGui = transformType == ItemCameraTransforms.TransformType.GUI;
            boolean tridentFlag = isGui || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
            if(stack.getItem() == Items.TRIDENT && tridentFlag)
            {
                model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
            matrixStack.func_227861_a_(-0.5, -0.5, -0.5);
            if(!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || tridentFlag))
            {
                RenderType renderType = RenderTypeLookup.func_228389_a_(stack);
                if(isGui && Objects.equals(renderType, Atlases.func_228784_i_()))
                {
                    renderType = Atlases.func_228785_j_();
                }
                IVertexBuilder vertexBuilder = ItemRenderer.func_229113_a_(renderTypeBuffer, renderType, true, stack.hasEffect());
                renderModel(model, stack, -1, lightTexture, overlayTexture, matrixStack, vertexBuilder);
            }
            else
            {
                stack.getItem().getTileEntityItemStackRenderer().func_228364_a_(stack, matrixStack, renderTypeBuffer, lightTexture, overlayTexture);
            }

            matrixStack.func_227865_b_();
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
        MatrixStack.Entry entry = matrixStack.func_227866_c_();
        for(BakedQuad quad : quads)
        {
            if(useItemColor && quad.hasTintIndex())
            {
                color = Minecraft.getInstance().getItemColors().getColor(stack, quad.getTintIndex());
            }
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            vertexBuilder.func_227889_a_(entry, quad, red, green, blue, lightTexture, overlayTexture);
        }
    }
}
