package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityShoppingCart extends EntityLandVehicle implements IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -1.0F;
    public static final float WHEEL_OFFSET = 2.0F;
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, 0.165, 0, 0, 0, 1.05);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(4.0D, 9.25D, 0.0D);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0.0D, -0.03125D, -0.25D);

    private EntityPlayer pusher;

    public EntityShoppingCart(World worldIn)
    {
        super(worldIn);
        this.setMaxTurnAngle(90);
        this.setTurnSensitivity(15);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.setFuelCapacity(0F);
        this.setFuelConsumption(0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.SHOPPING_CART_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
    }

    @Override
    public void onUpdate()
    {
        if(pusher != null)
        {
            this.prevRotationYaw = this.rotationYaw;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            float x = MathHelper.sin(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            float z = MathHelper.cos(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            this.posX = pusher.posX + x;
            this.posY = pusher.posY;
            this.posZ = pusher.posZ + z;
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
            this.rotationYaw = pusher.rotationYaw;
        }
        else
        {
            super.onUpdate();
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            if(player.isSneaking())
            {
                if(pusher == player)
                {
                    pusher = null;
                    player.getDataManager().set(CommonEvents.PUSHING_CART, false);
                    return true;
                }
                else if(pusher == null)
                {
                    pusher = player;
                    player.getDataManager().set(CommonEvents.PUSHING_CART, true);
                }
            }
            else if(pusher != player)
            {
                super.processInitialInteract(player, hand);
            }

        }
        return true;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return null;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return null;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0625 * 7.5;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.NONE;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
