package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.client.StandHudOverlay;
import net.guerkhd.minecraftrequiem.item.Custom.RequiemArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandRemoveArrowItem;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.AbilityC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.ArrowC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.RemoverC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.util.KeyBinding;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        }

        @SubscribeEvent
        public static void onItemUse(PlayerInteractEvent.RightClickItem event)
        {
            Item item = event.getItemStack().getItem();
            Player player = event.getEntity();

            if(item instanceof RequiemArrowItem) ModMessages.sendToServer(new ArrowC2SPacket());
            else if(item instanceof StandRemoveArrowItem) ModMessages.sendToServer(new RemoverC2SPacket());
            else if(item instanceof StandArrowItem && player.experienceLevel >= 30)
            {
                ModMessages.sendToServer(new ArrowC2SPacket());
                player.giveExperiencePoints(-1395);
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

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event)
        {
            event.registerAboveAll("stand", StandHudOverlay.STAND_HUD);
        }
    }
}
