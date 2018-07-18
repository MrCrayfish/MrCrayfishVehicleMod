package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.block.BlockJack;
import com.mrcrayfish.vehicle.block.BlockObject;
import com.mrcrayfish.vehicle.block.BlockTrafficCone;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final Block TRAFFIC_CONE;
    public static final Block JACK;
    public static final Block JACK_HEAD;

    static
    {
        TRAFFIC_CONE = new BlockTrafficCone();
        JACK = new BlockJack();
        JACK_HEAD = new BlockObject(Material.WOOD, "jack_head");
    }

    public static void register()
    {
        registerBlock(TRAFFIC_CONE, new ItemTrafficCone(TRAFFIC_CONE));
        registerBlock(JACK);
        registerBlock(JACK_HEAD, null);
    }

    private static void registerBlock(Block block)
    {
        registerBlock(block, new ItemBlock(block));
    }

    private static void registerBlock(Block block, ItemBlock item)
    {
        if(block.getRegistryName() == null)
            throw new IllegalArgumentException("A block being registered does not have a registry name and could be successfully registered.");

        RegistrationHandler.Blocks.add(block);

        if(item != null)
        {
            item.setRegistryName(block.getRegistryName());
            RegistrationHandler.Items.add(item);
        }
    }
}
