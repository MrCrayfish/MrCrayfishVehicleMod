package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends TileEntityRenderer<JackTileEntity>
{
    public JackRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(JackTileEntity jack, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1)
    {
        /*matrixStack.func_227860_a_();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            //this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
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
            IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(jack.getWorld(), pos);
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
                buffer.func_225582_a_(-pos.getX(), -pos.getY(), -pos.getZ());
                rendererDispatcher.getBlockModelRenderer().renderModel(jack.getWorld(), model, state, pos, matrixStack, buffer.getVertexBuilder(), false);
                buffer.func_225582_a_(0, 0, 0);
                tessellator.draw();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -2 * 0.0625, 0);
                float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
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
                    if(passenger instanceof VehicleEntity && !passenger.isDead)
                    {
                        GlStateManager.translate(0.5, 0.5, 0.5);
                        GlStateManager.translate(0, -1 * 0.0625, 0);
                        float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
                        GlStateManager.translate(0, 0.5 * progress, 0);

                        VehicleEntity vehicle = (VehicleEntity) passenger;
                        Vec3d heldOffset = vehicle.getProperties().getHeldOffset().rotateYaw(passenger.rotationYaw * 0.017453292F);
                        GlStateManager.translate(-heldOffset.z * 0.0625, -heldOffset.y * 0.0625, -heldOffset.x * 0.0625);
                        GlStateManager.rotate(-passenger.rotationYaw, 0, 1, 0);

                        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper(vehicle.getClass());
                        if(wrapper != null)
                        {
                            wrapper.render(vehicle, , , partialTicks);
                        }
                    }
                }
            }
            GlStateManager.popMatrix();

            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();*/
    }
}
