package dev.revature;

import dev.revature.models.Order;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDateTime;



public class HibernateDemoApplication {

    public static void main(String[] args) {


        try {
            Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // we need a sessionfactory to connect to our database
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Transaction tx = session.beginTransaction();

        Order o1 = new Order(3,"pants", LocalDateTime.now());
        Order o2 = new Order(4,"shirt", LocalDateTime.now());

        session.persist(o1);
        session.persist(o2);

        tx.commit();

        session.close();
        sessionFactory.close();



    }

}
