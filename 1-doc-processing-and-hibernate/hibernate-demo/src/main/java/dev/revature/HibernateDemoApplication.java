package dev.revature;

import dev.revature.models.Customer;
import dev.revature.models.Order;
import dev.revature.models.OrderStatus;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDateTime;



public class HibernateDemoApplication {

    public HibernateDemoApplication() {
    }

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

        Customer c1 = new Customer("Jane Doe", "jdoe@gmail.com");
        Customer c2 = new Customer("Paul Smith", "psmith25@gmail.com");

        session.persist(c1);
        session.persist(c2);

        Order o1 = new Order(c1,"pants", OrderStatus.PENDING, LocalDateTime.now());
        Order o2 = new Order(c2,"shirt", OrderStatus.PENDING, LocalDateTime.now());
        // we've created 2 Order objects - these objects are not yet tracked by a Hib Session
        // TRANSIENT STATE

        session.persist(o1);
        session.persist(o2);
        // my orders are now persisted to my session - Hibernate is tracking these objects
        // PERSISTENT STATE

        tx.commit();

        session.close();
        // once my session closes, these orders are no longer managed by a Hib session
        //DETACHED STATE


        sessionFactory.close();



    }

}
