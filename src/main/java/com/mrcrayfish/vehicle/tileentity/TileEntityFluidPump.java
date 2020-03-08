package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.block.BlockFluidPump;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidPump extends TileEntityFluidPipe
{
    private PowerMode powerMode;

    public TileEntityFluidPump()
    {
        this.transferAmount = VehicleConfig.SERVER.pumpTransferAmount;
        this.powerMode = PowerMode.RQUIRES_SIGNAL_ON;
    }

    public enum PowerMode
    {
        ALWAYS_ACTIVE("always"),
        RQUIRES_SIGNAL_ON("on"),
        REQUIRES_SIGNAL_OFF("off");

        private static final String LANG_KEY_CHAT_PREFIX = Reference.MOD_ID + ".chat.pump.power";
        private String langKeyChat;

        PowerMode(String langKeyChat)
        {
            this.langKeyChat = langKeyChat;
        }

        public void notifyPlayerOfChange(EntityPlayer player)
        {
            player.sendMessage(new TextComponentTranslation(LANG_KEY_CHAT_PREFIX, new TextComponentTranslation(LANG_KEY_CHAT_PREFIX + "." + langKeyChat)));
        }
    }

    public void cyclePowerMode(EntityPlayer player)
    {
        powerMode = PowerMode.values()[(powerMode.ordinal() + 1) % PowerMode.values().length];
        powerMode.notifyPlayerOfChange(player);
        syncToClient();
    }

    @Override
    public void update()
    {
        if(powerMode != PowerMode.ALWAYS_ACTIVE && (world.isBlockPowered(pos) != (powerMode == PowerMode.RQUIRES_SIGNAL_ON)))
            return;

        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockFluidPump.FACING);

        // Collect connections
        state = state.getActualState(world, pos);
        List<IFluidHandler> fluidHandlers = new ArrayList<>();
        for(EnumFacing face : EnumFacing.VALUES)
        {
            if(!disabledConnections[face.getIndex()] && state.getValue(BlockFluidPump.CONNECTED_PIPES[face.getIndex()]))
            {
                IFluidHandler handler = getConnectedFluidHandler(face);
                if (handler != null)
                {
                    fluidHandlers.add(handler);
                }
            }
        }

        int outputCount = fluidHandlers.size();
        if(outputCount == 0)
            return;

        IFluidHandler handler = getConnectedFluidHandler(facing.getOpposite());
        if (handler != null)
        {
            FluidUtils.transferFluid(handler, tank, transferAmount);
        }

        // Return and transfer full amount if one connection
        if (outputCount == 1)
        {
            FluidUtils.transferFluid(tank, fluidHandlers.get(0), transferAmount);
            return;
        }

        // Evenly distribute truncated proportion to all connections
        int remainder = Math.min(tank.getFluidAmount(), transferAmount * outputCount);
        int amount = remainder / outputCount;
        if(amount > 0)
        {
            fluidHandlers.removeIf(iFluidHandler -> FluidUtils.transferFluid(tank, iFluidHandler, amount) < amount);
        }

        // Randomly distribute to the remaining non-full connections the proportion that would otherwise be lost in the above truncation
        remainder %= outputCount;
        if(fluidHandlers.size() == 1)
        {
            FluidUtils.transferFluid(tank, fluidHandlers.get(0), remainder);
        }

        int filled;
        for(int i = 0; i < remainder && !fluidHandlers.isEmpty(); i++)
        {
            int index = world.rand.nextInt(fluidHandlers.size());
            filled = FluidUtils.transferFluid(tank, fluidHandlers.get(index), 1);
            remainder -= filled;
            if(filled == 0)
            {
                fluidHandlers.remove(index);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);
        powerMode = PowerMode.values()[tag.getInteger("power_mode")];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("power_mode", powerMode.ordinal());
        return tag;
    }
}
