package utils;

/**
 * Utils
 * Description: Final class that contains any helper methods used throughout the PMO system
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: July 25th 2026
 */

public final class Utils {

    // Don't ever need an instance of this class
    private Utils() {
    }

    /**
     * Description: Checks if a string is also an int and returns a boolean result
     * Pre-Condition: Param is a string
     * Post-Condition: Boolean is returned true if param is also an int or false if not
     * @param str The string being checked
     * @return The result of the check
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
