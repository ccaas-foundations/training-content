package dev.revature.daos;

import dev.revature.models.Order;
import dev.revature.models.OrderStatus;
import dev.revature.utility.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderDaoImpl implements OrderDao{
    @Override
    public void create(Order order) {
        try(Session session = HibernateUtil.getSession();){
            Transaction tx = session.beginTransaction();
            session.persist(order);
            tx.commit();
        }
    }

    @Override
    public Order getOrderById(int id) {
        try(Session session = HibernateUtil.getSession()){
            return session.find(Order.class, id);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        //pure SQL -> SELECT * FROM PURCHASE_ORDER
        //HQL -> from Order
//        try(Session session = HibernateUtil.getSession()){
//            return session.createQuery("from Order", Order.class).list();
//        }

        //with Criteria
        try(Session session = HibernateUtil.getSession()){
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            cq.select(root);
            return session.createQuery(cq).list();
        }
    }

    @Override
    public List<Order> getOrdersByCustomer(int customerId) {
        try(Session session = HibernateUtil.getSession()){
            // select * from orders where customer_id = [id]
            return session.createQuery("from Order o where o.customer.id = :custId", Order.class)
                    .setParameter("custId",customerId)
                    .list();
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        try(Session session = HibernateUtil.getSession()){
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            //get the orderStatus field from the root and compare to the method argument status
            cq.select(root).where(cb.equal(root.get("orderStatus"), status));
            return session.createQuery(cq).list();
        }
    }

    @Override
    public List<Order> getOrdersByCustomerAndStatus(int CustomerId, OrderStatus status) {
        return null;
    }
}
