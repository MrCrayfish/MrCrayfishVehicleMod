package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class EntityVehicleRenderer<T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable> extends EntityRenderer<T>
{
    private final AbstractVehicleRenderer<T> wrapper;

    public EntityVehicleRenderer(EntityRendererManager renderManager, AbstractVehicleRenderer<T> wrapper)
    {
        super(renderManager);
        this.wrapper = wrapper;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return null;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!entity.isAlive())
            return;

        if(entity.getVehicle() instanceof EntityJack)
            return;

        matrixStack.pushPose();
        wrapper.applyPreRotations(entity, matrixStack, partialTicks);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-entityYaw));
        this.setupBreakAnimation(entity, matrixStack, partialTicks);
        wrapper.setupTransformsAndRender(entity, matrixStack, renderTypeBuffer, partialTicks, light);
        this.drawDebugging(entity, matrixStack);
        matrixStack.popPose();

        EntityRayTracer.instance().renderRayTraceElements(entity, matrixStack, renderTypeBuffer, entityYaw);
    }

    private void setupBreakAnimation(VehicleEntity vehicle, MatrixStack matrixStack, float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        if(timeSinceHit > 0.0F)
        {
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.sin(timeSinceHit) * timeSinceHit));
        }
    }

    private void drawDebugging(T entity, MatrixStack stack)
    {
        if(!Config.CLIENT.renderDebugging.get())
            return;

        VehicleProperties properties = entity.getProperties();
        this.drawAxle(properties.getFrontAxelVec(), properties, stack);
        this.drawAxle(properties.getRearAxelVec(), properties, stack);
    }

    private void drawAxle(@Nullable Vector3d position, VehicleProperties properties, MatrixStack stack)
    {
        if(position != null)
        {
            PartPosition body = properties.getBodyPosition();
            double offset = properties.getWheels().stream().findFirst().map(wheel -> wheel.getOffset().y).orElse(0.0);
            Vector3d wheelOffset = new Vector3d(0, properties.getWheelOffset(), 0).add(0, offset, 0);
            Vector3d axle = position.add(wheelOffset).scale(0.0625).scale(body.getScale());
            this.drawLine(stack, axle.add(-1, 0, 0), axle.add(1, 0, 0), 0xFFB64C);
        }
    }

    private void drawLine(MatrixStack stack, Vector3d from, Vector3d to, int color)
    {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(Math.max(2.0F, (float) Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.0F));
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(stack.last().pose(), (float) from.x, (float) from.y, (float) from.z).color(red, green, blue, 1.0F).endVertex();
        buffer.vertex(stack.last().pose(), (float) to.x, (float) to.y, (float) to.z).color(red, green, blue, 1.0F).endVertex();
        tessellator.end();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
    }
}
