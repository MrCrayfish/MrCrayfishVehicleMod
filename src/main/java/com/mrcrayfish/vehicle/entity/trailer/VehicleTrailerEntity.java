package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class VehicleTrailerEntity extends TrailerEntity
{
    public VehicleTrailerEntity(EntityType<? extends VehicleTrailerEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return 8 * 0.0625;
    }

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return true;
    }

    @Override
    public void positionRider(Entity passenger)
    {
        if(passenger instanceof VehicleEntity)
        {
            Vector3d offset = ((VehicleEntity) passenger).getProperties().getTrailerOffset().yRot((float) Math.toRadians(-this.yRot));
            passenger.setPos(this.getX() + offset.x, this.getY() + getPassengersRidingOffset() + offset.y, this.getZ() + offset.z);
            passenger.yRotO = this.yRotO;
            passenger.yRot = this.yRot;
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return passenger instanceof VehicleEntity && this.getPassengers().size() == 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerInteractionBoxes()
    {
        EntityRayTracer.instance().registerInteractionBox(ModEntities.VEHICLE_TRAILER.get(), () -> {
            return createScaledBoundingBox(-7.0, -0.5, 14.0, 7.0, 3.5, 24.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageAttachTrailer(entity.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);
    }
}
