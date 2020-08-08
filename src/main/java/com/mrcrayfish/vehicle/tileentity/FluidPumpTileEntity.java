package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockFluidPump;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FluidPumpTileEntity extends FluidPipeTileEntity
{
    private PowerMode powerMode;

    public FluidPumpTileEntity()
    {
        super(ModTileEntities.FLUID_PUMP.get(), Config.SERVER.pumpCapacity.get());
        this.transferAmount = Config.SERVER.pumpTransferAmount.get();
        this.powerMode = PowerMode.REQUIRES_SIGNAL_ON;
    }

    public void cyclePowerMode(PlayerEntity player)
    {
        this.powerMode = PowerMode.values()[(this.powerMode.ordinal() + 1) % PowerMode.values().length];
        this.powerMode.notifyPlayerOfChange(player);
        this.syncFluidToClient();
    }

    @Override
    public void tick()
    {
        if(this.powerMode != PowerMode.ALWAYS_ACTIVE && (this.world.isBlockPowered(this.pos) != (this.powerMode == PowerMode.REQUIRES_SIGNAL_ON)))
            return;

        BlockState state = this.world.getBlockState(this.pos);
        Direction facing = state.get(BlockFluidPump.DIRECTION);

        List<IFluidHandler> fluidHandlers = new ArrayList<>();
        for(Direction face : Direction.values())
        {
            if(!this.getDisabledConnections()[face.getIndex()] && state.get(BlockFluidPump.CONNECTED_PIPES[face.getIndex()]))
            {
                IFluidHandler handler = this.getConnectedFluidHandler(face);
                if (handler != null)
                {
                    fluidHandlers.add(handler);
                }
            }
        }

        int outputCount = fluidHandlers.size();
        if(outputCount == 0)
            return;

        IFluidHandler handler = this.getConnectedFluidHandler(facing.getOpposite());
        if (handler != null)
        {
            FluidUtils.transferFluid(handler, this.tank, this.transferAmount);
        }

        // Return and transfer full amount if one connection
        if (outputCount == 1)
        {
            FluidUtils.transferFluid(this.tank, fluidHandlers.get(0), this.transferAmount);
            return;
        }

        // Evenly distribute truncated proportion to all connections
        int remainder = Math.min(this.tank.getFluidAmount(), this.transferAmount * outputCount);
        int amount = remainder / outputCount;
        if(amount > 0)
        {
            fluidHandlers.removeIf(iFluidHandler -> FluidUtils.transferFluid(this.tank, iFluidHandler, amount) < amount);
        }

        // Randomly distribute to the remaining non-full connections the proportion that would otherwise be lost in the above truncation
        remainder %= outputCount;
        if(fluidHandlers.size() == 1)
        {
            FluidUtils.transferFluid(this.tank, fluidHandlers.get(0), remainder);
        }

        int filled;
        for(int i = 0; i < remainder && !fluidHandlers.isEmpty(); i++)
        {
            int index = this.world.rand.nextInt(fluidHandlers.size());
            filled = FluidUtils.transferFluid(this.tank, fluidHandlers.get(index), 1);
            remainder -= filled;
            if(filled == 0)
            {
                fluidHandlers.remove(index);
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
        if(compound.contains("PowerMode", Constants.NBT.TAG_INT))
        {
            this.powerMode = PowerMode.fromOrdinal(compound.getInt("PowerMode"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putInt("PowerMode", this.powerMode.ordinal());
        return super.write(compound);
    }

    public enum PowerMode
    {
        ALWAYS_ACTIVE("always"),
        REQUIRES_SIGNAL_ON("on"),
        REQUIRES_SIGNAL_OFF("off");

        private static final String LANG_KEY_CHAT_PREFIX = Reference.MOD_ID + ".chat.pump.power";
        private String langKeyChat;

        PowerMode(String langKeyChat)
        {
            this.langKeyChat = langKeyChat;
        }

        public void notifyPlayerOfChange(PlayerEntity player)
        {
            player.sendStatusMessage(new TranslationTextComponent(LANG_KEY_CHAT_PREFIX, new TranslationTextComponent(LANG_KEY_CHAT_PREFIX + "." + this.langKeyChat)), true);
        }

        @Nullable
        public static PowerMode fromOrdinal(int ordinal)
        {
            if(ordinal < 0 || ordinal >= values().length)
                return null;
            return values()[ordinal];
        }
    }
}
