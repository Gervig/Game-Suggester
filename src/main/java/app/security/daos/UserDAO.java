package app.security.daos;

import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDAO
{
    private static EntityManagerFactory emf;
    private static UserDAO instance;

    // singleton **
    public UserDAO()
    {
    }

    public static UserDAO getInstance(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
            instance = new UserDAO();
        }
        return instance;
    }
    // ** singleton


    public User create(User user)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            Set<Role> newRoleSet = new HashSet<>();
            if (user.getRoles().isEmpty())
            {
                Role userRole = em.find(Role.class, "user");
                if (userRole == null)
                {
                    // sets default user role
                    userRole = new Role("user");
                    em.persist(userRole);
                }
                user.addRole(userRole);
            }
            user.getRoles().forEach(role ->
            {
                Role foundRole = em.find(Role.class, role.getName());
                if (foundRole == null)
                {
                    throw new EntityNotFoundException("No role found with that id");
                } else
                {
                    newRoleSet.add(foundRole);
                }
            });
            user.setRoles(newRoleSet);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        return user;
    }

    public User read(String name)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            User user = em.find(User.class, name);
            if (user == null)
            {
                throw new NullPointerException();
            }
            return user;
        } catch (Exception e)
        {
            throw new ApiException(404, "Error user not found", e);
        }
    }

    public UserDTO getVerifiedUser(String username, String password) throws ValidationException
    {
        try (EntityManager em = emf.createEntityManager())
        {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        }
    }

    public List<User> readAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding users", e);
        }
    }

    public User update(User user)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error updating user", e);
        }
    }

    public void delete(String name)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            try
            {
                em.getTransaction().begin();
                User user = em.find(User.class, name);
                if (user == null)
                {
                    em.getTransaction().rollback();
                    throw new NullPointerException();
                }
                em.remove(user);
                em.getTransaction().commit();
            } catch (Exception e)
            {
                throw new ApiException(401, "Error deleting User", e);
            }
        }
    }

}
