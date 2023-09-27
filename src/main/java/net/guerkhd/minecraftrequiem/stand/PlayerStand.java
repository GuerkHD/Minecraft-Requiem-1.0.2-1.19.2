package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.nbt.CompoundTag;

public class PlayerStand
{
    private boolean standUser = false;
    private boolean standActive = false;
    private int standID;
    //ID 1 = The World
    //ID 2 = D4C

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

    public int getStandID()
    {
        return standID;
    }
    public void setStandID(int standID)
    {
        this.standID = standID;
    }

    public void copyFrom(PlayerStand source)
    {
        this.standUser = source.standUser;
        this.standActive = source.standActive;
        this.standID = source.standID;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putBoolean("standUser", standUser);
        nbt.putBoolean("standActive", standActive);
        nbt.putInt("standID", standID);
    }

    public void loadNBTData(CompoundTag nbt)
    {
        standUser = nbt.getBoolean("standUser");
        standActive = nbt.getBoolean("standActive");
        standID = nbt.getInt("standID");
    }
}
