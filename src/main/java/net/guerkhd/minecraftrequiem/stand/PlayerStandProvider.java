package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStandProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<PlayerStand> PLAYER_STAND = CapabilityManager.get(new CapabilityToken<PlayerStand>() { });

    private PlayerStand stand = null;
    private final LazyOptional<PlayerStand> optional = LazyOptional.of(this::createPlayerStand);

    private PlayerStand createPlayerStand()
    {
        if(this.stand == null)
        {
            this.stand = new PlayerStand();
        }
        return this.stand;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if(cap == PLAYER_STAND)
        {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        createPlayerStand().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        createPlayerStand().loadNBTData(nbt);
    }
}
