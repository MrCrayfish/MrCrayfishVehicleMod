package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class Action
{
    private boolean dirty = false;

    /**
     * Called when a player interacts (right clicks) the cosmetic. This is called on both logical
     * client and server.
     *
     * @param vehicle the vehicle associated with this action
     * @param player the player interacting with the cosmetic
     */
    public void onInteract(VehicleEntity vehicle, PlayerEntity player) {}

    /**
     * Called every time the vehicle ticks. This is called on both logical client and server.
     *
     * @param vehicle the vehicle this cosmetic action is bound to
     */
    public void tick(VehicleEntity vehicle) {}

    /**
     * Saves the data of the action into a new CompoundNBT. If the sync tag is true,
     * the data being saved is going to be used to synchronized to clients.
     *
     * @param sync if true, the returned compound tag is used for syncing to clients
     * @return a compound tag containing data from the action to save
     */
    public CompoundNBT save(boolean sync)
    {
        return new CompoundNBT();
    }

    /**
     * Loads a compound tag containing saved data for this action. If sync is true, the data
     * is coming from a synchronization update form the server.
     *
     * @param tag  a compound tag contain data for this action
     * @param sync if the compound tag is from a sync update
     */
    public void load(CompoundNBT tag, boolean sync) {}

    public abstract void serialize(JsonObject object);

    /**
     * Marks the action as dirty, causing the data to be synchronized to clients
     */
    protected void setDirty()
    {
        this.dirty = true;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    /**
     * Resets the dirty state
     */
    public void clean()
    {
        this.dirty = false;
    }

    @OnlyIn(Dist.CLIENT)
    public void beforeRender(MatrixStack stack, VehicleEntity vehicle, float partialTicks) {}

    @OnlyIn(Dist.CLIENT)
    public void gatherTransforms(List<MatrixTransform> transforms) {}
}
