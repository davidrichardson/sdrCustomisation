package shop.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.hateoas.Identifiable;


@Data
public class Order implements Identifiable<String> {

    private @Id String id;
    private @Version Long version;
    private String orderName;
}
