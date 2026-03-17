package dev.revature;

import dev.revature.marshalling.PizzaReader;
import dev.revature.marshalling.PizzaWriter;
import dev.revature.models.Pizza;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SetupRunner implements CommandLineRunner {

    private final PizzaWriter pizzaWriter;
    private final PizzaReader pizzaReader;

    public SetupRunner(PizzaWriter pizzaWriter, PizzaReader pizzaReader){
        this.pizzaWriter = pizzaWriter;
        this.pizzaReader = pizzaReader;
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("hello world from my setup runner");
//        List<String> toppings = List.of("Cheese", "Pepperoni");
//        Pizza newPizza = new Pizza(5,toppings, "johndoe@gmail.com");
//        pizzaWriter.addPizza(newPizza);

        Pizza pizza = pizzaReader.readPizza();
        System.out.println("got pizza in run method from "+ pizza.getCustomerEmail());



    }
}
