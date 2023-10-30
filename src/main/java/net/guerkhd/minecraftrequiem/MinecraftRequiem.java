package net.guerkhd.minecraftrequiem;

import com.mojang.logging.LogUtils;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemClientConfig;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.world.feature.ModConfiguredFeatures;
import net.guerkhd.minecraftrequiem.world.feature.ModPlacedFeatures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MinecraftRequiem.MOD_ID)
public class MinecraftRequiem
{
    public static final String MOD_ID = "minecraftrequiem";
    private static final Logger LOGGER = LogUtils.getLogger();
    public MinecraftRequiem()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        ModConfiguredFeatures.register(modEventBus);
        ModPlacedFeatures.register(modEventBus);

        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MinecraftRequiemClientConfig.SPEC, "minecraftrequiem-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MinecraftRequiemCommonConfig.SPEC, "minecraftrequiem-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ModMessages.register();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}