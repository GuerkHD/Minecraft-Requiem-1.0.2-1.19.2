package net.guerkhd.minecraftrequiem.networking;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.networking.packet.ArrowC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandActiveDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandUserDataSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages
{
    private static SimpleChannel INSTANCE;

    private static int packetID = 0;

    private static int id()
    {
        return packetID++;
    }

    public static void register()
    {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MinecraftRequiem.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(StandC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StandC2SPacket::new)
                .encoder(StandC2SPacket::toBytes)
                .consumerMainThread(StandC2SPacket::handle)
                .add();
        net.messageBuilder(ArrowC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ArrowC2SPacket::new)
                .encoder(ArrowC2SPacket::toBytes)
                .consumerMainThread(ArrowC2SPacket::handle)
                .add();

        net.messageBuilder(StandUserDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StandUserDataSyncS2CPacket::new)
                .encoder(StandUserDataSyncS2CPacket::toBytes)
                .consumerMainThread(StandUserDataSyncS2CPacket::handle)
                .add();
        net.messageBuilder(StandActiveDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StandActiveDataSyncS2CPacket::new)
                .encoder(StandActiveDataSyncS2CPacket::toBytes)
                .consumerMainThread(StandActiveDataSyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message)
    {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
