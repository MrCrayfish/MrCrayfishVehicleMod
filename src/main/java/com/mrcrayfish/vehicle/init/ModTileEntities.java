package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.*;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities
{
    public static final TileEntityType<FluidExtractorTileEntity> FLUID_EXTRACTOR = null;
    public static final TileEntityType<FluidPipeTileEntity> FLUID_PIPE = null;
    public static final TileEntityType<FluidPumpTileEntity> FLUID_PUMP = null;
    public static final TileEntityType<FuelDrumTileEntity> FUEL_DRUM = null;
    public static final TileEntityType<IndustrialFuelDrumTileEntity> INDUSTRIAL_FUEL_DRUM = null;
    public static final TileEntityType<FluidMixerTileEntity> FLUID_MIXER = null;
    public static final TileEntityType<VehicleCrateTileEntity> VEHICLE_CRATE = null;
    public static final TileEntityType<WorkstationTileEntity> WORKSTATION = null;
    public static final TileEntityType<JackTileEntity> JACK = null;
    public static final TileEntityType<BoostTileEntity> BOOST = null;
    public static final TileEntityType<GasPumpTileEntity> GAS_PUMP = null;
    public static final TileEntityType<GasPumpTankTileEntity> GAS_PUMP_TANK = null;

    private static <T extends TileEntity> TileEntityType<T> buildType(String id, TileEntityType.Builder<T> builder)
    {
        TileEntityType<T> type = builder.build(null); //TODO may not allow null
        type.setRegistryName(id);
        return type;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerTypes(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(buildType(Names.TileEntity.FLUID_EXTRACTOR, TileEntityType.Builder.create(FluidExtractorTileEntity::new, ModBlocks.FLUID_EXTRACTOR)));
        registry.register(buildType(Names.TileEntity.FLUID_PIPE, TileEntityType.Builder.create(FluidPipeTileEntity::new, ModBlocks.FLUID_PIPE)));
        registry.register(buildType(Names.TileEntity.FLUID_PUMP, TileEntityType.Builder.create(FluidPumpTileEntity::new, ModBlocks.FLUID_PUMP)));
        registry.register(buildType(Names.TileEntity.FLUID_MIXER, TileEntityType.Builder.create(FluidMixerTileEntity::new, ModBlocks.FLUID_MIXER)));
        registry.register(buildType(Names.TileEntity.FUEL_DRUM, TileEntityType.Builder.create(FuelDrumTileEntity::new, ModBlocks.FUEL_DRUM)));
        registry.register(buildType(Names.TileEntity.INDUSTRIAL_FUEL_DRUM, TileEntityType.Builder.create(IndustrialFuelDrumTileEntity::new, ModBlocks.INDUSTRIAL_FUEL_DRUM)));
        registry.register(buildType(Names.TileEntity.VEHICLE_CRATE, TileEntityType.Builder.create(VehicleCrateTileEntity::new, ModBlocks.VEHICLE_CRATE)));
        registry.register(buildType(Names.TileEntity.WORKSTATION, TileEntityType.Builder.create(WorkstationTileEntity::new, ModBlocks.WORKSTATION)));
        registry.register(buildType(Names.TileEntity.JACK, TileEntityType.Builder.create(JackTileEntity::new, ModBlocks.JACK)));
        registry.register(buildType(Names.TileEntity.BOOST, TileEntityType.Builder.create(BoostTileEntity::new, ModBlocks.BOOST_PAD, ModBlocks.BOOST_RAMP, ModBlocks.STEEP_BOOST_RAMP)));
        registry.register(buildType(Names.TileEntity.GAS_PUMP, TileEntityType.Builder.create(GasPumpTileEntity::new, ModBlocks.GAS_PUMP)));
        registry.register(buildType(Names.TileEntity.GAS_PUMP_TANK, TileEntityType.Builder.create(GasPumpTankTileEntity::new, ModBlocks.GAS_PUMP)));
    }
}
