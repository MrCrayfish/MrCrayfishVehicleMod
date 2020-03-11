package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderVehicle<T extends VehicleEntity>
{
    public ISpecialModel getKeyHoleModel()
    {
        return SpecialModels.KEY_HOLE;
    }

    public ISpecialModel getTowBarModel()
    {
        return SpecialModels.TOW_BAR;
    }

    public abstract void render(T entity, float partialTicks);

    public void applyPlayerModel(T entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks) {}

    public void applyPlayerRender(T entity, PlayerEntity player, float partialTicks) {}

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    protected void renderDamagedPart(VehicleEntity vehicle, ItemStack part)
    {
        this.renderDamagedPart(vehicle, RenderUtil.getModel(part));
    }

    protected void renderDamagedPart(VehicleEntity vehicle, IBakedModel model)
    {
        this.renderDamagedPart(vehicle, model, false);
        this.renderDamagedPart(vehicle, model, true);
    }

    private void renderDamagedPart(VehicleEntity vehicle, IBakedModel model, boolean renderDamage)
    {
        if(renderDamage)
        {
            int stage = vehicle.getDestroyedStage();
            if(stage <= 0)
                return;
            RenderUtil.renderDamagedVehicleModel(model, ItemCameraTransforms.TransformType.NONE, false, stage, vehicle.getColor());
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, vehicle.getColor());
        }
    }
}