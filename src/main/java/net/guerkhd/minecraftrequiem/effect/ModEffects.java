package net.guerkhd.minecraftrequiem.effect;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects
{
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MinecraftRequiem.MOD_ID);

    public static final RegistryObject<MobEffect> FREEZE = MOB_EFFECTS.register("freeze",
            () -> new FreezeEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<MobEffect> BOMB = MOB_EFFECTS.register("bomb",
            () -> new BombEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<MobEffect> GREEN_DAY = MOB_EFFECTS.register("green_day",
            () -> new GreenDayEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<MobEffect> EPITAPH = MOB_EFFECTS.register("epitaph",
            () -> new KCEffect(MobEffectCategory.BENEFICIAL, 0));


    public static void register(IEventBus eventBus)
    {
        MOB_EFFECTS.register(eventBus);
    }
}
