package net.guerkhd.minecraftrequiem.event;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.item.Custom.StandArrowItem;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.*;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.PlayerStandProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MinecraftRequiem.MOD_ID)
public class ModEvents
{
    private static int tick = 0;
    //private static double x = random() / 2;
    //private static double z = random() / 2;

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
        {
            if(!event.getObject().getCapability(PlayerStandProvider.PLAYER_STAND).isPresent())
            {
                event.addCapability(new ResourceLocation(MinecraftRequiem.MOD_ID, "properties"), new PlayerStandProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())
        {
            event.getOriginal().getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(oldStore ->
            {
                event.getOriginal().getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(newStore ->
                {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerStand.class);
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide())
        {
            if(event.getEntity() instanceof ServerPlayer player)
            {
                player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
                {
                    ModMessages.sendToPlayer(new StandUserDataSyncS2CPacket(stand.getStandUser()), player);
                    ModMessages.sendToPlayer(new StandActiveDataSyncS2CPacket(stand.getStandActive()), player);
                    ModMessages.sendToPlayer(new StandIDDataSyncS2CPacket(stand.getStandID()), player);
                    ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
                    ModMessages.sendToPlayer(new StandMaxYDataSyncS2CPacket(stand.getMaxY()), player);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event)
    {
        List<Component> tooltips = event.getToolTip();

        if(isGuerkItem(event.getItemStack().getItem()))
        {
            tooltips.add(getIntex(tooltips.size()), Component.translatable("message.minecraftrequiem.food_leech").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event)
    {
        Player player = event.getEntity();
        String name = player.getName().getString();
        Level level = player.getLevel();
        Item item = event.getStack().getItem();

        if(!level.isClientSide() && name.equals("Knorke75") && item.equals(Items.DIAMOND))
        {
            level.playSound(null
                    , player.getOnPos()
                    , SoundEvents.CREEPER_PRIMED
                    , SoundSource.VOICE
                    , 8f
                    , level.random.nextFloat() * 0.1f + 0.9f);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntity();
        Entity source = event.getSource().getEntity();
        float amount = event.getAmount();

        if(!entity.getLevel().isClientSide())
        {
            if(source instanceof LivingEntity livingSource && isGuerkItem(livingSource.getMainHandItem().getItem()))
            {
                foodLeech(livingSource, entity, 1);
            }

            threeFreeze(entity, source);
            reflectDmg(entity, amount);
            applyBomb(entity, source);
            timeSkip(entity, source, event);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;

        if(!player.getLevel().isClientSide())
        {
            refreshThreeFreeze(player);
            greenDay(player);
            glowClosest(player);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        LivingEntity entity = event.getEntity();

        boolean user = false;

        if(!event.getEntity().getLevel().isClientSide())
        {
            refreshBomb(entity);
            moveStand(entity, user);
        }

        tick++;
        if(tick >= 20) tick = 0;
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent event)
    {
        if(event.getEntity() instanceof Player player && player.hasEffect(ModEffects.CONSTIPATION.get()) && event.getItem().isEdible())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event)
    {
        Item item = event.getItemStack().getItem();
        Player player = event.getEntity();

        if(!event.getLevel().isClientSide() && item instanceof StandArrowItem && player.experienceLevel < 30) player.hurt(DamageSource.MAGIC, Float.MAX_VALUE);
    }

    private static int getStandID(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(PlayerStand::getStandID)
                .orElse(10);
    }

    private static boolean standIsActive(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(PlayerStand::getStandActive)
                .orElse(false);
    }

    private static boolean getBomb(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(PlayerStand::getBomb)
                .orElse(false);
    }

    private static double getMaxY(Player player)
    {
        return player.getCapability(PlayerStandProvider.PLAYER_STAND)
                .map(PlayerStand::getMaxY)
                .orElse(player.getY());
    }

    private static void moveStand(LivingEntity entity, boolean user)
    {
        if(isStand(entity))
        {
            List<Player> list = entity.getLevel().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(50));
            entity.clearFire();

            for(Player player : list)
            {
                if(entity.getCustomName().equals(player.getName()))
                {
                    user = true;

                    entity.move(MoverType.SELF, behindMove(player, entity, 0.5));

                    if(tick == 0)
                    {
                        entity.setYRot(player.getYRot());
                        entity.setXRot(player.getXRot());
                        //x = random() / 2;
                        //z = random() / 2;
                    }
                }
            }
            if(!user) entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static void refreshBomb(LivingEntity entity)
    {
        if(entity.hasEffect(ModEffects.BOMB.get()))
        {
            List<Player> list = entity.getLevel().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(50));

            for(Player player : list)
            {
                if(getBomb(player)) entity.addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 20, 0, false, false, true));
            }
        }

        if(entity instanceof Turtle turtle && turtle.hasCustomName() && turtle.getCustomName().getString().equals("Sheer Heart Attack") && !turtle.hasEffect(ModEffects.BOMB.get()))
        {
            turtle.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static void greenDay(Player player)
    {
        if(player.hasEffect(ModEffects.GREEN_DAY.get()))
        {
            double down = getMaxY(player) - player.getY();
            int duration = player.getEffect(ModEffects.GREEN_DAY.get()).getDuration();

            if(down > 0 && down <= 5)
            {
                player.removeEffect(ModEffects.GREEN_DAY.get());
                player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, (int) Math.round(down), true, true, true));
            }
            else if(down > 0 && down > 5)
            {
                player.removeEffect(ModEffects.GREEN_DAY.get());
                player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, 5, true, true, true));
            }
            else if(down <= 0)
            {
                player.removeEffect(ModEffects.GREEN_DAY.get());
                player.addEffect(new MobEffectInstance(ModEffects.GREEN_DAY.get(), duration, 0, true, true, true));
            }
        }
    }

    private static void threeFreeze(LivingEntity entity, Entity source)
    {
        if(source instanceof ServerPlayer player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() >= MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST.get() && !isStand(entity))
        {
            entity.addEffect(new MobEffectInstance(ModEffects.THREE_FREEZE.get(), 20, 0, false, false, true));
            if(player.gameMode.isSurvival()) player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST.get());
            entity.getLevel().playSound(null
                    , player.getOnPos()
                    , ModSounds.THREE_FREEZE.get()
                    , SoundSource.PLAYERS
                    , 1f
                    , entity.getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
        else if(source instanceof ServerPlayer player && getStandID(player) == 5 && standIsActive(player) && player.getFoodData().getFoodLevel() < MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST.get() && !isStand(entity))
        {
            entity.getLevel().playSound(null
                    , player.getOnPos()
                    , SoundEvents.PLAYER_BURP
                    , SoundSource.PLAYERS
                    , 1f
                    , entity.getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
    }

    private static void refreshThreeFreeze(Player player)
    {
        if(getStandID(player) == 5 && standIsActive(player))
        {
            List<LivingEntity> list = player.getLevel().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
            list.remove(player);

            for(LivingEntity ent : list)
            {
                if(ent.hasEffect(ModEffects.THREE_FREEZE.get()))
                {
                    //Vec3 pos = event.player.getPosition(1f);
                    ent.addEffect(new MobEffectInstance(ModEffects.THREE_FREEZE.get(), 20, 0, false, false, true));
                    //event.player.getLevel().addParticle(ParticleTypes.LAVA, pos.x, pos.y, pos.z, 0, 0.7, 0);
                }
            }
        }
    }

    private static void reflectDmg(LivingEntity entity, float amount)
    {
        //Player List
        if(entity instanceof Player player && getStandID(player) == 6 && standIsActive(player))
        {
            List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(20));
            list.remove(player);

            if(!list.isEmpty()) getClosest(list, player).hurt(DamageSource.MAGIC, amount);
        }
    }

    private static void glowClosest(Player player)
    {
        //Player List
        if(getStandID(player) == 6 && standIsActive(player))
        {
            List<LivingEntity> list = player.getLevel().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20));
            list.remove(player);

            if(!list.isEmpty()) getClosest(list, player).addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
        }
    }

    private static void applyBomb(LivingEntity entity, Entity source)
    {
        if(source instanceof ServerPlayer player && getStandID(player) == 7 && standIsActive(player) && !getBomb(player) && !isStand(entity))
        {
            entity.addEffect(new MobEffectInstance(ModEffects.BOMB.get(), 20, 0, false, false, true));

            player.getCapability(PlayerStandProvider.PLAYER_STAND).ifPresent(stand ->
            {
                stand.setBomb(true);
                ModMessages.sendToPlayer(new StandBombDataSyncS2CPacket(stand.getBomb()), player);
            });
        }
    }

    private static void timeSkip(LivingEntity target, Entity source, LivingHurtEvent event)
    {
        if(source instanceof LivingEntity entity && target instanceof ServerPlayer player && player.hasEffect(ModEffects.EPITAPH.get()))
        {
            player.setXRot(entity.getXRot());
            player.setYRot(entity.getYRot());
            player.moveTo(behindTP(entity, player, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, false, false, false));
            event.setCanceled(true);
            if(player.hasEffect(ModEffects.EPITAPH.get())) player.removeEffect(ModEffects.EPITAPH.get());

            target.getLevel().playSound(null
                    , player.getOnPos()
                    , ModSounds.KING_CRIMSON.get()
                    , SoundSource.PLAYERS
                    , 1f
                    , target.getLevel().random.nextFloat() * 0.1f + 0.9f);
        }
    }

    private static boolean isGuerkItem(Item item)
    {
        if(item.equals(ModItems.GUERK_SWORD.get()) || item.equals(ModItems.GUERK_PICKAXE.get()) || item.equals(ModItems.GUERK_AXE.get()) || item.equals(ModItems.GUERK_SHOVEL.get()) || item.equals(ModItems.GUERK_HOE.get()))
        {
            return true;
        }
        else return false;
    }

    private static boolean isStand(LivingEntity entity)
    {
        if(entity instanceof Zombie stand && stand.hasCustomName() && stand.hasEffect(MobEffects.INVISIBILITY) && stand.isNoAi()) return true;
        else return false;
    }

    private static void foodLeech(LivingEntity attacker, LivingEntity target, int food)
    {
        if(attacker instanceof Player attackerP)
        {
            if(attackerP.getFoodData().getFoodLevel() + food <= 20) attackerP.getFoodData().setFoodLevel(attackerP.getFoodData().getFoodLevel() + food);
        }
        if(target instanceof Player targetP)
        {
            if(targetP.getFoodData().getFoodLevel() - food >= 0) targetP.getFoodData().setFoodLevel(targetP.getFoodData().getFoodLevel() - food);
        }
    }

    private static Vec3 behindTP(LivingEntity target, LivingEntity traveler, double yOffset)
    {
        Vec3 pos = target.getViewVector(1f);
        pos = pos.multiply(1.5, 0, 1.5);
        pos = pos.reverse();
        //pos = pos.add(x, 0, z);
        Vec3 play = new Vec3(target.getPosition(1f).x, target.getPosition(1f).y + yOffset, target.getPosition(1f).z);
        pos = play.add(pos);
        //pos = pos.subtract(traveler.getPosition(1f));

        return pos;
    }

    private static Vec3 behindMove(LivingEntity target, LivingEntity traveler, double yOffset)
    {
        Vec3 pos = target.getViewVector(1f);
        pos = pos.multiply(1.5, 0, 1.5);
        pos = pos.reverse();
        //pos = pos.add(x, 0, z);
        Vec3 play = new Vec3(target.getPosition(1f).x, target.getPosition(1f).y + yOffset, target.getPosition(1f).z);
        pos = play.add(pos);
        pos = pos.subtract(traveler.getPosition(1f));

        if(pos.add(traveler.getPosition(1f)).distanceToSqr(target.getPosition(1f)) < 0.5)
        {
            return pos.subtract(0.7, 0, 0.7);
        }
        return pos;
    }

    //Player List
    private static LivingEntity getClosest(List<LivingEntity> list, LivingEntity user)
    {
        LivingEntity closest = list.get(0);

        for(LivingEntity livingEntity : list)
        {
            if(livingEntity.position().distanceToSqr(user.position()) < closest.position().distanceToSqr(user.position()))
            {
                closest = livingEntity;
            }
        }

        return closest;
    }

    private static int getIntex(int size)
    {
        if(size > 5) size = 5;
        return size;
    }
}
