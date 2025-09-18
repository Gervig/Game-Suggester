package app.security.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "users") // user is a reserved word in PostgreSQL, so we use users
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User"))
public class User implements ISecurityUser
{
    // Username og password er bare minimum når man skal kunne oprette sig

    @Id
    @Getter
    private String username;
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @Getter
    @Setter
    Set<Role> roles = new HashSet<>();

    public User(String username, String password)
    {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User()
    {
    }

    @Override
    public Set<String> getRolesAsStrings()
    {
        return Set.of();
    }

    @Override
    public boolean verifyPassword(String pw)
    {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role)
    {
        roles.add(role);
        role.getUsers().add(this);
    }

    @Override
    public void removeRole(String role)
    {
        for (Role roleEntity : roles)
        {
            if (roleEntity.getName().equals(role))
            {
                roles.remove(roleEntity);
                roleEntity.getUsers().remove(this);
            }
        }
    }
}
