package app.security.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "roles")
public class Role
{
    @Id
    private String name;
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public Role(String name)
    {
        this.name = name;
    }

    public Role()
    {
    }
}
