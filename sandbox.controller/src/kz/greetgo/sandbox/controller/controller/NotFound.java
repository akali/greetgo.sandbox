package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.sandbox.controller.errors.RestError;

public class NotFound extends RestError {
  public NotFound() {
    super(404, "Not found exception");
  }
}
