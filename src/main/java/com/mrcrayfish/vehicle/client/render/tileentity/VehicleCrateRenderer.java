package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.LightType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateRenderer extends TileEntityRenderer<VehicleCrateTileEntity>
{
    public VehicleCrateRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(VehicleCrateTileEntity crate, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay)
    {
        BlockState state = crate.getWorld().getBlockState(crate.getPos());
        if(state.getBlock() != ModBlocks.VEHICLE_CRATE)
            return;
        
        matrixStack.func_227860_a_();

        Direction facing = state.get(BlockRotatedObject.DIRECTION);
        matrixStack.func_227861_a_(0.5, 0.5, 0.5);
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(facing.getHorizontalIndex() * -90F + 180F));
        matrixStack.func_227861_a_(-0.5, -0.5, -0.5);

        this.field_228858_b_.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        matrixStack.func_227860_a_();

        light = WorldRenderer.func_228421_a_(crate.getWorld(), crate.getPos().up()); //TODO figure out the correct way to calculate light

        if(crate.isOpened() && crate.getTimer() > 150)
        {
            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 50.0);
            matrixStack.func_227861_a_(0, (-4 * 0.0625) * progress, 0);
        }

        //Sides panels
        for(int i = 0; i < 4; i++)
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.5, 0, 0.5);
            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(90F * i));
            matrixStack.func_227861_a_(0, 0, 8 * 0.0625);

            if(crate.isOpened())
            {
                double progress = Math.min(1.0, Math.max(0, crate.getTimer() - (i * 20) + 5 * partialTicks) / 90.0);
                double angle = (progress * progress) * 90F;
                double rotation = 1.0 - Math.cos(Math.toRadians(angle));
                matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_((float) rotation * 90F));
            }
            matrixStack.func_227861_a_(0.0, 0.5, 0.0);
            matrixStack.func_227861_a_(0, 0, -2 * 0.0625);
            RenderUtil.renderColoredModel(SpecialModel.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
            matrixStack.func_227865_b_();
        }

        //Render top panel
        if(!crate.isOpened())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.5, 0.5, 0.5);
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-90F));
            matrixStack.func_227862_a_(1.005F, 1.005F, 1.005F);
            matrixStack.func_227861_a_(0, 0, (6 * 0.0625) * 0.998);
            RenderUtil.renderColoredModel(SpecialModel.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
            matrixStack.func_227865_b_();
        }

        //Render bottom panel
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.5, 0.5, 0.5);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(90F));
        matrixStack.func_227862_a_(1.001F, 1.001F, 1.001F);
        matrixStack.func_227861_a_(0, 0, (6 * 0.0625) * 0.998);
        RenderUtil.renderColoredModel(SpecialModel.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
        matrixStack.func_227865_b_();

        matrixStack.func_227865_b_();

        if(crate.getEntity() != null && crate.isOpened())
        {
            matrixStack.func_227861_a_(0.5F, 0.0F, 0.5F);

            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 100.0);
            Pair<Float, Float> scaleAndOffset = EntityRaytracer.getCrateScaleAndOffset(crate.getEntity().getClass());
            float scaleStart = scaleAndOffset.getLeft();
            float scale = scaleStart + (1 - scaleStart) * (float) progress;
            matrixStack.func_227861_a_(0, 0, scaleAndOffset.getRight() * (1 - progress) * scale);

            if(crate.getTimer() >= 150)
            {
                matrixStack.func_227861_a_(0, Math.sin(Math.PI * progress) * 5, 0);
                matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_((float) (720F * progress)));
            }

            matrixStack.func_227861_a_(0, (2 * 0.0625F) * (1.0F - progress), 0);
            matrixStack.func_227862_a_(scale, scale, scale);

            EntityRenderer<? extends Entity> renderer = Minecraft.getInstance().getRenderManager().getRenderer(crate.getEntity());
            renderer.func_225623_a_(crate.getEntity(), 0.0F, partialTicks, matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();
    }
}
