package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.illnesses.IllnessEntity;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillEntity;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Utility class for Hibernate ORM operations and database management.
 * 
 * <p>This class provides a centralized interface for database operations using
 * Hibernate ORM. It manages SessionFactory creation, session lifecycle, and
 * transaction handling. The class is configured to work with a MySQL database
 * and includes all entity mappings for the PawsOfTheForest plugin.</p>
 * 
 * <p>Key features include:</p>
 * <ul>
 *   <li>Automatic session management with resource cleanup</li>
 *   <li>Transaction handling with automatic commit/rollback</li>
 *   <li>Player entity caching integration</li>
 *   <li>Pre-configured database connection settings</li>
 * </ul>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class HibernateUtils {

    /**
     * Executes a callback function with a Hibernate session.
     * 
     * <p>This method handles session lifecycle automatically, opening a new
     * session, passing it to the callback, and ensuring proper cleanup by
     * closing the session when the callback completes or throws an exception.</p>
     * 
     * @param callback the function to execute with the session
     */
    public static void withSession(Consumer<Session> callback) {
        try (Session session = getSessionFactory().openSession()) {
            callback.accept(session);
        }
    }

    /**
     * Executes a callback function within a database transaction.
     * 
     * <p>This method handles both session and transaction lifecycle, opening
     * a session, beginning a transaction, executing the callback, and committing
     * the transaction. If the callback returns a PlayerEntity, it automatically
     * updates the player cache.</p>
     * 
     * @param <T> the return type of the callback function
     * @param callback the function to execute within the transaction
     */
    public static <T> void withTransaction(BiFunction<Transaction, Session, T> callback) {
        withSession(session -> {
            var transaction = session.beginTransaction();
            T obj = callback.apply(transaction, session);
            // Update player cache if the result is a PlayerEntity
            if (obj instanceof PlayerEntity player) {
                EventsCore.PLAYERS_CACHE.put(player.getUuid(), player);
            }
            transaction.commit();
        });
    }

    /**
     * The singleton SessionFactory instance used for all database operations.
     * Initialized once during class loading.
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Builds and configures the Hibernate SessionFactory.
     * 
     * <p>This method sets up the database connection configuration, registers
     * all entity classes, and creates the SessionFactory for the application.
     * The configuration includes MySQL-specific settings and entity mappings.</p>
     * 
     * @return the configured SessionFactory
     * @throws ExceptionInInitializerError if SessionFactory creation fails
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Build the service registry with database configuration
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                    .applySetting("hibernate.connection.url", "jdbc:mysql://localhost:3306/pawsoftheforest_dev?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true")
                    .applySetting("hibernate.connection.username", "root")
                    .applySetting("hibernate.connection.password", "mysql")
                    .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                    .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                    .applySetting("hibernate.show_sql", "false")
                    .applySetting("hibernate.format_sql", "false")
                    .build();

            // Register all entity classes with Hibernate
            MetadataSources sources = new MetadataSources(registry);

            sources.addAnnotatedClass(PlayerEntity.class);
            sources.addAnnotatedClass(SettingsEntity.class);
            sources.addAnnotatedClass(SkillBranchEntity.class);
            sources.addAnnotatedClass(SkillEntity.class);
            sources.addAnnotatedClass(IllnessEntity.class);

            // Build the SessionFactory from metadata
            Metadata metadata = sources.getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns the singleton SessionFactory instance.
     * 
     * @return the SessionFactory for database operations
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Shuts down the Hibernate SessionFactory and releases all resources.
     * 
     * <p>This method should be called during plugin shutdown to ensure
     * proper cleanup of database connections and resources.</p>
     */
    public static void shutdown() {
        sessionFactory.close();
    }

}
