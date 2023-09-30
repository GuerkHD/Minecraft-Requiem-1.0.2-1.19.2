package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArrowC2SPacket
{
    public ArrowC2SPacket()
    {

    }

    public ArrowC2SPacket(FriendlyByteBuf buf)
    {

    }

    public void toBytes(FriendlyByteBuf buf)
    {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if(!isStandUser(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.WITHER_DEATH
                        , SoundSource.PLAYERS
                        , 0.5f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    stand.makeStandUser();
                    stand.setStandID(RandomSource.createNewThreadLocalInstance().nextInt(6));
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    ModMessages.sendToPlayer(new StandIDDataSyncS2CPacket(stand.getStandID()), player);
                    player.sendSystemMessage(Component.literal("StandID = " + stand.getStandID()));
                });
            }
        });
        return true;
    }

    private boolean isStandUser(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandUser(); })
                .orElse(false);
    }

    private boolean standIsActive(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandActive(); })
                .orElse(false);
    }
}
