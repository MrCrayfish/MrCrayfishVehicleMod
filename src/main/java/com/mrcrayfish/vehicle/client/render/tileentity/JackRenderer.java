package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityJack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.FastTESR;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends TileEntitySpecialRenderer<TileEntityJack>
{
    @Override
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
                float progress = (te.prevLiftProgress + (te.liftProgress - te.prevLiftProgress) * partialTicks) / 10F;
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

            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();
    }
}
