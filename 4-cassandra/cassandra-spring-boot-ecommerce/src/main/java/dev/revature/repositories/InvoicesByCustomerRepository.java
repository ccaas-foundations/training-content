package dev.revature.repositories;

import dev.revature.models.InvoicesByCustomer;
import dev.revature.models.InvoicesByCustomerKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicesByCustomerRepository extends CassandraRepository<InvoicesByCustomer, InvoicesByCustomerKey> {

    public List<InvoicesByCustomer> findByInvoicesByCustomerKeyCustomerId(String customerId);

}
