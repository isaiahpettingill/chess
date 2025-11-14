package client;

public record ResponseEpistle<T>(T body, int status) {}

