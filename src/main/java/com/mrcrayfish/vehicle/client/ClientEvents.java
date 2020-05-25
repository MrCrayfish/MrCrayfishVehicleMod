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
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCycleSeats;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import com.mrcrayfish.vehicle.util.RenderUtil;
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
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
import org.lwjgl.input.Keyboard;
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
    private double fuelingHandOffset;
    private int tickCounter;
    private boolean fueling;
    private double offsetPrev, offsetPrevPrev;
    private boolean shouldRenderNozzle;

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
        if(Minecraft.getMinecraft().player == null)
            return;

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

        if(ClientProxy.KEY_CYCLE_SEATS.isPressed())
        {
            if(Minecraft.getMinecraft().player.getRidingEntity() instanceof EntityVehicle)
            {
                PacketHandler.INSTANCE.sendToServer(new MessageCycleSeats());
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
        if(ridingEntity instanceof EntityVehicle)
        {
            event.setNewfov(1.0F);
        }
    }

    @SubscribeEvent
    public void onPreRender(ModelPlayerEvent.Render.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        Entity ridingEntity = player.getRidingEntity();
        if(ridingEntity instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) ridingEntity;
            /* Suppressed due to warning however it's safe to say cast won't throw an exception
             * due to the strict registration process of vehicle renders */
            @SuppressWarnings("unchecked")
            AbstractRenderVehicle<EntityVehicle> render = (AbstractRenderVehicle<EntityVehicle>) VehicleRenderRegistry.getRender(vehicle.getClass());
            if(render != null)
            {
                render.applyPlayerRender(vehicle, player, event.getPartialTicks());
            }

            if(vehicle instanceof EntityLandVehicle)
            {
                EntityLandVehicle landVehicle = (EntityLandVehicle) vehicle;
                if(landVehicle.canWheelie())
                {
                    int index = vehicle.getSeatTracker().getSeatIndex(player.getUniqueID());
                    if(index != -1)
                    {
                        VehicleProperties properties = landVehicle.getProperties();
                        if(properties.getRearAxelVec() == null)
                        {
                            return;
                        }
                        Seat seat = properties.getSeats().get(index);
                        Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
                        double vehicleScale = properties.getBodyPosition().getScale();
                        double playerScale = 32.0 / 30.0;
                        double offsetX = -(seatVec.x * playerScale);
                        double offsetY = (seatVec.y + player.getYOffset()) * playerScale + 24 * 0.0625 - properties.getWheelOffset() * 0.0625 * vehicleScale;
                        double offsetZ = (seatVec.z * playerScale) - properties.getRearAxelVec().z * 0.0625 * vehicleScale;
                        GlStateManager.translate(offsetX, offsetY, offsetZ);
                        float wheelieProgress = (float) (MathHelper.clampedLerp(landVehicle.prevWheelieCount, landVehicle.wheelieCount, event.getPartialTicks()) / 4F);
                        wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
                        GlStateManager.rotate(-30F * wheelieProgress, 1, 0, 0);
                        GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
                    }
                }
            }
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

        if(SyncedPlayerData.getGasPumpPos(player).isPresent())
        {
            boolean rightHanded = player.getPrimaryHand() == EnumHandSide.RIGHT;
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

            if(VehicleConfig.CLIENT.debug.reloadVehiclePropertiesEachTick)
            {
                VehicleProperties.register();
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
        if (event.getHand() == EnumHand.OFF_HAND && fuelingHandOffset > -1)
        {
            GlStateManager.rotate(25F, 1, 0, 0);
            GlStateManager.translate(0, -0.35 - fuelingHandOffset, 0.2);
        }

        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemSprayCan && event.getItemStack().getMetadata() == 0)
        {
            ItemStack stack = event.getItemStack().copy();
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(Minecraft.getMinecraft().player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), stack, event.getEquipProgress());
            event.setCanceled(true);
        }

        fuelingHandOffset = -1;
        RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
        if (result != null && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING) && event.getHand() == EntityRaytracer.getContinuousInteractionObject())
        {
            double offset = Math.sin((tickCounter + Minecraft.getMinecraft().getRenderPartialTicks()) * 0.4) * 0.01;
            if (offsetPrev > offsetPrevPrev && offsetPrev > offset)
            {
                Minecraft.getMinecraft().player.playSound(ModSounds.LIQUID_GLUG, 0.3F, 1F);
            }
            offsetPrevPrev = offsetPrev;
            offsetPrev = offset;
            GlStateManager.translate(0, 0.35 + offset, -0.2);
            GlStateManager.rotate(-25F, 1, 0, 0);
            if (event.getHand() == EnumHand.MAIN_HAND)
            {
                fuelingHandOffset = offset;
            }
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if(SyncedPlayerData.getGasPumpPos(player).isPresent())
        {
            if(event.getSwingProgress() > 0)
            {
                shouldRenderNozzle = true;
            }
            if(event.getHand() == EnumHand.MAIN_HAND && shouldRenderNozzle)
            {
                if(event.getSwingProgress() > 0 && event.getSwingProgress() <= 0.25) return;
                GlStateManager.pushMatrix();
                boolean mainHand = event.getHand() == EnumHand.MAIN_HAND;
                EnumHandSide handSide = mainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
                float f = -0.6F * MathHelper.sin(MathHelper.sqrt(event.getSwingProgress()) * (float) Math.PI);
                float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(event.getSwingProgress()) * ((float) Math.PI * 2F));
                float f2 = -0.2F * MathHelper.sin(event.getSwingProgress() * (float) Math.PI);
                int handOffset = handSide == EnumHandSide.RIGHT ? 1 : -1;
                GlStateManager.translate((float) handOffset * f, f1, f2);
                GlStateManager.translate((float) handOffset * 0.65F, -0.52F + 0.25F, -0.72F);
                GlStateManager.rotate(45F, 1, 0, 0);
                RenderUtil.renderItemModel(new ItemStack(ModItems.MODELS), SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
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
        if(entity instanceof EntityPlayer && SyncedPlayerData.getGasPumpPos((EntityPlayer) entity).isPresent())
        {
            event.setCanceled(true);
            return;
        }

        if(!event.getItem().isEmpty() && event.getItem().getItem() instanceof ItemSprayCan && event.getItem().getMetadata() == 0)
        {
            ItemStack stack = event.getItem().copy();
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(event.getEntity(), stack, event.getTransformType(), event.getHandSide() == EnumHandSide.LEFT);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onModelRenderPost(ModelPlayerEvent.Render.Post event)
    {
        EntityPlayer entity = event.getEntityPlayer();
        if(SyncedPlayerData.getGasPumpPos(entity).isPresent())
        {
            GlStateManager.pushMatrix();
            {
                if(event.getModelPlayer().isChild)
                {
                    GlStateManager.translate(0.0F, 0.75F, 0.0F);
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                }
                GlStateManager.pushMatrix();
                {
                    if(entity.isSneaking())
                    {
                        GlStateManager.translate(0.0F, 0.2F, 0.0F);
                    }
                    event.getModelPlayer().postRenderArm(0.0625F, entity.getPrimaryHand());
                    GlStateManager.rotate(180F, 1, 0, 0);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    boolean leftHanded = entity.getPrimaryHand() == EnumHandSide.LEFT;
                    GlStateManager.translate((float) (leftHanded ? -1 : 1) / 16.0F, 0.125F, -0.625F);
                    GlStateManager.translate(0, -9 * 0.0625F, 5.75 * 0.0625F);
                    RenderUtil.renderItemModel(new ItemStack(ModItems.MODELS), SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
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
                if (!(player.getHeldItem(hand).getItem() == ModItems.WRENCH))
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

    @SubscribeEvent
    public void onJump(InputEvent.KeyInputEvent event)
    {
        if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode())
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if(Minecraft.getMinecraft().currentScreen == null && player.getRidingEntity() instanceof EntityVehicle)
            {
                EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                if(vehicle.canTowTrailer())
                {
                    PacketHandler.INSTANCE.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                }
            }
        }
    }
}
