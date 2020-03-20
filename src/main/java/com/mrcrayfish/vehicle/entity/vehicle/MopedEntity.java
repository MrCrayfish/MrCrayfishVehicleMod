package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTracePart;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.EntityRaytracer.TriangleRayTraceList;
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
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
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
public class MopedEntity extends MotorcycleEntity implements IEntityRaytraceable, IAttachableChest
{
    private static final DataParameter<Boolean> CHEST = EntityDataManager.createKey(MopedEntity.class, DataSerializers.BOOLEAN);
    private static final RayTracePart CHEST_BOX = new RayTracePart(new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375));
    private static final RayTracePart TRAY_BOX = new RayTracePart(createScaledBoundingBox(-4 * 0.0625, 8 * 0.0625 + 0.1, -4.5 * 0.0625, 4 * 0.0625, 9F * 0.0625F + 0.1, -12.5 * 0.0625, 1.2));
    private static final Map<RayTracePart, TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
    {
        Map<RayTracePart, TriangleRayTraceList> map = new HashMap<>();
        map.put(CHEST_BOX, EntityRaytracer.boxToTriangles(CHEST_BOX.getBox(), null));
        map.put(TRAY_BOX, EntityRaytracer.boxToTriangles(TRAY_BOX.getBox(), null));
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
    public void registerData()
    {
        super.registerData();
        this.dataManager.register(CHEST, false);
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
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
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
    protected void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
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
    protected void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("Chest", this.hasChest());
        if(this.hasChest() && inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", inventory);
        }
    }

    public boolean hasChest()
    {
        return this.dataManager.get(CHEST);
    }

    public void setChest(boolean chest)
    {
        this.dataManager.set(CHEST, chest);
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
                PacketHandler.instance.sendToServer(new MessageOpenStorage(this.getEntityId()));
                Minecraft.getInstance().player.swingArm(Hand.MAIN_HAND);
                return true;
            }
            else if(partHit == TRAY_BOX && !this.hasChest())
            {
                PacketHandler.instance.sendToServer(new MessageAttachChest(this.getEntityId()));
                return true;
            }
        }
        return IEntityRaytraceable.super.processHit(result, rightClick);
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
        IVertexBuilder ivertexbuilder = p_225619_2_.getBuffer(RenderType.func_228659_m_());

      for(VoxelShape voxelshape : this.collisionData) {
         WorldRenderer.func_228431_a_(p_225619_1_, ivertexbuilder, voxelshape, -p_225619_3_, -p_225619_5_, -p_225619_7_, 1.0F, 1.0F, 1.0F, 1.0F);
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
            for(int i = 0; i < original.getSizeInventory(); i++)
            {
                ItemStack stack = original.getStackInSlot(i);
                if(!stack.isEmpty())
                {
                    this.inventory.setInventorySlotContents(i, stack.copy());
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
            InventoryHelper.dropInventoryItems(world, this, inventory);
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
        if(!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))
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
                        this.inventory.setInventorySlotContents(i, chestInventory.get(i));
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
            Vec3d target = new Vec3d(0, 0.75, -0.75).rotateYaw(-(this.rotationYaw - this.additionalYaw) * 0.017453292F).add(getPositionVector());
            InventoryUtil.dropInventoryItems(world, target.x, target.y, target.z, this.inventory);
            this.inventory = null;
            this.setChest(false);
            world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.addEntity(new ItemEntity(world, target.x, target.y, target.z, new ItemStack(Blocks.CHEST)));
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
    public void openInventory(PlayerEntity player)
    {
        Vec3d target = new Vec3d(0, 0.75, -0.75).rotateYaw(-(this.rotationYaw - this.additionalYaw) * 0.017453292F).add(getPositionVector());
        this.world.playSound(null, target.x, target.y, target.z, SoundEvents.BLOCK_CHEST_OPEN, this.getSoundCategory(), 0.5F, 0.9F);
    }
}
