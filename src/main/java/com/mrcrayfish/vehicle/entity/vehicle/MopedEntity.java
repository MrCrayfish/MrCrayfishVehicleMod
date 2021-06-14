package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.EntityRayTracer.RayTracePart;
import com.mrcrayfish.vehicle.client.EntityRayTracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.EntityRayTracer.TriangleRayTraceList;
import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.AxisAlignedBB;
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
    private static final RayTracePart CHEST_BOX = new RayTracePart(new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375));
    private static final RayTracePart TRAY_BOX = new RayTracePart(createScaledBoundingBox(-4 * 0.0625, 8 * 0.0625 + 0.1, -4.5 * 0.0625, 4 * 0.0625, 9F * 0.0625F + 0.1, -12.5 * 0.0625, 1.2));
    private static final Map<RayTracePart, TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
    {
        Map<RayTracePart, TriangleRayTraceList> map = new HashMap<>();
        map.put(CHEST_BOX, EntityRayTracer.boxToTriangles(CHEST_BOX.getBox(), null));
        map.put(TRAY_BOX, EntityRayTracer.boxToTriangles(TRAY_BOX.getBox(), null));
        return map;
    });

    private StorageInventory inventory;

    public MopedEntity(EntityType<? extends MopedEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(12F);
        this.setTurnSensitivity(15);
        this.setMaxTurnAngle(45);
        this.setFuelCapacity(12000F);
        this.setFuelConsumption(0.225F);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CHEST, false);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.MOPED_ENGINE_MONO.get();
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.MOPED_ENGINE_STEREO.get();
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        /*if(this.hasChest()) //TODO fix this
        {
            RenderGlobal.drawSelectionBoundingBox(CHEST_BOX.getBox(), 0, 1, 0, 0.4F);
        }
        else
        {
            RenderGlobal.drawSelectionBoundingBox(TRAY_BOX.getBox(), 0, 1, 0, 0.4F);
        }*/
        /*
        IVertexBuilder ivertexbuilder = p_225619_2_.getBuffer(RenderType.lines());

      for(VoxelShape voxelshape : this.collisionData) {
         WorldRenderer.renderVoxelShape(p_225619_1_, ivertexbuilder, voxelshape, -p_225619_3_, -p_225619_5_, -p_225619_7_, 1.0F, 1.0F, 1.0F, 1.0F);
      }
         */
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
            Vector3d target = new Vector3d(0, 0.75, -0.75).yRot(-(this.yRot - this.additionalYaw) * 0.017453292F).add(this.position());
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
        Vector3d target = new Vector3d(0, 0.75, -0.75).yRot(-(this.yRot - this.additionalYaw) * 0.017453292F).add(this.position());
        this.level.playSound(null, target.x, target.y, target.z, SoundEvents.CHEST_OPEN, this.getSoundSource(), 0.5F, 0.9F);
    }
}
