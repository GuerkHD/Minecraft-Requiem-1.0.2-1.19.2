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
    public StandC2SPacket() {    }

    public StandC2SPacket(FriendlyByteBuf buf) {    }

    public void toBytes(FriendlyByteBuf buf) {    }

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

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            StandType standType = getStandType(player);

            Zombie standEntity = initializeStand(level, player, standType);

            if(isStandUser(player) && !standIsActive(player))
            {
                if(getStandType(player) != StandType.HIGHWAY_TO_HELL) level.addFreshEntity(standEntity);

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
                        , ModSounds.STAND_UNSUMMON.get()
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

                List<Zombie> list = level.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(50));

                for(Zombie stand : list)
                {
                    if(isStand(stand) && stand.getCustomName().equals(player.getName())) stand.remove(Entity.RemovalReason.DISCARDED);
                }
            }
            else player.sendSystemMessage(Component.literal("Skill issue."));
        });
        return true;
    }

    private Zombie initializeStand(ServerLevel level, ServerPlayer player, StandType standType)
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

        if(getStandType(player) == StandType.ECHOS) stand.setBaby(true);

        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack leggins = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);

        List<DyeItem> color = getList(standType);

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

    private List<DyeItem> getList(StandType standType)
    {
        List<DyeItem> list = new ArrayList<>();

        switch(standType)
        {
            case THE_WORLD:
                list.add(DyeItem.byColor(DyeColor.YELLOW));
                break;
            case D4C:
                list.add(DyeItem.byColor(DyeColor.LIGHT_BLUE));
                break;
            case MAGICIANS_RED:
                list.add(DyeItem.byColor(DyeColor.ORANGE));
                break;
            case C_MOON:
                list.add(DyeItem.byColor(DyeColor.GREEN));
                break;
            case WEATHER_REPORT:
                list.add(DyeItem.byColor(DyeColor.WHITE));
                break;
            case ECHOS: list.add(DyeItem.byColor(DyeColor.LIME));
                break;
            case KILLER_QUEEN:
                list.add(DyeItem.byColor(DyeColor.PINK));
                break;
            case KING_CRIMSON:
                list.add(DyeItem.byColor(DyeColor.RED));
                break;
            case GREEN_DAY:
                list.add(DyeItem.byColor(DyeColor.GREEN));
                break;
            default:
                list.add(DyeItem.byColor(DyeColor.GRAY));
        }

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

    private StandType getStandType(ServerPlayer player)
    {
        int ID = player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(stand -> { return stand.getStandID(); })
                .orElse(10);
        return StandType.values()[ID];
    }

    private boolean isStand(LivingEntity entity)
    {
        if(entity instanceof Zombie stand && stand.hasCustomName() && stand.hasEffect(MobEffects.INVISIBILITY) && stand.isNoAi()) return true;
        else return false;
    }
}
