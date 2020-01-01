package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockJack;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class JackTileEntity extends TileEntity implements ITickableTileEntity
{
    public static final int MAX_LIFT_PROGRESS = 20;

    private EntityJack jack = null;

    private boolean activated = false;
    public int prevLiftProgress;
    public int liftProgress;

    public JackTileEntity()
    {
        super(ModTileEntities.JACK);
    }

    public void setVehicle(VehicleEntity vehicle)
    {
        this.jack = new EntityJack(this.world, this.pos, 9 * 0.0625, vehicle.rotationYaw);
        vehicle.startRiding(this.jack, true);
        this.jack.updateRidden();
        this.world.addEntity(this.jack);
    }

    @Nullable
    public EntityJack getJack()
    {
        return this.jack;
    }

    @Override
    public void tick()
    {
        this.prevLiftProgress = this.liftProgress;

        if(this.world.isRemote && this.jack == null)
        {
            List<EntityJack> jacks = this.world.getEntitiesWithinAABB(EntityJack.class, new AxisAlignedBB(this.pos));
            if(jacks.size() > 0)
            {
                this.jack = jacks.get(0);
            }
        }

        if(this.jack != null && (this.jack.getPassengers().isEmpty() || !jack.isAlive()))
        {
            this.jack = null;
        }

        if(this.jack != null)
        {
            if(this.jack.getPassengers().size() > 0)
            {
                if(!this.activated)
                {
                    this.world.playSound(null, this.pos, ModSounds.JACK_UP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.activated = true;
                }
            }
            else if(this.activated)
            {
                this.world.playSound(null, this.pos, ModSounds.JACK_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                this.activated = false;
            }
        }
        else if(this.activated)
        {
            this.world.playSound(null, this.pos, ModSounds.JACK_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.activated = false;
        }

        if(this.activated)
        {
            if(this.liftProgress < MAX_LIFT_PROGRESS)
            {
                this.liftProgress++;
                this.moveCollidedEntities();
            }
        }
        else if(liftProgress > 0)
        {
            this.liftProgress--;
            this.moveCollidedEntities();
        }
    }

    private void moveCollidedEntities()
    {
        BlockState state = this.world.getBlockState(this.getPos());
        if(state.getBlock() instanceof BlockJack)
        {
            AxisAlignedBB boundingBox = state.getShape(this.world, this.pos).getBoundingBox().offset(this.pos);
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.jack, boundingBox);
            if(!list.isEmpty())
            {
                for(Entity entity : list)
                {
                    if(entity.getPushReaction() != PushReaction.IGNORE)
                    {
                        AxisAlignedBB entityBoundingBox = entity.getBoundingBox();
                        double posY = boundingBox.maxY - entityBoundingBox.minY;
                        entity.move(MoverType.PISTON, new Vec3d(0.0, posY, 0.0));
                    }
                }
            }
        }
    }

    public float getProgress()
    {
        return this.liftProgress / (float) JackTileEntity.MAX_LIFT_PROGRESS;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }
}
