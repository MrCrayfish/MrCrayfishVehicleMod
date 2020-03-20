package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class SeederTrailerEntity extends TrailerEntity implements EntityRaytracer.IEntityRaytraceable, IStorage
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 6.2 * 0.0625, 6 * 0.0625, 7 * 0.0625, 8.4 * 0.0625F, 17 * 0.0625, 1.1));
    private static final Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
        Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> map = new HashMap<>();
        map.put(CONNECTION_BOX, EntityRaytracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    });

    private int inventoryTimer;
    private StorageInventory inventory;

    public SeederTrailerEntity(EntityType<? extends SeederTrailerEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.initInventory();
    }

    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return false;
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand)
    {
        ItemStack heldItem = player.getHeldItem(hand);
        if((heldItem.isEmpty() || !(heldItem.getItem() instanceof SprayCanItem)) && player instanceof ServerPlayerEntity)
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, this.getInventory(), buffer -> buffer.writeVarInt(this.getEntityId()));
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(!this.world.isRemote && Config.SERVER.trailerInventorySyncCooldown.get() > 0 && inventoryTimer++ == Config.SERVER.trailerInventorySyncCooldown.get())
        {
            this.inventoryTimer = 0;
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> SeederTrailerEntity.this), new MessageSyncInventory(this.getEntityId(), this.inventory));
        }
    }

    @Override
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();

        Vec3d lookVec = this.getLookVec();
        this.plantSeed(lookVec.rotateYaw((float) Math.toRadians(90F)).scale(0.85));
        this.plantSeed(Vec3d.ZERO);
        this.plantSeed(lookVec.rotateYaw((float) Math.toRadians(-90F)).scale(0.85));
    }

    private void plantSeed(Vec3d vec)
    {
        BlockPos pos = new BlockPos(prevPosX + vec.x, prevPosY + 0.25, prevPosZ + vec.z);
        if(world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() instanceof FarmlandBlock)
        {
            ItemStack seed = this.getSeed();
            if(seed.isEmpty() && this.getPullingEntity() instanceof StorageTrailerEntity)
            {
                seed = this.getSeedFromStorage((StorageTrailerEntity) this.getPullingEntity());
            }
            if(this.isSeed(seed))
            {
                Block seedBlock = ((BlockNamedItem) seed.getItem()).getBlock();
                this.world.setBlockState(pos, seedBlock.getDefaultState());
                seed.shrink(1);
            }
        }
    }

    private ItemStack getSeed()
    {
        for(int i = 0; i < this.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if(this.isSeed(stack))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean isSeed(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() instanceof BlockNamedItem && ((BlockNamedItem) stack.getItem()).getBlock() instanceof CropsBlock;
    }

    private ItemStack getSeedFromStorage(StorageTrailerEntity storageTrailer)
    {
        if(storageTrailer == null)
            return ItemStack.EMPTY;

        if(storageTrailer.getInventory() != null)
        {
            StorageInventory storage = storageTrailer.getInventory();
            for(int i = 0; i < storage.getSizeInventory(); i++)
            {
                ItemStack stack = storage.getStackInSlot(i);
                if(!stack.isEmpty() && stack.getItem() instanceof net.minecraftforge.common.IPlantable)
                {
                    return stack;
                }
            }

            if(storageTrailer.getPullingEntity() instanceof StorageTrailerEntity)
            {
                return this.getSeedFromStorage((StorageTrailerEntity) storageTrailer.getPullingEntity());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        if(compound.contains("Inventory", Constants.NBT.TAG_LIST))
        {
            this.initInventory();
            InventoryUtil.readInventoryToNBT(compound, "Inventory", this.inventory);
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if(this.inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", this.inventory);
        }
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
                    inventory.setInventorySlotContents(i, stack.copy());
                }
            }
        }
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        if(inventory != null)
        {
            InventoryHelper.dropInventoryItems(world, this, inventory);
        }
    }

    @Override
    public StorageInventory getInventory()
    {
        return inventory;
    }

    @Override
    public double getHitchOffset()
    {
        return -16.0 * 1.1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<EntityRaytracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return ImmutableList.of(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        //TODO figure this out
        //RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean processHit(EntityRaytracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(rightClick)
        {
            if(result.getPartHit() == CONNECTION_BOX)
            {
                PacketHandler.instance.sendToServer(new MessageAttachTrailer(this.getEntityId(), Minecraft.getInstance().player.getEntityId()));
                return true;
            }
        }
        return EntityRaytracer.IEntityRaytraceable.super.processHit(result, rightClick);
    }

    @Override
    public boolean isStorageItem(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem().isIn(Tags.Items.SEEDS);
    }

    @Override
    public ITextComponent getStorageName()
    {
        return this.getDisplayName();
    }
}
