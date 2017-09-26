package shop.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import shop.lineItem.LineItem;

import java.net.URI;

@RepositoryRestController
public class OrderContentsController {


    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

    @RequestMapping(value = "/orders/{orderId}/{repository}", method = RequestMethod.POST)
    public ResponseEntity<PersistentEntityResource> createOrderContents(
            @PathVariable String orderId,
            @PathVariable String repository,
            PersistentEntityResource payload,
            PersistentEntityResourceAssembler assembler,
            RootResourceInformation resourceInformation
    ) {

        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            throw new ResourceNotFoundException("no such order");
        }

        LineItem lineItem = (LineItem) payload.getContent();

        lineItem.setOrder(order);

        publisher.publishEvent(new BeforeCreateEvent(lineItem));
        Object savedLineItem = resourceInformation.getInvoker().invokeSave(lineItem);
        publisher.publishEvent(new AfterCreateEvent(savedLineItem));

        PersistentEntityResource resource = assembler.toResource(savedLineItem);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink("self").expand().getHref()));

        ResponseEntity<PersistentEntityResource> responseEntity = new ResponseEntity<>(
                resource,
                httpHeaders,
                HttpStatus.CREATED
        );

        return responseEntity;
    }

}
