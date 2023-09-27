package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandIDDataSyncS2CPacket
{
    private final int standID;

    public StandIDDataSyncS2CPacket(int standID)
    {
        this.standID = standID;
    }

    public StandIDDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.standID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(standID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientStandData.setStandID(standID);
        });
        return true;
    }
}
