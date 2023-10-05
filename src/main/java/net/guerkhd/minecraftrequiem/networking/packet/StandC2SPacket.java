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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Zombie;
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

            Zombie standEntity = initializeStand(level, player);

            if(isStandUser(player) && !standIsActive(player))
            {
                level.addFreshEntity(standEntity);

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

    private Zombie initializeStand(ServerLevel level, ServerPlayer player)
    {
        Zombie stand = new Zombie(level);

        stand.setPos(player.getX()-1, player.getY()+0.5, player.getZ()-1);
        stand.setAggressive(false);
        stand.setCanPickUpLoot(false);
        stand.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3600, 0));
        //stand.setInvisible(true);
        stand.setGlowingTag(true);
        stand.setInvulnerable(true);
        stand.setSilent(true);
        stand.setNoGravity(true);
        stand.setNoAi(true);
        stand.setCustomName(player.getName());
        stand.setCustomNameVisible(false);

        return stand;
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
