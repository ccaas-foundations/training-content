package dev.revature;

import dev.revature.daos.OrderDao;
import dev.revature.daos.OrderDaoImpl;
import dev.revature.models.Customer;
import dev.revature.models.Order;
import dev.revature.models.OrderStatus;
import dev.revature.utility.HibernateUtil;
import org.h2.tools.Server;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


public class HibernateDemoApplication {

    public HibernateDemoApplication() {
    }

    public static void main(String[] args) {


        try {
            Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Session seedSession = HibernateUtil.getSession();
        Transaction tx = seedSession.beginTransaction();
        Customer c1 = new Customer("Jane Doe", "jdoe@gmail.com");
        Customer c2 = new Customer("Paul Smith", "psmith25@gmail.com");
        seedSession.persist(c1);
        seedSession.persist(c2);
        tx.commit();
        seedSession.close();

        OrderDao orderDao = new OrderDaoImpl();
        Order newOrder1 = new Order(c1, "shirt", OrderStatus.PENDING, LocalDateTime.now());
        Order newOrder2 = new Order(c1, "pants", OrderStatus.CONFIRMED, LocalDateTime.now());
        Order newOrder3 = new Order(c2, "mug", OrderStatus.FULFILLED, LocalDateTime.now());
        Order newOrder4 = new Order(c2, "shirt", OrderStatus.CONFIRMED, LocalDateTime.now());


        orderDao.create(newOrder1);
        orderDao.create(newOrder2);
        orderDao.create(newOrder3);
        orderDao.create(newOrder4);

//        System.out.println(orderDao.getOrderById(1));

//        List<Order> orderList = orderDao.getAllOrders();

//        List<Order> orderList = orderDao.getOrdersByCustomer(1);

        List<Order> orderList = orderDao.getOrdersByStatus(OrderStatus.CONFIRMED);
        for(Order o: orderList){
            System.out.println(o);
        }





        /*
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

        // even if we don't explicitly tell hibernate to make an update,
            // because o1 and o2 are persistent, they will be updated
            // (automatic dirty checking)
        o1.setOrderStatus(OrderStatus.FULFILLED);
        o2.setSku("jacket");

        tx.commit();

        session.close();
        // once my session closes, these orders are no longer managed by a Hib session
        //DETACHED STATE

        Session session2 = sessionFactory.openSession();
        Customer fetchedCustomer1 = session2.find(Customer.class,1);
        Customer fetchedCustomer2 = session2.find(Customer.class,1);
        System.out.println("Do these customers have the same values? "+(fetchedCustomer1.equals(fetchedCustomer2)));
        System.out.println("Are these customers the same Customer instance? "+(fetchedCustomer1 == fetchedCustomer2));
        session2.close();

        sessionFactory.close();
        */


    }

}
