package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityHelicopter;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntitySofacopter extends EntityHelicopter implements EntityRaytracer.IEntityRaytraceable
{
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, 0.0625, 0, 0, 0, 1);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-2, 1.75, 8.25, 0, 0, 0, 0.45);
    public static final PartPosition KEY_PORT_POSITION = new PartPosition(-9.25, 8, 5, 0, 0, 0, 0.8);

    @SideOnly(Side.CLIENT)
    public ItemStack arm;

    @SideOnly(Side.CLIENT)
    public ItemStack blade;

    @SideOnly(Side.CLIENT)
    public ItemStack skid;

    public EntitySofacopter(World worldIn)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
        this.setBodyPosition(BODY_POSITION);
        this.setKeyHolePosition(KEY_PORT_POSITION);
        this.setFuelCapacity(40000F);
        this.setFuelConsumption(2.0F);

        this.dataManager.set(COLOR, 11546150);
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(Item.getByNameOrId("cfm:couch"), 1, 14);
        arm = new ItemStack(ModItems.COUCH_HELICOPTER_ARM);
        blade = new ItemStack(Item.getByNameOrId("cfm:ceiling_fan_fans"));
        skid = new ItemStack(ModItems.COUCH_HELICOPTER_SKID);
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.3125;
    }

    @Override
    public boolean canBeColored()
    {
        return false;
    }
}
