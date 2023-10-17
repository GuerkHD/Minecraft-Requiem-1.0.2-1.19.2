package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandMaxYDataSyncS2CPacket
{
    private final double maxY;

    public StandMaxYDataSyncS2CPacket(double maxY)
    {
        this.maxY = maxY;
    }

    public StandMaxYDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.maxY = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeDouble(maxY);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientStandData.setMaxY(maxY);
        });
        return true;
    }
}
