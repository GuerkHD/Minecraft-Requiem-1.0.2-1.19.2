package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
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
import net.minecraft.world.entity.monster.Zombie;
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

            int food = player.getFoodData().getFoodLevel();
            int cost = 0;

            if(getStandID(player) == 0)
            {
                cost = 6;

                if(food >= cost)
                {
                    Vec3 theWorld = player.pick(20, 1f, false).getLocation();
                    player.moveTo(theWorld);
                    standSound(level, player, 0, true);

                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10));

                    for(LivingEntity ent : list)
                    {
                        if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), 20, 0));
                    }
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 0, false);
                }
            }
            else if(getStandID(player) == 1)
            {
                cost = 17;

                if(food >= cost && !player.getLevel().dimension().equals(Level.OVERWORLD))
                {
                    standSound(level, player, 1, true);
                    player.teleportTo(player.getServer().getLevel(Level.OVERWORLD), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), player.getYRot(), player.getXRot());
                }
                else if(food >= 6)
                {
                    cost = 6;
                    D4C(level, player);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 1, false);
                }
            }
            else if(getStandID(player) == 2)
            {
                cost = 5;

                if(food >= cost)
                {
                    standSound(level, player, 2, true);
                    LargeFireball fireball = new LargeFireball(level, player, player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3);
                    fireball.setPosRaw(player.getX(), player.getY()+player.getEyeHeight(), player.getZ());
                    fireball.shoot(player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3f, 0f);
                    level.addFreshEntity(fireball);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 2, false);
                }
            }
            else if(getStandID(player) == 3)
            {
                cost = 7;

                if(food >= cost)
                {
                    standSound(level, player, 3, true);
                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15));

                    for(LivingEntity ent : list)
                    {
                        if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 9));
                    }
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 3, false);
                }
            }
            else if(getStandID(player) == 4)
            {
                cost = 6;

                if(food >= cost)
                {
                    standSound(level, player, 4, true);

                    if(level.isThundering())
                    {
                            Vec3 blitz = player.pick(30, 1f, false).getLocation();

                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                            lightningBolt.setPosRaw(blitz.x, blitz.y, blitz.z);
                            level.addFreshEntity(lightningBolt);
                    }

                    if(!level.isThundering()) level.setWeatherParameters(0, 1200, true, true);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 4, false);
                }
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
                cost = 7;

                if(food >= cost)
                {
                    standSound(level, player, 7, true);
                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50));

                    for(LivingEntity ent : list)
                    {
                        if(ent.hasEffect(ModEffects.BOMB.get()))
                        {
                            ent.removeEffect(ModEffects.BOMB.get());
                            ent.removeEffect(MobEffects.GLOWING);
                            level.explode(player, ent.getX(), ent.getY()+1, ent.getZ(), 2, Explosion.BlockInteraction.NONE);

                            player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                            {
                                stand.setBomb(false);
                                ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
                            });
                        }
                    }

                    player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                    {
                        if(stand.getBomb())
                        {
                            stand.setBomb(false);
                            ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
                        }
                    });
                }
                else
                {
                    cost = 0;
                    standSound(level, player, 7, false);
                }
            }
            else if(getStandID(player) == 8)
            {

            }
            else if(getStandID(player) == 9)
            {

            }

            if(player.gameMode.isSurvival()) player.getFoodData().setFoodLevel(food - cost);
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

    private void standSound(ServerLevel level, ServerPlayer player, int ID, boolean success)
    {
        if(ID == 0 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , ModSounds.ZA_WARUDO.get()
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 0 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.FIRE_EXTINGUISH
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 1 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.WARDEN_EMERGE
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 1 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.WARDEN_DEATH
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 2 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.CHICKEN_DEATH
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 2 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.CHICKEN_AMBIENT
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 3 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDER_CHEST_OPEN
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 3 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDER_CHEST_CLOSE
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 4 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.BEACON_ACTIVATE
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 4 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.BEACON_DEACTIVATE
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 7 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.STONE_BUTTON_CLICK_ON
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 7 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.STONE_BUTTON_CLICK_OFF
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 8 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDERMAN_TELEPORT
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 8 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDERMAN_TELEPORT
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 9 && success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDERMAN_TELEPORT
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
        else if(ID == 9 && !success)
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.ENDERMAN_TELEPORT
                    , SoundSource.PLAYERS
                    , 0.5f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
    }

    public void D4C(Level level, LivingEntity livingEntity)
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
