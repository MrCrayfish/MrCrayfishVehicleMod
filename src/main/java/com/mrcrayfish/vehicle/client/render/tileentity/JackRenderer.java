package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends TileEntityRenderer<JackTileEntity>
{
    @Override
    @SuppressWarnings("unchecked")
    public void render(JackTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if(Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            BlockPos pos = te.getPos();
            BlockState state = te.getWorld().getBlockState(pos);
            BlockRendererDispatcher rendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            GlStateManager.pushMatrix();
            {
                float scale = 1.0F;
                GlStateManager.translated(0.5, 0, 0.5);
                GlStateManager.scalef(scale, scale, scale);
                GlStateManager.translated(-0.5, 0, -0.5);

                //Render the base
                IBakedModel model = rendererDispatcher.getBlockModelShapes().getModel(state);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
                rendererDispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, state, pos, buffer, true, new Random(), 0L);
                buffer.setTranslation(0, 0, 0);
                tessellator.draw();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translated(0, -2 * 0.0625, 0);
                float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
                GlStateManager.translated(0, 0.5 * progress, 0);

                //Render the head
                IBakedModel model = SpecialModels.JACK_PISTON_HEAD.getModel();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
                rendererDispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, state, pos, buffer, true, new Random(), 0L);
                buffer.setTranslation(0, 0, 0);
                tessellator.draw();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                Entity jack = te.getJack();
                if(jack != null && jack.getPassengers().size() > 0)
                {
                    Entity passenger = jack.getPassengers().get(0);
                    if(passenger instanceof VehicleEntity && passenger.isAlive())
                    {
                        GlStateManager.translated(0.5, 0.5, 0.5);
                        GlStateManager.translated(0, -1 * 0.0625, 0);
                        float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
                        GlStateManager.translated(0, 0.5 * progress, 0);

                        VehicleEntity vehicle = (VehicleEntity) passenger;
                        Vec3d heldOffset = vehicle.getProperties().getHeldOffset().rotateYaw(passenger.rotationYaw * 0.017453292F);
                        GlStateManager.translated(-heldOffset.z * 0.0625, -heldOffset.y * 0.0625, -heldOffset.x * 0.0625);
                        GlStateManager.rotatef(-passenger.rotationYaw, 0, 1, 0);

                        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper((EntityType<? extends VehicleEntity>) vehicle.getType());
                        if(wrapper != null)
                        {
                            wrapper.render(vehicle, partialTicks);
                        }
                    }
                }
            }
            GlStateManager.popMatrix();

            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();
    }
}
