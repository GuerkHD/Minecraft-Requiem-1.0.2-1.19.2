package net.guerkhd.minecraftrequiem.client;

public class ClientStandData
{
    private static boolean standUser;
    private static boolean standActive;

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

}
