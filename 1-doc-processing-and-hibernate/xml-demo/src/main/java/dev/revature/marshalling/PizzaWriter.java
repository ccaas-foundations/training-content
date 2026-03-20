package dev.revature.marshalling;

import dev.revature.models.Pizza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

@Component
public class PizzaWriter {

    private final Marshaller marshaller;

    @Autowired
    public PizzaWriter(Marshaller m){
        this.marshaller = m;
    }

    public Pizza addPizza(Pizza pizza){
        //convert the pizza argument that's being passed into our method
        // to an xml representation of that pizza
        String path = "src/main/resources/pizza.xml";
        try (FileOutputStream fos = new FileOutputStream(path)){
            marshaller.marshal(pizza,new StreamResult(fos));
            return pizza;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
