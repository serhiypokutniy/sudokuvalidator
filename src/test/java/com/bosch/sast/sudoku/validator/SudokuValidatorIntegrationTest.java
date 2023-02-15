package com.bosch.sast.sudoku.validator;

import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import com.bosch.sast.sudoku.validator.mapper.SudokuMapper;
import com.bosch.sast.sudoku.validator.model.Board;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SudokuValidatorIntegrationTest {
    private static final int[][] THE_ONE_BOARD_GRID = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {9, 8, 7, 1, 2, 3, 4, 5, 6},
            {4, 5, 6, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}};

    private static final int[][] THE_BAD_BOARD_GRID = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {9, 8, 7, 1, 2, 3, 4, 5, 6},
            {4, 5, 6, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {9, 0, 0, 0, 0, 0, 0, 0, 0}};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SudokuMapper sudokuMapper;

    @Test
    void boardToBoardDtoMapper(){
        Board b = new Board();
        b.setId(1L);
        b.setCells(IntStream.range(0, 81).boxed().map(e -> 1).collect(Collectors.toList()));
        BoardDTO dto = sudokuMapper.boardToBoardDto(b);
        Assertions.assertNotNull(dto.getGrid());
        Assertions.assertEquals(dto.getGrid().length, Constants.BOARD_SIZE);
    }

    @Test
    void newBoardDtoToBoardMapper(){
        NewBoardDTO dto = new NewBoardDTO();
        dto.setGrid(THE_ONE_BOARD_GRID);
        Board b = sudokuMapper.newBoardDtoToBoard(dto);
        Assertions.assertNotNull(b.getCells());
        Assertions.assertEquals(b.getCells().size(), Constants.BOARD_SIZE * Constants.BOARD_SIZE);
    }

    @Test
    void uploadingTheOneBoard() throws Exception {
        final var jsonBody = new ObjectMapper().writeValueAsString(new NewBoardDTO().setGrid(THE_ONE_BOARD_GRID));
        this.mockMvc.perform(post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(isA(Integer.class)))
                .andExpect(jsonPath("$.grid").value(isJsonArray(THE_ONE_BOARD_GRID)));
    }

    private Matcher<Iterable<? extends Iterable<? extends Integer>>> isJsonArray(int[][] grid) {
        return contains(Arrays.stream(grid)
                .map(array -> contains(Arrays.stream(array).mapToObj(Matchers::is).collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    @Test
    void retrievingAJustUploadedBoard() throws Exception {
        long id = upload(THE_ONE_BOARD_GRID);
        this.mockMvc.perform(get("/board/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void theValidBoardIsValid() throws Exception {
        long id = upload(THE_ONE_BOARD_GRID);
        this.mockMvc.perform(get("/board/{id}/isvalid", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void theBadBoardIsInvalid() throws Exception {
        long id = upload(THE_BAD_BOARD_GRID);
        this.mockMvc.perform(get("/board/{id}/isvalid", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void deleteBoard() throws Exception {
        long id = upload(THE_ONE_BOARD_GRID);
        this.mockMvc.perform(delete("/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    private long upload(int[][] board) throws Exception {
        final var jsonBody = new ObjectMapper().writeValueAsString(new NewBoardDTO().setGrid(board));
        final var mvcResult = this.mockMvc.perform(post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();
        final var returnedBody = mvcResult.getResponse().getContentAsString();
        final var resultingBoard = new ObjectMapper().readValue(returnedBody, BoardDTO.class);
        return resultingBoard.getId();
    }
}