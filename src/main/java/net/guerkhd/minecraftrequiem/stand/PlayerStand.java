package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Zombie;

public class PlayerStand
{
    private boolean standUser = false;
    private boolean standActive = false;
    private int standID;
    private boolean bomb;
    //ID 0 = The World
    //ID 1 = D4C
    //ID 2 = Magicians Red
    //ID 3 = C-Moon
    //ID 4 = Weather Report
    //ID 5 = Echos Act 3
    //ID 6 = Highway To Hell
    //ID 7 = Killer Queen

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

    public void copyFrom(PlayerStand source)
    {
        this.standUser = source.standUser;
        this.standActive = source.standActive;
        this.standID = source.standID;
        this.bomb = source.bomb;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putBoolean("standUser", standUser);
        nbt.putBoolean("standActive", standActive);
        nbt.putInt("standID", standID);
        nbt.putBoolean("bomb", bomb);
    }

    public void loadNBTData(CompoundTag nbt)
    {
        standUser = nbt.getBoolean("standUser");
        standActive = nbt.getBoolean("standActive");
        standID = nbt.getInt("standID");
        bomb = nbt.getBoolean("bomb");
    }
}
