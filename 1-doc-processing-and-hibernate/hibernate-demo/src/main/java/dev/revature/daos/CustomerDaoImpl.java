package dev.revature.daos;

import dev.revature.models.Customer;
import dev.revature.utility.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public void create(Customer customer) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(customer);
            tx.commit();
        }
    }

    @Override
    public List<Customer> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
            Root<Customer> root = cq.from(Customer.class);
            cq.select(root);
            return session.createQuery(cq).list();
        }
    }

    @Override
    public Customer update(Customer customer) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Customer updated = session.merge(customer);
            tx.commit();
            return updated;
        }
    }

}
