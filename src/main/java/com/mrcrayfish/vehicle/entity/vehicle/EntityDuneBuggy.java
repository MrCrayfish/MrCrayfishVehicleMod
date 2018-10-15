package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
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
public class EntityDuneBuggy extends EntityLandVehicle implements IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -2.3F;
    public static final float WHEEL_OFFSET = 2.5F;
    public static final PartPosition BODY_POSITION = new PartPosition(0.0F, 0.0F, 0.225F, 0.0F, 0.0F, 0.0F, 1.3F);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(1.15F, 3.0F, -7.25F, 0.0F, 180.0F, 0.0F, 0.25F);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(2.0D, 0.0D, 0.0D);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0.0D, -0.025D, -0.25D);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityDuneBuggy(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(10);
        this.setSize(0.75F, 0.75F);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.stepHeight = 0.5F;
        this.setFuelCapacity(5000F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.DUNE_BUGGY_BODY);
        handleBar = new ItemStack(ModItems.DUNE_BUGGY_HANDLE_BAR);
        wheel = new ItemStack(ModItems.DUNE_BUGGY_WHEEL);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ATV_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ATV_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3.25 * 0.0625;
    }

    @Override
    public boolean isEngineLockable()
    {
        return false;
    }
}
