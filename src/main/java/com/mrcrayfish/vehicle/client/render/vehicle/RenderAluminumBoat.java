package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityAluminumBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderAluminumBoat extends AbstractRenderVehicle<EntityAluminumBoat>
{
    private ModelRenderer noWater;

    public RenderAluminumBoat()
    {
        this.setFuelPortPosition(EntityAluminumBoat.FUEL_PORT_POSITION);
        this.noWater = (new ModelRenderer(new ModelBase()
        {
            @Override
            public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
            {
                super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }, 0, 0)).setTextureSize(0, 0);
        this.noWater.addBox(-15F, -4F, -21F, 30, 8, 35, 0.0F);
    }

    @Override
    public void render(EntityAluminumBoat entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
    }

    @Override
    public void applyPlayerModel(EntityAluminumBoat entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
    }

    @Override
    public void applyPlayerRender(EntityAluminumBoat entity, EntityPlayer player, float partialTicks)
    {
        double offsetX = -0.5;
        double offsetY = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        double offsetZ = -0.9;

        int index = entity.getPassengers().indexOf(player);
        if(index > 0)
        {
            offsetX += (index % 2) * 1F;
            offsetZ += (index / 2) * 1.2F;
        }

        GlStateManager.translate(offsetX, offsetY, offsetZ);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
        GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
        GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
    }

    //TODO fix this
    /*@Override
    public void renderMultipass(EntityAluminumBoat entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.scale(1.1, 1.1, 1.1);
            GlStateManager.translate(0, 0.5, 0.2);

            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * -15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.colorMask(false, false, false, false);
            this.noWater.render(0.0625F);
            GlStateManager.colorMask(true, true, true, true);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isMultipass()
    {
        return true;
    }*/
}
