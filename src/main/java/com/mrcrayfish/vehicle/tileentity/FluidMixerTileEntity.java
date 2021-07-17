package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidExtractorBlock;
import com.mrcrayfish.vehicle.block.FluidMixerBlock;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.RecipeType;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.util.InventoryUtil;
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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class FluidMixerTileEntity extends TileEntitySynced implements IInventory, ITickableTileEntity, INamedContainerProvider, IFluidTankWriter
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    private FluidTank tankBlaze = new FluidTank(Config.SERVER.mixerInputCapacity.get(), this::isValidFluid);
    private FluidTank tankEnderSap = new FluidTank(Config.SERVER.mixerInputCapacity.get(), this::isValidFluid);
    private FluidTank tankFuelium = new FluidTank(Config.SERVER.mixerOutputCapacity.get(), stack -> stack.getFluid() == ModFluids.FUELIUM.get());

    private static final int SLOT_FUEL = 0;
    public static final int SLOT_INGREDIENT = 1;

    private FluidMixerRecipe currentRecipe = null;
    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;
    private boolean mixing = false;

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

        public int getCount()
        {
            return 9;
        }
    };

    public FluidMixerTileEntity()
    {
        super(ModTileEntities.FLUID_MIXER.get());
    }

    @Override
    public int getContainerSize()
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
    public ItemStack getItem(int index)
    {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        ItemStack stack = ItemStackHelper.removeItem(this.inventory, index, count);
        if(!stack.isEmpty())
        {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return ItemStackHelper.takeItem(this.inventory, index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);
        if(stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return this.level.getBlockEntity(this.worldPosition) == this && player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        if(index == 0)
        {
            return ForgeHooks.getBurnTime(stack) > 0;
        }
        else if(index == 1)
        {
            return this.isValidIngredient(stack);
        }
        return false;
    }

    @Override
    public void clearContent()
    {
        this.inventory.clear();
    }

    @Override
    public void tick()
    {
        if(this.level != null && !this.level.isClientSide())
        {
            ItemStack ingredient = this.getItem(SLOT_INGREDIENT);
            ItemStack fuel = this.getItem(SLOT_FUEL);

            if(this.currentRecipe == null && !ingredient.isEmpty())
            {
                this.currentRecipe = this.getRecipe().orElse(null);
            }
            else if(!this.canMix(this.currentRecipe))
            {
                this.currentRecipe = null;
                this.extractionProgress = 0;
            }

            if(this.canMix(this.currentRecipe))
            {
                this.updateFuel(fuel);

                if(this.remainingFuel > 0)
                {
                    this.setMixing(true);

                    if(this.extractionProgress++ == Config.SERVER.mixerMixTime.get())
                    {
                        FluidMixerRecipe recipe = this.currentRecipe;
                        this.tankFuelium.fill(recipe.getResult().createStack(), IFluidHandler.FluidAction.EXECUTE);
                        this.tankBlaze.drain(recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        this.tankEnderSap.drain(recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        this.shrinkItem(SLOT_INGREDIENT);
                        this.extractionProgress = 0;
                        this.currentRecipe = null;
                    }
                }
                else
                {
                    this.extractionProgress = 0;
                    this.setMixing(false);
                }
            }
            else
            {
                this.extractionProgress = 0;
                this.setMixing(false);
            }

            if(this.remainingFuel > 0)
            {
                this.remainingFuel--;
                this.updateFuel(fuel);

                // Updates the enabled state of the fluid extractor
                if(this.remainingFuel == 0)
                {
                    this.setMixing(false);
                }
            }
        }
    }

    private void updateFuel(ItemStack fuel)
    {
        if(!fuel.isEmpty() && ForgeHooks.getBurnTime(fuel) > 0 && this.remainingFuel == 0 && this.canMix(this.currentRecipe))
        {
            this.fuelMaxProgress = ForgeHooks.getBurnTime(fuel);
            this.remainingFuel = this.fuelMaxProgress;
            this.shrinkItem(SLOT_FUEL);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canMix()
    {
        ItemStack ingredient = this.getItem(SLOT_INGREDIENT);
        if(!ingredient.isEmpty() && !this.tankBlaze.getFluid().isEmpty() && !this.tankEnderSap.getFluid().isEmpty())
        {
            if(this.currentRecipe == null)
            {
                this.currentRecipe = this.getRecipe().orElse(null);
            }
        }
        else
        {
            this.currentRecipe = null;
        }
        return this.currentRecipe != null && this.canMix(this.currentRecipe) && this.remainingFuel >= 0;
    }

    private void shrinkItem(int index)
    {
        ItemStack stack = this.getItem(index);
        stack.shrink(1);
        if(stack.isEmpty())
        {
            this.setItem(index, ItemStack.EMPTY);
        }
    }

    private boolean canMix(@Nullable FluidMixerRecipe recipe)
    {
        if(recipe == null)
            return false;
        ItemStack ingredient = this.getItem(SLOT_INGREDIENT);
        if(ingredient.getItem() != recipe.getIngredient().getItem())
            return false;
        if(this.tankBlaze.getFluid().isEmpty())
            return false;
        if(this.tankEnderSap.getFluid().isEmpty())
            return false;
        if(this.tankBlaze.getFluidAmount() < recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid()))
            return false;
        if(this.tankEnderSap.getFluidAmount() < recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid()))
            return false;
        if(this.tankFuelium.getFluidAmount() >= this.tankFuelium.getCapacity())
            return false;
        return this.tankFuelium.getFluidAmount() + recipe.getResult().getAmount() <= this.tankFuelium.getCapacity();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound)
    {
        super.load(state, compound);
        if(compound.contains("Items", Constants.NBT.TAG_LIST))
        {
            this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
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
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);

        ItemStackHelper.saveAllItems(compound, this.inventory);

        if(this.hasCustomName())
        {
            compound.putString("CustomName", this.customName);
        }

        this.writeTanks(compound);

        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putInt("FuelMaxProgress", this.fuelMaxProgress);
        compound.putInt("ExtractionProgress", this.extractionProgress);
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT tag = super.save(new CompoundNBT());
        this.writeTanks(tag);
        return tag;
    }

    @Override
    public void writeTanks(CompoundNBT compound)
    {
        CompoundNBT tagTankBlaze = new CompoundNBT();
        this.tankBlaze.writeToNBT(tagTankBlaze);
        compound.put("TankBlaze", tagTankBlaze);

        CompoundNBT tagTankEnderSap = new CompoundNBT();
        this.tankEnderSap.writeToNBT(tagTankEnderSap);
        compound.put("TankEnderSap", tagTankEnderSap);

        CompoundNBT tagTankFuelium = new CompoundNBT();
        this.tankFuelium.writeToNBT(tagTankFuelium);
        compound.put("TankFuelium", tagTankFuelium);
    }

    @Override
    public boolean areTanksEmpty()
    {
        return this.tankBlaze.isEmpty() && this.tankEnderSap.isEmpty() && this.tankFuelium.isEmpty();
    }

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

    public Optional<FluidMixerRecipe> getRecipe()
    {
        return this.level.getRecipeManager().getRecipeFor(RecipeType.FLUID_MIXER, this, this.level);
    }

    private boolean isValidIngredient(ItemStack ingredient)
    {
        List<FluidMixerRecipe> recipes = this.level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == RecipeType.FLUID_MIXER).map(recipe -> (FluidMixerRecipe) recipe).collect(Collectors.toList());
        return recipes.stream().anyMatch(recipe -> InventoryUtil.areItemStacksEqualIgnoreCount(ingredient, recipe.getIngredient()));
    }

    private boolean isValidFluid(FluidStack stack)
    {
        List<FluidMixerRecipe> recipes = this.level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == RecipeType.FLUID_MIXER).map(recipe -> (FluidMixerRecipe) recipe).collect(Collectors.toList());
        return recipes.stream().anyMatch(recipe ->
        {
            for(FluidEntry entry : recipe.getInputs())
            {
                if(entry.getFluid() == stack.getFluid())
                {
                    return true;
                }
            }
            return false;
        });
    }

    public FluidTank getEnderSapTank()
    {
        return tankEnderSap;
    }

    public FluidTank getBlazeTank()
    {
        return tankBlaze;
    }

    public FluidTank getFueliumTank()
    {
        return tankFuelium;
    }

    private final net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(this::createUnSidedHandler);

    @Nonnull
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction facing)
    {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if(state.getProperties().contains(RotatedObjectBlock.DIRECTION))
            {
                Direction direction = state.getValue(RotatedObjectBlock.DIRECTION);
                if(facing == direction.getCounterClockWise())
                {
                    return LazyOptional.of(() -> this.tankBlaze).cast();
                }
                if(facing == direction)
                {
                    return LazyOptional.of(() -> this.tankEnderSap).cast();
                }
                if(facing == direction.getClockWise())
                {
                    return LazyOptional.of(() -> this.tankFuelium).cast();
                }
            }
            return LazyOptional.empty();
        }
        else if(!this.remove && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return this.itemHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

    private void setMixing(boolean state)
    {
        if(this.mixing != state)
        {
            this.mixing = state;
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(FluidMixerBlock.ENABLED, state), Constants.BlockFlags.DEFAULT);
        }
    }
}









