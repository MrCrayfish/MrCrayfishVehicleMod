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
import net.minecraft.client.gui.FontRenderer;
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
        BlockPos blockPos = gasPump.getBlockPos();
        BlockState state = gasPump.getLevel().getBlockState(blockPos);
        if(state.getBlock() != ModBlocks.GAS_PUMP.get())
        {
            return;
        }

        boolean top = state.getValue(GasPumpBlock.TOP);
        if(!top)
        {
            return;
        }

        Direction facing = state.getValue(GasPumpBlock.DIRECTION);
        double[] pos = CollisionHelper.fixRotation(facing, 0.620625, 1.05, 0.620625, 1.05);

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
        {
            if(gasPump.getFuelingEntity() != null)
            {
                gasPump.setRecentlyUsed(true);
                PlayerEntity entity = gasPump.getFuelingEntity();
                double side = entity.getMainArm() == HandSide.RIGHT ? 1 : -1;
                double playerX = (double) blockPos.getX() - (entity.xo + (entity.getX() - entity.xo) * partialTicks);
                double playerY = (double) blockPos.getY() - (entity.yo + (entity.getY() - entity.yo) * partialTicks);
                double playerZ = (double) blockPos.getZ() - (entity.zo + (entity.getZ() - entity.zo) * partialTicks);
                float renderYawOffset = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
                Vector3d lookVec = Vector3d.directionFromRotation(-20F, renderYawOffset);
                Vector3d hoseVec = new Vector3d(-0.35 * side, -0.025, -0.025);
                if(entity instanceof AbstractClientPlayerEntity)
                {
                    String skinType = ((AbstractClientPlayerEntity) entity).getModelName();
                    if(skinType.equals("slim"))
                    {
                        hoseVec = hoseVec.add(0.03 * side, -0.03, 0.0);
                    }
                }
                hoseVec = hoseVec.yRot(-renderYawOffset * 0.017453292F);
                if(entity.equals(Minecraft.getInstance().player))
                {
                    if(Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON)
                    {
                        lookVec = Vector3d.directionFromRotation(0F, entity.yRot);
                        hoseVec = new Vector3d(-0.25, 0.5, -0.25).yRot(-entity.yRot * 0.017453292F);
                    }
                }
                HermiteInterpolator.Point destPoint = new HermiteInterpolator.Point(new Vector3d(-playerX + hoseVec.x, -playerY + 0.8 + hoseVec.y, -playerZ + hoseVec.z), new Vector3d(lookVec.x * 3, lookVec.y * 3, lookVec.z * 3));
                gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vector3d(pos[0], 0.6425, pos[1]), new Vector3d(0, -5, 0)), destPoint));
            }
            else
            {
                if(gasPump.getCachedSpline() == null || gasPump.isRecentlyUsed())
                {
                    gasPump.setRecentlyUsed(false);
                    double[] destPos = CollisionHelper.fixRotation(facing, 0.345, 1.06, 0.345, 1.06);
                    HermiteInterpolator.Point destPoint = new HermiteInterpolator.Point(new Vector3d(destPos[0], 0.1, destPos[1]), new Vector3d(0, 3, 0));
                    gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vector3d(pos[0], 0.6425, pos[1]), new Vector3d(0, -5, 0)), destPoint));
                }
            }

            //new HermiteInterpolator.Point(new Vector3d(-fuelX, -fuelY, -fuelZ), new Vector3d(fuelRot.x * 3, -fuelRot.y * 3, fuelRot.z * 3))
            //new HermiteInterpolator.Point(new Vector3d(-x + v.x / 2, -y + 1.5 + v.y / 2, -z + v.z / 2), new Vector3d(v.x * 5, v.y, v.z * 5))
            //new HermiteInterpolator.Point(new Vector3d(-x + v.x / 2, -y + 1.25, -z + v.z / 2), new Vector3d(-x + v.x * 10, -y, -z + v.z * 10))

            HermiteInterpolator spline = gasPump.getCachedSpline();
            if(spline != null)
            {
                float gray = 0.05F;
                float red = gray;
                float hoseDiameter = 0.07F;
                if(gasPump.getFuelingEntity() != null)
                {
                    red = (float) (Math.sqrt(gasPump.getFuelingEntity().distanceToSqr(gasPump.getBlockPos().getX() + 0.5, gasPump.getBlockPos().getY() + 0.5, gasPump.getBlockPos().getZ() + 0.5)) / Config.SERVER.maxHoseDistance.get());
                    red = red * red * red * red * red * red;
                    red = Math.max(red, gray);
                }

                matrixStack.pushPose();

                IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.leash());

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

                        EntityRayTracer.MatrixTransformation.createTranslation(hoseDiameter / 2, -hoseDiameter / 2, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(hoseDiameter / 2, hoseDiameter / 2, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, -hoseDiameter, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);

                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(-hoseDiameter, -hoseDiameter, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);

                        EntityRayTracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);

                        EntityRayTracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(startTemp);
                        this.createVertex(builder, parent, startTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                        EntityRayTracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(endTemp);
                        this.createVertex(builder, parent, endTemp, red, gray, light);
                    }
                }

                matrixStack.popPose();
            }

            if(gasPump.getFuelingEntity() == null)
            {
                matrixStack.pushPose();
                {
                    double[] destPos = CollisionHelper.fixRotation(facing, 0.29, 1.06, 0.29, 1.06);
                    matrixStack.translate(destPos[0], 0.5, destPos[1]);
                    matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(facing.get2DDataValue() * -90F));
                    matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
                    matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(90F));
                    matrixStack.scale(0.8F, 0.8F, 0.8F);
                    RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
                }
                matrixStack.popPose();
            }

            matrixStack.pushPose();
            {
                matrixStack.translate(0.5, 0, 0.5);
                matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(facing.get2DDataValue() * -90F));
                matrixStack.translate(-0.5, 0, -0.5);
                matrixStack.translate(0.5, 11 * 0.0625, 3 * 0.0625);
                matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));

                matrixStack.translate(0F, 0F, 0.01F);

                matrixStack.pushPose();
                {
                    matrixStack.scale(0.015F, -0.015F, 0.015F);
                    FontRenderer fontRenderer = this.renderer.font;
                    if(gasPump.getTank() != null)
                    {
                        int amount = (int) Math.ceil(100 * (gasPump.getTank().getFluidAmount() / (double) gasPump.getTank().getCapacity()));
                        String percent = String.format("%d%%", amount);
                        int width = fontRenderer.width(percent);
                        fontRenderer.drawInBatch(percent, -width / 2, 10, 16777215, false, matrixStack.last().pose(), renderTypeBuffer, false, 0, light);
                    }
                }
                matrixStack.popPose();

                matrixStack.pushPose();
                {
                    matrixStack.translate(0, 1 * 0.0625, 0);
                    matrixStack.scale(0.01F, -0.01F, 0.01F);
                    FontRenderer fontRenderer = this.renderer.font;
                    int width = fontRenderer.width("Fuelium");
                    fontRenderer.drawInBatch("Fuelium", -width / 2, 10, 9761325, false, matrixStack.last().pose(), renderTypeBuffer, false, 0, light);
                }
                matrixStack.popPose();
            }
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    private void createVertex(IVertexBuilder buffer, Matrix4f parent, Matrix4f pos, float red, float gray, int light)
    {
        Vector4f vec = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        vec.transform(pos);
        buffer.vertex(parent, vec.x(), vec.y(), vec.z()).color(red, gray, gray, 1.0F).uv2(light).endVertex();
    }
}
