package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockFluidExtractor;
import com.mrcrayfish.vehicle.crafting.FluidExtract;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipes;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidExtractor extends TileFluidHandler implements IInventory, ITickable
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    public static final int TANK_CAPACITY = 1000 * 5;
    public static final int FLUID_MAX_PROGRESS = 20 * 30;
    private static final int SLOT_FUEL_SOURCE = 0;
    private static final int SLOT_FLUID_SOURCE = 1;

    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;

    private String customName;

    public TileEntityFluidExtractor()
    {
        tank = new FluidTank(TANK_CAPACITY);
        tank.setCanFill(false);
        tank.setCanDrain(true);
    }

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            ItemStack source = this.getStackInSlot(SLOT_FLUID_SOURCE);
            ItemStack fuel = this.getStackInSlot(SLOT_FUEL_SOURCE);
            if(!fuel.isEmpty() && !source.isEmpty() && remainingFuel == 0 && canFillWithFluid(source))
            {
                fuelMaxProgress = TileEntityFurnace.getItemBurnTime(fuel);
                remainingFuel = fuelMaxProgress;
                shrinkItem(SLOT_FUEL_SOURCE);
            }

            if(!source.isEmpty() && canFillWithFluid(source) && remainingFuel > 0)
            {
                if(extractionProgress++ == FLUID_MAX_PROGRESS)
                {
                    FluidExtract extract = FluidExtractorRecipes.getInstance().getRecipeResult(source);
                    if(extract != null) tank.fillInternal(extract.createStack(), true);
                    extractionProgress = 0;
                    shrinkItem(SLOT_FLUID_SOURCE);
                    sendUpdate(wrap("fluidLevel", tank.getFluidAmount()));
                }
            }
            else
            {
                extractionProgress = 0;
            }

            if(remainingFuel > 0 && canFillWithFluid(source))
            {
                remainingFuel--;
            }
        }
    }

    private boolean canFillWithFluid(ItemStack stack)
    {
        if(!stack.isEmpty() && tank.getFluidAmount() < tank.getCapacity())
        {
            FluidExtract extract = getFluidExtractSource();
            if(extract != null)
            {
                return tank.getFluid() == null || extract.getFluid() == tank.getFluid().getFluid();
            }
        }
        return false;
    }

    @Nullable
    public FluidStack getFluidStackTank()
    {
        return tank.getFluid();
    }

    @Nullable
    public FluidExtract getFluidExtractSource()
    {
        return FluidExtractorRecipes.getInstance().getRecipeResult(this.getStackInSlot(SLOT_FLUID_SOURCE));
    }

    @Override
    public int getSizeInventory()
    {
        return 2;
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
            return FluidExtractorRecipes.getInstance().getRecipeResult(stack) != null;
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
                return tank.getFluidAmount();
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
                if(tank.getFluid() != null)
                    tank.getFluid().amount = value;
                break;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 4;
    }

    @Override
    public void clear()
    {
        inventory.clear();
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

    public int getFluidLevel()
    {
        return this.getField(3);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);

        if(tag.hasKey("ExtractionProgress", Constants.NBT.TAG_INT))
        {
            extractionProgress = tag.getInteger("ExtractionProgress");
        }
        if(tag.hasKey("RemainingFuel", Constants.NBT.TAG_INT))
        {
            remainingFuel = tag.getInteger("RemainingFuel");
        }
        if(tag.hasKey("FuelMaxProgress", Constants.NBT.TAG_INT))
        {
            fuelMaxProgress = tag.getInteger("FuelMaxProgress");
        }
        if(tag.hasKey("FluidLevel", Constants.NBT.TAG_INT))
        {
            if(tank.getFluid() != null)
            {
                tank.getFluid().amount = tag.getInteger("FluidLevel");
            }
        }
        if(tag.hasKey("Items", Constants.NBT.TAG_LIST))
        {
            inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(tag, inventory);
        }
        if(tag.hasKey("CustomName", Constants.NBT.TAG_STRING))
        {
            customName = tag.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("ExtractionProgress", extractionProgress);
        tag.setInteger("RemainingFuel", remainingFuel);
        tag.setInteger("FuelMaxProgress", fuelMaxProgress);

        ItemStackHelper.saveAllItems(tag, inventory);

        if(this.hasCustomName())
        {
            tag.setString("CustomName", customName);
        }

        return tag;
    }

    private void sendUpdate(NBTTagCompound tag)
    {
        if(!world.isRemote)
        {
            if(world instanceof WorldServer)
            {
                WorldServer server = (WorldServer) world;
                PlayerChunkMapEntry entry = server.getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4);
                if(entry != null)
                {
                    NBTTagCompound tagCompound = super.writeToNBT(new NBTTagCompound());
                    tagCompound.merge(tag);
                    SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(pos, 0, tagCompound);
                    entry.sendPacket(packet);
                }
            }
        }
    }

    public void syncFluidLevelToClients()
    {
        sendUpdate(wrap("FluidLevel", tank.getFluidAmount()));
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.fluid_extractor";
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

    private NBTTagCompound wrap(String key, int value)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(key, value);
        return tag;
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

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            IBlockState state = world.getBlockState(pos);
            if(state.getValue(BlockFluidExtractor.FACING).getOpposite() == facing)
            {
                return false;
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(!hasCapability(capability, facing))
        {
            return null;
        }
        return super.getCapability(capability, facing);
    }
}
