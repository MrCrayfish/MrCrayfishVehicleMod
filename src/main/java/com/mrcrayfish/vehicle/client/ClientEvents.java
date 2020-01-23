package com.mrcrayfish.vehicle.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private int lastSlot = -1;
    private int originalPerspective = -1;
    private double fuelingHandOffset;
    private int tickCounter;
    private boolean fueling;
    private double offsetPrev, offsetPrevPrev;
    private boolean shouldRenderNozzle;

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event)
    {
        if(Config.CLIENT.autoPerspective.get())
        {
            if(event.getWorldObj().isRemote)
            {
                if(event.getEntityMounting().equals(Minecraft.getInstance().player))
                {
                    if(event.isMounting())
                    {
                        Entity entity = event.getEntityBeingMounted();
                        if(entity instanceof VehicleEntity)
                        {
                            originalPerspective = Minecraft.getInstance().gameSettings.thirdPersonView;
                            Minecraft.getInstance().gameSettings.thirdPersonView = 1;
                        }
                    }
                    else if(originalPerspective != -1)
                    {
                        Minecraft.getInstance().gameSettings.thirdPersonView = originalPerspective;
                        originalPerspective = -1;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(Config.CLIENT.autoPerspective.get() && Minecraft.getInstance().player != null)
        {
            Entity entity = Minecraft.getInstance().player.getRidingEntity();
            if(entity instanceof VehicleEntity)
            {
                if(Minecraft.getInstance().gameSettings.keyBindTogglePerspective.isKeyDown())
                {
                    originalPerspective = -1;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(Config.CLIENT.enabledSpeedometer.get() && event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.isGameFocused() && !mc.gameSettings.hideGUI)
            {
                PlayerEntity player = mc.player;
                if(player != null)
                {
                    Entity entity = player.getRidingEntity();
                    if(entity instanceof PoweredVehicleEntity)
                    {
                        PoweredVehicleEntity vehicle = (PoweredVehicleEntity) entity;

                        String speed = new DecimalFormat("0.0").format(vehicle.getKilometersPreHour());
                        mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + "BPS: " + TextFormatting.YELLOW + speed, 10, 10, Color.WHITE.getRGB());

                        if(vehicle.requiresFuel())
                        {
                            DecimalFormat format = new DecimalFormat("0.0");
                            String fuel = format.format(vehicle.getCurrentFuel()) + "/" + format.format(vehicle.getFuelCapacity());
                            mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + "Fuel: " + TextFormatting.YELLOW + fuel, 10, 25, Color.WHITE.getRGB());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event)
    {
        Entity ridingEntity = Minecraft.getInstance().player.getRidingEntity();
        if(ridingEntity instanceof VehicleEntity)
        {
            event.setNewfov(1.0F);
        }
    }

    @SubscribeEvent
    public void onPreRender(PlayerModelEvent.Render.Pre event)
    {
        PlayerEntity player = event.getPlayer();
        Entity ridingEntity = player.getRidingEntity();
        if(ridingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) ridingEntity;
            /* Suppressed due to warning however it's safe to say cast won't throw an exception
             * due to the strict registration process of vehicle renders */
            @SuppressWarnings("unchecked")
            AbstractRenderVehicle<VehicleEntity> render = (AbstractRenderVehicle<VehicleEntity>) VehicleRenderRegistry.getRender((EntityType<? extends VehicleEntity>) vehicle.getType());
            if(render != null)
            {
                render.applyPlayerRender(vehicle, player, event.getPartialTicks(), event.getMatrixStack(), event.getBuilder());
            }
        }
    }

    @SubscribeEvent
    public void onSetupAngles(PlayerModelEvent.SetupAngles.Post event)
    {
        PlayerEntity player = event.getPlayer();

        if(player.equals(Minecraft.getInstance().player) && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
            return;

        Entity ridingEntity = player.getRidingEntity();
        PlayerModel model = event.getModelPlayer();

        if(player.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            boolean rightHanded = player.getPrimaryHand() == HandSide.RIGHT;
            if(rightHanded)
            {
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-20F);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
                model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(0F);
            }
            else
            {
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-20F);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
                model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(0F);
            }
            return;
        }

        if(player.getRidingEntity() != null)
        {
            boolean rightHanded = player.getPrimaryHand() == HandSide.RIGHT;
            ItemStack rightItem = rightHanded ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
            ItemStack leftItem = rightHanded ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
            if(!rightItem.isEmpty() && rightItem.getItem() instanceof SprayCanItem)
            {
                copyModelAngles(model.bipedHead, model.bipedRightArm);
                model.bipedRightArm.rotateAngleX += Math.toRadians(-80F);
            }
            if(!leftItem.isEmpty() && leftItem.getItem() instanceof SprayCanItem)
            {
                model.bipedLeftArm.copyModelAngles(model.bipedHead);
                model.bipedLeftArm.rotateAngleX += Math.toRadians(-80F);
            }
        }

        if(player.getDataManager().get(CustomDataParameters.PUSHING_CART))
        {
            player.renderYawOffset = player.rotationYawHead;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
            return;
        }

        if(ridingEntity != null && ridingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) ridingEntity;
            /* Suppressed due to warning however it's safe to say cast won't throw an exception
             * due to the registration process of vehicle renders */
            @SuppressWarnings("unchecked")
            AbstractRenderVehicle<VehicleEntity> render = (AbstractRenderVehicle<VehicleEntity>) VehicleRenderRegistry.getRender((EntityType<? extends VehicleEntity>) vehicle.getType());
            if(render != null)
            {
                render.applyPlayerModel(vehicle, player, model, event.getPartialTicks());
                return;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void copyModelAngles(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(event.phase == TickEvent.Phase.END && player != null)
        {
            int slot = player.inventory.currentItem;
            if(this.lastSlot != slot)
            {
                this.lastSlot = slot;
                if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof SprayCanItem)
                {
                    SprayCanItem sprayCan = (SprayCanItem) player.inventory.getCurrentItem().getItem();
                    float pitch = 0.85F + 0.15F * sprayCan.getRemainingSprays(player.inventory.getCurrentItem());
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(ModSounds.SPRAY_CAN_SHAKE, pitch, 0.75F));
                }
            }

            if(player.getRidingEntity() == null)
            {
                originalPerspective = -1;
            }

            tickCounter++;
            RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                if (!fueling)
                {
                    tickCounter = 0;
                    fueling = true;
                }
            }
            else
            {
                fueling = false;
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderSpecificHandEvent event)
    {
        MatrixStack matrixStack = event.getMatrixStack();
        if (event.getHand() == Hand.OFF_HAND && fuelingHandOffset > -1)
        {
            matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(25F));
            matrixStack.translate(0, -0.35 - fuelingHandOffset, 0.2);
        }

        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof SprayCanItem)
        {
            //ItemStack stack = event.getItemStack().copy(); //TODO fix the spray can item render
            //stack.setItemDamage(1);
            //Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(Minecraft.getMinecraft().player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), stack, event.getEquipProgress());
            //event.setCanceled(true);
        }

        fuelingHandOffset = -1;
        RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
        if (result != null && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING) && event.getHand() == EntityRaytracer.getContinuousInteractionObject())
        {
            double offset = Math.sin((tickCounter + Minecraft.getInstance().getRenderPartialTicks()) * 0.4) * 0.01;
            if (offsetPrev > offsetPrevPrev && offsetPrev > offset)
            {
                Minecraft.getInstance().player.playSound(ModSounds.LIQUID_GLUG, 0.3F, 1F);
            }
            offsetPrevPrev = offsetPrev;
            offsetPrev = offset;
            matrixStack.translate(0, 0.35 + offset, -0.2);
            matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-25F));
            if (event.getHand() == Hand.MAIN_HAND)
            {
                fuelingHandOffset = offset;
            }
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if(player.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            if(event.getSwingProgress() > 0)
            {
                shouldRenderNozzle = true;
            }
            if(event.getHand() == Hand.MAIN_HAND && shouldRenderNozzle)
            {
                if(event.getSwingProgress() > 0 && event.getSwingProgress() <= 0.25) return;
                matrixStack.push();
                boolean mainHand = event.getHand() == Hand.MAIN_HAND;
                HandSide handSide = mainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
                int handOffset = handSide == HandSide.RIGHT ? 1 : -1;
                matrixStack.translate(handOffset * 0.65, -0.52 + 0.25, -0.72);
                matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(45F));
                IRenderTypeBuffer renderTypeBuffer = Minecraft.getInstance().func_228019_au_().func_228487_b_();
                int light = Minecraft.getInstance().getRenderManager().func_229085_a_(player, event.getPartialTicks());
                RenderUtil.renderColoredModel(SpecialModel.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.DEFAULT_LIGHT); //TODO check
                matrixStack.pop();
                event.setCanceled(true);
            }
        }
        else
        {
            shouldRenderNozzle = false;
        }
    }

    @SubscribeEvent
    public void onRenderThirdPerson(RenderItemEvent.Held.Pre event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof PlayerEntity && entity.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
            return;
        }

        if(!event.getItem().isEmpty() && event.getItem().getItem() instanceof SprayCanItem)
        {
            /*ItemStack stack = event.getItem().copy(); //TODO fix this spray can render
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(event.getEntity(), stack, event.getTransformType(), event.getHandSide() == EnumHandSide.LEFT);
            event.setCanceled(true);*/
        }
    }

    @SubscribeEvent
    public void onModelRenderPost(PlayerModelEvent.Render.Post event)
    {
        MatrixStack matrixStack = event.getMatrixStack();
        PlayerEntity entity = event.getPlayer();
        if(entity.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            matrixStack.push();
            {
                if(event.getModelPlayer().isChild)
                {
                    matrixStack.translate(0.0, 0.75, 0.0);
                    matrixStack.scale(0.5F, 0.5F, 0.5F);
                }
                matrixStack.push();
                {
                    if(entity.isCrouching())
                    {
                        matrixStack.translate(0.0, 0.2, 0.0);
                    }
                    //event.getModelPlayer().postRenderArm(0.0625F, entity.getPrimaryHand()); //TODO find out what this is
                    matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(180F));
                    matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(180F));
                    boolean leftHanded = entity.getPrimaryHand() == HandSide.LEFT;
                    matrixStack.translate((leftHanded ? -1 : 1) / 16.0, 0.125, -0.625);
                    matrixStack.translate(0, -9 * 0.0625F, 5.75 * 0.0625F);
                    //TODO figure this out. Missing mappings is making this difficult
                    //RenderUtil.renderColoredModel(SpecialModel.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, event.getBuilder(), -1, 15728880, OverlayTexture.DEFAULT_LIGHT);
                }
                matrixStack.pop();
            }
            matrixStack.pop();
        }
    }

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
                if (!(player.getHeldItem(hand).getItem() == ModItems.WRENCH))
                {
                    continue;
                }

                FluidPipeTileEntity pipe = BlockFluidPipe.getPipeTileEntity(world, pos);
                Vec3d hitVec = objectMouseOver.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
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
        boolean isSap = block == ModBlocks.ENDER_SAP;
        if (isSap || block == ModBlocks.FUELIUM || block == ModBlocks.BLAZE_JUICE)
        {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);
            event.setDensity(isSap ? 1 : 0.5F);
            event.setCanceled(true);
        }*/
    }

    @SubscribeEvent
    public void onJump(InputEvent.KeyInputEvent event)
    {
        if(Screen.hasControlDown())
        {
            PlayerEntity player = Minecraft.getInstance().player;
            if(Minecraft.getInstance().currentScreen == null && player.getRidingEntity() instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) player.getRidingEntity();
                if(vehicle.canTowTrailer())
                {
                    PacketHandler.instance.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                }
            }
        }
    }
}
