package shop.lineItem;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.hateoas.Identifiable;
import shop.order.Order;

@Data
public class LineItem  implements Identifiable<String> {

    private @Id String id;
    private @Version Long version;
    private String item;
    private Integer quantity;
    private @DBRef Order order;


}
