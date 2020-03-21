package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerData;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerGasPumpPos;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerTrailer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Basically a clone of DataParameter system. It's not good to register custom data parameters to
 * other entities that aren't your own. It can cause mismatched ids and crash the game. This synced
 * data attempts to solve the problem (at least for players) and allows data to be synced to clients.
 * The data can only be controlled on the server. Changing the data on the client will have no affect
 * on the server.
 *
 * TODO make synced data dynamic instead of hardcoded. Basically I should be able to register them similar to data params
 *
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.DEDICATED_SERVER)
public class SyncedPlayerData
{
    private static final WeakHashMap<PlayerEntity, Holder> PLAYER_DATA_MAP = new WeakHashMap<>();

    private static Holder getPlayerData(PlayerEntity player)
    {
        return PLAYER_DATA_MAP.computeIfAbsent(player, player1 -> new Holder());
    }

    public static int getTrailer(PlayerEntity player)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            return holder.getTrailer();
        }
        return -1;
    }

    public static void setTrailer(PlayerEntity player, int trailer)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            holder.setTrailer(trailer);
        }
        if(!player.world.isRemote)
        {
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageSyncPlayerTrailer(player.getEntityId(), trailer));
        }
    }

    public static Optional<BlockPos> getGasPumpPos(PlayerEntity player)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            return holder.getGasPumpPos();
        }
        return Optional.empty();
    }

    public static void setGasPumpPos(PlayerEntity player, Optional<BlockPos> optional)
    {
        Holder holder = getPlayerData(player);
        if(holder != null)
        {
            holder.setGasPumpPos(optional);
        }
        if(!player.world.isRemote)
        {
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageSyncPlayerGasPumpPos(player.getEntityId(), optional));
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getTarget();
            Holder holder = getPlayerData(player);
            if(holder != null)
            {
                PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageSyncPlayerData(player.getEntityId(), holder));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof PlayerEntity && !event.getWorld().isRemote)
        {
            PlayerEntity player = (PlayerEntity) entity;
            Holder holder = getPlayerData(player);
            if(holder != null)
            {
                PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MessageSyncPlayerData(player.getEntityId(), holder));
            }
        }
    }

    public static class Holder
    {
        private int trailer = -1;
        private Optional<BlockPos> gasPumpPos = Optional.empty();

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