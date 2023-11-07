package net.guerkhd.minecraftrequiem.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class StandHudOverlay
{
    public static final IGuiOverlay STAND_HUD = (((gui, poseStack, partialTick, screenWidth, screenHeight) ->
    {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();

        int x = (int) (window.getGuiScaledWidth() / 30);
        int y = (int) (window.getGuiScaledHeight() / 18);

        Font font = mc.font;

        if(standIsActive())
        {
            font.draw(poseStack, getComponent(getStandType()), x, y, 0);

            //font.draw(poseStack, "TEST1", (float) (window.getGuiScaledWidth() * 0.25), (float) (window.getGuiScaledHeight() * 0.25), 0);
            //font.draw(poseStack, "TEST2", (float) (window.getGuiScaledWidth() * 0.25), (float) (window.getGuiScaledHeight() * 0.75), 0);
            //font.draw(poseStack, "TEST3", (float) (window.getGuiScaledWidth() * 0.75), (float) (window.getGuiScaledHeight() * 0.75), 0);
            //font.draw(poseStack, "TEST4", (float) (window.getGuiScaledWidth() * 0.75), (float) (window.getGuiScaledHeight() * 0.25), 0);
        }
    }));

    public enum StandType
    {
        THE_WORLD,
        D4C,
        MAGICIANS_RED,
        C_MOON,
        WEATHER_REPORT,
        ECHOS,
        HIGHWAY_TO_HELL,
        KILLER_QUEEN,
        KING_CRIMSON,
        GREEN_DAY,
        UNKNOWN;
    }

    private static StandType getStandType()
    {
        return StandType.values()[ClientStandData.getStandID()];
    }

    private static boolean standIsActive()
    {
        return ClientStandData.getStandActive();
    }

    private static Component getComponent(StandType standType)
    {
        switch(standType)
        {
            case THE_WORLD:
                return Component.literal("The World").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD);
            case D4C:
                return Component.literal("D4C").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE);
            case MAGICIANS_RED:
                return Component.literal("Magicians Red").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED);
            case C_MOON:
                return Component.literal("C-Moon").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN);
            case WEATHER_REPORT:
                return Component.literal("Weather Report").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE);
            case ECHOS:
                return Component.literal("Echos Act 3").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN);
            case HIGHWAY_TO_HELL:
                return Component.literal("Highway To Hell").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            case KILLER_QUEEN:
                return Component.literal("Killer Queen").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE);
            case KING_CRIMSON:
                return Component.literal("King Crimson").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED);
            case GREEN_DAY:
                return Component.literal("Green Day").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN);
            default:
                return Component.literal("unknown").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GRAY);
        }
    }
}
