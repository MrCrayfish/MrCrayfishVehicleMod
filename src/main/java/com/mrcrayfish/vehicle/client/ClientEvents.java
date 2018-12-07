package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.block.BlockFluidPipe;
import com.mrcrayfish.vehicle.block.BlockFluidPump;
import com.mrcrayfish.vehicle.block.BlockFuelDrum;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.item.ItemWrench;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private int lastSlot = -1;
    private int originalPerspective = -1;
    private double jerryCanMainHandOffset;
    private int tickCounter;
    private boolean fueling;
    private double offsetPrev, offsetPrevPrev;

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event)
    {
        if(VehicleConfig.CLIENT.display.autoPerspective)
        {
            if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            {
                if(event.getEntityMounting().equals(Minecraft.getMinecraft().player))
                {
                    if(event.isMounting())
                    {
                        Entity entity = event.getEntityBeingMounted();
                        if(entity instanceof EntityVehicle)
                        {
                            originalPerspective = Minecraft.getMinecraft().gameSettings.thirdPersonView;
                            Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
                        }
                    }
                    else if(originalPerspective != -1)
                    {
                        Minecraft.getMinecraft().gameSettings.thirdPersonView = originalPerspective;
                        originalPerspective = -1;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(VehicleConfig.CLIENT.display.autoPerspective)
        {
            Entity entity = Minecraft.getMinecraft().player.getRidingEntity();
            if(entity instanceof EntityVehicle)
            {
                if(Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.isKeyDown())
                {
                    originalPerspective = -1;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(VehicleConfig.CLIENT.display.enabledSpeedometer && event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.inGameHasFocus)
            {
                EntityPlayer player = mc.player;
                if(player != null)
                {
                    Entity entity = player.getRidingEntity();
                    if(entity instanceof EntityPoweredVehicle)
                    {
                        EntityPoweredVehicle vehicle = (EntityPoweredVehicle) entity;

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
        Entity ridingEntity = Minecraft.getMinecraft().player.getRidingEntity();
        if(ridingEntity instanceof EntityPlane || ridingEntity instanceof EntityHelicopter)
        {
            event.setNewfov(1.0F);
        }
    }

    @SubscribeEvent
    public void onPreRender(ModelPlayerEvent.Render.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        Entity ridingEntity = event.getEntityPlayer().getRidingEntity();

        if(ridingEntity != null && ridingEntity instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            /* Suppressed due to warning however it's safe to say cast won't throw an exception
             * due to the registration process of vehicle renders */
            @SuppressWarnings("unchecked")
            AbstractRenderVehicle<EntityVehicle> render = (AbstractRenderVehicle<EntityVehicle>) VehicleRenderRegistry.getRender(vehicle.getClass());
            if(render != null)
            {
                render.applyPlayerRender(vehicle, player, event.getPartialTicks());
                return;
            }
        }

        if(ridingEntity instanceof EntityMotorcycle)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset() * 3 - 3 * 0.0625;
            GlStateManager.translate(0, offset, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translate(0, -offset, 0);
        }

        if(ridingEntity instanceof EntityJetSki)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset() * 3 - 3 * 0.0625;
            GlStateManager.translate(0, offset, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
            GlStateManager.translate(0, -offset, 0);
        }

        if(ridingEntity instanceof EntitySpeedBoat)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset();
            GlStateManager.translate(0, offset + ridingEntity.getEyeHeight() + 0.25, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
            GlStateManager.translate(0, -(offset + ridingEntity.getEyeHeight()) - 0.25, 0);
        }

        if(ridingEntity instanceof EntityAluminumBoat)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset();
            GlStateManager.translate(0, offset + ridingEntity.getEyeHeight() + 0.25, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
            GlStateManager.translate(0, -(offset + ridingEntity.getEyeHeight()) - 0.25, 0);
        }

        if(ridingEntity instanceof EntityBath)
        {
            EntityBath vehicle = (EntityBath) ridingEntity;
            GlStateManager.translate(0, 0, 0.25);
            GlStateManager.translate(0, 0.625, 0);
            float bodyPitch = vehicle.prevBodyRotationX + (vehicle.bodyRotationX - vehicle.prevBodyRotationX) * event.getPartialTicks();
            float bodyRoll = vehicle.prevBodyRotationZ + (vehicle.bodyRotationZ - vehicle.prevBodyRotationZ) * event.getPartialTicks();
            GlStateManager.rotate(bodyRoll, 0, 0, 1);
            GlStateManager.rotate(-bodyPitch, 1, 0, 0);
            GlStateManager.translate(0, -0.625, 0);
            GlStateManager.translate(0, 0, -0.25);
        }
    }

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if(player.equals(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            return;

        Entity ridingEntity = player.getRidingEntity();
        ModelPlayer model = event.getModelPlayer();

        if(!player.isRiding())
        {
            boolean rightHanded = player.getPrimaryHand() == EnumHandSide.RIGHT;
            ItemStack rightItem = rightHanded ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
            ItemStack leftItem = rightHanded ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
            if(!rightItem.isEmpty() && rightItem.getItem() instanceof ItemSprayCan)
            {
                copyModelAngles(model.bipedHead, model.bipedRightArm);
                model.bipedRightArm.rotateAngleX += Math.toRadians(-80F);
            }
            if(!leftItem.isEmpty() && leftItem.getItem() instanceof ItemSprayCan)
            {
                ModelBiped.copyModelAngles(model.bipedHead, model.bipedLeftArm);
                model.bipedLeftArm.rotateAngleX += Math.toRadians(-80F);
            }
        }

        if(player.getDataManager().get(CommonEvents.PUSHING_CART))
        {
            player.renderYawOffset = player.rotationYawHead;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
            return;
        }

        if(ridingEntity != null && ridingEntity instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            /* Suppressed due to warning however it's safe to say cast won't throw an exception
             * due to the registration process of vehicle renders */
            @SuppressWarnings("unchecked")
            AbstractRenderVehicle<EntityVehicle> render = (AbstractRenderVehicle<EntityVehicle>) VehicleRenderRegistry.getRender(vehicle.getClass());
            if(render != null)
            {
                render.applyPlayerModel(vehicle, player, model, event.getPartialTicks());
                return;
            }
        }

        if(ridingEntity instanceof EntityMoped)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F - turnRotation);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(7F);
            model.bipedRightArm.offsetZ -= 0.05 * wheelAngleNormal;
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F + turnRotation);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-7F);
            model.bipedLeftArm.offsetZ -= 0.05 * -wheelAngleNormal;

            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-55F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-55F);

            return;
        }

        if(ridingEntity instanceof EntityBath)
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
            return;
        }

        if(ridingEntity instanceof EntityAluminumBoat)
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
            return;
        }

        if(ridingEntity instanceof EntitySpeedBoat)
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);

            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
            return;
        }

        if(ridingEntity instanceof EntityMiniBike)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 8F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        }

        if(ridingEntity instanceof EntityShoppingCart)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-70F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-70F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
            return;
        }

        if(ridingEntity instanceof EntitySmartCar)
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);

            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;

            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F - turnRotation);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F + turnRotation);

            return;
        }

        if(ridingEntity instanceof EntityPoweredVehicle)
        {
            EntityPoweredVehicle vehicle = (EntityPoweredVehicle) ridingEntity;

            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;

            if(ridingEntity instanceof EntityJetSki)
            {
                float turnRotation = wheelAngleNormal * 12F;
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);

                if(ridingEntity.getControllingPassenger() != player)
                {
                    model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F);
                    model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
                    model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F);
                    model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
                }

            }
            else if(ridingEntity instanceof EntityDuneBuggy)
            {
                float turnRotation = wheelAngleNormal * 8F;
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-50F - turnRotation);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-50F + turnRotation);
            }

            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void copyModelAngles(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(event.phase == TickEvent.Phase.END && player != null)
        {
            int slot = player.inventory.currentItem;
            if(this.lastSlot != slot)
            {
                this.lastSlot = slot;
                if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemSprayCan)
                {
                    float pitch = 0.85F + 0.15F * ItemSprayCan.getRemainingSprays(player.inventory.getCurrentItem());
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(ModSounds.sprayCanShake, pitch, 0.75F));
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
        if (event.getHand() == EnumHand.OFF_HAND && jerryCanMainHandOffset > -1)
        {
            GlStateManager.rotate(25F, 1, 0, 0);
            GlStateManager.translate(0, -0.35 - jerryCanMainHandOffset, 0.2);
        }
        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemSprayCan && event.getItemStack().getMetadata() == 0)
        {
            ItemStack stack = event.getItemStack().copy();
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(Minecraft.getMinecraft().player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), stack, event.getEquipProgress());
            event.setCanceled(true);
        }
        jerryCanMainHandOffset = -1;
        RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
        if (result != null && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING) && event.getHand() == EntityRaytracer.getContinuousInteractionObject())
        {
            double offset = Math.sin((tickCounter + Minecraft.getMinecraft().getRenderPartialTicks()) * 0.4) * 0.01;
            if (offsetPrev > offsetPrevPrev && offsetPrev > offset)
            {
                Minecraft.getMinecraft().player.playSound(ModSounds.liquidGlug, 0.3F, 1F);
            }
            offsetPrevPrev = offsetPrev;
            offsetPrev = offset;
            GlStateManager.translate(0, 0.35 + offset, -0.2);
            GlStateManager.rotate(-25F, 1, 0, 0);
            if (event.getHand() == EnumHand.MAIN_HAND)
            {
                jerryCanMainHandOffset = offset;
            }
        }
    }

    @SubscribeEvent
    public void onRenderThirdPerson(RenderItemEvent.Held.Pre event)
    {
        if(!event.getItem().isEmpty() && event.getItem().getItem() instanceof ItemSprayCan && event.getItem().getMetadata() == 0)
        {
            ItemStack stack = event.getItem().copy();
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(event.getEntity(), stack, event.getTransformType(), event.getHandSide() == EnumHandSide.LEFT);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderCustomBlockHighlights(DrawBlockHighlightEvent event)
    {
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return;
        }

        EntityPlayer player = event.getPlayer();
        World world = player.world;
        BlockPos pos = target.getBlockPos();
        if (!world.getWorldBorder().contains(pos))
        {
            return;
        }

        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFuelDrum)
        {
            boxRenderGlStart();
            AxisAlignedBB box = state.getSelectedBoundingBox(world, pos).grow(0.0020000000949949026D).offset(-dx, -dy, -dz);
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
            RayTraceResult objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
            for (EnumHand hand : EnumHand.values())
            {
                if (!(player.getHeldItem(hand).getItem() instanceof ItemWrench))
                {
                    continue;
                }

                TileEntityFluidPipe pipe = BlockFluidPipe.getTileEntity(world, pos);
                Vec3d hitVec = objectMouseOver.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
                Pair<AxisAlignedBB, EnumFacing> hit = ((BlockFluidPipe) state.getBlock()).getWrenchableBox(world, pos, state, player, hand, objectMouseOver.sideHit, hitVec.x, hitVec.y, hitVec.z, pipe);
                if (hit != null)
                {
                    boxRenderGlStart();
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
        }
    }

    private void boxRenderGlStart()
    {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
    }

    private void boxRenderGlEnd()
    {
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public void setLiquidFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        Block block = event.getState().getBlock();
        boolean isSap = block == ModBlocks.ENDER_SAP;
        if (isSap || block == ModBlocks.FUELIUM || block == ModBlocks.BLAZE_JUICE)
        {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);
            event.setDensity(isSap ? 1 : 0.5F);
            event.setCanceled(true);
        }
    }
}
