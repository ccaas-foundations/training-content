package dev.revature.repositories;

import dev.revature.models.InvoiceEvent;
import dev.revature.models.InvoiceEventKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceEventRepository extends CassandraRepository<InvoiceEvent, InvoiceEventKey> {

    List<InvoiceEvent> findByInvoiceEventKeyInvoiceId(UUID invoiceId);

}
