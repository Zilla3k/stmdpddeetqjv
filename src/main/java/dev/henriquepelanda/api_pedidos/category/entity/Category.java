package dev.henriquepelanda.api_pedidos.category.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;

    protected Category()
    {
    }

    public Category
    (
        String name,
        String description
    )
    {
        this.name = name;
        this.description = description;
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void update(
            String name,
            String description
    ){
        this.name = name;
        this.description = description;
    }

}
