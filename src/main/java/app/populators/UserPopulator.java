package app.populators;

import app.security.entities.Role;
import app.security.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserPopulator
{
    public static List<User> populate()
    {
        List<User> users = new ArrayList<>();

        User user1 = new User("User1", "Password123");
        User user2 = new User("User2", "Password456");

        users.add(user1);
        users.add(user2);

        return users;
    }
}
