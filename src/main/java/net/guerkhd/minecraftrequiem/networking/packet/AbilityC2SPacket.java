package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AbilityC2SPacket
{
    public AbilityC2SPacket()
    {

    }

    public AbilityC2SPacket(FriendlyByteBuf buf)
    {

    }

    public void toBytes(FriendlyByteBuf buf)
    {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if(getStandID(player) == 0)
            {
                theWorld(level, player);
            }
            else if(getStandID(player) == 1)
            {

            }
            else if(getStandID(player) == 2)
            {
                LargeFireball fireball = new LargeFireball(level, player, player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3);
                fireball.setPosRaw(player.getX(), player.getY()+player.getEyeHeight(), player.getZ());
                fireball.shoot(player.getViewVector(1f).x, player.getViewVector(1f).y, player.getViewVector(1f).z, 3f, 0f);
                level.addFreshEntity(fireball);
            }
        });
        return true;
    }

    private boolean isStandUser(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandUser(); })
                .orElse(false);
    }

    private boolean standIsActive(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandActive(); })
                .orElse(false);
    }

    private int getStandID(ServerPlayer player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
    }

    public void theWorld(Level level, LivingEntity livingEntity)
    {
        if (!level.isClientSide)
        {
            double d0 = livingEntity.getX();
            double d1 = livingEntity.getY();
            double d2 = livingEntity.getZ();

            for(int i = 0; i < 16; ++i)
            {
                double d3 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                double d4 = Mth.clamp(livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8), (double)level.getMinBuildHeight(), (double)(level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
                double d5 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                if (livingEntity.isPassenger())
                {
                    livingEntity.stopRiding();
                }

                Vec3 vec3 = livingEntity.position();
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                EntityTeleportEvent.ChorusFruit event = ForgeEventFactory.onChorusFruitTeleport(livingEntity, d3, d4, d5);
                if (event.isCanceled())
                {
                    return;
                }

                if (livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true))
                {
                    SoundEvent soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    level.playSound((Player)null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    livingEntity.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }
}
