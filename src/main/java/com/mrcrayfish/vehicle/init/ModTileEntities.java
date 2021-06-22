package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<TileEntityType<FluidExtractorTileEntity>> FLUID_EXTRACTOR = register("fluid_extractor", FluidExtractorTileEntity::new, () -> new Block[]{ModBlocks.FLUID_EXTRACTOR.get()});
    public static final RegistryObject<TileEntityType<PipeTileEntity>> FLUID_PIPE = register("fluid_pipe", PipeTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PIPE.get()});
    public static final RegistryObject<TileEntityType<PumpTileEntity>> FLUID_PUMP = register("fluid_pump", PumpTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PUMP.get()});
    public static final RegistryObject<TileEntityType<FuelDrumTileEntity>> FUEL_DRUM = register("fuel_drum", FuelDrumTileEntity::new, () -> new Block[]{ModBlocks.FUEL_DRUM.get()});
    public static final RegistryObject<TileEntityType<IndustrialFuelDrumTileEntity>> INDUSTRIAL_FUEL_DRUM = register("industrial_fuel_drum", IndustrialFuelDrumTileEntity::new, () -> new Block[]{ModBlocks.INDUSTRIAL_FUEL_DRUM.get()});
    public static final RegistryObject<TileEntityType<FluidMixerTileEntity>> FLUID_MIXER = register("fluid_mixer", FluidMixerTileEntity::new, () -> new Block[]{ModBlocks.FLUID_MIXER.get()});
    public static final RegistryObject<TileEntityType<VehicleCrateTileEntity>> VEHICLE_CRATE = register("vehicle_crate", VehicleCrateTileEntity::new, () -> new Block[]{ModBlocks.VEHICLE_CRATE.get()});
    public static final RegistryObject<TileEntityType<WorkstationTileEntity>> WORKSTATION = register("workstation", WorkstationTileEntity::new, () -> new Block[]{ModBlocks.WORKSTATION.get()});
    public static final RegistryObject<TileEntityType<JackTileEntity>> JACK = register("jack", JackTileEntity::new, () -> new Block[]{ModBlocks.JACK.get()});
    public static final RegistryObject<TileEntityType<BoostTileEntity>> BOOST = register("boost", BoostTileEntity::new, () -> new Block[]{});
    public static final RegistryObject<TileEntityType<GasPumpTileEntity>> GAS_PUMP = register("gas_pump", GasPumpTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});
    public static final RegistryObject<TileEntityType<GasPumpTankTileEntity>> GAS_PUMP_TANK = register("gas_pump_tank", GasPumpTankTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String id, Supplier<T> factoryIn, Supplier<Block[]> validBlocksSupplier)
    {
        return REGISTER.register(id, () -> TileEntityType.Builder.of(factoryIn, validBlocksSupplier.get()).build(null));
    }
}