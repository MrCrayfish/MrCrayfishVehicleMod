package net.hdt.hva.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredLandVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.hdt.hva.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: HuskyTheArtist
 */
public class EntityRaceCar extends EntityColoredLandVehicle {
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityRaceCar(World worldIn) {
        super(worldIn);
        this.setMaxSpeed(15);
        this.setSize(3.5F, 1.0F);
    }

    @Override
    public void entityInit() {
        super.entityInit();

        if (world.isRemote) {
            body = new ItemStack(ModItems.RACE_CAR_BODY);
            wheel = new ItemStack(ModItems.CAR_WHEEL);
            steeringWheel = new ItemStack(ModItems.STEERING_WHEEL);
        }
    }

    @Override
    public boolean shouldRenderEngine() {
        return false;
    }

    @Override
    public SoundEvent getMovingSound() {
        return ModSounds.ELECTRIC_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound() {
        return ModSounds.ELECTRIC_ENGINE_STEREO;
    }

    @Override
    public float getMaxEnginePitch() {
        return 0.8F;
    }

    @Override
    public double getMountedYOffset() {
        return 2 * 0.0625F;
    }

}
