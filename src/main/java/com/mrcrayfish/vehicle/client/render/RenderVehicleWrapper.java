package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleWrapper<T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>>
{
    protected R renderVehicle;

    public RenderVehicleWrapper(R renderVehicle)
    {
        this.renderVehicle = renderVehicle;
    }

    public R getRenderVehicle()
    {
        return renderVehicle;
    }

    public void render(T entity, float partialTicks)
    {
        if(entity.isDead)
            return;

        GlStateManager.pushMatrix();
        {
            //Enable the standard item lighting so vehicles render correctly
            RenderHelper.enableStandardItemLighting();

            //Apply vehicle rotations and translations. This is applied to all other parts
            PartPosition bodyPosition = entity.getBodyPosition();
            GlStateManager.rotate((float) bodyPosition.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) bodyPosition.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) bodyPosition.getRotZ(), 0, 0, 1);

            //Translate the body
            GlStateManager.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

            //Translate the vehicle to match how it is shown in the model creator
            GlStateManager.translate(0, 0.5, 0);

            //Apply vehicle scale
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(bodyPosition.getScale(), bodyPosition.getScale(), bodyPosition.getScale());
            GlStateManager.translate(0, 0.5, 0);

            //Translate the vehicle so it's axles are half way into the ground
            GlStateManager.translate(0, entity.getAxleOffset() * 0.0625F, 0);

            //Translate the vehicle so it's actually riding on it's wheels
            GlStateManager.translate(0, entity.getWheelOffset() * 0.0625F, 0);

            //Render body
            renderVehicle.render(entity, partialTicks);
        }
        GlStateManager.popMatrix();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, float partialTicks) {}

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to apply to the part
     * @param part the part to render onto the vehicle
     */
    protected void renderPart(@Nullable PartPosition position, ItemStack part)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.translate(0, 0.5, 0);
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    protected void renderKey(@Nullable PartPosition position, ItemStack part)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.25, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            GlStateManager.translate(0, 0, -0.05);
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param position the render definitions to apply to the part
     * @param part the part to render onto the vehicle
     */
    protected void renderEngine(EntityPoweredVehicle entity, @Nullable PartPosition position, ItemStack part)
    {
        if(entity.isFueled() && entity.getControllingPassenger() != null)
        {
            GlStateManager.rotate(0.5F * (entity.ticksExisted % 2), 1, 0, 1);
            GlStateManager.rotate(-0.5F * (entity.ticksExisted % 2), 0, 1, 0);
        }
        this.renderPart(position, part);
    }

    public static void renderBody(ItemStack stack, ItemCameraTransforms.TransformType transform)
    {
        if (!stack.isEmpty())
        {
            IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
            //Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            //Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/destroy_stage_9.png"));
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, transform, false);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);

            //Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            //Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        }
    }
}
