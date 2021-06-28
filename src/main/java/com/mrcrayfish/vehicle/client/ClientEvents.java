package com.mrcrayfish.vehicle.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void renderCustomBlockHighlights(DrawHighlightEvent.HighlightBlock event)
    {
        BlockRayTraceResult target = event.getTarget();
        Entity entity = event.getInfo().getEntity();
        if(!(entity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entity;
        World world = player.level;
        BlockPos pos = target.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() == ModBlocks.FLUID_PUMP.get() && player.getMainHandItem().getItem() == ModItems.WRENCH.get())
        {
            FluidPumpBlock fluidPumpBlock = (FluidPumpBlock) state.getBlock();
            if(fluidPumpBlock.isLookingAtHousing(state, target.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ())))
            {
                event.setCanceled(true);
                VoxelShape baseShape = FluidPumpBlock.PUMP_BOX[state.getValue(FluidPumpBlock.DIRECTION).getOpposite().get3DDataValue()];
                IVertexBuilder builder = event.getBuffers().getBuffer(RenderType.lines());
                MatrixStack matrixStack = event.getMatrix();
                matrixStack.pushPose();
                Vector3d position = event.getInfo().getPosition();
                matrixStack.translate(-position.x, -position.y, -position.z);
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                EntityRayTracer.renderShape(matrixStack, builder, baseShape, 0.0F, 1.0F, 0.0F, 1.0F);
                EntityRayTracer.renderShape(matrixStack, builder, fluidPumpBlock.getPipeShape(state, world, pos), 0.0F, 0.0F, 0.0F, 0.4F);
                matrixStack.popPose();
            }
        }

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

    /*@SubscribeEvent
    public void setLiquidFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        event.getInfo().getBlockAtCamera();
        *//*Block block = event.getState().getBlock(); //TODO do i need to fix this
        boolean isSap = block == ModBlocks.ENDER_SAP.get();
        if (isSap || block == ModBlocks.FUELIUM.get() || block == ModBlocks.BLAZE_JUICE.get())
        {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);
            event.setDensity(isSap ? 1 : 0.5F);
            event.setCanceled(true);
        }*//*
    }*/
}
