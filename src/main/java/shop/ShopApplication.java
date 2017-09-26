package shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import shop.lineItem.BeforeCreateLineItemValidator;

@SpringBootApplication
@Configuration
@EnableMongoRepositories
@EnableMongoAuditing
public class ShopApplication extends RepositoryRestConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ShopApplication.class);
        app.run(args);
    }

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", new BeforeCreateLineItemValidator());
    }
}
