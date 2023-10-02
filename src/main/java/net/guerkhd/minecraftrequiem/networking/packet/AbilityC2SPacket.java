package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

public class AbilityC2SPacket
{
    public AbilityC2SPacket()
    {

    }

    public AbilityC2SPacket(FriendlyByteBuf buf)
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
            BlockPos respawnPos = player.getLevel().getSharedSpawnPos();

            if(getStandID(player) == 0)
            {
                theWorld(level, player);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15));

                for(LivingEntity ent : list)
                {
                    if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), 40, 0));
                }
            }
            else if(getStandID(player) == 1)
            {
                player.teleportTo(player.getServer().getLevel(Level.OVERWORLD), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), player.getYRot(), player.getXRot());
            }
            else if(getStandID(player) == 2)
            {
                LargeFireball fireball = new LargeFireball(level, player, player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3);
                fireball.setPosRaw(player.getX(), player.getY()+player.getEyeHeight(), player.getZ());
                fireball.shoot(player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3f, 0f);
                level.addFreshEntity(fireball);
            }
            else if(getStandID(player) == 3)
            {
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15));

                for(LivingEntity ent : list)
                {
                    if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 9));
                }
            }
            else if(getStandID(player) == 4)
            {
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15));

                for(LivingEntity ent : list)
                {
                    if(!ent.equals(player) && level.isThundering() && RandomSource.createNewThreadLocalInstance().nextInt(3) == 0)
                    {
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                        lightningBolt.setPosRaw(ent.getX(), ent.getY(), ent.getZ());
                        level.addFreshEntity(lightningBolt);
                    }
                }

                if(!level.isThundering()) level.setWeatherParameters(0, 1200, true, true);
            }
            else if(getStandID(player) == 5)
            {
                //Nothing to see here
            }
            else if(getStandID(player) == 6)
            {
                if(player.getMainHandItem().getItem() instanceof SwordItem sword)
                {
                    player.hurt(DamageSource.playerAttack(player), sword.getDamage() + 1f);
                }
                else if(player.getMainHandItem().getItem() instanceof DiggerItem tool)
                {
                    player.hurt(DamageSource.playerAttack(player), tool.getAttackDamage() + 1f);
                }
                else
                {
                    player.hurt(DamageSource.playerAttack(player), player.getAttackStrengthScale(0));
                }
            }
            else if(getStandID(player) == 7)
            {
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(100));

                for(LivingEntity ent : list)
                {
                    if(ent.hasEffect(ModEffects.BOMB.get()))
                    {
                        ent.removeEffect(ModEffects.BOMB.get());
                        ent.removeEffect(MobEffects.GLOWING);
                        level.explode(player, ent.getX(), ent.getY()+2, ent.getZ(), 1, Explosion.BlockInteraction.NONE);

                        player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                        {
                            stand.setBomb(false);
                            ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
                        });
                    }
                }
            }
            else if(getStandID(player) == 8)
            {

            }
            else if(getStandID(player) == 9)
            {

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

    private int getStandID(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
    }

    public void theWorld(Level level, LivingEntity livingEntity)
    {
        if (!level.isClientSide)
        {
            double d0 = livingEntity.getX();
            double d1 = livingEntity.getY();
            double d2 = livingEntity.getZ();

            for(int i = 0; i < 16; ++i)
            {
                double d3 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                double d4 = Mth.clamp(livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8), (double)level.getMinBuildHeight(), (double)(level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
                double d5 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                if (livingEntity.isPassenger())
                {
                    livingEntity.stopRiding();
                }

                Vec3 vec3 = livingEntity.position();
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                EntityTeleportEvent.ChorusFruit event = ForgeEventFactory.onChorusFruitTeleport(livingEntity, d3, d4, d5);
                if (event.isCanceled())
                {
                    return;
                }

                if (livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true))
                {
                    SoundEvent soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    level.playSound((Player)null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    livingEntity.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }
}
