package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FluidTrailerRenderer extends AbstractTrailerRenderer<FluidTrailerEntity>
{
    protected final PropertyFunction<FluidTrailerEntity, FluidTank> fluidTankProperty = new PropertyFunction<>(FluidTrailerEntity::getTank, new FluidTank(FluidAttributes.BUCKET_VOLUME));

    public FluidTrailerRenderer(EntityType<FluidTrailerEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    public void render(@Nullable FluidTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);

        FluidTank tank = this.fluidTankProperty.get();
        float height = 9.9F * (tank.getFluidAmount() / (float) tank.getCapacity()) * 0.0625F;
        this.drawFluid(vehicle, tank, matrixStack, renderTypeBuffer, -0.3875F, -0.1875F, -0.99F, 0.7625F, height, 1.67F, light);
    }

    private void drawFluid(@Nullable FluidTrailerEntity vehicle, FluidTank tank, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluid.getFluid().getAttributes().getStillTexture());

        int fluidColor = vehicle != null ? fluid.getAttributes().getColor(vehicle.getCommandSenderWorld(), vehicle.blockPosition()) : 0xFF3F76E4;
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
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.FLUID_TRAILER, parts, transforms);
        };
    }
}
