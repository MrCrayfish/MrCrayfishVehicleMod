package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class RenderEntityVehicle<T extends VehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends EntityRenderer<T>
{
    private final RenderVehicleWrapper<T, R> wrapper;

    public RenderEntityVehicle(EntityRendererManager renderManager, RenderVehicleWrapper<T, R> wrapper)
    {
        super(renderManager);
        this.wrapper = wrapper;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    public void func_225623_a_(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_)
    {
        if(!entity.isAlive())
            return;

        if(entity.getRidingEntity() instanceof EntityJack)
            return;

        matrixStack.func_227860_a_();
        wrapper.applyPreRotations(entity, matrixStack, partialTicks);
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-entityYaw));
        this.setupBreakAnimation(entity, matrixStack, partialTicks);
        wrapper.render(entity, matrixStack, renderTypeBuffer, partialTicks);
        matrixStack.func_227865_b_();

        EntityRaytracer.renderRaytraceElements(entity, matrixStack, entityYaw);
    }

    private void setupBreakAnimation(VehicleEntity vehicle, MatrixStack matrixStack, float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        if(timeSinceHit > 0.0F)
        {
            matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(MathHelper.sin(timeSinceHit) * timeSinceHit));
        }
    }
}
