package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class AbilityC2SPacket
{
    public AbilityC2SPacket() {    }

    public AbilityC2SPacket(FriendlyByteBuf buf) {    }

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
            BlockPos respawnPos = player.getLevel().getSharedSpawnPos();

            StandType standType = getStandType(player);
            int food = player.getFoodData().getFoodLevel();
            int cost = 0;

            switch(standType)
            {
                case THE_WORLD:
                    cost = MinecraftRequiemCommonConfig.THE_WORLD_COST.get();

                    if(food >= cost)
                    {
                        Vec3 theWorld = player.pick(20, 1f, false).getLocation();
                        player.moveTo(theWorld);
                        standSound(level, player, 0, true);

                        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10));

                        for(LivingEntity ent : list)
                        {
                            if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(ModEffects.TIME_STOP.get(), 20, 0, false, false, true));
                        }
                    }
                    else
                    {
                        cost = 0;
                        standSound(level, player, 0, false);
                    }
                    break;
                case D4C:
                    cost = MinecraftRequiemCommonConfig.D4C_COST.get();

                    if(food >= cost && !player.getLevel().dimension().equals(Level.OVERWORLD))
                    {
                        standSound(level, player, 1, true);
                        player.teleportTo(player.getServer().getLevel(Level.OVERWORLD), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), player.getYRot(), player.getXRot());
                    }
                    else if(food >= MinecraftRequiemCommonConfig.THE_WORLD_COST.get())
                    {
                        cost = MinecraftRequiemCommonConfig.THE_WORLD_COST.get();
                        D4C(level, player);
                    }
                    else
                    {
                        cost = 0;
                        standSound(level, player, 1, false);
                    }
                    break;
                case MAGICIANS_RED:
                    cost = MinecraftRequiemCommonConfig.MAGICIANS_RED_COST.get();

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
                    break;
                case C_MOON:
                    cost = MinecraftRequiemCommonConfig.C_MOON_COST.get();

                    if(food >= cost)
                    {
                        standSound(level, player, 3, true);
                        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15));

                        for(LivingEntity ent : list)
                        {
                            if(!ent.equals(player)) ent.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 9, false, false, false));
                        }
                    }
                    else
                    {
                        cost = 0;
                        standSound(level, player, 3, false);
                    }
                    break;
                case WEATHER_REPORT:
                    cost = MinecraftRequiemCommonConfig.WEATHER_REPORT_COST.get();

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
                    break;
                case ECHOS:
                    break;
                case HIGHWAY_TO_HELL:
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
                    break;
                case KILLER_QUEEN:
                    cost = MinecraftRequiemCommonConfig.KILLER_QUEEN_COST.get();

                    if(player.isCrouching() && !getBomb(player))
                    {
                        spawnHeartAttack(level, player);
                        cost = 0;
                    }
                    else
                    {
                        cost = bomb(level, player, food, cost);
                    }
                    break;
                case KING_CRIMSON:
                    cost = MinecraftRequiemCommonConfig.KING_CRIMSON_COST.get();

                    if(food >= cost)
                    {
                        player.addEffect(new MobEffectInstance(ModEffects.EPITAPH.get(), 100, 0, false, false, true));
                    }
                    else
                    {
                        cost = 0;
                        standSound(level, player, 8, false);
                    }
                    break;
                case GREEN_DAY:
                    cost = MinecraftRequiemCommonConfig.GREEN_DAY_COST.get();

                    if(food >= cost)
                    {
                        List<ServerPlayer> list = level.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(15));
                        list.remove(player);

                        for(ServerPlayer play : list)
                        {
                            play.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                            {
                                stand.setMaxY(play.getY());
                                ModMessages.sendToPlayer(new StandMaxYDataSyncS2CPacket(stand.getMaxY()), play);
                            });

                            play.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), 500, 0, false, false, true));
                        }

                        List<Monster> mobList = level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(15));
                        for(Monster mob : mobList) mob.addEffect(new MobEffectInstance(MobEffects.POISON, 500, 0));

                        standSound(level, player, 9, true);
                    }
                    else
                    {
                        cost = 0;
                        standSound(level, player, 9, false);
                    }
                    break;
            }
            if(player.gameMode.isSurvival()) player.getFoodData().setFoodLevel(food - cost);
        });
        return true;
    }

    private StandType getStandType(ServerPlayer player)
    {
        int ID = player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
        return StandType.values()[ID];
    }

    private boolean getBomb(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getBomb(); })
                .orElse(false);
    }

    private void standSound(ServerLevel level, ServerPlayer player, int ID, boolean success)
    {
        level.playSound(null
                , player.getOnPos()
                , sound(ID, success)
                , SoundSource.PLAYERS
                , 1f
                , level.random.nextFloat() * 0.1f + 0.9f);
    }

    private SoundEvent sound(int ID, boolean success)
    {
        if(!success) return SoundEvents.PLAYER_BURP;
        else if(ID == 0) return ModSounds.ZA_WARUDO.get();
        else if(ID == 1) return SoundEvents.WARDEN_EMERGE;
        else if(ID == 2) return SoundEvents.CHICKEN_DEATH;
        else if(ID == 3) return ModSounds.C_MOON.get();
        else if(ID == 4) return SoundEvents.BEACON_ACTIVATE;
        else if(ID == 7) return ModSounds.KILLER_QUEEN.get();
        else if(ID == 9) return ModSounds.GREEN_DAY.get();

        return null;
    }

    private int bomb(ServerLevel level, ServerPlayer player, int food, int cost)
    {
        standSound(level, player, 7, true);

        if(food >= cost)
        {
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50));

            for(LivingEntity ent : list)
            {
                if(ent.hasEffect(ModEffects.BOMB.get()))
                {
                    ent.removeEffect(ModEffects.BOMB.get());

                    if(ent.hasCustomName() && ent.getCustomName().getString().equals("Sheer Heart Attack"))
                    {
                        level.explode(player, ent.getX(), ent.getY(), ent.getZ(), 3, MinecraftRequiemCommonConfig.HEART_ATTACK_DESTRUCTION.get());
                        ent.remove(Entity.RemovalReason.DISCARDED);
                    }
                    else
                    {
                        level.explode(player, ent.getX(), ent.getY()+1, ent.getZ(), 2, MinecraftRequiemCommonConfig.BOMB_DESTRUCTION.get());
                    }
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

        return cost;
    }

    private void spawnHeartAttack(ServerLevel level, ServerPlayer player)
    {
        Turtle heartAttack = heartAttack(level, inFront(player));
        level.addFreshEntity(heartAttack);

        player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
        {
            stand.setBomb(true);
            ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
        });
    }

    private Turtle heartAttack(ServerLevel level, Vec3 pos)
    {
        Turtle turtle = new Turtle(EntityType.TURTLE, level);

        turtle.setPos(pos);
        turtle.setBaby(true);
        turtle.addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 20, 0, false, false));
        turtle.setInvulnerable(true);
        turtle.setSilent(true);
        turtle.setNoAi(true);
        turtle.setCustomName(Component.literal("Sheer Heart Attack"));
        turtle.setCustomNameVisible(true);

        return turtle;
    }

    private Vec3 inFront(ServerPlayer player)
    {
        Vec3 view = player.getViewVector(1f);
        view = view.multiply(2, 0, 2);
        Vec3 pos = player.getPosition(1f);
        pos = pos.add(view);

        return pos;
    }

    private void D4C(Level level, LivingEntity livingEntity)
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
