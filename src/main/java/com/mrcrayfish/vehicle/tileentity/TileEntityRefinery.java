package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockRefinery;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
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
public class TileEntityRefinery extends TileFluidHandler implements IInventory, ITickable
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    public static final int TANK_CAPACITY = 1000 * 5;
    public static final int ETHANOL_MAX_PROGRESS = 20 * 5;
    public static final int FUELIUM_MAX_PROGRESS = 20 * 2;
    private static final int SLOT_WATER_BUCKET = 0;
    private static final int SLOT_ETHANOL_SOURCE = 1;
    private static final int SLOT_ETHANOL_FUEL = 2;
    private static final int SLOT_OIL_SOURCE = 3;

    private int remainingEthanolFuel;
    private int ethanolProgress;
    private int fueliumProgress;
    private int waterLevel;
    private int ethanolLevel;
    private int ethanolMaxProgess;

    private String customName;

    public TileEntityRefinery()
    {
        tank = new FluidTank(TANK_CAPACITY)
        {
            @Override
            public boolean canFillFluidType(FluidStack fluid)
            {
                return fluid.getFluid() == ModFluids.FUELIUM;
            }

            @Override
            public boolean canDrain()
            {
                return super.canDrain();
            }
        };
        tank.setCanFill(false);
    }

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            ItemStack waterBucket = this.getStackInSlot(SLOT_WATER_BUCKET);
            if(!waterBucket.isEmpty())
            {
                if(waterBucket.getItem() == Items.WATER_BUCKET)
                {
                    if(waterLevel < TANK_CAPACITY)
                    {
                        waterLevel += 1000;
                        if(waterLevel > TANK_CAPACITY)
                        {
                            waterLevel = TANK_CAPACITY;
                        }
                        Item containerItem = waterBucket.getItem().getContainerItem();
                        if(containerItem != null)
                        {
                            ItemStack containerStack = new ItemStack(containerItem);
                            this.setInventorySlotContents(SLOT_WATER_BUCKET, containerStack);
                        }
                    }
                }
            }

            if(canMakeEthanol())
            {
                ItemStack fuel = this.getStackInSlot(SLOT_ETHANOL_FUEL);
                if((remainingEthanolFuel == 0 || ethanolMaxProgess == 0) && !fuel.isEmpty() && TileEntityFurnace.getItemBurnTime(fuel) > 0)
                {
                    ItemStack source = this.getStackInSlot(SLOT_ETHANOL_SOURCE);
                    if(!source.isEmpty() && TileEntityFurnace.getItemBurnTime(source) > 0)
                    {
                        ethanolMaxProgess = TileEntityFurnace.getItemBurnTime(source);
                        remainingEthanolFuel = TileEntityFurnace.getItemBurnTime(fuel);
                        shrinkItem(SLOT_ETHANOL_FUEL);
                    }
                }

                if(remainingEthanolFuel > 0)
                {
                    ethanolProgress++;
                    waterLevel--;
                    if(ethanolProgress >= ethanolMaxProgess)
                    {
                        ethanolLevel += ethanolMaxProgess / 2;
                        ethanolProgress = 0;
                        shrinkItem(SLOT_ETHANOL_SOURCE);
                    }
                }
            }
            else
            {
                ethanolProgress = 0;
                ethanolMaxProgess = 0;
            }

            if(remainingEthanolFuel > 0)
            {
                remainingEthanolFuel--;
            }

            if(canMakeFuelium())
            {
                fueliumProgress++;
                ethanolLevel--;
                if(fueliumProgress == FUELIUM_MAX_PROGRESS)
                {
                    fueliumProgress = 0;
                    tank.fillInternal(new FluidStack(ModFluids.FUELIUM, 20), true);
                    shrinkItem(getOilItemIndex());
                    syncFueliumAmountToClients();
                }
            }
            else
            {
                fueliumProgress = 0;
            }
        }
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
        if(index == 0 && stack.getItem() == Items.WATER_BUCKET)
        {
            return true;
        }
        else if(index == 1 || index == 2)
        {
            return TileEntityFurnace.getItemBurnTime(stack) > 0;
        }
        else if(index >= 3 && index <= 6)
        {
            return stack.getItem() == Items.WHEAT_SEEDS;
        }
        return false;
    }

    @Override
    public int getField(int id)
    {
        switch(id)
        {
            case 0:
                return ethanolProgress;
            case 1:
                return fueliumProgress;
            case 2:
                return ethanolLevel;
            case 3:
                return waterLevel;
            case 4:
                return remainingEthanolFuel;
            case 5:
                return tank.getFluidAmount();
            case 6:
                return ethanolMaxProgess;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
        switch(id)
        {
            case 0:
                ethanolProgress = value;
                break;
            case 1:
                fueliumProgress = value;
                break;
            case 2:
                ethanolLevel = value;
                break;
            case 3:
                waterLevel = value;
                break;
            case 4:
                remainingEthanolFuel = value;
                break;
            case 5:
                if(tank.getFluid() != null)
                    tank.getFluid().amount = value;
                else
                    tank.setFluid(new FluidStack(ModFluids.FUELIUM, value));
                break;
            case 6:
                ethanolMaxProgess = value;
                break;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 7;
    }

    @Override
    public void clear()
    {
        inventory.clear();
    }

    private int getOilItemIndex()
    {
        for(int i = 0; i < 4; i ++)
        {
            ItemStack source = this.getStackInSlot(SLOT_OIL_SOURCE + i);
            if(!source.isEmpty() && source.getItem() == Items.WHEAT_SEEDS)
            {
                return SLOT_OIL_SOURCE + i;
            }
        }
        return -1;
    }

    private boolean canMakeEthanol()
    {
        ItemStack source = this.getStackInSlot(SLOT_ETHANOL_SOURCE);
        if(!source.isEmpty() && TileEntityFurnace.getItemBurnTime(source) > 0)
        {
            return waterLevel >= 100;
        }
        return false;
    }

    private boolean canMakeFuelium()
    {
        return ethanolLevel > 0 && getOilItemIndex() != -1 && tank.getFluidAmount() < TANK_CAPACITY;
    }

    public int getEthanolProgress()
    {
        return this.getField(0);
    }

    public int getFueliumProgress()
    {
        return this.getField(1);
    }

    public int getEthanolLevel()
    {
        return this.getField(2);
    }

    public int getWaterLevel()
    {
        return this.getField(3);
    }

    public int getRemainingEthanolFuel()
    {
        return this.getField(4);
    }

    public int getFueliumLevel()
    {
        return this.getField(5);
    }

    public int getEthanolMaxProgress()
    {
        return this.getField(6);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if(tag.hasKey("ethanolProgress", Constants.NBT.TAG_INT))
        {
            ethanolProgress = tag.getInteger("ethanolProgress");
        }
        if(tag.hasKey("fueliumProgress", Constants.NBT.TAG_INT))
        {
            fueliumProgress = tag.getInteger("fueliumProgress");
        }
        if(tag.hasKey("ethanolLevel", Constants.NBT.TAG_INT))
        {
            ethanolLevel = tag.getInteger("ethanolLevel");
        }
        if(tag.hasKey("waterLevel", Constants.NBT.TAG_INT))
        {
            waterLevel = tag.getInteger("waterLevel");
        }
        if(tag.hasKey("remainingEthanolFuel", Constants.NBT.TAG_INT))
        {
            remainingEthanolFuel = tag.getInteger("remainingEthanolFuel");
        }
        if(tag.hasKey("ethanolMaxProgess", Constants.NBT.TAG_INT))
        {
            ethanolMaxProgess = tag.getInteger("ethanolMaxProgess");
        }
        if(tag.hasKey("fueliumLevel", Constants.NBT.TAG_INT))
        {
            if(tank.getFluid() != null)
            {
                tank.getFluid().amount = tag.getInteger("fueliumLevel");
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
        tag.setInteger("ethanolProgress", ethanolProgress);
        tag.setInteger("fueliumProgress", fueliumProgress);
        tag.setInteger("ethanolLevel", ethanolLevel);
        tag.setInteger("waterLevel", waterLevel);
        tag.setInteger("remainingEthanolFuel", remainingEthanolFuel);
        tag.setInteger("ethanolMaxProgress", ethanolMaxProgess);

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

    public void syncFueliumAmountToClients()
    {
        sendUpdate(wrap("fueliumLevel", tank.getFluidAmount()));
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
        return this.hasCustomName() ? this.customName : "container.fuelium_refinery";
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
            if(state.getValue(BlockRefinery.FACING).getOpposite() == facing)
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
