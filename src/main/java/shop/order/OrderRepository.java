package shop.order;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource
public interface OrderRepository extends MongoRepository<Order,String> {
}
