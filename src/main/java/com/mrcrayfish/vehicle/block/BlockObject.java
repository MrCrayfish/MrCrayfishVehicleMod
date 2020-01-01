package com.mrcrayfish.vehicle.block;

import net.minecraft.block.Block;

/**
 * Author: MrCrayfish
 */
public class BlockObject extends Block
{
    public BlockObject(String id, Block.Properties properties)
    {
        super(properties);
        this.setRegistryName(id);
    }
}
