package net.guerkhd.minecraftrequiem.client;

public class ClientStandData
{
    private static boolean standUser;
    private static boolean standActive;
    private static int standID;
    //ID 1 = The World
    //ID 2 = D4C
    //ID 3 = Magicians Red

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
