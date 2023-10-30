package net.guerkhd.minecraftrequiem.client;

public class ClientStandData
{
    private static boolean standUser;
    private static boolean standActive;
    private static int standID;
    private static boolean bomb;
    private static double maxY;
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

    public static boolean getStandUser()
    {
        return standUser;
    }
    public static void setStandUser(boolean standUser)
    {
        ClientStandData.standUser = standUser;
    }

    public static boolean getStandActive()
    {
        return standActive;
    }
    public static void setStandActive(boolean standActive)
    {
        ClientStandData.standActive = standActive;
    }

    public static int getStandID()
    {
        return standID;
    }
    public static void setStandID(int standID)
    {
        ClientStandData.standID = standID;
    }

    public static boolean getBomb()
    {
        return bomb;
    }
    public static void setBomb(boolean bomb)
    {
        ClientStandData.bomb = bomb;
    }

    public static double getMaxY()
    {
        return maxY;
    }
    public static void setMaxY(double maxY)
    {
        ClientStandData.maxY = maxY;
    }
}
