package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandBombDataSyncS2CPacket
{
    private final boolean bomb;

    public StandBombDataSyncS2CPacket(boolean bomb)
    {
        this.bomb = bomb;
    }

    public StandBombDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.bomb = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(bomb);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientStandData.setBomb(bomb);
        });
        return true;
    }
}
