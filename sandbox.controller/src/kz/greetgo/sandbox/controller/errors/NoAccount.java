package kz.greetgo.sandbox.controller.errors;

public class NoAccount extends RestError {
  public NoAccount(int clientId) {
    super(404, "No active account found for client, id:" + clientId);
  }
}
