package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.BlockGasPump;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4d;

/**
 * Author: MrCrayfish
 */
public class GasPumpRenderer extends TileEntityRenderer<GasPumpTileEntity>
{
    @Override
    public void render(GasPumpTileEntity gasPump, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockPos blockPos = gasPump.getPos();
        BlockState state = gasPump.getWorld().getBlockState(blockPos);
        if(state.getBlock() != ModBlocks.GAS_PUMP.get())
        {
            return;
        }

        boolean top = state.get(BlockGasPump.TOP);
        if(!top)
        {
            return;
        }

        Direction facing = state.get(BlockGasPump.DIRECTION);
        double[] pos = CollisionHelper.fixRotation(facing, 0.640625, 1.078125, 0.640625, 1.078125);

       /* List<VehicleEntity> vehicles = te.getWorld().getEntitiesWithinAABB(VehicleEntity.class, new AxisAlignedBB(te.getPos()).grow(5.0));
        if(vehicles.size() == 0)
            return;

        VehicleEntity vehicle = vehicles.get(0);
        VehicleProperties properties = VehicleProperties.getProperties(vehicle.getClass());
        PartPosition position = properties.getFuelPortPosition();
        if(position == null)
            return;

        Vec3d fuelVec = vehicle.getPartPositionAbsoluteVec(position, partialTicks);
        double fuelX = (double) blockPos.getX() - fuelVec.x;
        double fuelY = (double) blockPos.getY() - fuelVec.y;
        double fuelZ = (double) blockPos.getZ() - fuelVec.z;

        Vec3d fuelRot = Vec3d.fromPitchYaw((float) position.getRotX(), (float) position.getRotY());
        fuelRot = fuelRot.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)).normalize();*/
       
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(x, y, z);

            if(gasPump.getFuelingEntity() != null)
            {
                gasPump.setRecentlyUsed(true);
                PlayerEntity entity = gasPump.getFuelingEntity();
                double playerX = (double) blockPos.getX() - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks);
                double playerY = (double) blockPos.getY() - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
                double playerZ = (double) blockPos.getZ() - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
                double side = entity.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
                float renderYawOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
                Vec3d lookVec = Vec3d.fromPitchYaw(-20F, renderYawOffset);
                Vec3d hoseVec = new Vec3d(-0.35 * side, -0.025, -0.025);
                if(entity instanceof AbstractClientPlayerEntity)
                {
                    String skinType = ((AbstractClientPlayerEntity) entity).getSkinType();
                    if(skinType.equals("slim"))
                    {
                        hoseVec = hoseVec.add(0.03 * side, -0.03, 0.0);
                    }
                }
                hoseVec = hoseVec.rotateYaw(-renderYawOffset * 0.017453292F);
                if(entity.equals(Minecraft.getInstance().player))
                {
                    if(Minecraft.getInstance().gameSettings.thirdPersonView == 0)
                    {
                        lookVec = Vec3d.fromPitchYaw(0F, entity.rotationYaw);
                        hoseVec = new Vec3d(-0.25, 0.5, -0.25).rotateYaw(-entity.rotationYaw * 0.017453292F);
                    }
                }
                HermiteInterpolator.Point destPoint = new HermiteInterpolator.Point(new Vec3d(-playerX + hoseVec.x, -playerY + 0.8 + hoseVec.y, -playerZ + hoseVec.z), new Vec3d(lookVec.x * 3, lookVec.y * 3, lookVec.z * 3));
                gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vec3d(pos[0], 0.6425, pos[1]), new Vec3d(0, -5, 0)), destPoint));
            }
            else
            {
                if(gasPump.getCachedSpline() == null || gasPump.isRecentlyUsed())
                {
                    gasPump.setRecentlyUsed(false);
                    double[] destPos = CollisionHelper.fixRotation(facing, 0.345, 1.06, 0.345, 1.06);
                    HermiteInterpolator.Point destPoint = new HermiteInterpolator.Point(new Vec3d(destPos[0], 0.1, destPos[1]), new Vec3d(0, 3, 0));
                    gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vec3d(pos[0], 0.6425, pos[1]), new Vec3d(0, -5, 0)), destPoint));
                }
            }

            //new HermiteInterpolator.Point(new Vec3d(-fuelX, -fuelY, -fuelZ), new Vec3d(fuelRot.x * 3, -fuelRot.y * 3, fuelRot.z * 3))
            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.5 + v.y / 2, -z + v.z / 2), new Vec3d(v.x * 5, v.y, v.z * 5))
            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.25, -z + v.z / 2), new Vec3d(-x + v.x * 10, -y, -z + v.z * 10))

            HermiteInterpolator spline = gasPump.getCachedSpline();
            if(spline != null)
            {
                float gray = 0.1F;
                float red = gray;
                float hoseDiameter = 0.07F;
                if(gasPump.getFuelingEntity() != null)
                {
                    red = (float) (Math.sqrt(gasPump.getFuelingEntity().getDistanceSq(gasPump.getPos().getX() + 0.5, gasPump.getPos().getY() + 0.5, gasPump.getPos().getZ() + 0.5)) / Config.SERVER.maxHoseDistance.get());
                    red = red * red * red * red * red * red;
                    red = Math.max(red, gray);
                }

                GlStateManager.lineWidth(Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 1.0F);
                GlStateManager.disableTexture();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

                int segments = Config.CLIENT.hoseSegments.get();
                for(int i = 0; i < spline.getSize() - 1; i++)
                {
                    for(int j = 0; j < segments; j++)
                    {
                        float percent = j / (float) segments;
                        HermiteInterpolator.Result start = spline.get(i, percent);
                        HermiteInterpolator.Result end = spline.get(i, (float) (j + 1) / (float) segments);

                        Matrix4d startMatrix = new Matrix4d();
                        startMatrix.setIdentity();
                        EntityRaytracer.MatrixTransformation.createTranslation(start.getPoint().x, start.getPoint().y, start.getPoint().z).transform(startMatrix);
                        if(i == 0 && j == 0)
                        {
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z)), 0, 1, 0).transform(startMatrix);
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.asin(-end.getDir().normalize().y)), 1, 0, 0).transform(startMatrix);
                        }
                        else
                        {
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z)), 0, 1, 0).transform(startMatrix);
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.asin(-start.getDir().normalize().y)), 1, 0, 0).transform(startMatrix);
                        }

                        Matrix4d endMatrix = new Matrix4d();
                        endMatrix.setIdentity();
                        EntityRaytracer.MatrixTransformation.createTranslation(end.getPoint().x, end.getPoint().y, end.getPoint().z).transform(endMatrix);
                        if(i == spline.getSize() - 2 && j == segments - 1)
                        {
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z)), 0, 1, 0).transform(endMatrix);
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.asin(-start.getDir().normalize().y)), 1, 0, 0).transform(endMatrix);
                        }
                        else
                        {
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z)), 0, 1, 0).transform(endMatrix);
                            EntityRaytracer.MatrixTransformation.createRotation(Math.toDegrees(Math.asin(-end.getDir().normalize().y)), 1, 0, 0).transform(endMatrix);
                        }

                        Matrix4d startTemp = new Matrix4d(startMatrix);
                        Matrix4d endTemp = new Matrix4d(endMatrix);
                        
                        EntityRaytracer.MatrixTransformation.createTranslation(hoseDiameter / 2, -hoseDiameter / 2, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(hoseDiameter / 2, hoseDiameter / 2, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, -hoseDiameter, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();

                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(-hoseDiameter, -hoseDiameter, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();

                        EntityRaytracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, hoseDiameter, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();

                        EntityRaytracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(-hoseDiameter, 0, 0).transform(startTemp);
                        buffer.pos(startTemp.m03, startTemp.m13, startTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(0, 0, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                        EntityRaytracer.MatrixTransformation.createTranslation(hoseDiameter, 0, 0).transform(endTemp);
                        buffer.pos(endTemp.m03, endTemp.m13, endTemp.m23).color(red, gray, gray, 1.0F).endVertex();
                    }
                }
                tessellator.draw();
                GlStateManager.enableTexture();
            }

            if(gasPump.getFuelingEntity() == null)
            {
                GlStateManager.pushMatrix();
                {
                    double[] destPos = CollisionHelper.fixRotation(facing, 0.29, 1.06, 0.29, 1.06);
                    GlStateManager.translated(destPos[0], 0.5, destPos[1]);
                    GlStateManager.rotatef(facing.getHorizontalIndex() * -90F, 0, 1, 0);
                    GlStateManager.rotatef(180F, 0, 1, 0);
                    GlStateManager.rotatef(90F, 1, 0, 0);
                    GlStateManager.scalef(0.8F, 0.8F, 0.8F);
                    RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
                }
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            {
                GlStateManager.translated(0.5, 0, 0.5);
                GlStateManager.rotatef(facing.getHorizontalIndex() * -90F, 0, 1, 0);
                GlStateManager.translated(-0.5, 0, -0.5);
                GlStateManager.translated(0.5, 11 * 0.0625, 3 * 0.0625);
                GlStateManager.rotatef(180F, 0, 1, 0);

                GlStateManager.translated(0F, 0F, 0.01F);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.scalef(0.015F, -0.015F, 0.015F);
                    FontRenderer fontRenderer = this.rendererDispatcher.fontRenderer;
                    if(gasPump.getTank() != null)
                    {
                        int amount = (int) Math.ceil(100 * (gasPump.getTank().getFluidAmount() / (double) gasPump.getTank().getCapacity()));
                        String percent = String.format("%d%%", amount);
                        int width = fontRenderer.getStringWidth(percent);
                        fontRenderer.drawString(percent, -width / 2, 10, 0xFFFFFF);
                    }
                }
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                {
                    GlStateManager.translated(0, 1 * 0.0625, 0);
                    GlStateManager.scalef(0.01F, -0.01F, 0.01F);
                    FontRenderer fontRenderer = this.rendererDispatcher.fontRenderer;
                    int width = fontRenderer.getStringWidth("Fuelium");
                    fontRenderer.drawString("Fuelium", -width / 2, 10, 9761325);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
