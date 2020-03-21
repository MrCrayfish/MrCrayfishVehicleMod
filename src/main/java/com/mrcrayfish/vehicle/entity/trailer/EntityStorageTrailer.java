package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.EntityTrailer;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class EntityStorageTrailer extends EntityTrailer implements EntityRaytracer.IEntityRaytraceable, IStorage
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(-6 * 0.0625, 4.2 * 0.0625, 9 * 0.0625, 6 * 0.0625, 8.3 * 0.0625F, 17 * 0.0625, 1.1));
    private static final EntityRaytracer.RayTracePart CHEST_BOX = new EntityRaytracer.RayTracePart(new AxisAlignedBB(-0.4375, 0.475, -0.4375, 0.4375, 1.34, 0.4375));
    private static final Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> interactionBoxMapStatic = Maps.newHashMap();

    static
    {
        if(FMLCommonHandler.instance().getSide().isClient())
        {
            interactionBoxMapStatic.put(CONNECTION_BOX, EntityRaytracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
            interactionBoxMapStatic.put(CHEST_BOX, EntityRaytracer.boxToTriangles(CHEST_BOX.getBox(), null));
        }
    }

    private StorageInventory inventory;

    public EntityStorageTrailer(World worldIn)
    {
        super(worldIn);
        this.initInventory();
    }

    @Override
    public double getHitchOffset()
    {
        return -16.0 * 1.1;
    }

    @Override
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
    @SideOnly(Side.CLIENT)
    public Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public List<EntityRaytracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return ImmutableList.of(CONNECTION_BOX, CHEST_BOX);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
        RenderGlobal.drawSelectionBoundingBox(CHEST_BOX.getBox(), 0, 1, 0, 0.4F);
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
            else if(result.getPartHit() == CHEST_BOX)
            {
                PacketHandler.INSTANCE.sendToServer(new MessageOpenStorage(this.getEntityId()));
                Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                return true;
            }
        }
        return EntityRaytracer.IEntityRaytraceable.super.processHit(result, rightClick);
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
    public boolean canTowTrailer()
    {
        return true;
    }
}
