package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class EntityLawnMower extends EntityLandVehicle implements IEntityRaytraceable
{
    private static final Random RANDOM = new Random();
    public static final float AXLE_OFFSET = -2.0F;
    public static final float WHEEL_OFFSET = 2.85F;
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, 0.65, 0, 0, 0, 1.25);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-4.75, 12.5, 3.5, 0, -90, 0, 0.35);
    public static final PartPosition KEY_PORT_POSITION = new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(12.0, -1.5, 0.0);
    private static final Vec3d TOW_BAR_VEC = new Vec3d(0.0, 0.0, -20.0);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0.0, -0.01, -1.0);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityLawnMower(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(8);
        this.setSize(1.2F, 1.0F);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTowBarPosition(TOW_BAR_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.setFuelCapacity(5000F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.LAWN_MOWER_BODY);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
        wheel = new ItemStack(ModItems.WHEEL);
    }

    @Override
    public void updateVehicle()
    {
        super.updateVehicle();

        if(this.getControllingPassenger() != null)
        {
            AxisAlignedBB axisAligned = this.getEntityBoundingBox();
            Vec3d lookVec = this.getLookVec().scale(0.5);
            int minX = MathHelper.floor(axisAligned.minX + lookVec.x);
            int maxX = MathHelper.ceil(axisAligned.maxX + lookVec.x);
            int minZ = MathHelper.floor(axisAligned.minZ + lookVec.z);
            int maxZ = MathHelper.ceil(axisAligned.maxZ + lookVec.z);

            for(int x = minX; x < maxX; x++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, axisAligned.minY, z);
                    IBlockState state = world.getBlockState(pos);

                    EntityStorageTrailer trailer = null;
                    if(getTrailer() instanceof EntityStorageTrailer)
                    {
                        trailer = (EntityStorageTrailer) getTrailer();
                    }

                    if(state.getBlock() instanceof BlockBush)
                    {
                        NonNullList<ItemStack> drops = NonNullList.create();
                        state.getBlock().getDrops(drops, world, pos, state, 1);

                        for(ItemStack stack : drops)
                        {
                            this.addItemToStorage(trailer, stack);
                        }

                        world.setBlockToAir(pos);
                        world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, this).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.playEvent(2001, pos, Block.getStateId(state));
                    }
                }
            }
        }
    }

    private void addItemToStorage(EntityStorageTrailer storageTrailer, ItemStack stack)
    {
        if(stack.isEmpty())
            return;

        if(storageTrailer != null && storageTrailer.getChest() != null)
        {
            StorageInventory storage = storageTrailer.getChest();
            storage.addItemStack(stack);
            if(!stack.isEmpty())
            {
                if(storageTrailer.getTrailer() instanceof EntityStorageTrailer)
                {
                    this.addItemToStorage((EntityStorageTrailer) storageTrailer.getTrailer(), stack);
                }
                else
                {
                    spawnItemStack(world, stack);
                }
            }
        }
        else
        {
            spawnItemStack(world, stack);
        }
    }

    private void spawnItemStack(World worldIn, ItemStack stack)
    {
        while(!stack.isEmpty())
        {
            EntityItem entityItem = new EntityItem(worldIn, prevPosX, prevPosY, prevPosZ, stack.splitStack(RANDOM.nextInt(21) + 10));
            entityItem.setPickupDelay(20);
            entityItem.motionX = -this.motionX / 4.0;
            entityItem.motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            entityItem.motionZ = -this.motionZ / 4.0;
            worldIn.spawnEntity(entityItem);
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.atvEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.atvEngineStereo;
    }

    @Override
    public double getMountedYOffset()
    {
        return 10.5 * 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }
}
