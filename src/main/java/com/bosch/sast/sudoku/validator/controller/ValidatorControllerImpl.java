package com.bosch.sast.sudoku.validator.controller;

import com.bosch.sast.sudoku.validator.Constants;
import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import com.bosch.sast.sudoku.validator.service.ValidatorService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ValidatorController", description = "Controller used by SudokuValidator application")
@RestController
public class ValidatorControllerImpl implements ValidatorController {

  private final ValidatorService validatorService;

  public ValidatorControllerImpl(ValidatorService validatorService) {
    this.validatorService = validatorService;
  }

  @HystrixCommand(groupKey = Constants.HYSTRIX_GROUP)
  @Timed(value = "get.board.time", description = "Time required for getting the board for the specified ID")
  @Operation(summary = "Gets board for the specified ID")
  @Override
  public BoardDTO getBoard(long id) {
    return validatorService.getBoard(id);
  }

  @HystrixCommand(groupKey = Constants.HYSTRIX_GROUP)
  @Timed(value = "validate.time", description = "Time required to validate the board with the specified ID")
  @Operation(summary = "Validates board for the specified ID and returns result as a boolean")
  @Override
  public boolean validateBoard(long id) {
    return validatorService.isValidSudoku(id);
  }

  @Timed(value = "add.board.time", description = "Time required for adding a new board")
  @HystrixCommand(groupKey = Constants.HYSTRIX_GROUP)
  @Operation(summary = "Adds a new board and returns a new boardDTO object with the resulting ID")
  @Override
  public BoardDTO addBoard(NewBoardDTO newBoardDTO) {
    return validatorService.saveBoard(newBoardDTO);
  }

  @HystrixCommand(groupKey = Constants.HYSTRIX_GROUP)
  @Timed(value = "delete.board.time", description = "Time required for deleting the board for the specified ID")
  @Operation(summary = "Deletes the board for the specified ID")
  @Override
  public boolean deleteBoard(long id) {
    return validatorService.deleteBoard(id);
  }
}
