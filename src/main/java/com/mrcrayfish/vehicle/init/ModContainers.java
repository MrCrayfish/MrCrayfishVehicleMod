package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.inventory.container.*;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers
{
    public static final ContainerType<FluidExtractorContainer> FLUID_EXTRACTOR = build(Names.Container.FLUID_EXTRACTOR, (IContainerFactory<FluidExtractorContainer>) (windowId, playerInventory, data) -> {
        FluidExtractorTileEntity fluidExtractor = (FluidExtractorTileEntity) playerInventory.player.world.getTileEntity(data.readBlockPos());
        return new FluidExtractorContainer(windowId, playerInventory, fluidExtractor);
    });
    public static final ContainerType<FluidMixerContainer> FLUID_MIXER = build(Names.Container.FLUID_MIXER, (IContainerFactory<FluidMixerContainer>) (windowId, playerInventory, data) -> {
        FluidMixerTileEntity fluidMixer = (FluidMixerTileEntity) playerInventory.player.world.getTileEntity(data.readBlockPos());
        return new FluidMixerContainer(windowId, playerInventory, fluidMixer);
    });
    public static final ContainerType<EditVehicleContainer> EDIT_VEHICLE = build(Names.Container.EDIT_VEHICLE, (IContainerFactory<EditVehicleContainer>) (windowId, playerInventory, data) -> {
        PoweredVehicleEntity entity = (PoweredVehicleEntity) playerInventory.player.world.getEntityByID(data.readInt());
        return new EditVehicleContainer(windowId, entity.getVehicleInventory(), entity, playerInventory.player, playerInventory);
    });
    public static final ContainerType<WorkstationContainer> WORKSTATION = build(Names.Container.WORKSTATION, (IContainerFactory<WorkstationContainer>) (windowId, playerInventory, data) -> {
        WorkstationTileEntity workstation = (WorkstationTileEntity) playerInventory.player.world.getTileEntity(data.readBlockPos());
        return new WorkstationContainer(windowId, playerInventory, workstation);
    });
    public static final ContainerType<StorageContainer> STORAGE = build(Names.Container.STORAGE, (IContainerFactory<StorageContainer>) (windowId, playerInventory, data) -> {
        IStorage storage = (IStorage) playerInventory.player.world.getEntityByID(data.readVarInt());
        return new StorageContainer(windowId, playerInventory, storage.getInventory(), playerInventory.player);
    });

    private static <T extends Container> ContainerType<T> build(String key, ContainerType.IFactory<T> factory)
    {
        ContainerType<T> type = new ContainerType<>(factory);
        type.setRegistryName(key);
        return type;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerTypes(final RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(FLUID_EXTRACTOR);
        registry.register(FLUID_MIXER);
        registry.register(EDIT_VEHICLE);
        registry.register(WORKSTATION);
        registry.register(STORAGE);
    }
}
