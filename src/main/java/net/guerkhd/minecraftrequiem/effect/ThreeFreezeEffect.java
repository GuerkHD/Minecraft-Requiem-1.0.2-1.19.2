package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class ThreeFreezeEffect extends MobEffect
{
    public ThreeFreezeEffect(MobEffectCategory mobEffectCategory, int color)
    {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.level.isClientSide())
        {
            plivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 6, false, false, false));
        }
        super.applyEffectTick(plivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
