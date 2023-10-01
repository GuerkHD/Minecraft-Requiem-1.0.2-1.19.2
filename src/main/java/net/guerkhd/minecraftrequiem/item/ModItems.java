package net.guerkhd.minecraftrequiem.item;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.Custom.RequiemArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandRemoveArrowItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftRequiem.MOD_ID);

    public static final RegistryObject<Item> GUERK_INGOT = ITEMS.register("guerk_ingot",
            () -> new Item(new Item.Properties()
                    .food(Foods.GUERK_INGOT)
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<Item> RAW_GUERK = ITEMS.register("raw_guerk",
            () -> new Item(new Item.Properties()
                    .food(Foods.RAW_GUERK)
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));

    public static final RegistryObject<Item> STAND_ARROW = ITEMS.register("stand_arrow",
            () -> new StandArrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<Item> REQUIEM_ARROW = ITEMS.register("requiem_arrow",
            () -> new RequiemArrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<Item> STAND_ARROW_TIP = ITEMS.register("stand_arrow_tip",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<Item> REQUIEM_ARROW_TIP = ITEMS.register("requiem_arrow_tip",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<Item> REMOVER_ARROW = ITEMS.register("remover_arrow",
            () -> new StandRemoveArrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));

    public static class Foods
    {
        public static final FoodProperties GUERK_INGOT = new FoodProperties.Builder()
                .nutrition(5)
                .saturationMod(1.2f)
                .build();
        public static final FoodProperties RAW_GUERK = new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.6f)
                .fast()
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0), 0.8f)
                .build();
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
