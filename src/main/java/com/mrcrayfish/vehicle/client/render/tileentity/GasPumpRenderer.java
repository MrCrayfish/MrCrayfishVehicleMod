package com.mrcrayfish.vehicle.client.render.tileentity;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.block.BlockGasPump;
import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPump;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class GasPumpRenderer extends TileEntitySpecialRenderer<TileEntityGasPump>
{
    @Override
    public void render(TileEntityGasPump te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        BlockPos blockPos = te.getPos();
        IBlockState state = te.getWorld().getBlockState(blockPos);
        if(state.getBlock() != ModBlocks.GAS_PUMP)
        {
            return;
        }

        boolean top = state.getValue(BlockGasPump.TOP);
        if(!top)
        {
            return;
        }

        EnumFacing facing = state.getValue(BlockGasPump.FACING);
        double[] pos = CollisionHelper.fixRotation(facing, 0.640625, 1.078125, 0.640625, 1.078125);

       /* List<EntityVehicle> vehicles = te.getWorld().getEntitiesWithinAABB(EntityVehicle.class, new AxisAlignedBB(te.getPos()).grow(5.0));
        if(vehicles.size() == 0)
            return;

        EntityVehicle vehicle = vehicles.get(0);
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
            GlStateManager.translate(x, y, z);

            HermiteInterpolator.Point destPoint;
            if(te.getFuelingEntity() != null)
            {
                EntityPlayer entity = te.getFuelingEntity();
                double side = entity.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
                double playerX = (double) blockPos.getX() - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks);
                double playerY = (double) blockPos.getY() - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
                double playerZ = (double) blockPos.getZ() - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
                float renderYawOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
                Vec3d lookVec = Vec3d.fromPitchYaw(-20F, renderYawOffset);
                Vec3d hoseVec = new Vec3d(-0.35 * side, 0.01, 0.0625);
                if(entity instanceof AbstractClientPlayer)
                {
                    String skinType = ((AbstractClientPlayer) entity).getSkinType();
                    if(skinType.equals("slim"))
                    {
                        hoseVec = hoseVec.addVector(0.03 * side, -0.03, 0.0);
                    }
                }
                hoseVec = hoseVec.rotateYaw(-renderYawOffset * 0.017453292F);
                if(entity.equals(Minecraft.getMinecraft().player))
                {
                    if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
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

            ItemStack stack = new ItemStack(Blocks.CONCRETE, 1, 15);
            IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);

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
                        GlStateManager.translate(r.getPoint().x, r.getPoint().y, r.getPoint().z);
                        GlStateManager.rotate((float) Math.toDegrees(Math.atan2(r.getDir().x, r.getDir().z)), 0, 1, 0);
                        GlStateManager.rotate((float) Math.toDegrees(Math.asin(-r.getDir().normalize().y)), 1, 0, 0);
                        GlStateManager.scale(0.075, 0.075, 0.075);
                        RenderUtil.renderItemModel(stack, model, ItemCameraTransforms.TransformType.NONE);
                        GlStateManager.popMatrix();
                    }
                }
            }
            GlStateManager.popMatrix();

            if(te.getFuelingEntity() == null)
            {
                GlStateManager.pushMatrix();
                {
                    double[] destPos = CollisionHelper.fixRotation(facing, 0.29, 1.06, 0.29, 1.06);
                    GlStateManager.translate(destPos[0], 0.5, destPos[1]);
                    GlStateManager.rotate(facing.getHorizontalIndex() * -90F, 0, 1, 0);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    GlStateManager.rotate(90F, 1, 0, 0);
                    GlStateManager.scale(0.8, 0.8, 0.8);
                    RenderUtil.renderItemModel(new ItemStack(ModItems.MODELS), Models.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0.5, 0, 0.5);
                GlStateManager.rotate(facing.getHorizontalIndex() * -90F, 0, 1, 0);
                GlStateManager.translate(-0.5, 0, -0.5);
                GlStateManager.translate(0.5, 11 * 0.0625, 3 * 0.0625);
                GlStateManager.rotate(180F, 0, 1, 0);

                GlStateManager.translate(0F, 0F, 0.01F);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.scale(0.015F, -0.015F, 0.015F);
                    GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
                    GlStateManager.depthMask(false);
                    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                    if(te.getTank() != null)
                    {
                        int amount = (int) Math.ceil(100 * (te.getTank().getFluidAmount() / (double) te.getTank().getCapacity()));
                        String percent = String.format("%d%%", amount);
                        int width = fontRenderer.getStringWidth(percent);
                        fontRenderer.drawString(percent, -width / 2, 10, 16777215);
                    }
                    GlStateManager.depthMask(true);
                }
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, 1 * 0.0625, 0);
                    GlStateManager.scale(0.01F, -0.01F, 0.01F);
                    GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
                    GlStateManager.depthMask(false);
                    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                    int width = fontRenderer.getStringWidth("Fuelium");
                    fontRenderer.drawString("Fuelium", -width / 2, 10, 9761325);
                    GlStateManager.depthMask(true);
                }
                GlStateManager.popMatrix();

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
