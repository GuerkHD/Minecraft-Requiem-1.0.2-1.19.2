package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.*;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MinecraftRequiem.MOD_ID)
public class ModEvents
{
    private static int tick = 0;
    //private static double x = random() / 2;
    //private static double z = random() / 2;

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
        {
            if(!event.getObject().getCapability(PlayerStandProvider.PLAYER_STAND).isPresent())
            {
                event.addCapability(new ResourceLocation(MinecraftRequiem.MOD_ID, "properties"), new PlayerStandProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())
        {
            event.getOriginal().getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(oldStore ->
            {
                event.getOriginal().getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(newStore ->
                {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerStand.class);
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide())
        {
            if(event.getEntity() instanceof ServerPlayer player)
            {
                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                    ModMessages.sendToPlayer(new StandIDDataSyncS2CPacket(stand.getStandID()), player);
                    ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLeaveLevel(EntityLeaveLevelEvent event)
    {
        if(event.getEntity() instanceof Player player && standIsActive(player))
        {
            ModMessages.sendToServer(new StandC2SPacket());
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if(event.getSource().getEntity() instanceof ServerPlayer player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() >= 12 && !isStand(player, event.getEntity()))
        {
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.THREE_FREEZE.get(), 20, 0, false, false, true));
            if(player.gameMode.isSurvival()) player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 12);
            event.getEntity().getLevel().playSound(null
                    , player.getOnPos()
                    , ModSounds.THREE_FREEZE.get()
                    , SoundSource.PLAYERS
                    , 1f
                    , event.getEntity().getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
        else if(event.getSource().getEntity() instanceof ServerPlayer player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() < 12 && !isStand(player, event.getEntity()))
        {
            event.getEntity().getLevel().playSound(null
                    , player.getOnPos()
                    , SoundEvents.PLAYER_BURP
                    , SoundSource.PLAYERS
                    , 1f
                    , event.getEntity().getLevel().random.nextFloat() * 0.1f + 0.9f);
        }

        //Player List
        if(event.getEntity() instanceof Player player && getStandID(player) == 6 && standIsActive(player))
        {
            List<LivingEntity> list = event.getEntity().getLevel().getEntitiesOfClass(LivingEntity.class, event.getEntity().getBoundingBox().inflate(20));
            list.remove(event.getEntity());

            if(!list.isEmpty()) getClosest(list, event.getEntity()).hurt(DamageSource.MAGIC, event.getAmount());
        }

        if(event.getSource().getEntity() instanceof ServerPlayer player && getStandID(player) == 7 && standIsActive(player) && !getBomb(player) && !event.getEntity().hasCustomName())
        {
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 36000, 0, false, false, true));

            player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
            {
                stand.setBomb(true);
                ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
            });
        }

        if(event.getSource().getEntity() instanceof LivingEntity entity && event.getEntity() instanceof ServerPlayer player && player.hasEffect(ModEffects.EPITAPH.get()))
        {
            player.setXRot(entity.getXRot());
            player.setYRot(entity.getYRot());
            player.moveTo(behindTP(entity, player, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, false, false, false));
            event.setCanceled(true);
            if(player.hasEffect(ModEffects.EPITAPH.get())) player.removeEffect(ModEffects.EPITAPH.get());

            event.getEntity().getLevel().playSound(null
                    , player.getOnPos()
                    , ModSounds.KING_CRIMSON.get()
                    , SoundSource.PLAYERS
                    , 1f
                    , event.getEntity().getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(getStandID(event.player) == 5 && standIsActive(event.player))
        {
            List<LivingEntity> list3F = event.player.getLevel().getEntitiesOfClass(LivingEntity.class, event.player.getBoundingBox().inflate(5));
            list3F.remove(event.player);

            for(LivingEntity ent : list3F)
            {
                if(ent.hasEffect(ModEffects.THREE_FREEZE.get()))
                {
                    //Vec3 pos = event.player.getPosition(1f);
                    ent.addEffect(new MobEffectInstance(ModEffects.THREE_FREEZE.get(), 20, 0, false, false, true));
                    //event.player.getLevel().addParticle(ParticleTypes.LAVA, pos.x, pos.y, pos.z, 0, 0.7, 0);
                }
            }
        }

        if(event.player.hasEffect(ModEffects.GREEN_DAY.get()))
        {
            double down = getMaxY(event.player) - event.player.getY();
            int duration = event.player.getEffect(ModEffects.GREEN_DAY.get()).getDuration();

            if(down > 0 && down <= 5)
            {
                event.player.removeEffect(ModEffects.GREEN_DAY.get());
                event.player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, (int) Math.round(down), true, true, true));
            }
            else if(down > 0 && down > 5)
            {
                event.player.removeEffect(ModEffects.GREEN_DAY.get());
                event.player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, 5, true, true, true));
            }
            else if(down <= 0)
            {
                event.player.removeEffect(ModEffects.GREEN_DAY.get());
                event.player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, 0, true, true, true));
            }
        }

        //Player List
        if(getStandID(event.player) == 6 && standIsActive(event.player))
        {
            List<LivingEntity> listHTH = event.player.getLevel().getEntitiesOfClass(LivingEntity.class, event.player.getBoundingBox().inflate(20));
            listHTH.remove(event.player);

            if(!listHTH.isEmpty()) getClosest(listHTH, event.player).addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        List<Player> list = event.getEntity().getLevel().getEntitiesOfClass(Player.class, event.getEntity().getBoundingBox().inflate(50));
        boolean bomb = false;
        boolean user = false;

        for(Player player : list)
        {
            if(getBomb(player)) bomb = true;
        }
        if(!bomb)
        {
            //event.getEntity().removeEffect(MobEffects.GLOWING);
            event.getEntity().removeEffect(ModEffects.BOMB.get());
            if(event.getEntity() instanceof Turtle turtle && turtle.hasCustomName() && turtle.getCustomName().getString().equals("Sheer Heart Attack"))
            {
                turtle.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        if(event.getEntity().hasCustomName() && event.getEntity() instanceof Zombie)
        {
            event.getEntity().clearFire();

            for(Player player : list)
            {
                if(event.getEntity().getCustomName().equals(player.getName()))
                {
                    user = true;

                    Vec3 vec3 = event.getEntity().getPosition(1f);

                    event.getEntity().move(MoverType.SELF, behindMove(player, event.getEntity(), 0.5));
                    //if(getStandID(player) == 1 || getStandID(player) == 2 || getStandID(player) == 4) event.getEntity().getLevel().addParticle(getParticle(player), vec3.x, vec3.y + player.getEyeHeight(), vec3.z, 0, 0.7, 0);

                    //player.sendSystemMessage(Component.literal("Stand move: " + behindMove(player, event.getEntity(), 0.5)));

                    if(tick == 0)
                    {
                        event.getEntity().setYRot(player.getYRot());
                        event.getEntity().setXRot(player.getXRot());
                        //x = random() / 2;
                        //z = random() / 2;
                    }
                }
            }
            if(!user) event.getEntity().remove(Entity.RemovalReason.DISCARDED);
        }

        tick++;
        if(tick >= 20) tick = 0;
    }

    private static int getStandID(Player player)
    {
        /*
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
        */
        return ClientStandData.getStandID();
    }

    private static boolean standIsActive(Player player)
    {
        /*
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandActive(); })
                .orElse(false);
        */
        return ClientStandData.getStandActive();
    }

    private static boolean getBomb(Player player)
    {
        /*
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getBomb(); })
                .orElse(false);
        */
        return ClientStandData.getBomb();
    }

    private static double getMaxY(Player player)
    {
        return ClientStandData.getMaxY();
    }

    private static double random()
    {
        return RandomSource.createNewThreadLocalInstance().nextDouble();
    }

    private static boolean isStand(ServerPlayer player, LivingEntity entity)
    {
        if(entity.hasCustomName() && entity.getCustomName().equals(player.getName())) return true;
        else return false;
    }

    private static Vec3 behindTP(LivingEntity target, LivingEntity traveler, double yOffset)
    {
        Vec3 pos = target.getViewVector(1f);
        pos = pos.multiply(1.5, 0, 1.5);
        pos = pos.reverse();
        //pos = pos.add(x, 0, z);
        Vec3 play = new Vec3(target.getPosition(1f).x, target.getPosition(1f).y + yOffset, target.getPosition(1f).z);
        pos = play.add(pos);
        //pos = pos.subtract(traveler.getPosition(1f));

        return pos;
    }

    private static Vec3 behindMove(LivingEntity target, LivingEntity traveler, double yOffset)
    {
        Vec3 pos = target.getViewVector(1f);
        pos = pos.multiply(1.5, 0, 1.5);
        pos = pos.reverse();
        //pos = pos.add(x, 0, z);
        Vec3 play = new Vec3(target.getPosition(1f).x, target.getPosition(1f).y + yOffset, target.getPosition(1f).z);
        pos = play.add(pos);
        pos = pos.subtract(traveler.getPosition(1f));

        if(pos.add(traveler.getPosition(1f)).distanceToSqr(target.getPosition(1f)) < 0.5)
        {
            return pos.subtract(0.7, 0, 0.7);
        }
        return pos;
    }

    //Player List
    private static LivingEntity getClosest(List<LivingEntity> list, LivingEntity user)
    {
        LivingEntity closest = list.get(0);

        for(LivingEntity livingEntity : list)
        {
            if(livingEntity.position().distanceToSqr(user.position()) < closest.position().distanceToSqr(user.position()))
            {
                closest = livingEntity;
            }
        }

        return closest;
    }

    private static double distance(LivingEntity entity, LivingEntity player)
    {
        return player.position().distanceToSqr(entity.position());
    }

    private static ParticleOptions getParticle(Player player)
    {
        ParticleOptions particle = null;

        if(getStandID(player) == 1) particle = ParticleTypes.REVERSE_PORTAL;
        else if(getStandID(player) == 2) particle = ParticleTypes.LAVA;
        else if(getStandID(player) == 4) particle = ParticleTypes.CLOUD;

        return particle;
    }
}
