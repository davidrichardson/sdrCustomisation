package shop.order;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.HashMap;
import java.util.Map;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@Component
public class OrderResourceProcessor implements ResourceProcessor<Resource<Order>> {

    @Override
    public Resource<Order> process(Resource<Order> resource) {
        String orderId = resource.getContent().getId();

        Map<String, String> expansionParams = new HashMap<>();
        expansionParams.put("repository", "lineItems");

        Link lineItemsLink = linkTo(
                    methodOn(OrderContentsController.class)
                            .createOrderContents(
                                    orderId,
                                    null,
                                    null
                            )
            )
                    .withRel("lineItems:create")
                    .expand(expansionParams);

        resource.add(lineItemsLink);

        return resource;
    }
}
