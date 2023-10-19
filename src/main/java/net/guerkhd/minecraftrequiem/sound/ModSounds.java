package net.guerkhd.minecraftrequiem.sound;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MinecraftRequiem.MOD_ID);

    public static final RegistryObject<SoundEvent> ZA_WARUDO =
            registerSoundEvent("za_warudo");
    public static final RegistryObject<SoundEvent> C_MOON =
            registerSoundEvent("c_moon");
    public static final RegistryObject<SoundEvent> STAND_SUMMON =
            registerSoundEvent("stand_summon");
    public static final RegistryObject<SoundEvent> KING_CRIMSON =
            registerSoundEvent("king_crimson");
    public static final RegistryObject<SoundEvent> KILLER_QUEEN =
            registerSoundEvent("killer_queen");
    public static final RegistryObject<SoundEvent> THREE_FREEZE =
            registerSoundEvent("three_freeze");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(MinecraftRequiem.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus)
    {
        SOUND_EVENTS.register(eventBus);
    }
}
