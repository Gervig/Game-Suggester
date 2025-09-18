package app.security.daos;

import app.config.HibernateConfig;
import app.exceptions.ValidationException;
import app.populators.RolePopulator;
import app.populators.UserPopulator;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest // only works one by one, due to IDs being Strings
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final UserDAO userDAO = UserDAO.getInstance(emf);
    private static final RoleDAO roleDAO = RoleDAO.getInstance(emf);
    private static List<User> users = UserPopulator.populate();
    private static List<Role> roles = RolePopulator.populate();

    @BeforeEach
    void setup()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            // persists all users in the list
            users.forEach(userDAO::create);
            // persists all the roles in the list
            roles.forEach(roleDAO::createRole);
            em.getTransaction().commit();
        }
    }

    @AfterEach
    void teardown()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            // clears previous data
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void getInstance()
    {
        UserDAO instanceTest = UserDAO.getInstance(emf);
        assertNotNull(instanceTest);
        assertEquals(instanceTest, userDAO);
    }

    @Test
    void create()
    {
        User expected = new User("Test123", "PasswordTest123");
        User actual = userDAO.create(expected);

        assertEquals(expected, actual);
    }

    @Test
    void read()
    {
        String expected = users.get(0).getUsername();
        String actual = userDAO.read(expected).getUsername();

        assertEquals(expected, actual);
    }

    @Test
    void getVerifiedUser() throws ValidationException
    {
        String username = users.get(0).getUsername();
        String password = "Password123"; //password has to be plain text to be tested

        // verifies that user exists and password is correct, creates a new user DTO if successful
        UserDTO userDTO = userDAO.getVerifiedUser(username, password);

        // asserts that the DTO was created (not null)
        assertNotNull(userDTO);

        // asserts that the DTO has the same username as the one from the list
        assertEquals(userDTO.getUsername(), username);
    }

    @Test
    void readAll()
    {
        List<User> expected = userDAO.readAll();

        assertEquals(expected.size(), 2);
    }

    @Test
    void update()
    {
        User expected = users.get(0);

        expected.addRole(new Role("admin"));

        expected = userDAO.update(expected);

        assertEquals(expected.getRoles().size(), 2);
    }

    @Test
    void delete()
    {
        // reads all users
        List<User> userListBefore = userDAO.readAll();

        // asserts that the size is 2
        assertEquals(userListBefore.size(), 2);

        // finds a username and deletes it
        String username = users.get(0).getUsername();

        userDAO.delete(username);

        // asserts that the list has one less user
        List<User> userListAfter = userDAO.readAll();

        assertEquals(userListAfter.size(), 1);
    }
}