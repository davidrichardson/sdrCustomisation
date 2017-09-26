package shop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import shop.lineItem.LineItemRepository;
import shop.order.OrderRepository;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShopTest {


    @LocalServerPort
    private int port;
    private static final String SERVICE_URI = "http://localhost:%s";

    private Traverson traverson;

    private RestTemplate restTemplate = new RestTemplate();
    private MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    private ParameterizedTypeReference<Resource<LineItem>> lineItemReturnType = new ParameterizedTypeReference<Resource<LineItem>>() {
    };
    private ParameterizedTypeReference<Resource<Order>> orderReturnType = new ParameterizedTypeReference<Resource<Order>>() {
    };

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private LineItemRepository lineItemRepository;

    @Before
    public void buildUp() {
        traverson = new Traverson(URI.create(String.format(SERVICE_URI, port)), MediaTypes.HAL_JSON);

        messageConverter.getObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        restTemplate.getMessageConverters().add(messageConverter);
    }


    @Test
    public void postOrder() {

        Order order = new Order();
        order.setOrderName("postOrder");

        ResponseEntity<Order> response = postOrder(order);

        System.out.println(response);
    }

    @Test
    public void postOrderAndLineItem_classic() {
        Order order = new Order();
        order.setOrderName("postOrderAndLineItem_classic");

        ResponseEntity<Order> response = postOrder(order);
        URI orderUri = response.getHeaders().getLocation();

        LineItem lineItem = new LineItem();
        lineItem.setItem("fork handle");
        lineItem.setQuantity(1);
        lineItem.setOrder(orderUri.toString());

        URI lineItemsUri = URI.create(traverson.follow("lineItems").asLink().getHref());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LineItem> request = new HttpEntity<>(lineItem, headers);
        String string = restTemplate.postForObject(lineItemsUri, request, String.class);

        System.out.println(string);


    }

    @Test
    public void portOrderAndLineItem_desired() {
        Order order = new Order();
        order.setOrderName("postOrderAndLineItem_desired");

        ResponseEntity<Order> response = postOrder(order);
        URI orderUri = response.getHeaders().getLocation();

        LineItem lineItem = new LineItem();
        lineItem.setItem("candle");
        lineItem.setQuantity(4);

        Traverson orderTraverson = new Traverson(orderUri, MediaTypes.HAL_JSON);
        URI lineItemsUri = URI.create(orderTraverson.follow("lineItems:create").asLink().getHref());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LineItem> request = new HttpEntity<>(lineItem, headers);

        String string = restTemplate.postForObject(lineItemsUri, request, String.class);

        System.out.println(string);


    }

    private ResponseEntity<Order> postOrder(Order order) {
        URI ordersUri = URI.create(traverson.follow("orders").asLink().getHref());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        return restTemplate.postForEntity(ordersUri, request, Order.class);
    }


    @Data
    public static class Order {
        private String orderName;
    }

    @Data
    public static class LineItem {
        private String item;
        private Integer quantity;
        private String order;
    }


}
