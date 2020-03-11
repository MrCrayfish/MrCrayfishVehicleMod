package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityJack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends EntityRenderer<EntityJack>
{
    public JackRenderer(EntityRendererManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityJack entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityJack entity, double x, double y, double z, float entityYaw, float partialTicks) { }

}
