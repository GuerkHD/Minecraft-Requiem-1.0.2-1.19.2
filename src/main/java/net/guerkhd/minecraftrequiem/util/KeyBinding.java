package net.guerkhd.minecraftrequiem.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding
{
    public static final String KEY_CATEGORY_STAND = "key.category.minecraftrequiem.stand";
    public static final String KEY_SUMMON_STAND = "key.minecraftrequiem.summon_stand";
    public static final String KEY_STAND_ABILITY = "key.minecraftrequiem.stand_ability";

    public static final KeyMapping SUMMONING_KEY = new KeyMapping(KEY_SUMMON_STAND, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, KEY_CATEGORY_STAND);
    public static final KeyMapping ABILITY_KEY = new KeyMapping(KEY_STAND_ABILITY, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_STAND);
}
