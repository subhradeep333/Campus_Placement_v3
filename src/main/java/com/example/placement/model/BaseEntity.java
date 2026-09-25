package com.example.placement.model;

/**
 * ABSTRACT CLASS demonstrating OOP INHERITANCE and ABSTRACTION.
 * Serves as the base class for all placement domain entities.
 */
public abstract class BaseEntity {
    private int id;
    private String createdAt;

    public BaseEntity() {}

    public BaseEntity(int id, String createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    // Encapsulated getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * ABSTRACT METHOD (Abstraction & Polymorphism).
     * Must be implemented by all concrete subclasses to convert entity to JSON string.
     */
    public abstract String toJson();
}
