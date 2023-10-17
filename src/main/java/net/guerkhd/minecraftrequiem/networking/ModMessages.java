package net.guerkhd.minecraftrequiem.networking;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.networking.packet.*;
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
        net.messageBuilder(RemoverC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RemoverC2SPacket::new)
                .encoder(RemoverC2SPacket::toBytes)
                .consumerMainThread(RemoverC2SPacket::handle)
                .add();
        net.messageBuilder(AbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AbilityC2SPacket::new)
                .encoder(AbilityC2SPacket::toBytes)
                .consumerMainThread(AbilityC2SPacket::handle)
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
        net.messageBuilder(StandIDDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StandIDDataSyncS2CPacket::new)
                .encoder(StandIDDataSyncS2CPacket::toBytes)
                .consumerMainThread(StandIDDataSyncS2CPacket::handle)
                .add();
        net.messageBuilder(StandBombDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StandBombDataSyncS2CPacket::new)
                .encoder(StandBombDataSyncS2CPacket::toBytes)
                .consumerMainThread(StandBombDataSyncS2CPacket::handle)
                .add();
        net.messageBuilder(StandMaxYDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StandMaxYDataSyncS2CPacket::new)
                .encoder(StandMaxYDataSyncS2CPacket::toBytes)
                .consumerMainThread(StandMaxYDataSyncS2CPacket::handle)
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
