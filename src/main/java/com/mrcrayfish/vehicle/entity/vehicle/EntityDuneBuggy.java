package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
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
    static
    {
        VehicleProperties properties = new VehicleProperties();
        properties.setAxleOffset(-2.3F);
        properties.setWheelOffset(2.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.225, 0, 0, 0, 1.3));
        properties.setFuelPortPosition(new PartPosition(1.15, 3, -7.25, 0, 180, 0, 0.25));
        properties.setHeldOffset(new Vec3d(2.0, 0.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.025, -0.25));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, -4.25F, -5.7F, 1.0F);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, -4.25F, -5.7F, 1.0F);
        VehicleProperties.setProperties(EntityDuneBuggy.class, properties);
    }

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
        return ModSounds.electricEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.electricEngineStereo;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3.25 * 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.ELECTRIC_MOTOR;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
