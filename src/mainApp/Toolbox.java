package mainApp;

public class Toolbox
{
    /* Finds index of N specified char */
    public static int findNIndexOf(char character, int n, String string)
    {
        int index = -1;

        for (int i = 0; i < n; i++)
        {
            index = string.indexOf(character, index + 1);
        }

        return index;
    }

    /* Removes all characters of specified type */
    public static String removeAllOfChar(String character, String string)
    {
        while (string.contains(character))
        {
            string = string.substring(0, string.indexOf(character)) + string.substring(string.indexOf(character) + 1);
        }

        return string;
    }
}
