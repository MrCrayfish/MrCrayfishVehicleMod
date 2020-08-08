package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FuelDrumRenderer extends TileEntityRenderer<FuelDrumTileEntity>
{
    public static final RenderType LABEL_BACKGROUND = RenderType.makeType("vehicle:fuel_drum_label_background", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, RenderType.State.getBuilder().build(false));
    public static final RenderType LABEL_FLUID = RenderType.makeType("vehicle:fuel_drum_label_fluid", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder().texture(new RenderState.TextureState(PlayerContainer.LOCATION_BLOCKS_TEXTURE, false, true)).build(false));

    public FuelDrumRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(FuelDrumTileEntity fuelDrumTileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int lightTexture, int overlayTexture)
    {
        if(Minecraft.getInstance().player.isCrouching())
        {
            if(fuelDrumTileEntity.hasFluid() && this.renderDispatcher.cameraHitResult != null && this.renderDispatcher.cameraHitResult.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult result = (BlockRayTraceResult) this.renderDispatcher.cameraHitResult;
                if(result.getPos().equals(fuelDrumTileEntity.getPos()))
                {
                    this.drawFluidLabel(this.renderDispatcher.fontRenderer, fuelDrumTileEntity.getFluidTank(), matrixStack, renderTypeBuffer);
                }
            }
        }
    }

    private void drawFluidLabel(FontRenderer fontRendererIn, FluidTank tank, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer)
    {
        if(tank.getFluid().isEmpty())
            return;

        FluidStack stack = tank.getFluid();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(tank.getFluid().getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            float level = tank.getFluidAmount() / (float) tank.getCapacity();
            float width = 30F;
            float fuelWidth = width * level;
            float remainingWidth = width - fuelWidth;
            float offsetWidth = width / 2.0F;

            matrixStack.push();
            matrixStack.translate(0.5, 1.25, 0.5);
            matrixStack.rotate(this.renderDispatcher.renderInfo.getRotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);

            IVertexBuilder backgroundBuilder = renderTypeBuffer.getBuffer(LABEL_BACKGROUND);

            /* Background */
            Matrix4f matrix = matrixStack.getLast().getMatrix();
            backgroundBuilder.pos(matrix, -offsetWidth - 1.0F, -2.0F, -0.01F).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth - 1.0F, 5.0F, -0.01F).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth + width + 1.0F, 5.0F, -0.01F).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth + width + 1.0F, -2.0F, -0.01F).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();

            matrixStack.translate(0, 0, -0.05);

            /* Remaining */
            matrix = matrixStack.getLast().getMatrix();
            backgroundBuilder.pos(matrix, -offsetWidth + fuelWidth, -1.0F, 0.0F).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth + fuelWidth, 4.0F, 0.0F).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth + fuelWidth + remainingWidth, 4.0F, 0.0F).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            backgroundBuilder.pos(matrix, -offsetWidth + fuelWidth + remainingWidth, -1.0F, 0.0F).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();

            float minU = sprite.getMinU();
            float maxU = minU + (sprite.getMaxU() - minU) * level;
            float minV = sprite.getMinV();
            float maxV = minV + (sprite.getMaxV() - minV) * 4 * 0.0625F;

            /* Fluid Texture */
            IVertexBuilder fluidBuilder = renderTypeBuffer.getBuffer(LABEL_FLUID);
            fluidBuilder.pos(matrix, -offsetWidth, -1.0F, 0.0F).tex(minU, maxV).endVertex();
            fluidBuilder.pos(matrix, -offsetWidth, 4.0F, 0.0F).tex(minU, minV).endVertex();
            fluidBuilder.pos(matrix, -offsetWidth + fuelWidth, 4.0F, 0.0F).tex(maxU, minV).endVertex();
            fluidBuilder.pos(matrix, -offsetWidth + fuelWidth, -1.0F, 0.0F).tex(maxU, maxV).endVertex();

            /* Fluid Name */
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            String name = stack.getDisplayName().getString();
            int nameWidth = fontRendererIn.getStringWidth(name) / 2;
            fontRendererIn.drawString(matrixStack, name, -nameWidth, -14, -1);

            matrixStack.pop();
        }
    }
}
