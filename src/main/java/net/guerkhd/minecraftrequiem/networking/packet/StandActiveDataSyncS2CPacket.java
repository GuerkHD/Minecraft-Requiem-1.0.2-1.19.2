package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandActiveDataSyncS2CPacket
{
    private final boolean standActive;

    public StandActiveDataSyncS2CPacket(boolean standActive)
    {
        this.standActive = standActive;
    }

    public StandActiveDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.standActive = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(standActive);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientStandData.setStandActive(standActive);
        });
        return true;
    }
}
