package net.guerkhd.minecraftrequiem.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StandC2SPacket
{
    public StandC2SPacket()
    {

    }

    public StandC2SPacket(FriendlyByteBuf buf)
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

            if(isStandUser(player) && !standIsActive(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.ENDERMAN_TELEPORT
                        , SoundSource.PLAYERS
                        , 0.5f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                //TODO
            }
            else if(isStandUser(player) && standIsActive(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.ENDERMAN_TELEPORT
                        , SoundSource.PLAYERS
                        , 0.5f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                //TODO
            }
            else
            {
                player.sendSystemMessage(Component.literal("Skill issue."));
            }
        });
        return true;
    }

    private boolean standIsActive(ServerPlayer player)
    {
        //TODO
    }

    private boolean isStandUser(ServerPlayer player)
    {
        //TODO
    }
}
