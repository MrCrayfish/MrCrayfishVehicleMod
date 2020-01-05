package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.crafting.FluidExtract;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipes;
import com.mrcrayfish.vehicle.fluid.FluidTankMixerInput;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FluidMixerTileEntity extends TileEntitySynced implements IInventory, ITickableTileEntity, INamedContainerProvider
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    private FluidTank tankBlaze = new FluidTankMixerInput(FluidAttributes.BUCKET_VOLUME * 5);
    private FluidTank tankEnderSap = new FluidTankMixerInput(FluidAttributes.BUCKET_VOLUME * 5);
    private FluidTank tankFuelium = new FluidTank(FluidAttributes.BUCKET_VOLUME * 10, stack -> stack.getFluid() == ModFluids.FUELIUM);

    public static final int FLUID_MAX_PROGRESS = 20 * 5;
    private static final int SLOT_FUEL = 0;
    private static final int SLOT_INGREDIENT = 1;

    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;

    private String customName;

    protected final IIntArray fluidMixerData = new IIntArray()
    {
        public int get(int index)
        {
            switch(index)
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
                case 6:
                    return tankBlaze.getFluid().getFluid().getRegistryName().hashCode();
                case 7:
                    return tankEnderSap.getFluid().getFluid().getRegistryName().hashCode();
                case 8:
                    return tankFuelium.getFluid().getFluid().getRegistryName().hashCode();
            }
            return 0;
        }

        public void set(int index, int value)
        {
            switch(index)
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
                    if(!tankBlaze.isEmpty() || tankBlaze.getFluid().getRawFluid() != Fluids.EMPTY)
                    {
                        tankBlaze.getFluid().setAmount(value);
                    }
                    break;
                case 4:
                    if(!tankEnderSap.isEmpty() || tankEnderSap.getFluid().getRawFluid() != Fluids.EMPTY)
                    {
                        tankEnderSap.getFluid().setAmount(value);
                    }
                    break;
                case 5:
                    if(!tankFuelium.isEmpty() || tankFuelium.getFluid().getRawFluid() != Fluids.EMPTY)
                    {
                        tankFuelium.getFluid().setAmount(value);
                    }
                    break;
                case 6:
                    updateFluid(tankBlaze, value);
                    break;
                case 7:
                    updateFluid(tankEnderSap, value);
                    break;
                case 8:
                    updateFluid(tankFuelium, value);
                    break;
            }
        }

        public int size()
        {
            return 9;
        }
    };

    public FluidMixerTileEntity()
    {
        super(ModTileEntities.FLUID_MIXER);
    }

    @Override
    public int getSizeInventory()
    {
        return 7;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : this.inventory)
        {
            if(!stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack stack = ItemStackHelper.getAndSplit(this.inventory, index, count);
        if(!stack.isEmpty())
        {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);
        if(stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        if(index == 0)
        {
            return ForgeHooks.getBurnTime(stack) > 0;
        }
        else if(index == 1)
        {
            Fluid fluidOne = this.tankBlaze.getFluid().getFluid();
            Fluid fluidTwo = this.tankEnderSap.getFluid().getFluid();
            if(fluidOne != Fluids.EMPTY && fluidTwo != Fluids.EMPTY)
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
    public void clear()
    {
        this.inventory.clear();
    }

    @Override
    public void tick()
    {
        if(!this.world.isRemote)
        {
            ItemStack ingredient = this.getStackInSlot(SLOT_INGREDIENT);
            if(!this.tankBlaze.getFluid().isEmpty() && !this.tankEnderSap.getFluid().isEmpty() && !ingredient.isEmpty())
            {
                FluidMixerRecipe recipe = FluidMixerRecipes.getInstance().getRecipe(this.tankBlaze.getFluid().getFluid(), this.tankEnderSap.getFluid().getFluid(), ingredient);
                if(recipe != null && this.canMix(recipe))
                {
                    ItemStack fuel = this.getStackInSlot(SLOT_FUEL);
                    if(!fuel.isEmpty() && ForgeHooks.getBurnTime(fuel) > 0 && this.remainingFuel == 0)
                    {
                        this.fuelMaxProgress = ForgeHooks.getBurnTime(fuel);
                        this.remainingFuel = this.fuelMaxProgress;
                        this.shrinkItem(SLOT_FUEL);
                    }

                    if(this.remainingFuel > 0 && this.canMix(recipe))
                    {
                        if(this.extractionProgress++ == FLUID_MAX_PROGRESS)
                        {
                            FluidExtract extract = FluidMixerRecipes.getInstance().getRecipeResult(recipe);
                            if(extract != null)
                            {
                                this.tankFuelium.fill(extract.createStack(), IFluidHandler.FluidAction.EXECUTE);
                                this.tankBlaze.drain(recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                                this.tankEnderSap.drain(recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                                this.shrinkItem(SLOT_INGREDIENT);
                            }
                            this.extractionProgress = 0;
                        }
                    }
                    else
                    {
                        this.extractionProgress = 0;
                    }
                }
                else
                {
                    this.extractionProgress = 0;
                }
            }
            else
            {
                this.extractionProgress = 0;
            }

            if(this.remainingFuel > 0)
            {
                this.remainingFuel--;
            }
        }
    }

    public boolean canMix()
    {
        ItemStack ingredient = this.getStackInSlot(SLOT_INGREDIENT);
        if(!ingredient.isEmpty() && !this.tankBlaze.getFluid().isEmpty() && !this.tankEnderSap.getFluid().isEmpty())
        {
            FluidMixerRecipe recipe = FluidMixerRecipes.getInstance().getRecipe(this.tankBlaze.getFluid().getFluid(), this.tankEnderSap.getFluid().getFluid(), ingredient);
            return recipe != null && this.canMix(recipe) && this.remainingFuel > 0;
        }
        return false;
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
        if(this.tankBlaze.getFluid().isEmpty())
            return false;
        if(this.tankEnderSap.getFluid().isEmpty())
            return false;
        if(this.tankBlaze.getFluidAmount() < recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid()))
        {
            return false;
        }
        if(this.tankEnderSap.getFluidAmount() < recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid()))
        {
            return false;
        }
        return this.tankFuelium.getFluidAmount() < this.tankFuelium.getCapacity();
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        if(compound.contains("Items", Constants.NBT.TAG_LIST))
        {
            this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compound, this.inventory);
        }
        if(compound.contains("CustomName", Constants.NBT.TAG_STRING))
        {
            this.customName = compound.getString("CustomName");
        }
        if(compound.contains("TankBlaze", Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT tagCompound = compound.getCompound("TankBlaze");
            //FluidUtils.fixEmptyTag(tagCompound); //TODO might not need
            this.tankBlaze.readFromNBT(tagCompound);
        }
        if(compound.contains("TankEnderSap", Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT tagCompound = compound.getCompound("TankEnderSap");
            //FluidUtils.fixEmptyTag(tagCompound);
            this.tankEnderSap.readFromNBT(tagCompound);
        }
        if(compound.contains("TankFuelium", Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT tagCompound = compound.getCompound("TankFuelium");
            //FluidUtils.fixEmptyTag(tagCompound);
            this.tankFuelium.readFromNBT(tagCompound);
        }
        if(compound.contains("RemainingFuel", Constants.NBT.TAG_INT))
        {
            this.remainingFuel = compound.getInt("RemainingFuel");
        }
        if(compound.contains("FuelMaxProgress", Constants.NBT.TAG_INT))
        {
            this.fuelMaxProgress = compound.getInt("FuelMaxProgress");
        }
        if(compound.contains("ExtractionProgress", Constants.NBT.TAG_INT))
        {
            this.extractionProgress = compound.getInt("ExtractionProgress");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);

        ItemStackHelper.saveAllItems(compound, this.inventory);

        if(this.hasCustomName())
        {
            compound.putString("CustomName", this.customName);
        }

        CompoundNBT tagTankBlaze = new CompoundNBT();
        this.tankBlaze.writeToNBT(tagTankBlaze);
        compound.put("TankBlaze", tagTankBlaze);

        CompoundNBT tagTankEnderSap = new CompoundNBT();
        this.tankEnderSap.writeToNBT(tagTankEnderSap);
        compound.put("TankEnderSap", tagTankEnderSap);

        CompoundNBT tagTankFuelium = new CompoundNBT();
        this.tankFuelium.writeToNBT(tagTankFuelium);
        compound.put("TankFuelium", tagTankFuelium);

        /*
        if(compound.contains("RemainingFuel", Constants.NBT.TAG_INT))
        {
            remainingFuel = compound.getInteger("RemainingFuel");
        }
        if(compound.contains("FuelMaxProgress", Constants.NBT.TAG_INT))
        {
            fuelMaxProgress = compound.getInteger("FuelMaxProgress");
        }
        if(compound.contains("ExtractionProgress", Constants.NBT.TAG_INT))
        {
            extractionProgress = compound.getInteger("ExtractionProgress");
        }*/

        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putInt("FuelMaxProgress", this.fuelMaxProgress);
        compound.putInt("ExtractionProgress", this.extractionProgress);
        return compound;
    }

    /*@Override
    public CompoundNBT getUpdateTag() //TODO might not need. Commenting out for now
    {
        CompoundNBT tag = super.write(new CompoundNBT());

        CompoundNBT tagTankBlaze = new CompoundNBT();
        tankBlaze.write(tagTankBlaze);
        tag.put("TankBlaze", tagTankBlaze);

        CompoundNBT tagTankEnderSap = new CompoundNBT();
        tankEnderSap.write(tagTankEnderSap);
        tag.put("TankEnderSap", tagTankEnderSap);

        CompoundNBT tagTankFuelium = new CompoundNBT();
        tankFuelium.write(tagTankFuelium);
        tag.put("TankFuelium", tagTankFuelium);

        return tag;
    }*/

    private String getName()
    {
        return this.hasCustomName() ? this.customName : "container.fluid_mixer";
    }

    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new StringTextComponent(this.getName()) : new TranslationTextComponent(this.getName());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction facing)
    {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            BlockState state = this.world.getBlockState(this.pos);
            if(state.getProperties().contains(BlockRotatedObject.DIRECTION))
            {
                Direction direction = state.get(BlockRotatedObject.DIRECTION);
                if(facing == direction.rotateYCCW())
                {
                    return LazyOptional.of(() -> this.tankBlaze).cast();
                }
                if(facing == direction)
                {
                    return LazyOptional.of(() -> this.tankEnderSap).cast();
                }
                if(facing == direction.rotateY())
                {
                    return LazyOptional.of(() -> this.tankFuelium).cast();
                }
            }
            return LazyOptional.empty();
        }
        return super.getCapability(cap, facing);
    }

    @Nullable
    public FluidStack getBlazeFluidStack()
    {
        return this.tankBlaze.getFluid();
    }

    @Nullable
    public FluidStack getEnderSapFluidStack()
    {
        return this.tankEnderSap.getFluid();
    }

    @Nullable
    public FluidStack getFueliumFluidStack()
    {
        return this.tankFuelium.getFluid();
    }

    public int getExtractionProgress()
    {
        return this.fluidMixerData.get(0);
    }

    public int getRemainingFuel()
    {
        return this.fluidMixerData.get(1);
    }

    public int getFuelMaxProgress()
    {
        return this.fluidMixerData.get(2);
    }

    public int getBlazeLevel()
    {
        return this.fluidMixerData.get(3);
    }

    public int getEnderSapLevel()
    {
        return this.fluidMixerData.get(4);
    }

    public int getFueliumLevel()
    {
        return this.fluidMixerData.get(5);
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new FluidMixerContainer(windowId, playerInventory, this);
    }

    public IIntArray getFluidMixerData()
    {
        return fluidMixerData;
    }

    public void updateFluid(FluidTank tank, int fluidHash)
    {
        Optional<Fluid> optional = ForgeRegistries.FLUIDS.getValues().stream().filter(fluid -> fluid.getRegistryName().hashCode() == fluidHash).findFirst();
        optional.ifPresent(fluid -> tank.setFluid(new FluidStack(fluid, tank.getFluidAmount())));
    }
}










