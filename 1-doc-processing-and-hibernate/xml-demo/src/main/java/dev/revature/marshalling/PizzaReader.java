package dev.revature.marshalling;

import dev.revature.models.Pizza;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class PizzaReader {

    private final Unmarshaller unmarshaller;

    public PizzaReader(Unmarshaller unmarshaller){
        this.unmarshaller = unmarshaller;
    }

    public Pizza readPizza(){
        String pizzaPath = "src/main/resources/pizza.xml";
        try(FileInputStream fis = new FileInputStream(pizzaPath);){
            //Pizza pizzaFromFile = (Pizza) unmarshaller.unmarshal(new StreamSource(fis));
            Object objectFromFile = unmarshaller.unmarshal(new StreamSource(fis));
            if(objectFromFile instanceof Pizza pizzaFromFile){
                System.out.println(pizzaFromFile);
                return pizzaFromFile;
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return null;
    }


}
