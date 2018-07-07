package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemSprayCan;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private int lastSlot = -1;
    public static final AxisAlignedBB MOPED_CHEST = new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375);
    public static final AxisAlignedBB[] MOPED_BOXES = new AxisAlignedBB[]{MOPED_CHEST, new AxisAlignedBB(-0.3, 0.202, 0.0225, 0.3, 0.307, 0.8125),
        new AxisAlignedBB(-0.3, 0.1945, 0.81, 0.3, 1.357, 0.9975), new AxisAlignedBB(-0.075, 0.307, 0.125, 0.075, 0.382, 0.8254),
        new AxisAlignedBB(-0.1875, 0.1945, -0.1275, 0.1875, 0.7195, 0.1725), new AxisAlignedBB(-0.2025, 0.7195, -0.34375, 0.2025, 0.8695, 0.22595),
        new AxisAlignedBB(-0.2625, 0.307, -0.6754, -0.1875, 0.6445, -0.075), new AxisAlignedBB(0.1875, 0.307, -0.6754, 0.26256, 0.6445, -0.07594),
        new AxisAlignedBB(-0.1875, 0.307, -0.7125, 0.1875, 0.7195, -0.125), new AxisAlignedBB(-0.3, 0.7195, -0.9375, 0.3, 0.7945, -0.34375),
        new AxisAlignedBB(-0.25625, 1.26325, 0.71625, 0.25, 1.33200, 0.81), new AxisAlignedBB(0.20625, 1.262, 0.675, 0.3, 1.33075, 0.76875),
        new AxisAlignedBB(-0.30625, 1.26325, 0.675, -0.2125, 1.332, 0.76875), new AxisAlignedBB(0.25, 1.262, 0.625, 0.34375, 1.33075, 0.71875),
        new AxisAlignedBB(-0.35625, 1.262, 0.625, -0.2625, 1.33075, 0.71875), new AxisAlignedBB(0.3, 1.262, 0.58125, 0.39375, 1.33075, 0.675),
        new AxisAlignedBB(-0.40625, 1.262, 0.58125, -0.3125, 1.33075, 0.675), new AxisAlignedBB(0.34375, 1.262, 0.53125, 0.4375, 1.33075, 0.625),
        new AxisAlignedBB(-0.5, 1.262, 0.4875, -0.40625, 1.33075, 0.58125), new AxisAlignedBB(0.39375, 1.262, 0.4875, 0.48750, 1.33075, 0.58125),
        new AxisAlignedBB(-0.45, 1.262, 0.53125, -0.35625, 1.33075, 0.625)};

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.inGameHasFocus)
            {
                EntityPlayer player = mc.player;
                if(player != null)
                {
                    Entity entity = player.getRidingEntity();
                    if(entity instanceof EntityVehicle)
                    {
                        String speed = new DecimalFormat("0.0").format(((EntityVehicle) entity).getKilometersPreHour());
                        mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + "BPS: " + TextFormatting.YELLOW + speed, 10, 10, Color.WHITE.getRGB());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreRender(ModelPlayerEvent.Render.Pre event)
    {
        Entity ridingEntity = event.getEntityPlayer().getRidingEntity();
        if(ridingEntity instanceof EntityMotorcycle)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset() * 3 - 3 * 0.0625;
            GlStateManager.translate(0, offset, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translate(0, -offset, 0);
        }

        if(ridingEntity instanceof EntityJetSki)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
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
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
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
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            double offset = vehicle.getMountedYOffset();
            GlStateManager.translate(0, offset + ridingEntity.getEyeHeight() + 0.25, 0);
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * event.getPartialTicks()) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * event.getPartialTicks()) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
            GlStateManager.translate(0, -(offset + ridingEntity.getEyeHeight()) - 0.25, 0);
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

        if(ridingEntity instanceof EntityMoped)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
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

        if(ridingEntity instanceof EntityLawnMower)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
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

            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
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
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 8F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        }

        if(ridingEntity instanceof EntityCouch)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(25F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-25F);
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
            return;
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

        if(ridingEntity instanceof EntityGoKart || ridingEntity instanceof EntityBumperCar || ridingEntity instanceof EntitySmartCar)
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);

            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;

            if(ridingEntity instanceof EntitySmartCar)
            {
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F - turnRotation);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F + turnRotation);
            }
            else
            {
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
            }
            return;
        }

        if(ridingEntity instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;

            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * event.getPartialTicks();
            float wheelAngleNormal = wheelAngle / 45F;

            if(ridingEntity instanceof EntityATV || ridingEntity instanceof EntityJetSki)
            {
                float turnRotation = wheelAngleNormal * 12F;
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);

                if(ridingEntity instanceof EntityJetSki && ridingEntity.getControllingPassenger() != player)
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
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(ModSounds.SPRAY_CAN_SHAKE, pitch, 0.75F));
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderSpecificHandEvent event)
    {
        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemSprayCan && event.getItemStack().getMetadata() == 0)
        {
            ItemStack stack = event.getItemStack().copy();
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(Minecraft.getMinecraft().player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), stack, event.getEquipProgress());
            event.setCanceled(true);
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
    public void openMopedChest(MouseEvent event)
    {
        if (event.getButton() != 1 || !event.isButtonstate())
            return;
        
        float reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
        Vec3d eyes = Minecraft.getMinecraft().player.getPositionEyes(1);
        Vec3d focus = eyes.add(Minecraft.getMinecraft().player.getLook(1).scale(reach));
        AxisAlignedBB box = new AxisAlignedBB(eyes, eyes).grow(reach);
        RayTraceResultRotated lookObject = null;
        double distanceShortest = Double.MAX_VALUE;
        for (EntityMoped moped : Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityMoped.class, box))
        {
            if (moped.hasChest())
            {
                RayTraceResultRotated lookObjectPutative = rayTraceRotated(moped, eyes, focus);
                if (lookObjectPutative != null)
                {
                    double distance = lookObjectPutative.getDistanceToEyes();
                    if (distance < distanceShortest)
                    {
                        lookObject = lookObjectPutative;
                        distanceShortest = distance;
                    }
                }
            }
        }
        if (lookObject != null)
        {
            if (lookObject.getBoxHit().equals(MOPED_CHEST))
            {
                double eyeDistance = lookObject.getDistanceToEyes();;
                if (eyeDistance <= reach)
                {
                    Vec3d hit = focus;
                    RayTraceResult lookObjectMC = Minecraft.getMinecraft().objectMouseOver;
                    boolean bypass = false;
                    if (lookObjectMC != null && lookObjectMC.typeOfHit != Type.MISS)
                    {
                        hit = lookObjectMC.hitVec;
                        if (lookObjectMC.typeOfHit == Type.ENTITY && lookObjectMC.entityHit == lookObject.entityHit)
                            bypass = true;
                    }
                    if (bypass || eyeDistance < hit.distanceTo(eyes))
                    {
                        event.setCanceled(true);
                        //TODO open moped (lookObject.entityHit) inventory GUI
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("open chest GUI").setStyle(new Style().setColor(TextFormatting.values()[Minecraft.getMinecraft().world.rand.nextInt(15) + 1])));//debug
                    }
                }
            }
        }
    }

    @Nullable
    public RayTraceResultRotated rayTraceRotated(Entity entity, Vec3d start, Vec3d end)
    {
        Vec3d pos = entity.getPositionVector();
        double angle = -entity.rotationYaw * (Math.PI / 180);
        Vec3d startRotated = rotateVecXZ(start, angle, pos);
        Vec3d endRotated = rotateVecXZ(end, angle, pos);
        RayTraceResult lookObject = null;
        AxisAlignedBB boxHit = null;
        double distanceShortest = Double.MAX_VALUE;
        for (int i = 0; i < MOPED_BOXES.length; i++)
        {
            RayTraceResult lookObjectPutative = MOPED_BOXES[i].offset(pos).calculateIntercept(startRotated, endRotated);
            if (lookObjectPutative != null)
            {
                double distance = startRotated.distanceTo(lookObjectPutative.hitVec);
                if (distance < distanceShortest)
                {
                    lookObject = lookObjectPutative;
                    boxHit = MOPED_BOXES[i];
                    distanceShortest = distance;
                }
            }
        }
        return lookObject == null ? null : new RayTraceResultRotated(entity, rotateVecXZ(lookObject.hitVec, -angle, pos), boxHit, distanceShortest);
    }

    private Vec3d rotateVecXZ(Vec3d vec, double angle, Vec3d rotationPoint)
    {
        double x = rotationPoint.x + Math.cos(angle) * (vec.x - rotationPoint.x) - Math.sin(angle) * (vec.z - rotationPoint.z);
        double z = rotationPoint.z + Math.sin(angle) * (vec.x - rotationPoint.x) + Math.cos(angle) * (vec.z - rotationPoint.z);
        return new Vec3d(x, vec.y, z);
    }

    private static class RayTraceResultRotated extends RayTraceResult
    {
        private AxisAlignedBB boxHit;
        private double distanceToEyes;

        public RayTraceResultRotated(Entity entityHit, Vec3d hitVec, AxisAlignedBB boxHit, double distanceToEyes)
        {
            super(entityHit, hitVec);
            this.boxHit = boxHit;
            this.distanceToEyes = distanceToEyes;
        }

        public AxisAlignedBB getBoxHit()
        {
            return boxHit;
        }

        public double getDistanceToEyes()
        {
            return distanceToEyes;
        }
    }
}
