package com.mrcrayfish.vehicle.network;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.util.function.IntSupplier;

/**
 * Author: MrCrayfish
 */
public class HandshakeMessages
{
    static class LoginIndexedMessage implements IntSupplier
    {
        private int loginIndex;

        void setLoginIndex(final int loginIndex)
        {
            this.loginIndex = loginIndex;
        }

        int getLoginIndex()
        {
            return loginIndex;
        }

        @Override
        public int getAsInt()
        {
            return getLoginIndex();
        }
    }

    static class C2SAcknowledge extends LoginIndexedMessage
    {
        void encode(PacketBuffer buf) {}

        static C2SAcknowledge decode(PacketBuffer buf)
        {
            return new C2SAcknowledge();
        }
    }

    public static class S2CVehicleProperties extends LoginIndexedMessage
    {
        private ImmutableMap<ResourceLocation, VehicleProperties> propertiesMap;

        public S2CVehicleProperties() {}

        void encode(PacketBuffer buffer)
        {
            /* This shouldn't be null as it's encoded from the logical server but
             * it's just here to avoiding IDE warnings */
            Validate.notNull(VehicleProperties.Manager.get());
            VehicleProperties.Manager.get().writeVehicleProperties(buffer);
        }

        static S2CVehicleProperties decode(PacketBuffer buffer)
        {
            S2CVehicleProperties message = new S2CVehicleProperties();
            message.propertiesMap = VehicleProperties.Manager.readVehicleProperties(buffer);
            return message;
        }

        public ImmutableMap<ResourceLocation, VehicleProperties> getPropertiesMap()
        {
            return this.propertiesMap;
        }
    }
}