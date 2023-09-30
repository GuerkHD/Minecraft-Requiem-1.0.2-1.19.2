package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.StandActiveDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandUserDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if(event.getSource().getEntity() instanceof Player player && standIsActive(player) && getStandID(player) == 5)
        {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 6));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        List<LivingEntity> listIn = event.player.getLevel().getEntitiesOfClass(LivingEntity.class, event.player.getBoundingBox().inflate(5));
        List<LivingEntity> listOut = event.player.getLevel().getEntitiesOfClass(LivingEntity.class, event.player.getBoundingBox().inflate(20));

        for(LivingEntity ent : listOut)
        {
            if(!listIn.contains(ent) && ent.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && getStandID(event.player) == 5)
            {
                ent.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            }
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
}
