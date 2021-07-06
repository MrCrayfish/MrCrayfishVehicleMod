package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.GasPumpBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class GasPumpRenderer extends TileEntityRenderer<GasPumpTileEntity>
{
    public GasPumpRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(GasPumpTileEntity gasPump, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay)
    {
        BlockState state = gasPump.getBlockState();
        if(state.getBlock() != ModBlocks.GAS_PUMP.get())
            return;

        if(!state.getValue(GasPumpBlock.TOP))
            return;

        Direction facing = state.getValue(GasPumpBlock.DIRECTION);
        double[] hoseStartPos = CollisionHelper.fixRotation(facing, 0.620625, 1.05, 0.620625, 1.05);

        // Code to make hose connect to the fuel port on vehicles
       /* List<VehicleEntity> vehicles = te.getWorld().getEntitiesWithinAABB(VehicleEntity.class, new AxisAlignedBB(te.getPos()).grow(5.0));
        if(vehicles.size() == 0)
            return;

        VehicleEntity vehicle = vehicles.get(0);
        VehicleProperties properties = VehicleProperties.getProperties(vehicle.getClass());
        PartPosition position = properties.getFuelPortPosition();
        if(position == null)
            return;

        Vector3d fuelVec = vehicle.getPartPositionAbsoluteVec(position, partialTicks);
        double fuelX = (double) blockPos.getX() - fuelVec.x;
        double fuelY = (double) blockPos.getY() - fuelVec.y;
        double fuelZ = (double) blockPos.getZ() - fuelVec.z;

        Vector3d fuelRot = Vector3d.fromPitchYaw((float) position.getRotX(), (float) position.getRotY());
        fuelRot = fuelRot.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)).normalize();*/
       
        matrixStack.pushPose();

        if(gasPump.getFuelingEntity() != null)
        {
            PlayerEntity player = gasPump.getFuelingEntity();
            Vector3d nozzleVec = this.getNozzlePosition(player, gasPump.getBlockPos(), partialTicks);
            Vector3d lookVec = this.getLookVector(player, partialTicks);
            HermiteInterpolator.Point nozzlePoint = new HermiteInterpolator.Point(nozzleVec, new Vector3d(lookVec.x * 3, lookVec.y * 3, lookVec.z * 3));
            gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vector3d(hoseStartPos[0], 0.6425, hoseStartPos[1]), new Vector3d(0, -5, 0)), nozzlePoint));
            gasPump.setRecentlyUsed(true);
        }
        else if(gasPump.getCachedSpline() == null || gasPump.isRecentlyUsed())
        {
            double[] nozzlePos = CollisionHelper.fixRotation(facing, 0.345, 1.06, 0.345, 1.06);
            HermiteInterpolator.Point nozzlePoint = new HermiteInterpolator.Point(new Vector3d(nozzlePos[0], 0.1, nozzlePos[1]), new Vector3d(0, 3, 0));
            gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vector3d(hoseStartPos[0], 0.6425, hoseStartPos[1]), new Vector3d(0, -5, 0)), nozzlePoint));
            gasPump.setRecentlyUsed(false);
        }

        this.drawHose(gasPump.getCachedSpline(), matrixStack, renderTypeBuffer, light, this.getHoseColour(gasPump));

        // Renders the nozzle model on the gas pump if no one is using it
        if(gasPump.getFuelingEntity() == null)
        {
            matrixStack.pushPose();
            double[] nozzlePos = CollisionHelper.fixRotation(facing, 0.29, 1.06, 0.29, 1.06);
            matrixStack.translate(nozzlePos[0], 0.5, nozzlePos[1]);
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(facing.get2DDataValue() * -90F));
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(90F));
            matrixStack.scale(0.8F, 0.8F, 0.8F);
            RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    private void drawHose(@Nullable HermiteInterpolator spline, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, Triple<Float, Float, Float> color)
    {
        if(spline == null)
            return;

        float red = color.getLeft();
        float green = color.getMiddle();
        float blue = color.getRight();
        float diameter = 0.0625F;

        matrixStack.pushPose();

        IVertexBuilder builder = buffer.getBuffer(RenderType.leash());

        int segments = Config.CLIENT.hoseSegments.get();
        for(int i = 0; i < spline.getSize() - 1; i++)
        {
            for(int j = 0; j < segments; j++)
            {
                float percent = j / (float) segments;
                HermiteInterpolator.Result start = spline.get(i, percent);
                HermiteInterpolator.Result end = spline.get(i, (float) (j + 1) / (float) segments);

                Matrix4f startMatrix = new Matrix4f();
                startMatrix.setIdentity();
                EntityRayTracer.MatrixTransformation.createTranslation((float) start.getPoint().x(), (float) start.getPoint().y(), (float) start.getPoint().z()).transform(startMatrix);
                if(i == 0 && j == 0)
                {
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z))).transform(startMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) Math.toDegrees(Math.asin(-end.getDir().normalize().y))).transform(startMatrix);
                }
                else
                {
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z))).transform(startMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) Math.toDegrees(Math.asin(-start.getDir().normalize().y))).transform(startMatrix);
                }

                Matrix4f endMatrix = new Matrix4f();
                endMatrix.setIdentity();
                EntityRayTracer.MatrixTransformation.createTranslation((float) end.getPoint().x, (float) end.getPoint().y, (float) end.getPoint().z).transform(endMatrix);
                if(i == spline.getSize() - 2 && j == segments - 1)
                {
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z))).transform(endMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) Math.toDegrees(Math.asin(-start.getDir().normalize().y))).transform(endMatrix);
                }
                else
                {
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z))).transform(endMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) Math.toDegrees(Math.asin(-end.getDir().normalize().y))).transform(endMatrix);
                }

                Matrix4f startTemp = new Matrix4f(startMatrix);
                Matrix4f endTemp = new Matrix4f(endMatrix);
                Matrix4f parent = matrixStack.last().pose();

                EntityRayTracer.MatrixTransformation.createTranslation(diameter / 2, -diameter / 2, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, diameter, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter / 2, diameter / 2, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, -diameter, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);

                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, -diameter, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);

                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, diameter, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, diameter, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);

                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0, 0).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0, 0).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
            }
        }

        matrixStack.popPose();
    }

    private Triple<Float, Float, Float> getHoseColour(GasPumpTileEntity gasPump)
    {
        float red = 0.05F;
        float green = 0.05F;
        float blue = 0.05F;

        // Makes the hose turn to red when it's near the max hose distance
        if(gasPump.getFuelingEntity() != null)
        {
            red = (float) (Math.sqrt(gasPump.getFuelingEntity().distanceToSqr(gasPump.getBlockPos().getX() + 0.5, gasPump.getBlockPos().getY() + 0.5, gasPump.getBlockPos().getZ() + 0.5)) / Config.SERVER.maxHoseDistance.get());
            red = red * red * red * red * red * red;
            red = Math.max(red, 0.05F);
        }

        return Triple.of(red, green, blue);
    }

    private void createVertex(IVertexBuilder buffer, Matrix4f parent, Matrix4f pos, float red, float green, float blue, int light)
    {
        Vector4f vec = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        vec.transform(pos);
        buffer.vertex(parent, vec.x(), vec.y(), vec.z()).color(red, green, blue, 1.0F).uv2(light).endVertex();
    }

    private boolean isSlimModel(PlayerEntity player)
    {
        if(player instanceof AbstractClientPlayerEntity)
        {
            String skinType = ((AbstractClientPlayerEntity) player).getModelName();
            return skinType.equals("slim");
        }
        return false;
    }

    private float getPlayerBodyRotation(PlayerEntity player, float partialTicks)
    {
        return player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks;
    }

    private Vector3d getNozzlePosition(PlayerEntity player, BlockPos pos, float partialTicks)
    {
        double playerX = (double) pos.getX() - (player.xo + (player.getX() - player.xo) * partialTicks);
        double playerY = (double) pos.getY() - (player.yo + (player.getY() - player.yo) * partialTicks);
        double playerZ = (double) pos.getZ() - (player.zo + (player.getZ() - player.zo) * partialTicks);
        Vector3d playerVec = new Vector3d(-playerX, -playerY + 0.8, -playerZ);

        Minecraft minecraft = Minecraft.getInstance();
        if(player.equals(minecraft.player) && minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
        {
            return playerVec.add(new Vector3d(-0.25, 0.5, -0.25).yRot(-player.yRot * 0.017453292F));
        }

        double handSide = player.getMainArm() == HandSide.RIGHT ? 1 : -1;
        Vector3d nozzlePos = new Vector3d(-0.35 * handSide, -0.025, -0.025);
        if(this.isSlimModel(player))
        {
            nozzlePos = nozzlePos.add(0.03 * handSide, -0.03, 0.0);
        }

        float bodyRotation = this.getPlayerBodyRotation(player, partialTicks);
        nozzlePos = nozzlePos.yRot(-bodyRotation * 0.017453292F);
        return playerVec.add(nozzlePos);
    }

    private Vector3d getLookVector(PlayerEntity player, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(player.equals(minecraft.player) && minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
        {
            return Vector3d.directionFromRotation(0F, player.yRot);
        }

        float bodyRotation = this.getPlayerBodyRotation(player, partialTicks);
        return Vector3d.directionFromRotation(-20F, bodyRotation);
    }
}
