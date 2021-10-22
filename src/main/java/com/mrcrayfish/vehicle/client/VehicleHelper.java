package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.Controller;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Author: MrCrayfish
 */
public class VehicleHelper
{
    private static final WeakHashMap<UUID, Map<SoundType, ITickableSound>> SOUND_TRACKER = new WeakHashMap<>();
    
    public static void playVehicleSound(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        Minecraft.getInstance().tell(() ->
        {
            Map<SoundType, ITickableSound> soundMap = SOUND_TRACKER.computeIfAbsent(vehicle.getUUID(), uuid -> new HashMap<>());
            if(vehicle.getEngineSound() != null && player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.ENGINE_RIDING);
                if(sound == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(sound))
                {
                    sound = new MovingSoundVehicleRiding(player, vehicle);
                    soundMap.put(SoundType.ENGINE_RIDING, sound);
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
            if(vehicle.getEngineSound() != null && !player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.ENGINE);
                if(sound == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(sound))
                {
                    sound = new MovingSoundVehicle(vehicle);
                    soundMap.put(SoundType.ENGINE, sound);
                    Minecraft.getInstance().getSoundManager().play(new MovingSoundVehicle(vehicle));
                }
            }
            if(vehicle.hasHorn() && vehicle.getHornSound() != null && !player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.HORN);
                if(sound == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(sound))
                {
                    sound = new MovingSoundHorn(vehicle);
                    soundMap.put(SoundType.HORN, sound);
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
            if(vehicle.getHornSound() != null && player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.HORN_RIDING);
                if(sound == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(sound))
                {
                    sound = new MovingSoundHornRiding(player, vehicle);
                    soundMap.put(SoundType.HORN_RIDING, sound);
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
        });
    }

    public static void playSound(SoundEvent soundEvent, BlockPos pos, float volume, float pitch)
    {
        ISound sound = new SimpleSound(soundEvent, SoundCategory.BLOCKS, volume, pitch, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
        Minecraft.getInstance().submitAsync(() -> Minecraft.getInstance().getSoundManager().play(sound));
    }

    public static void playSound(SoundEvent soundEvent, float volume, float pitch)
    {
        Minecraft.getInstance().submitAsync(() -> Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(soundEvent, volume, pitch)));
    }

    //@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        /*if(event.getEntity().isInsideOfMaterial(ModMaterials.FUELIUM))
        {
            event.setDensity(0.5F);
        }
        else
        {
            event.setDensity(0.01F);
        }
        event.setCanceled(true);*/
    }

    @OnlyIn(Dist.CLIENT)
    public static float getSteeringAngle(PoweredVehicleEntity vehicle)
    {
        float steeringAngle = vehicle.getSteeringAngle();
        if(vehicle.getControllingPassenger() != null)
        {
            Entity entity = vehicle.getControllingPassenger();
            if(!(entity instanceof LivingEntity))
                return 0F;

            float strength = 0.25F * MathHelper.clamp(1.0F - (float) vehicle.getSpeed() / 30F, 0.2F, 1.0F);

            if(ClientHandler.isControllableLoaded())
            {
                Controller controller = Controllable.getController();
                if(Controllable.getInput().isControllerInUse() && controller != null)
                {
                    float leftStick = -MathHelper.clamp(controller.getLThumbStickXValue(), -1.0F, 1.0F);
                    return steeringAngle + (vehicle.getMaxSteeringAngle() * leftStick - steeringAngle) * strength;
                }
            }

            LivingEntity livingEntity = (LivingEntity) entity;
            float turnValue = MathHelper.clamp(livingEntity.xxa, -1.0F, 1.0F);
            return steeringAngle + (vehicle.getMaxSteeringAngle() * turnValue - steeringAngle) * strength * 0.75F;
        }
        return steeringAngle * 0.85F;
    }

    public static boolean isHandbraking()
    {
        if(ClientHandler.isControllableLoaded())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(controller.getButtonsStates().getState(Buttons.RIGHT_BUMPER))
                {
                    return true;
                }
            }
        }
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    public static boolean isHonking()
    {
        if(ClientHandler.isControllableLoaded())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(controller.isButtonPressed(Buttons.RIGHT_THUMB_STICK))
                {
                    return true;
                }
            }
        }
        return KeyBinds.KEY_HORN.isDown();
    }

    public static float getLift()
    {
        float up = Minecraft.getInstance().options.keyJump.isDown() ? 1.0F : 0F;
        float down = Minecraft.getInstance().options.keySprint.isDown() ? -1.0F : 0F;
        if(ClientHandler.isControllableLoaded())
        {
            Controller controller = Controllable.getController();
            if(controller != null && Controllable.getInput().isControllerInUse())
            {
                up = controller.getButtonsStates().getState(Buttons.RIGHT_BUMPER) ? 1.0F : up;
                down = controller.getButtonsStates().getState(Buttons.LEFT_BUMPER) ? -1.0F : down;
            }
        }
        return up + down;
    }

    public static float getTravelDirection(HelicopterEntity vehicle)
    {
        if(ClientHandler.isControllableLoaded())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                float xAxis = controller.getLThumbStickXValue();
                float yAxis = controller.getLThumbStickYValue();
                if(xAxis != 0.0F || yAxis != 0.0F)
                {
                    float angle = (float) Math.toDegrees(Math.atan2(-xAxis, yAxis)) + 180F;
                    return vehicle.yRot + angle;
                }
            }
        }

        //TODO fix keyboard movement for heli
        /*PoweredVehicleEntity.AccelerationDirection accelerationDirection = vehicle.getAcceleration();
        PoweredVehicleEntity.TurnDirection turnDirection = vehicle.getTurnDirection();
        if(vehicle.getControllingPassenger() != null)
        {
            if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.FORWARD)
            {
                return vehicle.yRot + turnDirection.getDir() * -45F;
            }
            else if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.REVERSE)
            {
                return vehicle.yRot + 180F + turnDirection.getDir() * 45F;
            }
            else
            {
                return vehicle.yRot + turnDirection.getDir() * -90F;
            }
        }*/
        return vehicle.yRot;
    }

    public static float getTravelSpeed(HelicopterEntity helicopter)
    {
        if(ClientHandler.isControllableLoaded())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                float xAxis = controller.getLThumbStickXValue();
                float yAxis = controller.getLThumbStickYValue();
                if(xAxis != 0.0F || yAxis != 0.0F)
                {
                    return (float) Math.min(1.0, Math.sqrt(Math.pow(xAxis, 2) + Math.pow(yAxis, 2)));
                }
            }
        }
        return 0F; //TODO fix heli travel speed
        //return helicopter.getAcceleration() != PoweredVehicleEntity.AccelerationDirection.NONE || helicopter.getTurnDirection() != PoweredVehicleEntity.TurnDirection.FORWARD ? 1.0F : 0.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getThrottle(LivingEntity livingEntity)
    {
        if(ClientHandler.isControllableLoaded() && Config.CLIENT.useTriggers.get())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                //Maybe add a dead zone option to controllable for triggers
                boolean forward = MathHelper.clamp(controller.getRTriggerValue(), 0.0F, 1.0F) > 0.1F;
                boolean reverse = MathHelper.clamp(controller.getLTriggerValue(), 0.0F, 1.0F) > 0.1F;
                if(forward && !reverse)
                {
                    return (controller.getRTriggerValue() - 0.1F) / 0.9F;
                }
                else if(!forward && reverse)
                {
                    return -(controller.getLTriggerValue() - 0.1F) / 0.9F;
                }
            }
        }
        return MathHelper.clamp(livingEntity.zza, -1.0F, 1.0F);
    }

    public static boolean canFollowVehicleOrientation(Entity passenger)
    {
        if(passenger.equals(Minecraft.getInstance().player))
        {
            return Config.CLIENT.followVehicleOrientation.get();
        }
        return false;
    }

    public static void spawnWheelParticle(BlockPos pos, BlockState state, double x, double y, double z, Vector3d motion)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if(world != null)
        {
            DiggingParticle particle = new DiggingParticle(world, x, y, z, motion.x, motion.y, motion.z, state);
            particle.init(pos);
            particle.setPower((float) motion.length());
            mc.particleEngine.add(particle);
        }
    }

    public static void spawnSmokeParticle(double x, double y, double z, Vector3d motion)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if(world != null)
        {
            Particle particle = mc.particleEngine.createParticle(ModParticleTypes.TYRE_SMOKE.get(), x, y, z, motion.x, motion.y, motion.z);
            if(particle != null)
            {
                mc.particleEngine.add(particle);
            }
        }
    }

    public static boolean isThirdPersonBack()
    {
        return Minecraft.getInstance().options.getCameraType() == PointOfView.THIRD_PERSON_BACK;
    }

    public static boolean isThirdPersonFront()
    {
        return Minecraft.getInstance().options.getCameraType() == PointOfView.THIRD_PERSON_FRONT;
    }

    private enum SoundType
    {
        ENGINE,
        ENGINE_RIDING,
        HORN,
        HORN_RIDING;
    }
}
