package net.guerkhd.minecraftrequiem.item.Custom;

import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.ArrowC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

        if(!level.isClientSide && hand == InteractionHand.MAIN_HAND && !ClientStandData.getStandUser())
        {
            if(player.experienceLevel >= 30)
            {
                ModMessages.sendToServer(new ArrowC2SPacket());
                player.giveExperiencePoints(-1395);
            }
            else player.hurt(DamageSource.MAGIC, Float.MAX_VALUE);

            player.getCooldowns().addCooldown(this, 20);
        }

        return super.use(level, player, hand);
    }
}
