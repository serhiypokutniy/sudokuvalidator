package com.bosch.sast.sudoku.validator.service;

import com.bosch.sast.sudoku.validator.Constants;
import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import com.bosch.sast.sudoku.validator.mapper.SudokuMapper;
import com.bosch.sast.sudoku.validator.model.Board;
import com.bosch.sast.sudoku.validator.repository.SudokuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
@Service
public class ValidatorServiceImpl implements ValidatorService {

  private final SudokuRepository sudokuRepository;

  private final SudokuMapper sudokuMapper;

  public ValidatorServiceImpl(SudokuRepository sudokuRepository, SudokuMapper sudokuMapper) {
    this.sudokuRepository = sudokuRepository;
    this.sudokuMapper = sudokuMapper;
  }

  @Override
  public boolean isValidSudoku(long id) {
    log.info("Validating board for the id {}", id);
    return execute(() -> sudokuRepository
            .findById(id)
            .map(sudokuMapper::boardToBoardDto)
            .map(b -> isValidSudoku(b.getGrid()))
            .orElseThrow(() -> new IllegalArgumentException("No board found by " + id)));
  }

  @Override
  public BoardDTO saveBoard(NewBoardDTO board) {
    return execute(() -> {
        log.info("Saving new board");
        Board savedBoard = sudokuRepository.save(sudokuMapper.newBoardDtoToBoard(board));
        return new BoardDTO(savedBoard.getId(), board.getGrid());
    });
  }

  @Override
  public BoardDTO getBoard(long id) {
    log.info("Getting board for the id {}", id);
    return execute(() ->
            sudokuRepository.findById(id).map(sudokuMapper::boardToBoardDto).orElse(null));
  }

  @Override
  public boolean isValidSudoku(int[][] grid) {
    log.info("Validating grid");
    return execute(() -> validSudokuGrid(grid));
  }

  @Override
  public boolean deleteBoard(long boardId){
    log.info("Deleting board for the id {}", boardId);
    return execute(() -> {
        if (sudokuRepository.existsById(boardId)) {
          sudokuRepository.deleteById(boardId);
          return true;
        }
        return false;
    });
  }

  private static boolean validSudokuGrid(int[][] grid) {
    log.info("Validating grid");
    if(grid == null || grid.length != Constants.BOARD_SIZE || grid[0].length != Constants.BOARD_SIZE){
      log.warn("Invalid board received, grid length: " + (grid == null ? "null" : grid.length));
      return false;
    }
    Set<String> set = new HashSet<>();
    for(int i = 0; i < Constants.BOARD_SIZE; i++){
      for(int j = 0; j < Constants.BOARD_SIZE; j++){
        int ch = grid[i][j];
        if(ch != 0){
          if(!set.add("row " + i + " "+ ch) || !set.add("col " + j + " "+ ch) || !set.add("square " + (i/3) + "-" + (j/3) + " "+ ch)){
            return false;
          }
        }
      }
    }
    return true;
  }

  private<T> T execute(Supplier<T> supplier){
    try {
      return supplier.get();
    } catch (Exception exc) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing request caused by " + exc.getMessage(), exc);
    }
  }
}
