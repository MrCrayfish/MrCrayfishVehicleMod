package com.mrcrayfish.vehicle.entity.vehicle;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.ClientEvents.IEntityRaytraceBoxProvider;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
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
public class EntityMoped extends EntityMotorcycle implements IEntityRaytraceBoxProvider
{
    private static final DataParameter<Boolean> CHEST = EntityDataManager.createKey(EntityMoped.class, DataSerializers.BOOLEAN);
    private static final AxisAlignedBB CHEST_BOX = new AxisAlignedBB(-0.31875, 0.7945, -0.978125, 0.31875, 1.4195, -0.34375);
    private static final AxisAlignedBB[] OCCLUSION_BOXES = new AxisAlignedBB[]{new AxisAlignedBB(-0.3, 0.202, 0.0225, 0.3, 0.307, 0.8125),
        new AxisAlignedBB(-0.3, 0.1945, 0.81, 0.3, 1.357, 0.9975), new AxisAlignedBB(-0.075, 0.307, 0.125, 0.075, 0.382, 0.8254),
        new AxisAlignedBB(-0.1875, 0.1945, -0.1275, 0.1875, 0.7195, 0.1725), new AxisAlignedBB(-0.2025, 0.7195, -0.34375, 0.2025, 0.8695, 0.22595),
        new AxisAlignedBB(-0.2625, 0.307, -0.6754, -0.1875, 0.6445, -0.075), new AxisAlignedBB(0.1875, 0.307, -0.6754, 0.26256, 0.6445, -0.07594),
        new AxisAlignedBB(-0.1875, 0.307, -0.7125, 0.1875, 0.7195, -0.125), new AxisAlignedBB(-0.3, 0.7195, -0.9375, 0.3, 0.7945, -0.34375),
        new AxisAlignedBB(-0.25625, 1.26325, 0.71625, 0.25, 1.33200, 0.81), new AxisAlignedBB(0.20625, 1.262, 0.675, 0.3, 1.33075, 0.76875),
        new AxisAlignedBB(-0.30625, 1.26325, 0.675, -0.2125, 1.332, 0.76875), new AxisAlignedBB(0.25, 1.262, 0.625, 0.34375, 1.33075, 0.71875),
        new AxisAlignedBB(-0.35625, 1.262, 0.625, -0.2625, 1.33075, 0.71875), new AxisAlignedBB(0.3, 1.262, 0.58125, 0.39375, 1.33075, 0.675),
        new AxisAlignedBB(-0.40625, 1.262, 0.58125, -0.3125, 1.33075, 0.675), new AxisAlignedBB(0.34375, 1.262, 0.53125, 0.4375, 1.33075, 0.625),
        new AxisAlignedBB(-0.5, 1.262, 0.4875, -0.40625, 1.33075, 0.58125), new AxisAlignedBB(0.39375, 1.262, 0.4875, 0.48750, 1.33075, 0.58125),
        new AxisAlignedBB(-0.45, 1.262, 0.53125, -0.35625, 1.33075, 0.625)};

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
    public Entity getEntity()
    {
        return this;
    }

    @Override
    public List<AxisAlignedBB> getApplicableBoxes()
    {
        List<AxisAlignedBB> boxes = Lists.<AxisAlignedBB>newArrayList();
        boxes.addAll(Arrays.<AxisAlignedBB>asList(OCCLUSION_BOXES));
        if (this.hasChest())
        {
            boxes.add(CHEST_BOX);
        }
        return boxes;
    }

    @Override
    public boolean processHit(AxisAlignedBB boxHit)
    {
        if (boxHit.equals(CHEST_BOX))
        {
            //TODO open moped (lookObject.entityHit) inventory GUI
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("open chest GUI").setStyle(new Style().setColor(TextFormatting.values()[Minecraft.getMinecraft().world.rand.nextInt(15) + 1])));//debug
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawBoxes()
    {
        for (AxisAlignedBB box : OCCLUSION_BOXES)
        {
            RenderGlobal.drawSelectionBoundingBox(box, 1, 0, 0, 1);
        }
        RenderGlobal.drawSelectionBoundingBox(CHEST_BOX, 0, 1, 0, 1);
    }
}
