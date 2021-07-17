package com.mrcrayfish.vehicle.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class TileEntityUtil
{
    /**
     * Sends an update packet to clients tracking a tile entity.
     *
     * @param tileEntity the tile entity to update
     */
    public static void sendUpdatePacket(TileEntity tileEntity)
    {
        SUpdateTileEntityPacket packet = tileEntity.getUpdatePacket();
        if(packet != null)
        {
            sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
        }
    }

    /**
     * Sends an update packet to clients tracking a tile entity with a specific CompoundNBT
     *
     * @param tileEntity the tile entity to update
     */
    public static void sendUpdatePacket(TileEntity tileEntity, CompoundNBT compound)
    {
        SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(tileEntity.getBlockPos(), 0, compound);
        sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
    }

    /**
     * Sends an update packet but only to a specific player. This helps reduce overhead on the network
     * when you only want to update a tile entity for a single player rather than everyone who is
     * tracking the tile entity.
     *
     * @param tileEntity the tile entity to update
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(TileEntity tileEntity, ServerPlayerEntity player)
    {
        sendUpdatePacket(tileEntity, tileEntity.getUpdateTag(), player);
    }

    /**
     * Sends an update packet with a custom nbt compound but only to a specific player. This helps
     * reduce overhead on the network when you only want to update a tile entity for a single player
     * rather than everyone who is tracking the tile entity.
     *
     * @param tileEntity the tile entity to update
     * @param compound the update tag to send
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(TileEntity tileEntity, CompoundNBT compound, ServerPlayerEntity player)
    {
        SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(tileEntity.getBlockPos(), 0, compound);
        player.connection.send(packet);
    }

    private static void sendUpdatePacket(World world, BlockPos pos, SUpdateTileEntityPacket packet)
    {
        if(world instanceof ServerWorld)
        {
            ServerWorld server = (ServerWorld) world;
            Stream<ServerPlayerEntity> players = server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> player.connection.send(packet));
        }
    }
}
