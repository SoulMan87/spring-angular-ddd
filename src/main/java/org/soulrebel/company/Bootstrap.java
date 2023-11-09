package org.soulrebel.company;

import lombok.RequiredArgsConstructor;
import org.soulrebel.company.model.ProductionOrder;
import org.soulrebel.company.repository.ProductionOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Bootstrap implements ApplicationRunner {

    private final ProductionOrders repository;

    public static void main(String[] args) {
        SpringApplication.run (Bootstrap.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        repository.save (ProductionOrder.create ("Order 1"));
        repository.save (ProductionOrder.create ("Order 2"));
    }
}
