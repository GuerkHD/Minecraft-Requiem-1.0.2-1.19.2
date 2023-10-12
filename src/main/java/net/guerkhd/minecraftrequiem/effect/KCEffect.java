package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class KCEffect extends MobEffect
{
    public KCEffect(MobEffectCategory mobEffectCategory, int color)
    {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.level.isClientSide())
        {
            //Nothing to see here
        }
        super.applyEffectTick(plivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
