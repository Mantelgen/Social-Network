package com.example.socialnetworkgui.Domain.Exceptions;

public class DatabaseConnectionException extends RuntimeException {
  private static final String MESSAGE = "Failed to connect to the database";

  public DatabaseConnectionException() {
    super(MESSAGE);
  }
}
