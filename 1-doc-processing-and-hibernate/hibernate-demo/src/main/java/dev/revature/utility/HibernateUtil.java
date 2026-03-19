package dev.revature.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {


    //follows the singleton design pattern
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory(){
        return new Configuration().configure().buildSessionFactory();
    }

    public static Session getSession(){
        return sessionFactory.openSession();
    }

    public static void close(){
        sessionFactory.close();
    }

}
