package com.partyhub.PartyHub.util;

import java.util.concurrent.ThreadLocalRandom;

public class PromoCodeGenerator {

    public static String generatePromoCode(String fullName) {
        if (fullName == null || fullName.split(" ").length < 2) {
            throw new IllegalArgumentException("Full name must contain at least two words");
        }

        String[] names = fullName.split(" ");
        String firstName = names[0];
        String lastName = names[1];

        String firstPart = firstName.length() > 2 ? firstName.substring(0, 3) : firstName;
        String secondPart = lastName.length() > 2 ? lastName.substring(0, 3) : lastName;
        String numbers = String.valueOf(ThreadLocalRandom.current().nextInt(100, 1000));

        return (firstPart + secondPart + numbers).toLowerCase();
    }
}