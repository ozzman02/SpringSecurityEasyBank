package com.eazybytes;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncodingTests {

    static final String PASSWORD1 = "12345";

    static final String PASSWORD2 = "54321";

    @Test
    void testBcrypt15() {
        PasswordEncoder bcrypt = new BCryptPasswordEncoder(15);
        System.out.println(bcrypt.encode(PASSWORD1));
        System.out.println(bcrypt.encode(PASSWORD2));
    }
}
