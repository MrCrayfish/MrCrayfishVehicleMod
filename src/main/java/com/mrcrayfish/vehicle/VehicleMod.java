package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.CustomDataSerializers;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.*;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import com.mrcrayfish.vehicle.proxy.Proxy;
import com.mrcrayfish.vehicle.proxy.ServerProxy;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class VehicleMod
{
    public static final Proxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final ItemGroup CREATIVE_TAB = new ItemGroup("tabVehicle")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModItems.WOOD_SMALL_ENGINE.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items)
        {
            super.fill(items);

            /* Fill all jerry cans to their capacity */
            items.forEach(stack ->
            {
                if(stack.getItem() instanceof JerryCanItem)
                {
                    JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
                    jerryCan.fill(stack, jerryCan.getCapacity(stack));
                }
                else if(stack.getItem() instanceof SprayCanItem)
                {
                    SprayCanItem sprayCan = (SprayCanItem) stack.getItem();
                    sprayCan.refill(stack);
                }
            });

            /* Adds vehicle creates the the creative inventory */
            BlockVehicleCrate.REGISTERED_CRATES.forEach(resourceLocation ->
            {
                CompoundNBT blockEntityTag = new CompoundNBT();
                blockEntityTag.putString("Vehicle", resourceLocation.toString());
                blockEntityTag.putInt("EngineTier", EngineTier.WOOD.ordinal());
                blockEntityTag.putInt("WheelType", WheelType.STANDARD.ordinal());
                CompoundNBT itemTag = new CompoundNBT();
                itemTag.put("BlockEntityTag", blockEntityTag);
                ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE.get());
                stack.setTag(itemTag);
                items.add(stack);
            });
        }
    };

    public VehicleMod()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModEntities.ENTITY_TYPES.register(eventBus);
        ModTileEntities.TILE_ENTITY_TYPES.register(eventBus);
        ModContainers.CONTAINER_TYPES.register(eventBus);
        ModSounds.SOUNDS.register(eventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        CustomDataSerializers.register();
        HeldVehicleDataHandler.register();
        VehicleProperties.register();
        ItemLookup.init();
        ModLootFunctions.register();
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        PROXY.setupClient();
    }
}
