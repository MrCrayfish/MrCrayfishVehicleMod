package com.mrcrayfish.vehicle.client.render.tileentity;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.block.BlockGasPump;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPump;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

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
        boolean top = state.getValue(BlockGasPump.TOP);
        if(state.getBlock() != ModBlocks.GAS_PUMP || !top)
            return;

        EnumFacing facing = state.getValue(BlockGasPump.FACING);
        double[] pos = CollisionHelper.fixRotation(facing, 0.640625, 1.078125, 0.640625, 1.078125);

        List<EntityVehicle> vehicles = te.getWorld().getEntitiesWithinAABB(EntityVehicle.class, new AxisAlignedBB(te.getPos()).grow(5.0));
        if(vehicles.size() == 0)
            return;

        EntityVehicle vehicle = vehicles.get(0);
        VehicleProperties properties = VehicleProperties.getProperties(vehicle.getClass());
        PartPosition position = properties.getFuelPortPosition();
        Vec3d fuelVec = vehicle.getPartPositionAbsoluteVec(position);
        double fuelX = (double) blockPos.getX() - fuelVec.x;
        double fuelY = (double) blockPos.getY() - fuelVec.y;
        double fuelZ = (double) blockPos.getZ() - fuelVec.z;

        Vec3d fuelRot = Vec3d.fromPitchYaw((float) position.getRotX(), (float) position.getRotY());
        fuelRot = fuelRot.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)).normalize();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            ItemStack stack = new ItemStack(Blocks.CONCRETE, 1, 15);
            IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);

            Vec3d v = Minecraft.getMinecraft().player.getLook(partialTicks);
            HermiteInterpolator spline = new HermiteInterpolator(Lists.newArrayList(
                    new HermiteInterpolator.Point(new Vec3d(pos[0], 0.55, pos[1]), new Vec3d(0, -5, 0)),
                    new HermiteInterpolator.Point(new Vec3d(-fuelX, -fuelY, -fuelZ), new Vec3d(fuelRot.x * 3, 0, fuelRot.z * 3))
            ));

            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.5 + v.y / 2, -z + v.z / 2), new Vec3d(v.x * 5, v.y, v.z * 5))
            //new HermiteInterpolator.Point(new Vec3d(-x + v.x / 2, -y + 1.25, -z + v.z / 2), new Vec3d(-x + v.x * 10, -y, -z + v.z * 10))

            GlStateManager.pushMatrix();
            {
                int steps = 100;
                for(int i = 0; i < spline.getSize() - 1; i++)
                {
                    for(int j = 0; j < steps; j++)
                    {
                        float percent = j / (float) steps;
                        HermiteInterpolator.Result r = spline.get(i, percent);
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(r.getPoint().x, r.getPoint().y, r.getPoint().z);
                        GlStateManager.rotate((float) Math.toDegrees(Math.atan2(r.getDir().x, r.getDir().z)), 0, 1, 0);
                        GlStateManager.rotate((float) Math.toDegrees(Math.asin(-r.getDir().normalize().y)), 1, 0, 0);
                        GlStateManager.scale(0.1, 0.1, 0.1);
                        RenderUtil.renderItemModel(stack, model, ItemCameraTransforms.TransformType.NONE);
                        GlStateManager.popMatrix();
                    }
                }
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
