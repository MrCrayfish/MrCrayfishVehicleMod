package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractRenderVehicle<T extends EntityVehicle>
{
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
            new ResourceLocation("textures/blocks/destroy_stage_0.png"),
            new ResourceLocation("textures/blocks/destroy_stage_1.png"),
            new ResourceLocation("textures/blocks/destroy_stage_2.png"),
            new ResourceLocation("textures/blocks/destroy_stage_3.png"),
            new ResourceLocation("textures/blocks/destroy_stage_4.png"),
            new ResourceLocation("textures/blocks/destroy_stage_5.png"),
            new ResourceLocation("textures/blocks/destroy_stage_6.png"),
            new ResourceLocation("textures/blocks/destroy_stage_7.png"),
            new ResourceLocation("textures/blocks/destroy_stage_8.png"),
            new ResourceLocation("textures/blocks/destroy_stage_9.png")};

    public abstract void render(T entity, float partialTicks);

    public void applyPlayerModel(T entity, EntityPlayer player, ModelPlayer model, float partialTicks) {}

    public void applyPlayerRender(T entity, EntityPlayer player, float partialTicks) {}

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    protected void renderDamagedPart(EntityVehicle vehicle, IBakedModel model)
    {
        this.renderDamagedPart(vehicle, model, false);
        this.renderDamagedPart(vehicle, model, true);
    }

    private void renderDamagedPart(EntityVehicle vehicle, IBakedModel model, boolean renderDamage)
    {
        if(renderDamage)
        {
            int stage = vehicle.getDestroyedStage();
            if(stage <= 0)
                return;
            Minecraft.getMinecraft().getTextureManager().bindTexture(DESTROY_STAGES[stage - 1]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.scale(64F, 32F, 32F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.translate(0F, 0.0015F, 0.0001F);
            GlStateManager.scale(1.0025F, 1.0025F, 1.0025F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            GlStateManager.pushMatrix();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false);
            if(!model.isBuiltInRenderer())
            {
                RenderUtil.renderModel(model, vehicle.getColor());
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, vehicle.getColor());
        }
    }

    public SpecialModels getTowBarModel()
    {
        return SpecialModels.TOW_BAR;
    }
}