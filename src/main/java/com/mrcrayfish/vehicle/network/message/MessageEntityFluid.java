package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageEntityFluid implements IMessage, IMessageHandler<MessageEntityFluid, IMessage>
{
    private int entityId;
    private FluidStack stack;

    public MessageEntityFluid() {}

    public MessageEntityFluid(int entityId, FluidStack stack)
    {
        this.entityId = entityId;
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
        buf.writeBoolean(stack != null);
        if(stack != null)
        {
            ByteBufUtils.writeTag(buf, stack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
        if(buf.readBoolean())
        {
            stack = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
        }
    }

    @Override
    public IMessage onMessage(MessageEntityFluid message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> VehicleMod.proxy.syncEntityFluid(message.entityId, message.stack));
        return null;
    }
}
