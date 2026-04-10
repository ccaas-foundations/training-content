package dev.revature.repositories;

import dev.revature.models.WarehouseQueueEntry;
import dev.revature.models.WarehouseQueueKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseQueueRepository extends CassandraRepository<WarehouseQueueEntry, WarehouseQueueKey> {

    List<WarehouseQueueEntry> findByWarehouseQueueKeyWarehouseAndWarehouseQueueKeyStatus(String warehouse, String status);

}
