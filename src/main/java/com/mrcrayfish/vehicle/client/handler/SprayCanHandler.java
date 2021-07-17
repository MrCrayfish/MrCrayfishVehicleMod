package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class SprayCanHandler
{
    private int lastSlot = -1;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        int slot = player.inventory.selected;
        if(this.lastSlot == slot)
            return;

        this.lastSlot = slot;

        if(player.inventory.getSelected().isEmpty())
            return;

        if(!(player.inventory.getSelected().getItem() instanceof SprayCanItem))
            return;

        SprayCanItem sprayCan = (SprayCanItem) player.inventory.getSelected().getItem();
        float pitch = 0.85F + 0.15F * sprayCan.getRemainingSprays(player.inventory.getSelected());
        Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(ModSounds.ITEM_SPRAY_CAN_SHAKE.get(), pitch, 0.75F));
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof SprayCanItem)
        {
            //ItemStack stack = event.getItemStack().copy(); //TODO fix the spray can item render
            //stack.setItemDamage(1);
            //Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(Minecraft.getMinecraft().player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), stack, event.getEquipProgress());
            //event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderThirdPerson(RenderItemEvent.Held.Pre event)
    {
        if(!event.getItem().isEmpty() && event.getItem().getItem() instanceof SprayCanItem)
        {
            /*ItemStack stack = event.getItem().copy(); //TODO fix this spray can render
            stack.setItemDamage(1);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(event.getEntity(), stack, event.getTransformType(), event.getHandSide() == EnumHandSide.LEFT);
            event.setCanceled(true);*/
        }
    }

    /**
     * Applies a pose to the player model if they are holding a spray can item
     *
     * @param player the player holding the spray can
     * @param model  the model of the player
     */
    static void applySprayCanPose(PlayerEntity player, PlayerModel<?> model)
    {
        if(player.getVehicle() != null)
            return;

        boolean rightHanded = player.getMainArm() == HandSide.RIGHT;
        ItemStack rightItem = rightHanded ? player.getMainHandItem() : player.getOffhandItem();
        ItemStack leftItem = rightHanded ? player.getOffhandItem() : player.getMainHandItem();
        if(!rightItem.isEmpty() && rightItem.getItem() instanceof SprayCanItem)
        {
            copyModelAngles(model.head, model.rightArm);
            model.rightArm.xRot += Math.toRadians(-80F);
        }
        if(!leftItem.isEmpty() && leftItem.getItem() instanceof SprayCanItem)
        {
            model.leftArm.copyFrom(model.head);
            model.leftArm.xRot += Math.toRadians(-80F);
        }
    }

    /**
     * A simple helper method to copy the rotation angles of a model renderer to another
     *
     * @param source the source model renderer to get the rotations form
     * @param target the target model renderer to apply to rotations to
     */
    private static void copyModelAngles(ModelRenderer source, ModelRenderer target)
    {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
