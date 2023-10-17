package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class GreenDayEffect extends MobEffect
{
    public GreenDayEffect(MobEffectCategory mobEffectCategory, int color)
    {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.level.isClientSide())
        {
            if(pAmplifier > 0) plivingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, pAmplifier-1));
        }
        super.applyEffectTick(plivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
