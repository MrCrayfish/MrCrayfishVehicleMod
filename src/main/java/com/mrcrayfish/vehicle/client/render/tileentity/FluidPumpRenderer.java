package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Author: MrCrayfish
 */
public class FluidPumpRenderer extends TileEntityRenderer<PumpTileEntity>
{
    public FluidPumpRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(PumpTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay)
    {
        Entity entity = this.renderer.camera.getEntity();
        if(!(entity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entity;
        if(player.getMainHandItem().getItem() != ModItems.WRENCH.get())
            return;

        if(this.renderer.cameraHitResult == null || this.renderer.cameraHitResult.getType() != RayTraceResult.Type.BLOCK)
            return;

        BlockRayTraceResult result = (BlockRayTraceResult) this.renderer.cameraHitResult;
        if(!result.getBlockPos().equals(tileEntity.getBlockPos()))
            return;

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);

        BlockState state = tileEntity.getBlockState();
        Direction direction = state.getValue(FluidPumpBlock.DIRECTION);
        matrixStack.translate(-direction.getStepX() * 0.35, -direction.getStepY() * 0.35, -direction.getStepZ() * 0.35);

        matrixStack.mulPose(this.renderer.camera.rotation());
        matrixStack.scale(-0.015F, -0.015F, 0.015F);
        Matrix4f matrix4f = matrixStack.last().pose();
        FontRenderer fontRenderer = this.renderer.font;
        ITextComponent text = new TranslationTextComponent(tileEntity.getPowerMode().getKey());
        float x = (float)(-fontRenderer.width(text) / 2);
        fontRenderer.drawInBatch(text, x, 0, -1, true, matrix4f, renderTypeBuffer, true, 0, 15728880);
        matrixStack.popPose();
    }
}
