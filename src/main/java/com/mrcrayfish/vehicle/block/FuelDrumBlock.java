package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FuelDrumBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    private static final VoxelShape[] SHAPE = {
            Block.box(0, 1, 1, 16, 15, 15),
            Block.box(1, 0, 1, 15, 16, 15),
            Block.box(1, 1, 0, 15, 15, 16)
    };

    public FuelDrumBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL).strength(1.0F));
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(INVERTED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE[state.getValue(AXIS).ordinal()];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE[state.getValue(AXIS).ordinal()];
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        if(Screen.hasShiftDown())
        {
            list.addAll(RenderUtil.lines(new TranslationTextComponent(ModBlocks.FUEL_DRUM.get().getDescriptionId() + ".info"), 150));
        }
        else
        {
            CompoundNBT tag = stack.getTag();
            if(tag != null && tag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                CompoundNBT blockEntityTag = tag.getCompound("BlockEntityTag");
                if(blockEntityTag.contains("FluidName", Constants.NBT.TAG_STRING))
                {
                    String fluidName = blockEntityTag.getString("FluidName");
                    Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
                    int amount = blockEntityTag.getInt("Amount");
                    if(fluid != null && amount > 0)
                    {
                        list.add(new TranslationTextComponent(fluid.getAttributes().getTranslationKey()).withStyle(TextFormatting.BLUE));
                        list.add(new StringTextComponent(amount + " / " + this.getCapacity() + "mb").withStyle(TextFormatting.GRAY));
                    }
                }
            }
            list.add(new TranslationTextComponent("vehicle.info_help").withStyle(TextFormatting.YELLOW));
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(!world.isClientSide())
        {
            if(FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getDirection()))
            {
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        switch(rotation)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch(state.getValue(AXIS))
                {
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
        builder.add(INVERTED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        boolean inverted = context.getClickedFace().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis()).setValue(INVERTED, inverted);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    public int getCapacity()
    {
        return Config.SERVER.fuelDrumCapacity.get();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new FuelDrumTileEntity();
    }
}
