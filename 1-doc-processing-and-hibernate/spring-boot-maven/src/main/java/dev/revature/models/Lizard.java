package dev.revature.models;

import java.util.Objects;

public class Lizard {

    private long id;
    private String name;
    private String color;
    private double weight;


//    generated my model code using intellij alt+insert shortcut
    public Lizard() {
    }

    public Lizard(long id, String name, String color, double weight) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lizard lizard = (Lizard) o;
        return id == lizard.id && Double.compare(weight, lizard.weight) == 0 && Objects.equals(name, lizard.name) && Objects.equals(color, lizard.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, weight);
    }

    @Override
    public String toString() {
        return "Lizard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", weight=" + weight +
                '}';
    }
}
