package net.hdt.hva.init;

import net.hdt.hva.client.gui.GuiVehicleCreator;
import net.hdt.hva.container.ContainerVehicleCreator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int VEHICLE_CREATOR = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == VEHICLE_CREATOR) {
			return new ContainerVehicleCreator(player.inventory);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == VEHICLE_CREATOR) {
			return new GuiVehicleCreator(player.inventory);
		}

		return null;
	}

}
