package com.mrcrayfish.vehicle.entity.vehicle;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTracePart;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.EntityRaytracer.TriangleRayTraceList;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.entity.IChest;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;

import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageVehicleChest;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class EntityMoped extends EntityMotorcycle implements IEntityRaytraceable, IChest
{
    private static final DataParameter<Boolean> CHEST = EntityDataManager.createKey(EntityMoped.class, DataSerializers.BOOLEAN);
    private static final RayTracePart CHEST_BOX = new RayTracePart(new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375));
    private static final RayTracePart TRAY_BOX = new RayTracePart(createScaledBoundingBox(-4 * 0.0625, 8 * 0.0625 + 0.1, -4.5 * 0.0625, 4 * 0.0625, 9F * 0.0625F + 0.1, -12.5 * 0.0625, 1.2));
    private static final Map<RayTracePart, TriangleRayTraceList> interactionBoxMapStatic = Maps.newHashMap();

    static
    {
        interactionBoxMapStatic.put(CHEST_BOX, EntityRaytracer.boxToTriangles(CHEST_BOX.getBox(), null));
        interactionBoxMapStatic.put(TRAY_BOX, EntityRaytracer.boxToTriangles(TRAY_BOX.getBox(), null));
    }

    private InventoryBasic inventory;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    @SideOnly(Side.CLIENT)
    public ItemStack mudGuard;

    public EntityMoped(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(12F);
        this.setTurnSensitivity(15);
        this.setMaxTurnAngle(45);
        this.setHeldOffset(new Vec3d(7D, 2D, 0D));
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(CHEST, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        body = new ItemStack(ModItems.MOPED_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        handleBar = new ItemStack(ModItems.MOPED_HANDLE_BAR);
        mudGuard = new ItemStack(ModItems.MOPED_MUD_GUARD);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                int color = this.dataManager.get(COLOR);
                this.setPartColor(handleBar, color);
                this.setPartColor(mudGuard, color);
            }
        }
    }

    private void setPartColor(ItemStack stack, int color)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("color", color);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.MOPED_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.MOPED_ENGINE_STEREO;
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
    public boolean shouldRenderEngine()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return 8.5 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("chest", Constants.NBT.TAG_BYTE))
        {
            this.setChest(compound.getBoolean("chest"));
            if(compound.hasKey("inventory", Constants.NBT.TAG_LIST))
            {
                this.initInventory();
                InventoryUtil.readInventoryToNBT(compound, "inventory", inventory);
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        boolean hasChest = this.hasChest();
        compound.setBoolean("chest", hasChest);

        if(hasChest && inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "inventory", inventory);
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
    @SideOnly(Side.CLIENT)
    public boolean processHit(RayTraceResultRotated result)
    {
        RayTracePart partHit = result.getPartHit();
        if(partHit == CHEST_BOX && this.hasChest())
        {
            PacketHandler.INSTANCE.sendToServer(new MessageVehicleChest(this.getEntityId()));
            return true;
        }
        else if(partHit == TRAY_BOX && !this.hasChest())
        {
            PacketHandler.INSTANCE.sendToServer(new MessageAttachChest(this.getEntityId()));
            return true;
        }
        return IEntityRaytraceable.super.processHit(result);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Map<RayTracePart, TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<RayTracePart> getApplicableInteractionBoxes()
    {
        List<RayTracePart> boxes = Lists.newArrayList();
        if (hasChest())
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
    @SideOnly(Side.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        if (hasChest())
        {
            RenderGlobal.drawSelectionBoundingBox(CHEST_BOX.getBox(), 0, 1, 0, 0.4F);
        }
        else
        {
            RenderGlobal.drawSelectionBoundingBox(TRAY_BOX.getBox(), 0, 1, 0, 0.4F);
        }

    }

    private void initInventory()
    {
        InventoryBasic original = inventory;
        inventory = new InventoryBasic(this.getName(), false, 27);
        // Copies the inventory if it exists already over to the new instance
        if(original != null)
        {
            for(int i = 0; i < original.getSizeInventory(); i++)
            {
                ItemStack stack = original.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    inventory.setInventorySlotContents(i, stack.copy());
                }
            }
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn)
    {
        if(this.hasChest() && inventory != null)
        {
            InventoryHelper.dropInventoryItems(world, this, inventory);
        }
    }

    @Nullable
    @Override
    public IInventory getChest()
    {
        if(this.hasChest() && inventory == null)
        {
            this.initInventory();
        }
        return inventory;
    }

    @Override
    public void attachChest(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))
        {
            this.setChest(true);
            this.initInventory();

            NBTTagCompound itemTag = stack.getTagCompound();
            if(itemTag != null)
            {
                NBTTagCompound blockEntityTag = itemTag.getCompoundTag("BlockEntityTag");
                if(!blockEntityTag.hasNoTags() && blockEntityTag.hasKey("Items", Constants.NBT.TAG_LIST))
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

    private static AxisAlignedBB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }
}
