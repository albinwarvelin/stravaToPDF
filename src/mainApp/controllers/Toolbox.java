package mainApp.controllers;

public class Toolbox
{
    public static int findNIndexOf(char character, int n, String string)
    {
        int index = -1;

        for (int i = 0; i < n; i++)
        {
            index = string.indexOf(character, index + 1);
        }

        return index;
    }
}
