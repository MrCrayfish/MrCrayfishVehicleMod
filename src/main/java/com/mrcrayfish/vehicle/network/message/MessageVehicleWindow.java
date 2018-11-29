package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.gui.GuiEditVehicle;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageVehicleWindow implements IMessage, IMessageHandler<MessageVehicleWindow, IMessage>
{
    private int windowId;
    private int entityId;

    public MessageVehicleWindow() {}

    public MessageVehicleWindow(int windowId, int entityId)
    {
        this.windowId = windowId;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowId);
        buf.writeInt(entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowId = buf.readInt();
        entityId = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageVehicleWindow message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            World world = player.getEntityWorld();
            Entity entity = world.getEntityByID(message.entityId);
            if(entity instanceof EntityPoweredVehicle)
            {
                EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) entity;
                Minecraft.getMinecraft().displayGuiScreen(new GuiEditVehicle(poweredVehicle, player));
                player.openContainer.windowId = message.windowId;
            }
        });
        return null;
    }
}
