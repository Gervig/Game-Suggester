package app.populators;

import app.security.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class RolePopulator
{
    public static List<Role> populate()
    {
        List<Role> roles = new ArrayList<>();

        Role admin = new Role("admin");

        roles.add(admin);

        return roles;
    }
}
