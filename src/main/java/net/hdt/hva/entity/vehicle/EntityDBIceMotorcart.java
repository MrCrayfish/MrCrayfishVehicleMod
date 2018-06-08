package net.hdt.hva.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.hdt.hva.init.ModItems;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityDBIceMotorcart extends EntityLandVehicle {

    public EntityDBIceMotorcart(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
        super.entityInit();

        if (world.isRemote) {
            body = new ItemStack(ModItems.DB_ICE_MOTORCART_BODY);
            wheel = new ItemStack(ModItems.TRAIN_WHEEL[1]);
            engine = new ItemStack(ModItems.ENGINE[0]);
        }
    }

    @Override
    public SoundEvent getMovingSound() {
        return SoundEvents.BLOCK_ANVIL_BREAK;
    }

    @Override
    public SoundEvent getRidingSound() {
        return null;
    }

    @Override
    public double getMountedYOffset() {
        return 0;
    }

}
