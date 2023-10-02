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
                    stand.setStandID(RandomSource.createNewThreadLocalInstance().nextInt(8));
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    ModMessages.sendToPlayer(new StandIDDataSyncS2CPacket(stand.getStandID()), player);

                    if(stand.getStandID() == 0)
                    {
                        player.sendSystemMessage(Component.literal("The World").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
                    }
                    else if(stand.getStandID() == 1)
                    {
                        player.sendSystemMessage(Component.literal("Dirty Deeds Done Dirt Cheap").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                    }
                    else if(stand.getStandID() == 2)
                    {
                        player.sendSystemMessage(Component.literal("Magicians Red").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED));
                    }
                    else if(stand.getStandID() == 3)
                    {
                        player.sendSystemMessage(Component.literal("C-Moon").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN));
                    }
                    else if(stand.getStandID() == 4)
                    {
                        player.sendSystemMessage(Component.literal("Weather Report").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE));
                    }
                    else if(stand.getStandID() == 5)
                    {
                        player.sendSystemMessage(Component.literal("Echos Act 3").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
                    }
                    else if(stand.getStandID() == 6)
                    {
                        player.sendSystemMessage(Component.literal("Highway To Hell").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE));
                    }
                    else if(stand.getStandID() == 7)
                    {
                        player.sendSystemMessage(Component.literal("Killer Queen").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE));
                    }
                    else if(stand.getStandID() == 8)
                    {
                        player.sendSystemMessage(Component.literal("").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLACK));
                    }
                    else if(stand.getStandID() == 9)
                    {
                        player.sendSystemMessage(Component.literal("").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLACK));
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

    private boolean standIsActive(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandActive(); })
                .orElse(false);
    }
}
