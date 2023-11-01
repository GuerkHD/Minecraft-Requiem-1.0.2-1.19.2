package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArrowC2SPacket
{
    public ArrowC2SPacket() {    }

    public ArrowC2SPacket(FriendlyByteBuf buf) {    }

    public void toBytes(FriendlyByteBuf buf) {    }

    public enum StandType
    {
        THE_WORLD,
        D4C,
        MAGICIANS_RED,
        C_MOON,
        WEATHER_REPORT,
        ECHOS,
        HIGHWAY_TO_HELL,
        KILLER_QUEEN,
        KING_CRIMSON,
        GREEN_DAY,
        UNKNOWN;
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
                    stand.setStandID(RandomSource.createNewThreadLocalInstance().nextInt(10));
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    ModMessages.sendToPlayer(new StandIDDataSyncS2CPacket(stand.getStandID()), player);

                    StandType standType = getStandType(player);

                    switch(standType)
                    {
                        case THE_WORLD:
                            player.sendSystemMessage(Component.literal("The World").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
                            break;
                        case D4C:
                            player.sendSystemMessage(Component.literal("Dirty Deeds Done Dirt Cheap").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                            break;
                        case MAGICIANS_RED:
                            player.sendSystemMessage(Component.literal("Magicians Red").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED));
                            break;
                        case C_MOON:
                            player.sendSystemMessage(Component.literal("C-Moon").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        case WEATHER_REPORT:
                            player.sendSystemMessage(Component.literal("Weather Report").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE));
                            break;
                        case ECHOS:
                            player.sendSystemMessage(Component.literal("Echos Act 3").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
                            break;
                        case HIGHWAY_TO_HELL:
                            player.sendSystemMessage(Component.literal("Highway To Hell").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE));
                            break;
                        case KILLER_QUEEN:
                            player.sendSystemMessage(Component.literal("Killer Queen").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE));
                            break;
                        case KING_CRIMSON:
                            player.sendSystemMessage(Component.literal("King Crimson").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED));
                            break;
                        case GREEN_DAY:
                            player.sendSystemMessage(Component.literal("Green Day").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN));
                            break;
                    }
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

    private StandType getStandType(ServerPlayer player)
    {
        int ID = player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
        return StandType.values()[ID];
    }
}
