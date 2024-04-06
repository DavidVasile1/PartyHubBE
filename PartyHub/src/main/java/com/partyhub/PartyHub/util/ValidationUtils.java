package com.partyhub.PartyHub.util;
public class ValidationUtils {
    public static boolean isValidPromoCode(String promoCode) {
        if (promoCode == null || promoCode.length() != 9) {
            return false;
        }
        for (int i = 0; i < promoCode.length(); i++) {
            char ch = promoCode.charAt(i);
            if (!Character.isLowerCase(ch) && !Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
}
