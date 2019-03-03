package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityJack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends TileEntitySpecialRenderer<TileEntityJack>
{
    @Override
    @SuppressWarnings("unchecked")
    public void render(TileEntityJack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
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
            IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
            IBlockState state = world.getBlockState(pos);
            BlockRendererDispatcher rendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            GlStateManager.pushMatrix();
            {
                float scale = 1.0F;
                GlStateManager.translate(0.5, 0, 0.5);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.translate(-0.5, 0, -0.5);

                //Render the base
                IBakedModel model = rendererDispatcher.getBlockModelShapes().getModelForState(state);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
                rendererDispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, state, pos, buffer, false);
                buffer.setTranslation(0, 0, 0);
                tessellator.draw();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -2 * 0.0625, 0);
                float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) TileEntityJack.MAX_LIFT_PROGRESS;
                GlStateManager.translate(0, 0.5 * progress, 0);

                //Render the head
                IBakedModel model = rendererDispatcher.getBlockModelShapes().getModelForState(ModBlocks.JACK_HEAD.getDefaultState());
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
                rendererDispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, state, pos, buffer, false);
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
                    if(passenger instanceof EntityVehicle && !passenger.isDead)
                    {
                        GlStateManager.translate(0.5, 0.5, 0.5);
                        GlStateManager.translate(0, -1 * 0.0625, 0);
                        float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) TileEntityJack.MAX_LIFT_PROGRESS;
                        GlStateManager.translate(0, 0.5 * progress, 0);

                        EntityVehicle vehicle = (EntityVehicle) passenger;
                        Vec3d heldOffset = vehicle.getProperties().getHeldOffset().rotateYaw(passenger.rotationYaw * 0.017453292F);
                        GlStateManager.translate(-heldOffset.z * 0.0625, -heldOffset.y * 0.0625, -heldOffset.x * 0.0625);
                        GlStateManager.rotate(-passenger.rotationYaw, 0, 1, 0);

                        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper(vehicle.getClass());
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
