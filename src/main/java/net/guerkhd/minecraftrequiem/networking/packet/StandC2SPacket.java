package net.guerkhd.minecraftrequiem.networking.packet;

import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.*;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StandC2SPacket
{
    public StandC2SPacket()
    {

    }

    public StandC2SPacket(FriendlyByteBuf buf)
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

            Zombie standEntity = initializeStand(level, player);

            if(isStandUser(player) && !standIsActive(player))
            {
                if(getStandID(player) != 6) level.addFreshEntity(standEntity);

                level.playSound(null
                        , player.getOnPos()
                        , ModSounds.STAND_SUMMON.get()
                        , SoundSource.PLAYERS
                        , 1f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    stand.activateStand();
                    if(stand.getStandID() == 2) player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 36000, 0, false, false, true));
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                });
            }
            else if(isStandUser(player) && standIsActive(player))
            {
                level.playSound(null
                        , player.getOnPos()
                        , SoundEvents.ENDERMAN_TELEPORT
                        , SoundSource.PLAYERS
                        , 1f
                        , level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    if(stand.getStandID() == 2) player.removeEffect(MobEffects.FIRE_RESISTANCE);
                    if(stand.getStandID() == 4 && level.isThundering()) level.setWeatherParameters(0, 0, false, false);
                    stand.deactivateStand();
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                });
            }
            else
            {
                player.sendSystemMessage(Component.literal("Skill issue."));
            }
        });
        return true;
    }

    private Zombie initializeStand(ServerLevel level, ServerPlayer player)
    {
        Zombie stand = new Zombie(level);

        stand.setPos(player.getX()-0.5, player.getY()+0.5, player.getZ()-0.5);
        stand.setAggressive(false);
        stand.setCanPickUpLoot(false);
        stand.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3600, 0, false, false));
        //stand.setGlowingTag(true);
        stand.setInvulnerable(true);
        stand.setSilent(true);
        stand.setNoGravity(true);
        stand.setNoAi(true);
        stand.setCustomName(player.getName());
        stand.setCustomNameVisible(false);

        if(getStandID(player) == 5) stand.setBaby(true);

        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack leggins = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);

        List<DyeItem> color = getList(player);

        helmet = DyeableLeatherItem.dyeArmor(helmet, color);
        chestplate = DyeableLeatherItem.dyeArmor(chestplate, color);
        leggins = DyeableLeatherItem.dyeArmor(leggins, color);
        boots = DyeableLeatherItem.dyeArmor(boots, color);

        stand.setItemSlot(EquipmentSlot.HEAD, helmet);
        stand.setItemSlot(EquipmentSlot.CHEST, chestplate);
        stand.setItemSlot(EquipmentSlot.LEGS, leggins);
        stand.setItemSlot(EquipmentSlot.FEET, boots);

        return stand;
    }

    private List<DyeItem> getList(ServerPlayer player)
    {
        List<DyeItem> list = new ArrayList<>();

        if(getStandID(player) == 0) list.add(DyeItem.byColor(DyeColor.YELLOW));
        else if(getStandID(player) == 1) list.add(DyeItem.byColor(DyeColor.LIGHT_BLUE));
        else if(getStandID(player) == 2) list.add(DyeItem.byColor(DyeColor.ORANGE));
        else if(getStandID(player) == 3) list.add(DyeItem.byColor(DyeColor.GREEN));
        else if(getStandID(player) == 4) list.add(DyeItem.byColor(DyeColor.WHITE));
        else if(getStandID(player) == 5) list.add(DyeItem.byColor(DyeColor.LIME));
        else if(getStandID(player) == 6) list.add(DyeItem.byColor(DyeColor.PURPLE));
        else if(getStandID(player) == 7) list.add(DyeItem.byColor(DyeColor.PINK));
        else if(getStandID(player) == 8) list.add(DyeItem.byColor(DyeColor.RED));
        else if(getStandID(player) == 9) list.add(DyeItem.byColor(DyeColor.GREEN));

        return list;
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
                .map(stand -> {
                    return stand.getStandID();
                })
                .orElse(10);
    }
}
