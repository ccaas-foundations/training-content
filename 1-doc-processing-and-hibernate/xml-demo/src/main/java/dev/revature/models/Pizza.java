package dev.revature.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class Pizza {

    private int id;

    private List<String> toppings;

    private String customerEmail;

    public Pizza() {
    }

    public Pizza(int id, List<String> toppings, String customerEmail) {
        this.id = id;
        this.toppings = toppings;
        this.customerEmail = customerEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name="topping")
    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "id=" + id +
                ", toppings=" + toppings +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}
