package net.guerkhd.minecraftrequiem.config;

import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MinecraftRequiemCommonConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue THE_WORLD_COST;
    public static final ForgeConfigSpec.IntValue D4C_COST;
    public static final ForgeConfigSpec.IntValue MAGICIANS_RED_COST;
    public static final ForgeConfigSpec.IntValue C_MOON_COST;
    public static final ForgeConfigSpec.IntValue WEATHER_REPORT_COST;
    public static final ForgeConfigSpec.IntValue ECHOS_ACT_3_COST;
    public static final ForgeConfigSpec.IntValue KILLER_QUEEN_COST;
    public static final ForgeConfigSpec.IntValue KING_CRIMSON_COST;
    public static final ForgeConfigSpec.IntValue GREEN_DAY_COST;

    public static final ForgeConfigSpec.EnumValue<Explosion.BlockInteraction> BOMB_DESTRUCTION;
    public static final ForgeConfigSpec.EnumValue<Explosion.BlockInteraction> HEART_ATTACK_DESTRUCTION;

    public static final ForgeConfigSpec.IntValue GUERK_ORE_VEINS_PER_CHUNK;
    public static final ForgeConfigSpec.IntValue GUERK_ORE_VEIN_SIZE;

    static
    {
        BUILDER.push("Stand ability costs");

        THE_WORLD_COST = BUILDER.comment("How much food the The World teleport costs.")
                        .defineInRange("The World cost", 6, 0, 20);
        D4C_COST = BUILDER.comment("How much food D4Cs dimensional teleport costs.")
                        .defineInRange("D4C cost", 17, 0, 20);
        MAGICIANS_RED_COST = BUILDER.comment("How much food Magicians Reds fireball costs.")
                        .defineInRange("Fireball cost", 5, 0, 20);
        C_MOON_COST = BUILDER.comment("How much food C-Moons gravity reversal costs.")
                        .defineInRange("C-Moon cost", 7, 0, 20);
        WEATHER_REPORT_COST = BUILDER.comment("How much food Weather Reports weather control costs.")
                        .defineInRange("Weather Report cost", 6, 0, 20);
        ECHOS_ACT_3_COST = BUILDER.comment("How much food Echos weight increase costs.")
                        .defineInRange("Act 3 cost", 12, 0, 20);
        KILLER_QUEEN_COST = BUILDER.comment("How much food Killer Queens bomb costs.")
                        .defineInRange("Bomb cost", 7, 0, 20);
        KING_CRIMSON_COST = BUILDER.comment("How much food King Crimsons precognition costs.")
                        .defineInRange("Epitaph cost", 9, 0, 20);
        GREEN_DAY_COST = BUILDER.comment("How much food Green Days fungus costs.")
                        .defineInRange("Green Day cost", 12, 0, 20);

        BUILDER.pop();
        BUILDER.push("Killer Queens bombs blockinteraction");

        BOMB_DESTRUCTION = BUILDER.comment("If Killer Queens bomb does block damage.")
                        .defineEnum("First bombs block damage", Explosion.BlockInteraction.NONE, Explosion.BlockInteraction.NONE, Explosion.BlockInteraction.BREAK, Explosion.BlockInteraction.DESTROY);
        HEART_ATTACK_DESTRUCTION = BUILDER.comment("If Killer Queens Sheer Heart Attack does block damage.")
                        .defineEnum("Second bombs block damage", Explosion.BlockInteraction.BREAK, Explosion.BlockInteraction.NONE, Explosion.BlockInteraction.BREAK, Explosion.BlockInteraction.DESTROY);

        BUILDER.pop();
        BUILDER.push("Guerk Ore spawn");

        GUERK_ORE_VEINS_PER_CHUNK = BUILDER.comment("How many Guerk Ore veins spawn per chunk.")
                        .defineInRange("Veins per chunk", 5, 1, 15);
        GUERK_ORE_VEIN_SIZE = BUILDER.comment("How many Guerk Ore blocks spawn per vein.")
                        .defineInRange("Vein size", 5, 1, 15);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
