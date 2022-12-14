package de.base2code.clansystem.utils;

public class Utils {
        public static boolean isAlphaNumeric(String s) {
                return s != null && s.matches("^[a-zA-Z0-9]*$");
        }
}
