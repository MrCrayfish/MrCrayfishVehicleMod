package net.hdt.hva.items;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;

public class ItemModelTrain extends ItemPart {

    private Class<? extends EntityLandVehicle> entity;

    public ItemModelTrain(String name, Class<? extends EntityLandVehicle> entity) {
        super(name);
        this.entity = entity;
    }

    /*@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        if(worldIn.getBlockState(new BlockPos(playerIn.posX, playerIn.posY, playerIn.posZ)) == MTBlocks.STRAIGHT_TRACK.getDefaultState()) {
            try {
                worldIn.spawnEntity(entity.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            System.out.print(String.format("Spawned %s" + "\n", entity.getName()));
            return ActionResult.newResult(EnumActionResult.PASS, new ItemStack(this));
        }

        System.out.print(String.format("Could not spawn %s because of that the block is not a rail" + "\n", entity.getName()));
        return ActionResult.newResult(EnumActionResult.PASS, new ItemStack(this));
    }*/

}
