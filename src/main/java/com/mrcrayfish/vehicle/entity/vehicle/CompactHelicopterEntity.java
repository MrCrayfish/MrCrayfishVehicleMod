package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class CompactHelicopterEntity extends HelicopterEntity
{
    public CompactHelicopterEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        if(this.canDrive() && this.tickCount % 2 == 0)
        {
            Vector3d exhaust = this.getExhaustFumesPosition().scale(0.0625);
            Vector4f fumePosition = new Vector4f(new Vector3f(exhaust));
            fumePosition.transform(this.getTransformMatrix(0F));
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + fumePosition.x(), this.getY() + fumePosition.y(), this.getZ() + fumePosition.z(), -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
        }
    }
}
