package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
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

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    stand.activateStand();
                    if(stand.getStandID() == 2) player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 36000, 0));
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                });
            }
            else if(isStandUser(player) && standIsActive(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.ENDERMAN_HURT
                        , SoundSource.PLAYERS
                        , 0.5f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20));
                    for(LivingEntity ent : list)
                    {
                        if(stand.getStandID() == 5 && ent.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) ent.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    }
                    if(stand.getStandID() == 2) player.removeEffect(MobEffects.FIRE_RESISTANCE);
                    if(stand.getStandID() == 4 && level.isThundering()) level.setWeatherParameters(0, 0, false, false);
                    stand.deactivateStand();
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                });
            }
            else
            {
                player.sendSystemMessage(Component.literal("Skill issue."));
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
