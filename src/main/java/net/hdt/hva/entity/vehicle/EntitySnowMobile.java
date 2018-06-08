package net.hdt.hva.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredMotorcycle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.hdt.hva.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntitySnowMobile extends EntityColoredMotorcycle {
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntitySnowMobile(World worldIn) {
        super(worldIn);
        this.setMaxSpeed(100F);
        this.setTurnSensitivity(12);
    }

    @Override
    public void entityInit() {
        super.entityInit();

        if (world.isRemote) {
            body = new ItemStack(ModItems.SNOW_MOBILE_BODY);
            wheel = new ItemStack(ModItems.SNOW_MOBILE_SKI);
            handleBar = new ItemStack(ModItems.SNOW_MOBILE_HANDLE_BAR);
        }
    }

    @Override
    public SoundEvent getMovingSound() {
        return ModSounds.GO_KART_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound() {
        return ModSounds.GO_KART_ENGINE_STEREO;
    }

    @Override
    public float getMinEnginePitch() {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.8F;
    }

    @Override
    public double getMountedYOffset() {
        return 9.5 * 0.0625;
    }

    @Override
    public boolean shouldRenderEngine() {
        return false;
    }
}
