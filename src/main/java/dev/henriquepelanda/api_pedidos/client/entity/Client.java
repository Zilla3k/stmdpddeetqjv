package dev.henriquepelanda.api_pedidos.client.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String document;
    private String password;

    protected Client()
    {
    }

    public Client
    (
        String name,
        String email,
        String document,
        String password
    )
    {
        this.name = name;
        this.email = email;
        this.document = document;
        this.password = password;
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getDocument()
    {
        return document;
    }

    public void update
    (
        String name,
        String email,
        String document,
        String password
    )
    {
        this.name = name;
        this.email = email;
        this.document = document;
        this.password = password;
    }
}
