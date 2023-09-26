package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.nbt.CompoundTag;

public class PlayerStand
{
    private boolean standUser = false;
    private boolean standActive = false;

    public boolean getStandUser()
    {
        return standUser;
    }
    public void makeStandUser()
    {
        this.standUser = true;
    }
    public void undoStandUser()
    {
        this.standUser = false;
    }

    public boolean getStandActive()
    {
        return standActive;
    }
    public void activateStand()
    {
        this.standActive = true;
    }
    public void deactivateStand()
    {
        this.standActive = false;
    }

    public void copyFrom(PlayerStand source)
    {
        this.standUser = source.standUser;
        this.standActive = source.standActive;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putBoolean("standUser", standUser);
        nbt.putBoolean("standActive", standActive);
    }

    public void loadNBTData(CompoundTag nbt)
    {
        standUser = nbt.getBoolean("standUser");
        standActive = nbt.getBoolean("standActive");
    }
}
