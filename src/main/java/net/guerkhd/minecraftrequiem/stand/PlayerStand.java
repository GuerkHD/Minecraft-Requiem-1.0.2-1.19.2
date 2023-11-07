package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.nbt.CompoundTag;

public class PlayerStand
{
    private boolean standUser = false;
    private boolean standActive = false;
    private int standID;
    private boolean bomb;
    private double maxY;
    //ID 0 = The World
    //ID 1 = D4C
    //ID 2 = Magicians Red
    //ID 3 = C-Moon
    //ID 4 = Weather Report
    //ID 5 = Echos Act 3
    //ID 6 = Highway To Hell
    //ID 7 = Killer Queen
    //ID 8 = King Crimson
    //ID 9 = Green Day

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

    public boolean getBomb()
    {
        return bomb;
    }
    public void setBomb(boolean bomb)
    {
        this.bomb = bomb;
    }

    public double getMaxY()
    {
        return maxY;
    }
    public void setMaxY(double maxY)
    {
        this.maxY = maxY;
    }

    public void copyFrom(PlayerStand source)
    {
        this.standUser = source.standUser;
        this.standActive = source.standActive;
        this.standID = source.standID;
        this.bomb = source.bomb;
        this.maxY = source.maxY;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putBoolean("standUser", standUser);
        nbt.putBoolean("standActive", standActive);
        nbt.putInt("standID", standID);
        nbt.putBoolean("bomb", bomb);
        nbt.putDouble("maxY", maxY);
    }

    public void loadNBTData(CompoundTag nbt)
    {
        standUser = nbt.getBoolean("standUser");
        standActive = nbt.getBoolean("standActive");
        standID = nbt.getInt("standID");
        bomb = nbt.getBoolean("bomb");
        maxY = nbt.getDouble("maxY");
    }
}
