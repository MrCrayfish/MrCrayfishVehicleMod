package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityGoKart extends EntityLandVehicle implements IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -2.5F;
    public static final float WHEEL_OFFSET = 3.15F;
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, 0, 0, 0, 0, 1);
    public static final PartPosition ENGINE_POSITION = new PartPosition(0, 2, -9, 0, 180, 0, 1.2);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(3.0D, 0.5D, 0.0D);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0D, -0.03125D, -0.375D);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityGoKart(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(20F);
        this.setTurnSensitivity(12);
        this.setSize(1.5F, 0.5F);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setEnginePosition(ENGINE_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.stepHeight = 0.625F;
        this.setFuelConsumption(2.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.GO_KART_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.goKartEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.goKartEngineStereo;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.6F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(0, 0.55, -0.9);
    }

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public boolean shouldRenderFuelPort()
    {
        return false;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
