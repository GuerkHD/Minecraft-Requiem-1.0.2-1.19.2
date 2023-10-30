package net.guerkhd.minecraftrequiem.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MinecraftRequiemClientConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static
    {
        BUILDER.push("Configs for Minecraft Requiem");

        //Define Configs

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
