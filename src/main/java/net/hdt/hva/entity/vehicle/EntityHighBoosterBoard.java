package net.hdt.hva.entity.vehicle;

import com.mrcrayfish.vehicle.init.ModSounds;
import net.hdt.hva.entity.EntityColoredAirVehicle;
import net.hdt.hva.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHighBoosterBoard extends EntityColoredAirVehicle {
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityHighBoosterBoard(World worldIn) {
        super(worldIn);
        this.setMaxSpeed(18F);
        this.setTurnSensitivity(12);
    }

    @Override
    public void entityInit() {
        super.entityInit();

        if (world.isRemote) {
            body = new ItemStack(ModItems.HIGH_BOOSTER_BOARD);
            wheel = new ItemStack(com.mrcrayfish.vehicle.init.ModItems.WHEEL);
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
    public boolean shouldRenderEngine() {
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 5 * 0.0625;
    }

}
