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
import net.minecraft.util.math.Vec3d;
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
            if(player != null && player.isSneaking())
            {
                //Spawns the vehicle and plays the placing sound
                if(HeldVehicleDataHandler.getHeldVehicle(player).isEmpty())
                    return;

                CompoundNBT heldTag = HeldVehicleDataHandler.getHeldVehicle(player);
                Optional<EntityType<?>> optional = EntityType.byKey(heldTag.getString("id"));
                if(!optional.isPresent())
                    return;

                EntityType<?> entityType = optional.get();
                Entity entity = entityType.create(player.world);
                if(entity instanceof VehicleEntity)
                {
                    entity.read(heldTag);

                    //Updates the player capability
                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

                    //Sets the positions and spawns the entity
                    float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                    Vec3d heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));

                    //Gets the clicked vec if it was a right click block event
                    Vec3d lookVec = player.getLookVec();
                    double posX = player.posX;
                    double posY = player.posY + player.getEyeHeight();
                    double posZ = player.posZ;
                    entity.setPositionAndRotation(posX + heldOffset.x * 0.0625D, posY + heldOffset.y * 0.0625D, posZ + heldOffset.z * 0.0625D, rotation, 0F);

                    Vec3d motion = entity.getMotion();
                    entity.setMotion(motion.getX() + lookVec.x, motion.getY() + lookVec.y, motion.getZ() + lookVec.z);
                    entity.fallDistance = 0.0F;

                    player.world.addEntity(entity);
                    player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.PICK_UP_VEHICLE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
