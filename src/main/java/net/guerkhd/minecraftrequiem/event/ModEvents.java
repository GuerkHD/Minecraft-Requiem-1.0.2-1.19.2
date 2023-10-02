package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.StandActiveDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandBombDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandIDDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandUserDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MinecraftRequiem.MOD_ID)
public class ModEvents
{
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
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if(event.getSource().getEntity() instanceof Player player && getStandID(player) == 5 && standIsActive(player))
        {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 6));
            player.causeFoodExhaustion(60);
        }

        if(event.getEntity() instanceof Player player && getStandID(player) == 6 && standIsActive(player))
        {
            List<Player> list = event.getEntity().getLevel().getEntitiesOfClass(Player.class, event.getEntity().getBoundingBox().inflate(20));
            list.remove(event.getEntity());

            if(!list.isEmpty()) getClosest(list, event.getEntity()).hurt(DamageSource.MAGIC, event.getAmount());
        }

        if(event.getSource().getEntity() instanceof ServerPlayer player && getStandID(player) == 7 && standIsActive(player) && !getBomb(player))
        {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, 36000, 0));
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 36000, 0));

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
        List<LivingEntity> list3F = event.player.getLevel().getEntitiesOfClass(LivingEntity.class, event.player.getBoundingBox().inflate(5));
        list3F.remove(event.player);

        for(LivingEntity ent : list3F)
        {
            if(ent.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && getStandID(event.player) == 5 && standIsActive(event.player))
            {
                ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 6));
            }
        }

        List<Player> listHTH = event.player.getLevel().getEntitiesOfClass(Player.class, event.player.getBoundingBox().inflate(20));
        listHTH.remove(event.player);

        if(!listHTH.isEmpty() && getStandID(event.player) == 6 && standIsActive(event.player)) getClosest(listHTH, event.player).addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        List<Player> listKQ = event.getEntity().getLevel().getEntitiesOfClass(Player.class, event.getEntity().getBoundingBox().inflate(50));
        boolean bomb = false;

        for(Player player : listKQ)
        {
            if(getBomb(player)) bomb = true;
        }
        if(!bomb)
        {
            event.getEntity().removeEffect(MobEffects.GLOWING);
            event.getEntity().removeEffect(ModEffects.BOMB.get());
        }
    }



    private static int getStandID(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
    }

    private static boolean standIsActive(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandActive(); })
                .orElse(false);
    }

    private static boolean getBomb(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getBomb(); })
                .orElse(false);
    }

    private static Player getClosest(List<Player> list, LivingEntity user)
    {
        Player closest = list.get(0);

        for(Player player : list)
        {
            if(player.position().distanceToSqr(user.position()) < closest.position().distanceToSqr(user.position()))
            {
                closest = player;
            }
        }

        return closest;
    }
}
