package net.guerkhd.minecraftrequiem.item.Custom;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StandArrowItem extends Item
{
    public StandArrowItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if(!level.isClientSide && hand == InteractionHand.MAIN_HAND)
        {
            if(player.experienceLevel >= 30)
            {
                outputWIP(player);
                player.giveExperiencePoints(-1395);
            }
            else player.die(DamageSource.MAGIC);

            player.getCooldowns().addCooldown(this, 20);
        }

        return super.use(level, player, hand);
    }

    private void outputWIP(Player player)
    {
        player.sendSystemMessage(Component.literal("Stands coming in a future Update."));
    }

    private int getRandomNumber()
    {
        return RandomSource.createNewThreadLocalInstance().nextInt(10);
    }
}
