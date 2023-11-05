package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class RemoverC2SPacket
{
    public RemoverC2SPacket() {    }

    public RemoverC2SPacket(FriendlyByteBuf buf) {    }

    public void toBytes(FriendlyByteBuf buf) {    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if(isStandUser(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.WITHER_SPAWN
                        , SoundSource.PLAYERS
                        , 0.5f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    if(stand.getStandActive())
                    {
                        if(stand.getStandID() == 2) player.removeEffect(MobEffects.FIRE_RESISTANCE);
                        if(stand.getStandID() == 4 && (level.isThundering() || level.isRaining())) level.setWeatherParameters(0, 0, false, false);
                        stand.deactivateStand();
                        ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);

                        List<Zombie> list = level.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(50));
                        for(Zombie zombie : list)
                        {
                            if(isStand(zombie) && zombie.getCustomName().equals(player.getName())) zombie.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                    stand.undoStandUser();
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    player.sendSystemMessage(Component.literal("Stand removed.").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
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

    private boolean isStand(LivingEntity entity)
    {
        if(entity instanceof Zombie stand && stand.hasCustomName() && stand.hasEffect(MobEffects.INVISIBILITY) && stand.isNoAi()) return true;
        else return false;
    }
}
