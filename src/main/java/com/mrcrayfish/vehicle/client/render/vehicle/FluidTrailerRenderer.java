package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FluidTrailerRenderer extends AbstractTrailerRenderer<FluidTrailerEntity>
{
    public FluidTrailerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable FluidTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 1.25F, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 1.25F, partialTicks, light);

        if(vehicle != null && vehicle.getTank() != null)
        {
            float height = 9.9F * (vehicle.getTank().getFluidAmount() / (float) vehicle.getTank().getCapacity()) * 0.0625F;
            this.drawFluid(vehicle, vehicle.getTank(), matrixStack, renderTypeBuffer, -0.3875F, -0.1875F, -0.99F, 0.7625F, height, 1.67F, light);
        }
    }

    private void drawFluid(FluidTrailerEntity vehicle, FluidTank tank, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluid.getFluid().getAttributes().getStillTexture());

        int fluidColor = fluid.getAttributes().getColor(vehicle.getCommandSenderWorld(), vehicle.blockPosition());
        float red = (float) (fluidColor >> 16 & 255) / 255.0F;
        float green = (float) (fluidColor >> 8 & 255) / 255.0F;
        float blue = (float) (fluidColor & 255) / 255.0F;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * width, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.translucentNoCrumbling());
        Matrix4f matrix = matrixStack.last().pose();

        //left side
        buffer.vertex(matrix, x + width, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        buffer.vertex(matrix, x, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());

        buffer.vertex(matrix, x, y + height, z).color(red, green, blue, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.FLUID_TRAILER, parts, transforms);
        };
    }
}
