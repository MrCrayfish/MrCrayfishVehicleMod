package com.mrcrayfish.vehicle.block;

import com.google.common.base.Strings;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateBlock extends RotatedObjectBlock
{
    public static final List<ResourceLocation> REGISTERED_CRATES = new ArrayList<>();
    private static final VoxelShape PANEL = box(0, 0, 0, 16, 2, 16);

    public VehicleCrateBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL, DyeColor.LIGHT_GRAY).dynamicShape().noOcclusion().strength(1.5F, 5.0F));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        REGISTERED_CRATES.forEach(resourceLocation ->
        {
            CompoundNBT blockEntityTag = new CompoundNBT();
            blockEntityTag.putString("Vehicle", resourceLocation.toString());
            blockEntityTag.putInt("EngineTier", EngineTier.IRON.ordinal());
            blockEntityTag.putInt("WheelType", WheelType.STANDARD.ordinal());
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.put("BlockEntityTag", blockEntityTag);
            ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE.get());
            stack.setTag(itemTag);
            items.add(stack);
        });
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        TileEntity te = worldIn.getBlockEntity(pos);
        if(te instanceof VehicleCrateTileEntity && ((VehicleCrateTileEntity)te).isOpened())
            return PANEL;
        return VoxelShapes.block();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos)
    {
        return this.isBelowBlockTopSolid(reader, pos) && this.canOpen(reader, pos);
    }

    private boolean canOpen(IWorldReader reader, BlockPos pos)
    {
        for(Direction side : Direction.Plane.HORIZONTAL)
        {
            BlockPos adjacentPos = pos.relative(side);
            BlockState state = reader.getBlockState(adjacentPos);
            if(state.isAir(reader, pos))
                continue;
            if(!state.getMaterial().isReplaceable() || this.isBelowBlockTopSolid(reader, adjacentPos))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isBelowBlockTopSolid(IWorldReader reader, BlockPos pos)
    {
        return reader.getBlockState(pos.below()).isFaceSturdy(reader, pos.below(), Direction.UP);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(result.getDirection() == Direction.UP && playerEntity.getItemInHand(hand).getItem() == ModItems.WRENCH.get())
        {
            this.openCrate(world, pos, state, playerEntity);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack)
    {
        if(livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).isCreative())
        {
            this.openCrate(world, pos, state, livingEntity);
        }
    }

    private void openCrate(World world, BlockPos pos, BlockState state, LivingEntity placer)
    {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof VehicleCrateTileEntity && this.canOpen(world, pos))
        {
            if(world.isClientSide)
            {
                this.spawnCrateOpeningParticles((ClientWorld) world, pos, state);
            }
            else
            {
                ((VehicleCrateTileEntity) tileEntity).open(placer.getUUID());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnCrateOpeningParticles(ClientWorld world, BlockPos pos, BlockState state)
    {
        double y = 0.875;
        double x, z;
        DiggingParticle.Factory factory = new DiggingParticle.Factory();
        for(int j = 0; j < 4; ++j)
        {
            for(int l = 0; l < 4; ++l)
            {
                x = (j + 0.5D) / 4.0D;
                z = (l + 0.5D) / 4.0D;
                Minecraft.getInstance().particleEngine.add(factory.createParticle(new BlockParticleData(ParticleTypes.BLOCK, state), world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, x - 0.5D, y - 0.5D, z - 0.5D));
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new VehicleCrateTileEntity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        ITextComponent vehicleName = EntityType.PIG.getDescription();
        CompoundNBT tagCompound = stack.getTag();
        if(tagCompound != null)
        {
            if(tagCompound.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                CompoundNBT blockEntityTag = tagCompound.getCompound("BlockEntityTag");
                String entityType = blockEntityTag.getString("Vehicle");
                if(!Strings.isNullOrEmpty(entityType))
                {
                    vehicleName = EntityType.byString(entityType).orElse(EntityType.PIG).getDescription();
                    list.add(vehicleName.copy().withStyle(TextFormatting.BLUE));
                }
            }
        }

        if(Screen.hasShiftDown())
        {
            list.addAll(RenderUtil.lines(new TranslationTextComponent(this.getDescriptionId() + ".info", vehicleName), 150));
        }
        else
        {
            list.add(new TranslationTextComponent("vehicle.info_help").withStyle(TextFormatting.YELLOW));
        }
    }

    //TODO turn this into a builder
    public static ItemStack create(ResourceLocation entityId, int color, @Nullable EngineTier engineTier, @Nullable WheelType wheelType, int wheelColor)
    {
        CompoundNBT blockEntityTag = new CompoundNBT();
        blockEntityTag.putString("Vehicle", entityId.toString());
        blockEntityTag.putInt("Color", color);

        if(engineTier != null)
        {
            blockEntityTag.putInt("EngineTier", engineTier.ordinal());
        }

        if(wheelType != null)
        {
            blockEntityTag.putInt("WheelType", wheelType.ordinal());
            if(wheelColor != -1)
            {
                blockEntityTag.putInt("WheelColor", wheelColor);
            }
        }

        CompoundNBT itemTag = new CompoundNBT();
        itemTag.put("BlockEntityTag", blockEntityTag);
        ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE.get());
        stack.setTag(itemTag);
        return stack;
    }

    public static void registerVehicle(ResourceLocation id)
    {
        if(!REGISTERED_CRATES.contains(id))
        {
            REGISTERED_CRATES.add(id);
            Collections.sort(REGISTERED_CRATES);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        if(!world.isClientSide && !player.isCreative())
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof VehicleCrateTileEntity)
            {
                ItemStack drop = new ItemStack(Item.byBlock(this));

                CompoundNBT tileEntityTag = new CompoundNBT();
                tileEntity.save(tileEntityTag);
                tileEntityTag.remove("x");
                tileEntityTag.remove("y");
                tileEntityTag.remove("z");
                tileEntityTag.remove("id");

                CompoundNBT compound = new CompoundNBT();
                compound.put("BlockEntityTag", tileEntityTag);
                drop.setTag(compound);

                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
