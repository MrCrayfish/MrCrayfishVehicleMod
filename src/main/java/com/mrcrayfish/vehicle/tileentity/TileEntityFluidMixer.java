package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.crafting.FluidExtract;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipes;
import com.mrcrayfish.vehicle.fluid.FluidTankMixerInput;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidMixer extends TileEntitySynced implements IInventory, ITickable
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    private FluidTank tankBlaze = new FluidTankMixerInput(Fluid.BUCKET_VOLUME * 5);
    private FluidTank tankEnderSap = new FluidTankMixerInput(Fluid.BUCKET_VOLUME * 5);
    private FluidTank tankFuelium = new FluidTank(Fluid.BUCKET_VOLUME * 10);

    public static final int FLUID_MAX_PROGRESS = 20 * 5;
    private static final int SLOT_FUEL = 0;
    private static final int SLOT_INGREDIENT = 1;

    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;

    private String customName;

    public TileEntityFluidMixer()
    {
        tankFuelium.setCanFill(false);
    }

    @Override
    public int getSizeInventory()
    {
        return 7;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : inventory)
        {
            if (!stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack stack = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!stack.isEmpty())
        {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        if(index == 0)
        {
            return TileEntityFurnace.getItemBurnTime(stack) > 0;
        }
        else if(index == 1)
        {
            Fluid fluidOne = tankBlaze.getFluid() != null ? tankBlaze.getFluid().getFluid() : null;
            Fluid fluidTwo = tankEnderSap.getFluid() != null ? tankEnderSap.getFluid().getFluid() : null;
            if(fluidOne != null && fluidTwo != null)
            {
                if(FluidMixerRecipes.getInstance().getRecipe(fluidOne, fluidTwo, stack) != null)
                {
                    return true;
                }
            }
            return FluidMixerRecipes.getInstance().isIngredient(stack);
        }
        return false;
    }

    @Override
    public int getField(int id)
    {
        switch(id)
        {
            case 0:
                return extractionProgress;
            case 1:
                return remainingFuel;
            case 2:
                return fuelMaxProgress;
            case 3:
                return tankBlaze.getFluidAmount();
            case 4:
                return tankEnderSap.getFluidAmount();
            case 5:
                return tankFuelium.getFluidAmount();
        }
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
        switch(id)
        {
            case 0:
                extractionProgress = value;
                break;
            case 1:
                remainingFuel = value;
                break;
            case 2:
                fuelMaxProgress = value;
                break;
            case 3:
                if(tankBlaze.getFluid() != null)
                    tankBlaze.getFluid().amount = value;
                break;
            case 4:
                if(tankEnderSap.getFluid() != null)
                    tankEnderSap.getFluid().amount = value;
                break;
            case 5:
                if(tankFuelium.getFluid() != null)
                    tankFuelium.getFluid().amount = value;
                break;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 6;
    }

    @Override
    public void clear()
    {
        inventory.clear();
    }

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            ItemStack ingredient = this.getStackInSlot(SLOT_INGREDIENT);
            if(tankBlaze.getFluid() != null && tankEnderSap.getFluid() != null && !ingredient.isEmpty())
            {
                FluidMixerRecipe recipe = FluidMixerRecipes.getInstance().getRecipe(tankBlaze.getFluid().getFluid(), tankEnderSap.getFluid().getFluid(), ingredient);
                if(recipe != null && canMix(recipe))
                {
                    ItemStack fuel = this.getStackInSlot(SLOT_FUEL);
                    if(!fuel.isEmpty() && TileEntityFurnace.getItemBurnTime(fuel) > 0 && remainingFuel == 0)
                    {
                        fuelMaxProgress = TileEntityFurnace.getItemBurnTime(fuel);
                        remainingFuel = fuelMaxProgress;
                        shrinkItem(SLOT_FUEL);
                    }

                    if(remainingFuel > 0 && canMix(recipe))
                    {
                        if(extractionProgress++ == FLUID_MAX_PROGRESS)
                        {
                            FluidExtract extract = FluidMixerRecipes.getInstance().getRecipeResult(recipe);
                            if(extract != null)
                            {
                                tankFuelium.fillInternal(extract.createStack(), true);
                                tankBlaze.drain(recipe.getFluidAmount(tankBlaze.getFluid().getFluid()), true);
                                tankEnderSap.drain(recipe.getFluidAmount(tankEnderSap.getFluid().getFluid()), true);
                                shrinkItem(SLOT_INGREDIENT);
                            }
                            extractionProgress = 0;
                        }
                    }
                    else
                    {
                        extractionProgress = 0;
                    }
                }
                else
                {
                    extractionProgress = 0;
                }
            }
            else
            {
                extractionProgress = 0;
            }

            if(remainingFuel > 0)
            {
                remainingFuel--;
            }
        }
    }

    private void shrinkItem(int index)
    {
        ItemStack stack = this.getStackInSlot(index);
        stack.shrink(1);
        if(stack.isEmpty())
        {
            this.setInventorySlotContents(index, ItemStack.EMPTY);
        }
    }

    public boolean canMix(FluidMixerRecipe recipe)
    {
        if(tankBlaze.getFluid() == null || tankEnderSap.getFluid() == null)
            return false;
        if(tankBlaze.getFluidAmount() < recipe.getFluidAmount(tankBlaze.getFluid().getFluid()))
            return false;
        if(tankEnderSap.getFluidAmount() < recipe.getFluidAmount(tankEnderSap.getFluid().getFluid()))
            return false;
        return tankFuelium.getFluidAmount() < tankFuelium.getCapacity();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        if(tag.hasKey("Items", Constants.NBT.TAG_LIST))
        {
            inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(tag, inventory);
        }
        if(tag.hasKey("CustomName", Constants.NBT.TAG_STRING))
        {
            customName = tag.getString("CustomName");
        }
        if(tag.hasKey("TankBlaze", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tagCompound = tag.getCompoundTag("TankBlaze");
            FluidUtils.fixEmptyTag(tagCompound);
            tankBlaze.readFromNBT(tagCompound);
        }
        if(tag.hasKey("TankEnderSap", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tagCompound = tag.getCompoundTag("TankEnderSap");
            FluidUtils.fixEmptyTag(tagCompound);
            tankEnderSap.readFromNBT(tagCompound);
        }
        if(tag.hasKey("TankFuelium", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tagCompound = tag.getCompoundTag("TankFuelium");
            FluidUtils.fixEmptyTag(tagCompound);
            tankFuelium.readFromNBT(tagCompound);
        }
        if(tag.hasKey("RemainingFuel", Constants.NBT.TAG_INT))
        {
            remainingFuel = tag.getInteger("RemainingFuel");
        }
        if(tag.hasKey("FuelMaxProgress", Constants.NBT.TAG_INT))
        {
            fuelMaxProgress = tag.getInteger("FuelMaxProgress");
        }
        if(tag.hasKey("ExtractionProgress", Constants.NBT.TAG_INT))
        {
            extractionProgress = tag.getInteger("ExtractionProgress");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        ItemStackHelper.saveAllItems(tag, inventory);

        if(this.hasCustomName())
        {
            tag.setString("CustomName", customName);
        }

        NBTTagCompound tagTankBlaze = new NBTTagCompound();
        tankBlaze.writeToNBT(tagTankBlaze);
        tag.setTag("TankBlaze", tagTankBlaze);

        NBTTagCompound tagTankEnderSap = new NBTTagCompound();
        tankEnderSap.writeToNBT(tagTankEnderSap);
        tag.setTag("TankEnderSap", tagTankEnderSap);

        NBTTagCompound tagTankFuelium = new NBTTagCompound();
        tankFuelium.writeToNBT(tagTankFuelium);
        tag.setTag("TankFuelium", tagTankFuelium);

        if(tag.hasKey("RemainingFuel", Constants.NBT.TAG_INT))
        {
            remainingFuel = tag.getInteger("RemainingFuel");
        }
        if(tag.hasKey("FuelMaxProgress", Constants.NBT.TAG_INT))
        {
            fuelMaxProgress = tag.getInteger("FuelMaxProgress");
        }
        if(tag.hasKey("ExtractionProgress", Constants.NBT.TAG_INT))
        {
            extractionProgress = tag.getInteger("ExtractionProgress");
        }
        tag.setInteger("RemainingFuel", remainingFuel);
        tag.setInteger("FuelMaxProgress", fuelMaxProgress);
        tag.setInteger("ExtractionProgress", extractionProgress);
        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());

        NBTTagCompound tagTankBlaze = new NBTTagCompound();
        tankBlaze.writeToNBT(tagTankBlaze);
        tag.setTag("TankBlaze", tagTankBlaze);

        NBTTagCompound tagTankEnderSap = new NBTTagCompound();
        tankEnderSap.writeToNBT(tagTankEnderSap);
        tag.setTag("TankEnderSap", tagTankEnderSap);

        NBTTagCompound tagTankFuelium = new NBTTagCompound();
        tankFuelium.writeToNBT(tagTankFuelium);
        tag.setTag("TankFuelium", tagTankFuelium);

        return tag;
    }

    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.fluid_mixer";
    }

    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            IBlockState state = world.getBlockState(pos);
            if(state.getPropertyKeys().contains(BlockRotatedObject.FACING))
            {
                EnumFacing blockFacing = state.getValue(BlockRotatedObject.FACING);
                if(facing == blockFacing.rotateYCCW())
                {
                    return true;
                }
                if(facing == blockFacing)
                {
                    return true;
                }
                return facing == blockFacing.rotateY();
            }
            return false;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            IBlockState state = world.getBlockState(pos);
            if(state.getPropertyKeys().contains(BlockRotatedObject.FACING))
            {
                EnumFacing blockFacing = state.getValue(BlockRotatedObject.FACING);
                if(facing == blockFacing.rotateYCCW())
                {
                    return (T) tankBlaze;
                }
                if(facing == blockFacing)
                {
                    return (T) tankEnderSap;
                }
                if(facing == blockFacing.rotateY())
                {
                    return (T) tankFuelium;
                }
            }
            return null;
        }
        return super.getCapability(capability, facing);
    }

    @Nullable
    public FluidStack getBlazeFluidStack()
    {
        return tankBlaze.getFluid();
    }

    @Nullable
    public FluidStack getEnderSapFluidStack()
    {
        return tankEnderSap.getFluid();
    }

    @Nullable
    public FluidStack getFueliumFluidStack()
    {
        return tankFuelium.getFluid();
    }

    public int getExtractionProgress()
    {
        return this.getField(0);
    }

    public int getRemainingFuel()
    {
        return this.getField(1);
    }

    public int getFuelMaxProgress()
    {
        return this.getField(2);
    }

    public int getBlazeLevel()
    {
        return this.getField(3);
    }

    public int getEnderSapLevel()
    {
        return this.getField(4);
    }

    public int getFueliumLevel()
    {
        return this.getField(5);
    }
}










