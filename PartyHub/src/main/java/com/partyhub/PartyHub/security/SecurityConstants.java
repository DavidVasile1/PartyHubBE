package com.partyhub.PartyHub.security;

import org.springframework.security.crypto.codec.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 7 * 24 * 60 * 60 * 1000;;
    public static final String JWT_SECRET = "6A586E3272357538782F413F4428472B4B61"
            + "50645367566B5970337336763979244226452948404D6351655468576D5A7134743"
            + "777217A25432A462D4A614E645267556A586E3272357538782F413F4428472B4B62"
            + "50655368566D5970337336763979244226452948404D635166546A576E5A7234743"
            + "777217A25432A462D4A";

    public static Key hmacShaKeyFor() {
        byte[] secretBytes = Base64.decode(JWT_SECRET.getBytes());
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

}