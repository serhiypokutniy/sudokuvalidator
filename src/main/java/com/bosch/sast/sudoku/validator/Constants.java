package com.bosch.sast.sudoku.validator;

public class Constants {

  static {
    io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
  }
  public static final int BOARD_SIZE = 9;
  public static final String HYSTRIX_GROUP = "SUDOKU_VALIDATOR";
}
