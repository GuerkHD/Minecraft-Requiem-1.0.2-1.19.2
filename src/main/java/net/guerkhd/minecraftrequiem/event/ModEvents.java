package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.*;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
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
        if(event.getSource().getEntity() instanceof Player player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() >= 12 && !event.getEntity().hasCustomName())
        {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 6));
            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 12);
            event.getEntity().getLevel().playSound(null
                    , player.getOnPos()
                    , SoundEvents.ANVIL_PLACE
                    , SoundSource.PLAYERS
                    , 1f
                    , event.getEntity().getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
        else if(event.getSource().getEntity() instanceof Player player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() < 12 && !event.getEntity().hasCustomName())
        {
            event.getEntity().getLevel().playSound(null
                    , player.getOnPos()
                    , SoundEvents.ANVIL_FALL
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

        if(event.getSource().getEntity() instanceof ServerPlayer player && getStandID(player) == 7 && standIsActive(player) && !getBomb(player) && !event.getEntity().hasEffect(MobEffects.GLOWING))
        {
            //event.getEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, 36000, 0, false, false));
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 36000, 0, false, false));

            player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
            {
                stand.setBomb(true);
                ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
            });
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
                if(ent.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
                {
                    ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 6));
                }
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
        }

        if(event.getEntity().hasCustomName())
        {
            event.getEntity().clearFire();

            for(Player player : list)
            {
                if(event.getEntity().getCustomName().equals(player.getName()))
                {
                    user = true;

                    Vec3 pos = new Vec3(player.getViewVector(3f).x, 0, player.getViewVector(3f).z);
                    pos = pos.reverse();
                    //pos = pos.add(x, 0, z);
                    Vec3 play = new Vec3(player.getPosition(1f).x, player.getPosition(1f).y + 0.5, player.getPosition(1f).z);
                    pos = play.add(pos);
                    pos = pos.subtract(event.getEntity().getPosition(1f));

                    //player.sendSystemMessage(Component.literal("Random double: " + random()));

                    event.getEntity().move(MoverType.SELF, pos);

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

    private static double random()
    {
        return RandomSource.createNewThreadLocalInstance().nextDouble();
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
}
