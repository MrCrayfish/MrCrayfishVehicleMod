package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

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

        if(this.bladeSpeed > 30.0F)
        {
            double bladeScale = this.bladeSpeed * 0.001;
            double spreadRange = 8.0;
            double randX = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double randZ = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double posX = this.getX() + randX;
            double posZ = this.getZ() + randZ;
            double downDistance = Math.min(12.0, this.bladeSpeed / 15.0);
            downDistance = (downDistance * 0.5) + (downDistance * 0.5) * this.random.nextDouble();
            Vector3d start = new Vector3d(posX, this.getY() + 3.0, posZ);
            Vector3d end = start.subtract(0, downDistance, 0);
            BlockRayTraceResult result = this.level.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, null));
            if(result.getType() != RayTraceResult.Type.MISS)
            {
                Vector3d loc = result.getLocation();
                double distanceScale = (downDistance - start.distanceTo(loc)) / downDistance;
                BlockState state = this.level.getBlockState(result.getBlockPos());
                if(state.is(Tags.Blocks.DIRT) || state.is(Tags.Blocks.GRAVEL) || state.is(Tags.Blocks.SAND))
                {
                    this.level.addParticle(ModParticleTypes.DUST.get(), loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                }
                else if(state.getFluidState().is(FluidTags.WATER))
                {
                    this.level.addParticle(ParticleTypes.SPLASH, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level.addParticle(ParticleTypes.BUBBLE, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level.addParticle(ParticleTypes.CLOUD, loc.x, loc.y, loc.z, 0, 0, 0);
                }
            }
        }
    }

    // Client only TODO move to base vehicle class
    private Matrix4f getTransformMatrix(float partialTicks)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        matrix.multiply(Vector3f.YP.rotationDegrees(-this.getBodyRotationYaw(partialTicks)));
        matrix.multiply(Vector3f.XP.rotationDegrees(this.getBodyRotationPitch(partialTicks)));
        matrix.multiply(Vector3f.ZP.rotationDegrees(this.getBodyRotationRoll(partialTicks)));
        VehicleProperties properties = this.getProperties();
        Transform bodyPosition = properties.getBodyTransform();
        matrix.multiply((Matrix4f.createScaleMatrix((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale())));
        Vector3f translate = new Vector3f();
        translate.add((float) bodyPosition.getX() * 0.0625F, (float) bodyPosition.getY() * 0.0625F, (float) bodyPosition.getZ() * 0.0625F);
        translate.add(0.0F, 0.5F, 0.0F);
        translate.add(0.0F, properties.getAxleOffset() * 0.0625F, 0.0F);
        translate.add(0.0F, properties.getWheelOffset() * 0.0625F, 0.0F);
        matrix.multiply(Matrix4f.createTranslateMatrix(translate.x(), translate.y(), translate.z()));
        return matrix;
    }
}
