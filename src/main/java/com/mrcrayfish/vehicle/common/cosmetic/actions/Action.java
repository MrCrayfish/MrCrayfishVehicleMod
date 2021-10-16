package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class Action
{
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

    public CompoundNBT save()
    {
        return new CompoundNBT();
    }

    public void load(CompoundNBT tag) {}

    public abstract void serialize(JsonObject object);

    @OnlyIn(Dist.CLIENT)
    public void beforeRender(MatrixStack stack, VehicleEntity vehicle, float partialTicks) {}

    @OnlyIn(Dist.CLIENT)
    public void gatherTransforms(List<MatrixTransform> transforms) {}
}
