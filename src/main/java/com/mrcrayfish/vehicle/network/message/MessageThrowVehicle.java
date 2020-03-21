package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageThrowVehicle implements IMessage, IMessageHandler<MessageThrowVehicle, IMessage>
{
    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(MessageThrowVehicle message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            if(player.isSneaking())
            {
                //Spawns the vehicle and plays the placing sound
                if(HeldVehicleDataHandler.isHoldingVehicle(player))
                {
                    NBTTagCompound tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                    Entity entity = EntityList.createEntityFromNBT(tagCompound, player.world);
                    if(entity instanceof EntityVehicle)
                    {
                        //Updates the DataParameter
                        HeldVehicleDataHandler.setHeldVehicle(player, new NBTTagCompound());

                        //Sets the positions and spawns the entity
                        float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                        Vec3d heldOffset = ((EntityVehicle) entity).getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));

                        //Gets the clicked vec if it was a right click block event
                        Vec3d lookVec = player.getLookVec();
                        double posX = player.posX;
                        double posY = player.posY + player.getEyeHeight();
                        double posZ = player.posZ;
                        entity.setPositionAndRotation(posX + heldOffset.x * 0.0625D, posY + heldOffset.y * 0.0625D, posZ + heldOffset.z * 0.0625D, rotation, 0F);
                        entity.motionX = player.motionX + lookVec.x;
                        entity.motionY = player.motionY + lookVec.y;
                        entity.motionZ = player.motionZ + lookVec.z;
                        entity.fallDistance = 0.0F;

                        player.world.spawnEntity(entity);
                        player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.PICK_UP_VEHICLE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        });
        return null;
    }
}
