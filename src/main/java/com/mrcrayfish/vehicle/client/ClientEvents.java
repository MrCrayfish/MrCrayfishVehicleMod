package com.mrcrayfish.vehicle.client;

import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void renderCustomBlockHighlights(DrawHighlightEvent.HighlightBlock event)
    {
        /*BlockRayTraceResult target = event.getTarget();
        Entity player = event.getInfo().getRenderViewEntity();
        World world = player.world;
        BlockPos pos = target.getPos();
        if (!world.getWorldBorder().contains(pos))
        {
            return;
        }

        double dx = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
        double dy = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
        double dz = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFuelDrum)
        {
            boxRenderGlStart();
            AxisAlignedBB box = state.getRaytraceShape(world, pos).getBoundingBox().grow(0.002D).offset(-dx, -dy, -dz);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            float alpha = 0.4F;
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;
            double offset = 0.0625 * 4 - 0.0020000000949949026D * 4;
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            minX += offset;
            maxX -= offset;
            buffer.pos(minX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, maxY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, maxY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            minX -= offset;
            maxX += offset;
            minZ += offset;
            maxZ -= offset;
            buffer.pos(minX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, maxY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX + offset, maxY, minZ - offset).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
            buffer.pos(minX, maxY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, minY, maxZ).color(0, 0, 0, 0).endVertex();
            minZ -= offset;
            maxZ += offset;
            minX += offset;
            maxX -= offset;
            buffer.pos(minX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, maxY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX - offset, maxY, maxZ - offset).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(minX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, maxZ).color(0, 0, 0, 0).endVertex();
            minX -= offset;
            maxX += offset;
            minZ += offset;
            maxZ -= offset;
            buffer.pos(maxX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX - offset, maxY, maxZ + offset).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
            buffer.pos(maxX, maxY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, maxZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, minY, minZ).color(0, 0, 0, 0).endVertex();
            minZ -= offset;
            maxZ += offset;
            minX += offset;
            maxX -= offset;
            buffer.pos(maxX, minY, minZ).color(0, 0, 0, alpha).endVertex();
            buffer.pos(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
            buffer.pos(maxX + offset, maxY, minZ + offset).color(0, 0, 0, alpha).endVertex();
            tessellator.draw();

            boxRenderGlEnd();
            event.setCanceled(true);
        }
        else if (state.getBlock() instanceof BlockFluidPipe)
        {
            for (Hand hand : Hand.values())
            {
                if (!(player.getHeldItem(hand).getItem() == ModItems.WRENCH.get()))
                {
                    continue;
                }

                FluidPipeTileEntity pipe = BlockFluidPipe.getPipeTileEntity(world, pos);
                Vector3d hitVec = objectMouseOver.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
                Pair<AxisAlignedBB, Direction> hit = ((BlockFluidPipe) state.getBlock()).getWrenchableBox(world, pos, state, player, hand, objectMouseOver.sideHit, hitVec.x, hitVec.y, hitVec.z, pipe);
                if (hit != null)
                {
                    boxRenderGlStart();
                    VoxelShape
                    RenderGlobal.drawSelectionBoundingBox(hit.getLeft().grow(0.0020000000949949026D).offset(-dx, -dy, -dz), 0, 0, 0, 0.4F);
                    boxRenderGlEnd();
                }
                else if (state.getBlock() instanceof BlockFluidPump)
                {
                    AxisAlignedBB boxHit = ((BlockFluidPump) state.getBlock()).getHousingBox(world, pos, state, player, hand, hitVec.x, hitVec.y, hitVec.z, pipe);
                    if (boxHit != null)
                    {
                        boxRenderGlStart();
                        RenderGlobal.drawSelectionBoundingBox(boxHit.grow(0.0020000000949949026D).offset(-dx, -dy, -dz), 0, 0, 0, 0.4F);
                        boxRenderGlEnd();
                    }
                }
                event.setCanceled(true);
                break;
            }
        }*/
    }

    private void boxRenderGlStart()
    {
        /*GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);*/
    }

    private void boxRenderGlEnd()
    {
        /*GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();*/
    }

    @SubscribeEvent
    public void setLiquidFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        event.getInfo().getBlockAtCamera();
        /*Block block = event.getState().getBlock(); //TODO do i need to fix this
        boolean isSap = block == ModBlocks.ENDER_SAP.get();
        if (isSap || block == ModBlocks.FUELIUM.get() || block == ModBlocks.BLAZE_JUICE.get())
        {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);
            event.setDensity(isSap ? 1 : 0.5F);
            event.setCanceled(true);
        }*/
    }


}
