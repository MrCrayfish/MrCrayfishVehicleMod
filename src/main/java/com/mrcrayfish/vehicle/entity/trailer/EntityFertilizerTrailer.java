package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.EntityTrailer;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class EntityFertilizerTrailer extends EntityTrailer implements EntityRaytracer.IEntityRaytraceable, IStorage
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 6.2 * 0.0625, 6 * 0.0625, 7 * 0.0625, 8.4 * 0.0625F, 18 * 0.0625, 1.1));
    private static final Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> interactionBoxMapStatic = Maps.newHashMap();

    static
    {
        if(FMLCommonHandler.instance().getSide().isClient())
        {
            interactionBoxMapStatic.put(CONNECTION_BOX, EntityRaytracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        }

    }

    private int inventoryTimer;
    private StorageInventory inventory;
    private BlockPos[] lastPos = new BlockPos[3];

    public EntityFertilizerTrailer(World worldIn)
    {
        super(worldIn);
        this.initInventory();
        this.setSize(1.5F, 1.0F);
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
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack heldItem = player.getHeldItem(hand);
        if((heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemSprayCan)) && player instanceof EntityPlayerMP)
        {
            if(!player.isSneaking())
            {
                this.inventory.openGui((EntityPlayerMP) player, this);
                return true;
            }
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        if(!world.isRemote && VehicleConfig.SERVER.trailerInventorySyncCooldown > 0 && inventoryTimer++ == VehicleConfig.SERVER.trailerInventorySyncCooldown)
        {
            inventoryTimer = 0;
            PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncInventory(this.getEntityId(), inventory), this);
        }
    }

    @Override
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();

        ItemStack fertilizer = this.getFertilizer();
        if(fertilizer.isEmpty() && this.getPullingEntity() instanceof EntityStorageTrailer)
        {
            fertilizer = this.getFertilizerFromStorage((EntityStorageTrailer) this.getPullingEntity());
        }
        if(!fertilizer.isEmpty())
        {
            Vec3d lookVec = this.getLookVec();
            boolean applied = this.applyFertilizer(lookVec.rotateYaw((float) Math.toRadians(90F)), 0);
            applied |= this.applyFertilizer(Vec3d.ZERO, 1);
            applied |= this.applyFertilizer(lookVec.rotateYaw((float) Math.toRadians(-90F)), 2);
            if(applied) fertilizer.shrink(1);
        }
    }

    private boolean applyFertilizer(Vec3d vec, int index)
    {
        Vec3d prevPosVec = new Vec3d(prevPosX, prevPosY + 0.25, prevPosZ);
        prevPosVec = prevPosVec.add(new Vec3d(0, 0, -1).rotateYaw(-this.rotationYaw * 0.017453292F));
        BlockPos pos = new BlockPos(prevPosVec.x + vec.x, prevPosVec.y, prevPosVec.z + vec.z);

        if(lastPos[index] != null && lastPos[index].equals(pos))
        {
            return false;
        }
        lastPos[index] = pos;

        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof IGrowable)
        {
            IGrowable growable = (IGrowable) state.getBlock();
            if(growable.canGrow(world, pos, state, false))
            {
                if(growable.canUseBonemeal(world, rand, pos, state))
                {
                    growable.grow(world, rand, pos, state);
                    world.playEvent(2005, pos, 0);
                    return true;
                }
            }
        }
        return false;
    }

    private ItemStack getFertilizer()
    {
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() instanceof ItemDye)
            {
                EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
                if(dyeColor == EnumDyeColor.WHITE)
                {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getFertilizerFromStorage(EntityStorageTrailer storageTrailer)
    {
        if(storageTrailer == null)
            return ItemStack.EMPTY;

        if(storageTrailer.getInventory() != null)
        {
            StorageInventory storage = storageTrailer.getInventory();
            for(int i = 0; i < storage.getSizeInventory(); i++)
            {
                ItemStack stack = storage.getStackInSlot(i);
                if(!stack.isEmpty() && stack.getItem() instanceof ItemDye)
                {
                    EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
                    if(dyeColor == EnumDyeColor.WHITE)
                    {
                        return stack;
                    }
                }
            }

            if(storageTrailer.getPullingEntity() instanceof EntityStorageTrailer)
            {
                return this.getFertilizerFromStorage((EntityStorageTrailer) storageTrailer.getPullingEntity());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("inventory", Constants.NBT.TAG_LIST))
        {
            this.initInventory();
            InventoryUtil.readInventoryToNBT(compound, "inventory", inventory);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        if(inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "inventory", inventory);
        }
    }

    private void initInventory()
    {
        InventoryBasic original = inventory;
        inventory = new StorageInventory(this.getName(), false, 27, this);
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
    protected void onVehicleDestroyed(EntityLivingBase entity)
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
        return -17.0 * 1.1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<EntityRaytracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return ImmutableList.of(CONNECTION_BOX);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean processHit(EntityRaytracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(rightClick)
        {
            if(result.getPartHit() == CONNECTION_BOX)
            {
                PacketHandler.INSTANCE.sendToServer(new MessageAttachTrailer(this.getEntityId(), Minecraft.getMinecraft().player.getEntityId()));
                return true;
            }
        }
        return EntityRaytracer.IEntityRaytraceable.super.processHit(result, rightClick);
    }

    @Override
    public boolean isStorageItem(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemDye)
        {
            EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
            return dyeColor == EnumDyeColor.WHITE;
        }
        return false;
    }
}
