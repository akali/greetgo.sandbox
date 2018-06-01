package kz.greetgo.sandbox.controller.errors;

public class InvalidClientData extends RestError {
  public InvalidClientData(String message) {
    super(400, message);
  }
}
