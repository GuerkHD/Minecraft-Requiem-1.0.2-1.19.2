package net.guerkhd.minecraftrequiem.client;

public class ClientStandData
{
    private static boolean standUser;
    private static boolean standActive;
    private static int standID;
    //ID 0 = The World
    //ID 1 = D4C
    //ID 2 = Magicians Red
    //ID 3 = C-Moon
    //ID 4 = Weather Report
    //ID 5 = Echos Act 3
    //ID 6 = Highway To Hell

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

}
