package org.soulrebel.company.repository;

import org.soulrebel.company.model.ProductionOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProductionOrders extends CrudRepository<ProductionOrder, Long> {
}
