package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityJack extends TileEntity implements ITickable
{
    public static final int MAX_LIFT_PROGRESS = 20;

    private EntityJack jack = null;

    private boolean activated = false;
    public int prevLiftProgress;
    public int liftProgress;

    public void setVehicle(EntityVehicle vehicle)
    {
        jack = new EntityJack(world, pos, 9 * 0.0625, vehicle.rotationYaw);
        vehicle.startRiding(jack, true);
        jack.updateRidden();
        world.spawnEntity(jack);
    }

    @Nullable
    public EntityJack getJack()
    {
        return jack;
    }

    @Override
    public void update()
    {
        prevLiftProgress = liftProgress;

        if(world.isRemote && jack == null)
        {
            List<EntityJack> jacks = world.getEntitiesWithinAABB(EntityJack.class, new AxisAlignedBB(pos));
            if(jacks.size() > 0)
            {
                jack = jacks.get(0);
            }
        }

        if(jack != null && (jack.getPassengers().isEmpty() || jack.isDead))
        {
            jack = null;
        }

        if(jack != null)
        {
            if(jack.getPassengers().size() > 0)
            {
                if(!activated)
                {
                    world.playSound(null, pos, ModSounds.JACK_UP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    activated = true;
                }
            }
            else if(activated)
            {
                world.playSound(null, pos, ModSounds.JACK_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                activated = false;
            }
        }
        else if(activated)
        {
            world.playSound(null, pos, ModSounds.JACK_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            activated = false;
        }

        if(activated)
        {
            if(liftProgress < MAX_LIFT_PROGRESS)
            {
                liftProgress++;
            }
        }
        else if(liftProgress > 0)
        {
            liftProgress--;
        }
    }
}
