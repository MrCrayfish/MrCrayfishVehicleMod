package com.mrcrayfish.vehicle.common.entity;

import com.google.common.base.Optional;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerData;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerGasPumpPos;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerTrailer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.WeakHashMap;

/**
 * Basically a clone of DataParameter system. It's not good to register custom data parameters to
 * other entities that aren't your own. It can cause mismatched ids and crash the game. This synced
 * data attempts to solve the problem (at least for players) and allows data to be synced to clients.
 * The data can only be controlled on the server. Changing the data on the client will have no affect
 * on the server. It should also be noted that this data is not saved (just like data params) and
 * will have to be manually saved.
 *
 * TODO make synced data dynamic instead of hardcoded. Basically I should be able to register them similar to data params
 *
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.SERVER)
public class SyncedPlayerData
{
    private static final WeakHashMap<EntityPlayer, Holder> PLAYER_DATA_MAP = new WeakHashMap<>();

    private static Holder getPlayerData(EntityPlayer player)
    {
        return PLAYER_DATA_MAP.computeIfAbsent(player, player1 -> new Holder());
    }

    public static int getTrailer(EntityPlayer player)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            return holder.getTrailer();
        }
        return -1;
    }

    public static void setTrailer(EntityPlayer player, int trailer)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            holder.setTrailer(trailer);
        }
        if(!player.world.isRemote)
        {
            PacketHandler.INSTANCE.sendTo(new MessageSyncPlayerTrailer(player.getEntityId(), trailer), (EntityPlayerMP) player);
            PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncPlayerTrailer(player.getEntityId(), trailer), player);
        }
    }

    public static Optional<BlockPos> getGasPumpPos(EntityPlayer player)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            return holder.getGasPumpPos();
        }
        return Optional.absent();
    }

    public static void setGasPumpPos(EntityPlayer player, Optional<BlockPos> optional)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            holder.setGasPumpPos(optional);
        }
        if(!player.world.isRemote)
        {
            PacketHandler.INSTANCE.sendTo(new MessageSyncPlayerGasPumpPos(player.getEntityId(), optional), (EntityPlayerMP) player);
            PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncPlayerGasPumpPos(player.getEntityId(), optional), player);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            Holder holder = getPlayerData(player);
            if(holder != null)
            {
                PacketHandler.INSTANCE.sendTo(new MessageSyncPlayerData(player.getEntityId(), holder), (EntityPlayerMP) event.getEntityPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayer && !event.getWorld().isRemote)
        {
            EntityPlayer player = (EntityPlayer) entity;
            Holder holder = getPlayerData(player);
            if(holder != null)
            {
                PacketHandler.INSTANCE.sendTo(new MessageSyncPlayerData(player.getEntityId(), holder), (EntityPlayerMP) player);
            }
        }
    }

    public static class Holder
    {
        private int trailer = -1;
        private Optional<BlockPos> gasPumpPos = Optional.absent();

        public void setTrailer(int id)
        {
            this.trailer = id;
        }

        public int getTrailer()
        {
            return this.trailer;
        }

        public void setGasPumpPos(Optional<BlockPos> gasPumpPos)
        {
            this.gasPumpPos = gasPumpPos;
        }

        public Optional<BlockPos> getGasPumpPos()
        {
            return this.gasPumpPos;
        }
    }
}
