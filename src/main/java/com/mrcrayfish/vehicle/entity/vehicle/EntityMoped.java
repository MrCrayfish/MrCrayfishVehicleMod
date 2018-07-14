package com.mrcrayfish.vehicle.entity.vehicle;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTracePart;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.client.EntityRaytracer.TriangleRayTraceList;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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

/**
 * Author: MrCrayfish
 */
public class EntityMoped extends EntityMotorcycle implements IEntityRaytraceable
{
    private static final DataParameter<Boolean> CHEST = EntityDataManager.createKey(EntityMoped.class, DataSerializers.BOOLEAN);
    private static final AxisAlignedBB CHEST_BOX = new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375);
    private static final Map<RayTracePart, TriangleRayTraceList> interactionBoxMapStatic = Maps.<RayTracePart, TriangleRayTraceList>newHashMap();

    static
    {
        interactionBoxMapStatic.put(new RayTracePart(CHEST_BOX), EntityRaytracer.boxToTriangles(CHEST_BOX, null));
    }

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
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("chest", this.hasChest());
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
    public boolean processHit(RayTraceResultRotated result)
    {
        if (result.getPartHit().getBox() != null)
        {
            //TODO open moped inventory GUI

            //TODO debug code - delete this code and this comment before release
            {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("open moped chest GUI").setStyle(new Style().setColor(TextFormatting.values()[Minecraft.getMinecraft().world.rand.nextInt(15) + 1])));
            }
            return true;
        }
        return IEntityRaytraceable.super.processHit(result);
    }

    @Override
    public Map<RayTracePart, TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Override
    public List<AxisAlignedBB> getApplicableInteractionBoxes()
    {
        List<AxisAlignedBB> boxes = Lists.<AxisAlignedBB>newArrayList();
        if (hasChest())
        {
            boxes.add(CHEST_BOX);
        }
        return boxes;
    }

    @Override
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        if (hasChest())
        {
            RenderGlobal.drawSelectionBoundingBox(CHEST_BOX, 0, 1, 0, 0.4F);
        }
    }
}
