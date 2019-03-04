package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.EntityTrailer;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MessageHitchTrailer implements IMessage, IMessageHandler<MessageHitchTrailer, IMessage>
{
    private boolean hitch;

    public MessageHitchTrailer() {}

    public MessageHitchTrailer(boolean hitch)
    {
        this.hitch = hitch;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(hitch);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        hitch = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(MessageHitchTrailer message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            if(player.getRidingEntity() instanceof EntityVehicle)
            {
                EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                if(vehicle.canTowTrailer())
                {
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
                        Vec3d vehicleVec = vehicle.getPositionVector();
                        Vec3d towBarVec = properties.getTowBarPosition();
                        towBarVec = new Vec3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + properties.getBodyPosition().getZ());
                        if(vehicle instanceof EntityLandVehicle)
                        {
                            EntityLandVehicle landVehicle = (EntityLandVehicle) vehicle;
                            vehicleVec = vehicleVec.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw + landVehicle.additionalYaw)));
                        }
                        else
                        {
                            vehicleVec = vehicleVec.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)));
                        }

                        AxisAlignedBB towBarBox = new AxisAlignedBB(vehicleVec.x, vehicleVec.y, vehicleVec.z, vehicleVec.x, vehicleVec.y, vehicleVec.z).grow(0.25);
                        List<EntityTrailer> trailers = player.world.getEntitiesWithinAABB(EntityTrailer.class, vehicle.getEntityBoundingBox().grow(5), input -> input.getPullingEntity() == null);
                        for(EntityTrailer trailer : trailers)
                        {
                            if(trailer.getPullingEntity() != null)
                                continue;
                            Vec3d trailerVec = trailer.getPositionVector();
                            Vec3d hitchVec = new Vec3d(0, 0, -trailer.getHitchOffset() / 16.0);
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
            }
        });
        return null;
    }
}
