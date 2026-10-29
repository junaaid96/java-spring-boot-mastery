package dev.junaaid96.mvc.service;

// Immutable request/response model (record) replacing the old mutable Programmer bean.
public record Programmer(int id, String name, String language) {}
