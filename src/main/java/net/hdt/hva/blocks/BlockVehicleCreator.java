package net.hdt.hva.blocks;

import com.mrcrayfish.vehicle.block.BlockObject;
import net.hdt.hva.tileentities.TileEntityVehicleCreator;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockVehicleCreator extends BlockObject {

    public BlockVehicleCreator() {
        super(Material.IRON, "vehicle_creator");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityVehicleCreator)
            {
                playerIn.displayGUIChest((TileEntityVehicleCreator)tileentity);
                playerIn.addStat(StatList.BEACON_INTERACTION);
            }

            return true;
        }
    }
}
