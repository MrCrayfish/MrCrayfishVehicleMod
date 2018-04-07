package com.mrcrayfish.vehicle.client;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.entity.EntityATV;
import com.mrcrayfish.vehicle.entity.EntityDuneBuggy;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if(player.equals(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            return;

        Entity ridingEntity = player.getRidingEntity();
        if(ridingEntity instanceof EntityVehicle)
        {
            ModelPlayer model = event.getModelPlayer();

            EntityVehicle vehicle = (EntityVehicle) ridingEntity;

            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;

            if(ridingEntity instanceof EntityATV)
            {
                float turnRotation = wheelAngleNormal * 12F;
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);
            }
            else if (ridingEntity instanceof EntityDuneBuggy)
            {
                float turnRotation = wheelAngleNormal * 8F;
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-50F - turnRotation);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-50F + turnRotation);
            }

            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
        }
    }
}
