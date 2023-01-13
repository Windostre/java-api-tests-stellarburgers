package utils;

import com.github.javafaker.Faker;
import pojo.User;

public class Utils {
    Faker faker = new Faker();
    public User generateRandomUser() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6,12,true,true,true);
        String name = faker.name().firstName();
        return new User(email, password, name);

    }
}
