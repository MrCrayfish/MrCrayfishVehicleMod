package net.hdt.hva.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.hdt.hva.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityC62SteamLocomotiveTender extends EntityLandVehicle {

    public EntityC62SteamLocomotiveTender(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
        super.entityInit();

        if (world.isRemote) {
            body = new ItemStack(ModItems.C62_STEAM_LOCOMOTIVE_TENDER_BODY);
            wheel = new ItemStack(ModItems.TRAIN_WHEEL[1]);
            engine = new ItemStack(ModItems.ENGINE[2]);
        }
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
    public double getMountedYOffset() {
        return 0;
    }

}
