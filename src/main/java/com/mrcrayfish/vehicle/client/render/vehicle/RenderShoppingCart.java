package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityShoppingCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderShoppingCart extends AbstractRenderLandVehicle<EntityShoppingCart>
{
    public RenderShoppingCart()
    {
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F);
    }

    @Override
    public void render(EntityShoppingCart entity, float partialTicks)
    {
        renderDamagedPart(entity, entity.body);
    }

    @Override
    public void applyPlayerModel(EntityShoppingCart entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-70F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-70F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
    }
}
