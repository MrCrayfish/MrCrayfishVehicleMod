package com.mrcrayfish.vehicle.client.render.tileentity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.block.BlockGasPump;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class GasPumpRenderer extends TileEntityRenderer<GasPumpTileEntity>
{
    @Override
    public void render(GasPumpTileEntity gasPump, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

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
            HermiteInterpolator.Point destPoint;
            if(gasPump.getFuelingEntity() != null)
            {
                PlayerEntity entity = gasPump.getFuelingEntity();
                double side = entity.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
                double playerX = (double) blockPos.getX() - (entity.prevPosX + (entity.posZ - entity.prevPosX) * partialTicks);
                double playerY = (double) blockPos.getY() - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
                double playerZ = (double) blockPos.getZ() - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
                float renderYawOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
                Vec3d lookVec = Vec3d.fromPitchYaw(-20F, renderYawOffset);
                Vec3d hoseVec = new Vec3d(-0.35 * side, 0.01, 0.0625);
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
                destPoint = new HermiteInterpolator.Point(new Vec3d(-playerX + hoseVec.x, -playerY + 0.8 + hoseVec.y, -playerZ + hoseVec.z), new Vec3d(lookVec.x * 3, lookVec.y * 3, lookVec.z * 3));
            }
            else
            {
                double[] destPos = CollisionHelper.fixRotation(facing, 0.345, 1.06, 0.345, 1.06);
                destPoint = new HermiteInterpolator.Point(new Vec3d(destPos[0], 0.0625, destPos[1]), new Vec3d(0, 3, 0));
            }

            HermiteInterpolator spline = new HermiteInterpolator(Lists.newArrayList(new HermiteInterpolator.Point(new Vec3d(pos[0], 0.5625, pos[1]), new Vec3d(0, -5, 0)), destPoint));

            //new HermiteInterpolator.Point(new Vec3d(-fuelX, -fuelY, -fuelZ), new Vec3d(fuelRot.x * 3, -fuelRot.y * 3, fuelRot.z * 3))
            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.5 + v.y / 2, -z + v.z / 2), new Vec3d(v.x * 5, v.y, v.z * 5))
            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.25, -z + v.z / 2), new Vec3d(-x + v.x * 10, -y, -z + v.z * 10))

            ItemStack stack = new ItemStack(Blocks.BLACK_CONCRETE);
            IBakedModel model = RenderUtil.getModel(stack);

            GlStateManager.pushMatrix();
            {
                int steps = 100;
                for(int i = 0; i < spline.getSize() - 1; i++)
                {
                    for(int j = 0; j <= steps; j++)
                    {
                        float percent = j / (float) steps;
                        HermiteInterpolator.Result r = spline.get(i, percent);
                        GlStateManager.pushMatrix();
                        GlStateManager.translated(r.getPoint().x, r.getPoint().y, r.getPoint().z);
                        GlStateManager.rotated(Math.toDegrees(Math.atan2(r.getDir().x, r.getDir().z)), 0, 1, 0);
                        GlStateManager.rotated(Math.toDegrees(Math.asin(-r.getDir().normalize().y)), 1, 0, 0);
                        GlStateManager.scalef(0.075F, 0.075F, 0.075F);
                        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                        GlStateManager.popMatrix();
                    }
                }
            }
            GlStateManager.popMatrix();

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

        GlStateManager.popMatrix();
    }
}
