package com.bosch.sast.sudoku.validator.controller;

import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidatorControllerImpl implements ValidatorController {

  @Override
  public BoardDTO getBoard(long id) {
    return null;
  }

  @Override
  public boolean validateBoard(long id) {
    return false;
  }

  @Override
  public BoardDTO addBoard(NewBoardDTO newBoardDTO) {
    return null;
  }
}
