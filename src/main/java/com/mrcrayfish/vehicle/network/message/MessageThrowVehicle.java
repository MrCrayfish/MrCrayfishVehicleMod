package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageThrowVehicle implements IMessage<MessageThrowVehicle>
{
    @Override
    public void encode(MessageThrowVehicle message, PacketBuffer buffer) {}

    @Override
    public MessageThrowVehicle decode(PacketBuffer buffer)
    {
        return new MessageThrowVehicle();
    }

    @Override
    public void handle(MessageThrowVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null && player.isCrouching())
            {
                //Spawns the vehicle and plays the placing sound
                if(!HeldVehicleDataHandler.isHoldingVehicle(player))
                    return;

                CompoundNBT heldTag = HeldVehicleDataHandler.getHeldVehicle(player);
                Optional<EntityType<?>> optional = EntityType.byString(heldTag.getString("id"));
                if(!optional.isPresent())
                    return;

                EntityType<?> entityType = optional.get();
                Entity entity = entityType.create(player.level);
                if(entity instanceof VehicleEntity)
                {
                    entity.load(heldTag);

                    //Updates the player capability
                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

                    //Sets the positions and spawns the entity
                    float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                    Vector3d heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));

                    //Gets the clicked vec if it was a right click block event
                    Vector3d lookVec = player.getLookAngle();
                    double posX = player.getX();
                    double posY = player.getY() + player.getEyeHeight();
                    double posZ = player.getZ();
                    entity.absMoveTo(posX + heldOffset.x * 0.0625D, posY + heldOffset.y * 0.0625D, posZ + heldOffset.z * 0.0625D, rotation, 0F);

                    Vector3d motion = entity.getDeltaMovement();
                    entity.setDeltaMovement(motion.x() + lookVec.x, motion.y() + lookVec.y, motion.z() + lookVec.z);
                    entity.fallDistance = 0.0F;

                    player.level.addFreshEntity(entity);
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
