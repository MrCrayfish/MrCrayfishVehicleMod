package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

/**
 * Author: MrCrayfish
 */
public class OpenableAction extends Action
{
    private final Axis axis;
    private final float angle;
    public boolean state = false; //Explicit to clearly indicate default state

    public OpenableAction(Axis axis, float angle)
    {
        this.axis = axis;
        this.angle = angle;
    }

    @Override
    public void onInteract(VehicleEntity vehicle, PlayerEntity player)
    {
        if(!player.level.isClientSide())
        {
            this.state = !this.state;
        }
    }

    @Override
    public void serialize(JsonObject object)
    {
        JsonObject rotation = new JsonObject();
        rotation.addProperty("axis", this.axis.getKey());
        rotation.addProperty("angle", this.angle);
        object.add("rotation", rotation);
    }

    /*@Override
    public void tick(VehicleEntity vehicle)
    {
        if(this.state)
        {

        }
    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public void beforeRender(MatrixStack matrixStack, VehicleEntity vehicle, float partialTicks)
    {
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(vehicle.tickCount + partialTicks));
    }

    public enum Axis
    {
        X(Vector3f.XP, "x"),
        Y(Vector3f.YP, "y"),
        Z(Vector3f.ZP, "z");

        private final Vector3f axis;
        private final String key;

        Axis(Vector3f axis, String key)
        {
            this.axis = axis;
            this.key = key;
        }

        public Vector3f getAxis()
        {
            return this.axis;
        }

        public String getKey()
        {
            return this.key;
        }

        public static Axis fromKey(String key)
        {
            return Arrays.stream(values()).filter(axis -> axis.key.equals(key)).findFirst().orElse(Axis.X);
        }
    }
}
