package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityJack extends TileEntity implements ITickable
{
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
            List<EntityJack> jacks = world.getEntitiesWithinAABB(EntityJack.class, new AxisAlignedBB(pos).grow(100));
            if(jacks.size() > 0)
            {
                jack = jacks.get(0);
                if(jack.getPassengers().size() > 0)
                {
                    Entity entity = jack.getPassengers().get(0);
                    jack.updateRidden();
                    entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                }
            }
        }

        if(jack != null && jack.isDead)
        {
            jack = null;
        }

        if(jack != null)
        {
            if(jack.getPassengers().size() > 0)
            {
                if(!activated)
                {
                    activated = true;
                }
            }
            else
            {
                activated = false;
            }
        }
        else
        {
            activated = false;
        }

        if(activated)
        {
            if(liftProgress < 10)
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
