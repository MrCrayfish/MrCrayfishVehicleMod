package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderVehicle<T extends VehicleEntity>
{
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
        new ResourceLocation("textures/block/destroy_stage_0.png"),
        new ResourceLocation("textures/block/destroy_stage_1.png"),
        new ResourceLocation("textures/block/destroy_stage_2.png"),
        new ResourceLocation("textures/block/destroy_stage_3.png"),
        new ResourceLocation("textures/block/destroy_stage_4.png"),
        new ResourceLocation("textures/block/destroy_stage_5.png"),
        new ResourceLocation("textures/block/destroy_stage_6.png"),
        new ResourceLocation("textures/block/destroy_stage_7.png"),
        new ResourceLocation("textures/block/destroy_stage_8.png"),
        new ResourceLocation("textures/block/destroy_stage_9.png")
    };

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

            Minecraft.getInstance().getTextureManager().bindTexture(DESTROY_STAGES[stage - 1]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.scalef(64F, 32F, 32F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.polygonOffset(-1.0F, -10.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false);
            if(!model.isBuiltInRenderer())
            {
                RenderUtil.renderModel(model, ItemStack.EMPTY, vehicle.getColor());
            }
            GlStateManager.popMatrix();
            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, vehicle.getColor());
        }
    }
}