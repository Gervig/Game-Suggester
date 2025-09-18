package app.config;

import app.security.entities.Role;
import app.security.entities.User;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig
{

    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    public static void setTest(Boolean test)
    {
        isTest = test;
    }

    public static Boolean getTest()
    {
        return isTest;
    }

    public static EntityManagerFactory getEntityManagerFactory()
    {
        if (emf == null)
            emf = createEMF(getTest());
        return emf;
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest()
    {
        if (emfTest == null)
        {
            setTest(true);
            emfTest = createEMF(getTest());  // No DB needed for test
        }
        return emfTest;
    }

    // TODO: IMPORTANT: Add Entity classes here for them to be registered with Hibernate
    private static void getAnnotationConfiguration(Configuration configuration)
    {
//             configuration.addAnnotatedClass();
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(User.class);
    }

    private static EntityManagerFactory createEMF(boolean forTest)
    {
        try
        {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            // Set the properties
            setBaseProperties(props);
            if (forTest)
            {
                props = setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null)
            {
                setDeployedProperties(props);
            } else
            {
                props = setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
            return emf;
        } catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Properties setBaseProperties(Properties props)
    {
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "create"); //create to wipe DB each time, update creates new data
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
        return props;
    }

    private static Properties setDeployedProperties(Properties props)
    {
        String DBName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props)
    {
        String DBName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String DBUsername = Utils.getPropertyValue("DB_USERNAME", "config.properties");
        String DBPassword = Utils.getPropertyValue("DB_PASSWORD", "config.properties");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", DBUsername);
        props.put("hibernate.connection.password", DBPassword);
        return props;
    }

    private static Properties setTestProperties(Properties props)
    {
        //props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.archive.autodetection", "class");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "create-drop"); // update for production
        return props;
    }
}
