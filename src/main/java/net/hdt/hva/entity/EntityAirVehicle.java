package net.hdt.hva.entity;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public abstract class EntityAirVehicle extends EntityVehicle {

    protected EntityAirVehicle(World worldIn) {
        super(worldIn);
    }

    @Override
    public void updateVehicle()
    {
        for(int i = 0; i < motionY; i++) {
            world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, posX, posY - Math.random() + i, posZ, 0.0d, -1.0d, 0.0d);
            world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, posX, posY - Math.random() + i, posZ + 0.45, 0.0d, -1.0d, 0.0d);
            world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, posX, posY - Math.random() + i, posZ + 0.7, 0.0d, -1.0d, 0.0d);
        }
    }

    @Override
    public void updateVehicleMotion() {
        float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
        double minHeightLimitation = 0.0D;
        double maxHeightLimitation = 0.0001D;
        this.vehicleMotionX = (-currentSpeed * f1);
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if(motionY < maxHeightLimitation) {
                this.motionY += 0.2D;
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_V)) {
            if(motionY > maxHeightLimitation) {
                this.motionY -= 0.1D;
            }
            if(motionY < maxHeightLimitation) {
                this.motionY -= 0.1D;
            }
        }

        this.vehicleMotionZ = (currentSpeed * f2);
    }



}

