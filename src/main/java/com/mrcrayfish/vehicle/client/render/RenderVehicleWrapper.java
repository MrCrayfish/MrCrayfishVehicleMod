package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleWrapper<T extends VehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>>
{
    protected final R renderVehicle;

    public RenderVehicleWrapper(R renderVehicle)
    {
        this.renderVehicle = renderVehicle;
    }

    public R getRenderVehicle()
    {
        return renderVehicle;
    }

    public void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        if(!entity.isAlive())
            return;

        matrixStack.func_227860_a_();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float) bodyPosition.getRotX()));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float) bodyPosition.getRotY()));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float) bodyPosition.getRotZ()));

        if(entity.canTowTrailer())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F));
            Vec3d towBarOffset = properties.getTowBarPosition();
            matrixStack.func_227861_a_(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(SpecialModel.TOW_BAR.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.field_229196_a_);
            matrixStack.func_227865_b_();
        }

        matrixStack.func_227861_a_(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.func_227862_a_((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.func_227861_a_(0.0, 0.5, 0.0);
        matrixStack.func_227861_a_(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.func_227861_a_(0.0, properties.getWheelOffset() * 0.0625, 0.0);
        this.renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks);

        matrixStack.func_227865_b_();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, MatrixStack stack, float partialTicks) {}

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to apply to the part
     * @param model the part to render onto the vehicle
     */
    protected void renderPart(@Nullable PartPosition position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null)
            return;

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.func_227861_a_(0.0, -0.5, 0.0);
        matrixStack.func_227862_a_((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.func_227861_a_(0.0, 0.5, 0.0);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float) position.getRotX()));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float) position.getRotY()));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float) position.getRotZ()));
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, buffer, color, lightTexture, overlayTexture);
        matrixStack.func_227865_b_();
    }

    protected void renderKey(@Nullable PartPosition position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null)
            return;

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.func_227861_a_(0.0, -0.25, 0.0);
        matrixStack.func_227862_a_((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float) position.getRotX()));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float) position.getRotY()));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float) position.getRotZ()));
        matrixStack.func_227861_a_(0.0, 0.0, -0.05);
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, buffer, color, lightTexture, overlayTexture);
        matrixStack.func_227865_b_();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param position the render definitions to apply to the part
     * @param part the part to render onto the vehicle
     */
    protected void renderEngine(PoweredVehicleEntity entity, @Nullable PartPosition position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer)
    {
        if(entity.isFueled() && entity.getControllingPassenger() != null)
        {
            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(0.5F * (entity.ticksExisted % 2)));
            matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(0.5F * (entity.ticksExisted % 2)));
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-0.5F * (entity.ticksExisted % 2)));
        }
        this.renderPart(position, model, matrixStack, buffer, -1, 15728880, OverlayTexture.field_229196_a_);
    }

    protected IBakedModel getWheelModel(PoweredVehicleEntity entity)
    {
        ItemStack stack = ItemLookup.getWheel(entity);
        if(!stack.isEmpty())
        {
            return RenderUtil.getModel(stack);
        }
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }

    protected IBakedModel getEngineModel(PoweredVehicleEntity entity)
    {
        ItemStack stack = ItemLookup.getEngine(entity);
        if(!stack.isEmpty())
        {
            return RenderUtil.getModel(stack);
        }
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }
}
