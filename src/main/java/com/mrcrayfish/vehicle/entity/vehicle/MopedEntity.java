package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.EntityRayTracer.RayTracePart;
import com.mrcrayfish.vehicle.client.EntityRayTracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.EntityRayTracer.TriangleRayTraceList;
import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class MopedEntity extends MotorcycleEntity implements IAttachableChest
{
    private static final DataParameter<Boolean> CHEST = EntityDataManager.defineId(MopedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHEST_OPEN = EntityDataManager.defineId(MopedEntity.class, DataSerializers.BOOLEAN);
    private static final RayTracePart CHEST_BOX = new RayTracePart(createBoxScaled(-3.5, 10.5, -7, 3.5, 17.5, -14, 1.2));
    private static final RayTracePart TRAY_BOX = new RayTracePart(createBoxScaled(-4, 9.5, -6.5, 4, 10.5, -14.5, 1.2));
    private static final Map<RayTracePart, TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
    {
        Map<RayTracePart, TriangleRayTraceList> map = new HashMap<>();
        map.put(CHEST_BOX, EntityRayTracer.boxToTriangles(CHEST_BOX.getBox(), null));
        map.put(TRAY_BOX, EntityRayTracer.boxToTriangles(TRAY_BOX.getBox(), null));
        return map;
    });

    private StorageInventory inventory;

    @OnlyIn(Dist.CLIENT)
    private float openProgress;
    @OnlyIn(Dist.CLIENT)
    private float prevOpenProgress;

    public MopedEntity(EntityType<? extends MopedEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(12F);
        this.setTurnSensitivity(5);
        this.setMaxTurnAngle(45);
        this.setFuelCapacity(12000F);
        this.setFuelConsumption(0.225F);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CHEST, false);
        this.entityData.define(CHEST_OPEN, false);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_MOPED_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.2F;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Chest", Constants.NBT.TAG_BYTE))
        {
            this.setChest(compound.getBoolean("Chest"));
            if(compound.contains("Inventory", Constants.NBT.TAG_LIST))
            {
                this.initInventory();
                InventoryUtil.readInventoryToNBT(compound, "Inventory", inventory);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Chest", this.hasChest());
        if(this.hasChest() && inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", inventory);
        }
    }

    public boolean hasChest()
    {
        return this.entityData.get(CHEST);
    }

    public void setChest(boolean chest)
    {
        this.entityData.set(CHEST, chest);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean processHit(RayTraceResultRotated result, boolean rightClick)
    {
        if (rightClick)
        {
            RayTracePart partHit = result.getPartHit();
            if(partHit == CHEST_BOX && this.hasChest())
            {
                PacketHandler.instance.sendToServer(new MessageOpenStorage(this.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
                return true;
            }
            else if(partHit == TRAY_BOX && !this.hasChest())
            {
                PacketHandler.instance.sendToServer(new MessageAttachChest(this.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
                return true;
            }
        }
        return super.processHit(result, rightClick);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<RayTracePart, TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<RayTracePart> getApplicableInteractionBoxes()
    {
        List<RayTracePart> boxes = Lists.newArrayList();
        if(hasChest())
        {
            boxes.add(CHEST_BOX);
        }
        else
        {
            boxes.add(TRAY_BOX);
        }
        return boxes;
    }

    private void initInventory()
    {
        StorageInventory original = this.inventory;
        this.inventory = new StorageInventory(this, 27);
        // Copies the inventory if it exists already over to the new instance
        if(original != null)
        {
            for(int i = 0; i < original.getContainerSize(); i++)
            {
                ItemStack stack = original.getItem(i);
                if(!stack.isEmpty())
                {
                    this.inventory.setItem(i, stack.copy());
                }
            }
        }
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        if(this.hasChest() && inventory != null)
        {
            InventoryHelper.dropContents(level, this, inventory);
        }
    }

    @Nullable
    @Override
    public StorageInventory getInventory()
    {
        if(this.hasChest() && this.inventory == null)
        {
            this.initInventory();
        }
        return this.inventory;
    }

    @Override
    public void attachChest(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() == Item.byBlock(Blocks.CHEST))
        {
            this.setChest(true);
            this.initInventory();

            CompoundNBT itemTag = stack.getTag();
            if(itemTag != null)
            {
                CompoundNBT blockEntityTag = itemTag.getCompound("BlockEntityTag");
                if(!blockEntityTag.isEmpty() && blockEntityTag.contains("Items", Constants.NBT.TAG_LIST))
                {
                    NonNullList<ItemStack> chestInventory = NonNullList.withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(blockEntityTag, chestInventory);
                    for(int i = 0; i < chestInventory.size(); i++)
                    {
                        this.inventory.setItem(i, chestInventory.get(i));
                    }
                }
            }
        }
    }

    @Override
    public void removeChest()
    {
        if(this.inventory != null)
        {
            Vector3d target = this.getChestPosition();
            InventoryUtil.dropInventoryItems(level, target.x, target.y, target.z, this.inventory);
            this.inventory = null;
            this.setChest(false);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            level.addFreshEntity(new ItemEntity(level, target.x, target.y, target.z, new ItemStack(Blocks.CHEST)));
        }
    }

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }

    @Override
    public ITextComponent getStorageName()
    {
        return this.getDisplayName();
    }

    @Override
    public void startOpen(PlayerEntity player)
    {
        Vector3d target = this.getChestPosition();
        this.level.playSound(null, target.x, target.y, target.z, SoundEvents.CHEST_OPEN, this.getSoundSource(), 0.5F, 0.9F);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.hasChest())
        {
            // Updates the chest open state
            if(!this.level.isClientSide())
            {
                this.entityData.set(CHEST_OPEN, this.getPlayerCountInChest() > 0);
            }
            else
            {
                // Updates the open progress for the animation
                this.prevOpenProgress = this.openProgress;
                if(this.entityData.get(CHEST_OPEN))
                {
                    this.openProgress = Math.min(1.0F, this.openProgress + 0.1F);
                }
                else
                {
                    float lastOpenProgress = this.openProgress;
                    this.openProgress = Math.max(0.0F, this.openProgress - 0.1F);
                    if(this.openProgress < 0.5F && lastOpenProgress >= 0.5F)
                    {
                        Vector3d target = this.getChestPosition();
                        this.level.playLocalSound(target.x, target.y, target.z, SoundEvents.CHEST_CLOSE, this.getSoundSource(), 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
                    }
                }
            }
        }
    }

    protected Vector3d getChestPosition()
    {
        return new Vector3d(0, 1.0, -0.75).yRot(-(this.yRot - this.additionalYaw) * 0.017453292F).add(this.position());
    }

    protected int getPlayerCountInChest()
    {
        if(!this.hasChest())
        {
            return 0;
        }

        int count = 0;
        for(PlayerEntity player : this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(5.0F)))
        {
            if(player.containerMenu instanceof StorageContainer)
            {
                IInventory container = ((StorageContainer) player.containerMenu).getStorageInventory();
                if(container == this)
                {
                    count++;
                }
            }
        }
        return count;
    }

    @OnlyIn(Dist.CLIENT)
    public float getOpenProgress()
    {
        return this.openProgress;
    }

    @OnlyIn(Dist.CLIENT)
    public float getPrevOpenProgress()
    {
        return this.prevOpenProgress;
    }
}
