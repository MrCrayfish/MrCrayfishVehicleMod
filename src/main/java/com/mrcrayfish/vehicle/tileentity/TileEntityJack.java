package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockJack;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
                this.moveCollidedEntities();
            }
        }
        else if(liftProgress > 0)
        {
            liftProgress--;
            this.moveCollidedEntities();
        }
    }

    private void moveCollidedEntities()
    {
        IBlockState state = this.world.getBlockState(this.getPos());
        if(state.getBlock() instanceof BlockJack)
        {
            AxisAlignedBB boundingBox = state.getBoundingBox(world, this.pos).offset(this.pos);
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(jack, boundingBox);
            if(!list.isEmpty())
            {
                for(Entity entity : list)
                {
                    if(entity.getPushReaction() != EnumPushReaction.IGNORE)
                    {
                        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
                        double posY = boundingBox.maxY - entityBoundingBox.minY;
                        entity.move(MoverType.PISTON, 0.0, posY, 0.0);
                    }
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }
}
