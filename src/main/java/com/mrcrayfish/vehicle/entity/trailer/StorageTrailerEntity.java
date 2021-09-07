package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class StorageTrailerEntity extends TrailerEntity implements IStorage
{
    private StorageInventory inventory;

    public StorageTrailerEntity(EntityType<? extends StorageTrailerEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.initInventory();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Inventory", Constants.NBT.TAG_LIST))
        {
            this.initInventory();
            InventoryUtil.readInventoryToNBT(compound, "Inventory", inventory);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        if(this.inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", inventory);
        }
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
        if(this.inventory != null)
        {
            InventoryHelper.dropContents(this.level, this, this.inventory);
        }
    }

    @Override
    public StorageInventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public void startOpen(PlayerEntity player)
    {
        this.playSound(SoundEvents.CHEST_OPEN, 0.5F, 0.9F);
    }

    @Override
    public ITextComponent getStorageName()
    {
        return this.getDisplayName();
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerInteractionBoxes()
    {
        EntityRayTracer.instance().registerInteractionBox(ModEntities.STORAGE_TRAILER.get(), () -> {
            return createScaledBoundingBox(-6.0, -0.5, 9.0, 6.0, 3.5, 17.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageAttachTrailer(entity.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);

        EntityRayTracer.instance().registerInteractionBox(ModEntities.STORAGE_TRAILER.get(), () -> {
            double chestScale = 0.9;
            double bodyScale = 1.0 / chestScale;
            return createScaledBoundingBox(-0.4375, 0.125 * bodyScale, -0.4375, 0.4375, 0.9125 * bodyScale, 0.4375, chestScale);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageOpenStorage(entity.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);
    }
}
