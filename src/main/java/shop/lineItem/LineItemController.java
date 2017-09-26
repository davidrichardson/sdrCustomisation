package shop.lineItem;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.*;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import shop.order.Order;
import shop.order.OrderRepository;

import java.net.URI;

/*@Component
@RestController*/
@RequiredArgsConstructor
@ExposesResourceFor(LineItem.class)
public class LineItemController {

    @NonNull final private LineItemRepository lineItemRepository;
    @NonNull final private OrderRepository orderRepository;
    @NonNull final private RepositoryEntityLinks repositoryEntityLinks;

    @NonNull private final RepositoryEntityLinks entityLinks;
    @NonNull private final RepositoryRestConfiguration config;
    @NonNull private final HttpHeadersPreparer headersPreparer;
    @NonNull private final ApplicationEventPublisher publisher;

    @RequestMapping(value = "/orders/{orderId}/lineItems",method = RequestMethod.POST)
    public ResponseEntity<Resource<LineItem>> createLineItem(@PathVariable String orderId, @RequestBody LineItem lineItem){

        Order order = orderRepository.findOne(orderId);

        if (order == null){
            throw new ResourceNotFoundException();
        }

        lineItem.setOrder(order);

        publisher.publishEvent(new BeforeCreateEvent(lineItem));
        LineItem savedObject = lineItemRepository.insert(lineItem);
        publisher.publishEvent(new AfterCreateEvent(savedObject));

/*        PersistentEntityResource resource = returnBody ? assembler.toFullResource(savedObject) : null;

        HttpHeaders headers = headersPreparer.prepareHeaders(resource);
        addLocationHeader(headers, assembler, savedObject);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, resource);
*/

        //TODO trigger validation + events
        //Before create
        lineItemRepository.insert(lineItem);
        //After create

        Resource resource = new Resource<>(lineItem);
        URI createdUri = URI.create(repositoryEntityLinks.linkForSingleResource(lineItem).withSelfRel().getHref());

        return ResponseEntity.created(createdUri).body(resource);
    }

}
