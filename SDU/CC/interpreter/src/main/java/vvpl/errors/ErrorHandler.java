package vvpl.errors;

import java.util.ArrayList;

public class ErrorHandler 
{
    private static Boolean doPrint = true;
    public static ArrayList<String> errors = new ArrayList<>();

    public static void error(int line, String message) 
    {
        if(doPrint)
        {
            System.err.println("[line " + line + "] Error" + ": " + message);
        }
        errors.add("[line " + line + "] Error" + ": " + message);
    }

    public static String getErrors()
    {
        StringBuilder sb = new StringBuilder();
        for(String error : errors)
        {
            sb.append(error).append("\n");
        }
        return sb.toString();
    }

    public static void flush()
    {
        errors.clear();
    }

    public static void setPrintsOn()
    {
        doPrint = true;
    }

    public static void setPrintsOff()
    {
        doPrint = false;
    }
}
