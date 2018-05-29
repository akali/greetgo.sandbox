package kz.greetgo.sandbox.controller.errors;

public class InvalidRequestDetails extends RestError {
  public InvalidRequestDetails() {
    super(400, "Invalid request details");
  }
}
