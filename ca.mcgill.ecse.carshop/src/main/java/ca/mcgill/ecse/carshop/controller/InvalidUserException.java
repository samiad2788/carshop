package ca.mcgill.ecse.carshop.controller;

public class InvalidUserException extends Exception{
  public InvalidUserException (String message) {
    super(message);
  }
}
