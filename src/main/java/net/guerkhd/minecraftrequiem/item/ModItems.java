package net.guerkhd.minecraftrequiem.item;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.Custom.RequiemArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandRemoveArrowItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
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
    public static final RegistryObject<Item> GUERK_NUGGET = ITEMS.register("guerk_nugget",
            () -> new Item(new Item.Properties()
                    .food(Foods.GUERK_NUGGET)
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));

    public static final RegistryObject<StandArrowItem> STAND_ARROW = ITEMS.register("stand_arrow",
            () -> new StandArrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<RequiemArrowItem> REQUIEM_ARROW = ITEMS.register("requiem_arrow",
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
    public static final RegistryObject<StandRemoveArrowItem> REMOVER_ARROW = ITEMS.register("remover_arrow",
            () -> new StandRemoveArrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));

    public static final RegistryObject<SwordItem> GUERK_SWORD = ITEMS.register("guerk_sword",
            () -> new SwordItem(Tiers.STONE, 3, -2.4f, new Item.Properties()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<PickaxeItem> GUERK_PICKAXE = ITEMS.register("guerk_pickaxe",
            () -> new PickaxeItem(Tiers.STONE, 1, -2.8f, new Item.Properties()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<AxeItem> GUERK_AXE = ITEMS.register("guerk_axe",
            () -> new AxeItem(Tiers.STONE, 7, -3.2f, new Item.Properties()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<ShovelItem> GUERK_SHOVEL = ITEMS.register("guerk_shovel",
            () -> new ShovelItem(Tiers.STONE, 1.5f, -3, new Item.Properties()
                    .tab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB)));
    public static final RegistryObject<HoeItem> GUERK_HOE = ITEMS.register("guerk_hoe",
            () -> new HoeItem(Tiers.STONE, -1, -2, new Item.Properties()
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
        public static final FoodProperties GUERK_NUGGET = new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.3f)
                .fast()
                .build();
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
