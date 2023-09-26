package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandUserDataSyncS2CPacket
{
    private final boolean standUser;

    public StandUserDataSyncS2CPacket(boolean standUser)
    {
        this.standUser = standUser;
    }

    public StandUserDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.standUser = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(standUser);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientStandData.setStandUser(standUser);
        });
        return true;
    }
}
