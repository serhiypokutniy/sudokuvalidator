package com.bosch.sast.sudoku.validator.service;

import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import org.springframework.stereotype.Service;

@Service
public class ValidatorServiceImpl implements ValidatorService {

  @Override
  public boolean isValidSudoku(long boardId) {
    return false;
  }

  @Override
  public boolean isValidSudoku(int[][] grid) {
    return false;
  }

  @Override
  public BoardDTO saveBoard(NewBoardDTO board) {
    return null;
  }

  @Override
  public BoardDTO getBoard(long id) {
    return null;
  }
}
