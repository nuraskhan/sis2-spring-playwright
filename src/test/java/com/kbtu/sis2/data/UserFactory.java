package com.kbtu.sis2.data;

public class UserFactory {

    public record UserData(
            String firstName, String lastName, String email, String password,
            String dob, String country, String postcode, String houseNumber,
            String street, String city, String state, String phone
    ) {}

    public static UserData generateUser() {
        long ts = System.currentTimeMillis();
        return new UserData(
                "Test", "User",
                "testuser" + ts + "@test.com",
                "Qz9$kL" + String.valueOf(ts).substring(String.valueOf(ts).length() - 4) + "xR!",
                "1990-01-01",
                "KZ", "050000", "42",
                "Abay Ave", "Almaty", "Almaty", "77001234567"
        );
    }
}
