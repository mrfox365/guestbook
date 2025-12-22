package com.example.core.domain;

public record PageRequest(int page, int size, String sortField, String query) {
    public int getOffset() { return (page - 1) * size; }
}