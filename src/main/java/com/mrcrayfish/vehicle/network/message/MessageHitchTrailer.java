package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageHitchTrailer implements IMessage<MessageHitchTrailer>
{
    private boolean hitch;

    public MessageHitchTrailer() {}

    public MessageHitchTrailer(boolean hitch)
    {
        this.hitch = hitch;
    }

    @Override
    public void encode(MessageHitchTrailer message, PacketBuffer buffer)
    {
        buffer.writeBoolean(message.hitch);
    }

    @Override
    public MessageHitchTrailer decode(PacketBuffer buffer)
    {
        return new MessageHitchTrailer(buffer.readBoolean());
    }

    @Override
    public void handle(MessageHitchTrailer message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                if(!(player.getRidingEntity() instanceof VehicleEntity))
                    return;

                VehicleEntity vehicle = (VehicleEntity) player.getRidingEntity();
                if(!vehicle.canTowTrailer())
                    return;

                if(!message.hitch)
                {
                    if(vehicle.getTrailer() != null)
                    {
                        vehicle.setTrailer(null);
                        player.world.playSound(null, vehicle.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
                else
                {
                    VehicleProperties properties = vehicle.getProperties();
                    Vector3d vehicleVec = vehicle.getPositionVec();
                    Vector3d towBarVec = properties.getTowBarPosition();
                    towBarVec = new Vector3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + properties.getBodyPosition().getZ());
                    if(vehicle instanceof LandVehicleEntity)
                    {
                        LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                        vehicleVec = vehicleVec.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw + landVehicle.additionalYaw)));
                    }
                    else
                    {
                        vehicleVec = vehicleVec.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)));
                    }

                    AxisAlignedBB towBarBox = new AxisAlignedBB(vehicleVec.x, vehicleVec.y, vehicleVec.z, vehicleVec.x, vehicleVec.y, vehicleVec.z).grow(0.25);
                    List<TrailerEntity> trailers = player.world.getEntitiesWithinAABB(TrailerEntity.class, vehicle.getBoundingBox().grow(5), input -> input.getPullingEntity() == null);
                    for(TrailerEntity trailer : trailers)
                    {
                        if(trailer.getPullingEntity() != null)
                            continue;

                        Vector3d trailerVec = trailer.getPositionVec();
                        Vector3d hitchVec = new Vector3d(0, 0, -trailer.getHitchOffset() / 16.0);
                        trailerVec = trailerVec.add(hitchVec.rotateYaw((float) Math.toRadians(-trailer.rotationYaw)));
                        AxisAlignedBB hitchBox = new AxisAlignedBB(trailerVec.x, trailerVec.y, trailerVec.z, trailerVec.x, trailerVec.y, trailerVec.z).grow(0.25);
                        if(towBarBox.intersects(hitchBox))
                        {
                            vehicle.setTrailer(trailer);
                            player.world.playSound(null, vehicle.getPosition(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.0F, 1.5F);
                            return;
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
