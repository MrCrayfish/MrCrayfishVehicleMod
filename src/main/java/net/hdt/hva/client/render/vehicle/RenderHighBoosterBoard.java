package net.hdt.hva.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.Wheel;
import net.hdt.hva.client.render.RenderAirVehicle;
import net.hdt.hva.entity.vehicle.EntityHighBoosterBoard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderHighBoosterBoard extends RenderAirVehicle<EntityHighBoosterBoard>
{
    public RenderHighBoosterBoard(RenderManager renderManager)
    {
        super(renderManager);
        this.wheels.add(new Wheel(Wheel.Side.NONE, Wheel.Position.REAR, 0F, -6.7F, 1.65F));
    }

    @Override
    public void doRender(EntityHighBoosterBoard entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.scale(1.05, 1.05, 1.05);
            GlStateManager.translate(0, 0, 0.15);

            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * -20F, 0, 0, 1);

            this.setupBreakAnimation(entity, partialTicks);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0, 0);
                super.doRender(entity, x, y, z, currentYaw, partialTicks);
            }
            GlStateManager.popMatrix();

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.25, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
