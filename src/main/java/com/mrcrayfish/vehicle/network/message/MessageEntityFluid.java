package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageEntityFluid implements IMessage<MessageEntityFluid>
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
    public void encode(MessageEntityFluid message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeCompoundTag(message.stack.writeToNBT(new CompoundNBT()));
    }

    @Override
    public MessageEntityFluid decode(PacketBuffer buffer)
    {
        return new MessageEntityFluid(buffer.readVarInt(), FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag()));
    }

    @Override
    public void handle(MessageEntityFluid message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncEntityFluid(message.entityId, message.stack));
        supplier.get().setPacketHandled(true);
    }
}
