package client;

public record ServerResponse<T>(T body, int status) {}

