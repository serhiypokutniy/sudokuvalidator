package com.bosch.sast.sudoku.validator.controller;

import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Please think about other possible operations.
 */
public interface ValidatorController {

  @GetMapping(path = "/board/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.OK)
  BoardDTO getBoard(@PathVariable("id") long id);

  @GetMapping(path = "/board/{id}/isvalid")
  @ResponseStatus(code = HttpStatus.OK)
  boolean validateBoard(@PathVariable("id") long id);

  @PostMapping(path = "/board", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.OK)
  BoardDTO addBoard(@RequestBody NewBoardDTO boardDTO);

  @DeleteMapping(path = "/delete/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  boolean deleteBoard(@PathVariable("id") long id);
}
