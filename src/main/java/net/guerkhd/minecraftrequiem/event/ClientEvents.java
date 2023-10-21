package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.AbilityC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public class ClientEvents
{
    @Mod.EventBusSubscriber(modid = MinecraftRequiem.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents
    {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event)
        {
            if(KeyBinding.SUMMONING_KEY.consumeClick())
            {
                ModMessages.sendToServer(new StandC2SPacket());
            }
            else if(KeyBinding.ABILITY_KEY.consumeClick() && ClientStandData.getStandActive())
            {
                ModMessages.sendToServer(new AbilityC2SPacket());
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event)
        {
            if(ClientStandData.getStandID() == 3 && event.player.horizontalCollision && ClientStandData.getStandActive())
            {
                Vec3 motion = event.player.getDeltaMovement();
                event.player.setDeltaMovement(motion.x, 0.2, motion.z);
            }

            List<Zombie> list = event.player.getLevel().getEntitiesOfClass(Zombie.class, event.player.getBoundingBox().inflate(50));

            for(Zombie stand : list)
            {
                if(stand.hasCustomName())
                {
                    if(stand.getCustomName().equals(event.player.getName()) && !ClientStandData.getStandActive())
                    {
                        stand.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MinecraftRequiem.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents
    {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event)
        {
            event.register(KeyBinding.SUMMONING_KEY);
            event.register(KeyBinding.ABILITY_KEY);
        }
    }
}
