package com.bosch.sast.sudoku.validator.mapper;

import com.bosch.sast.sudoku.validator.Constants;
import com.bosch.sast.sudoku.validator.dto.BoardDTO;
import com.bosch.sast.sudoku.validator.dto.NewBoardDTO;
import com.bosch.sast.sudoku.validator.model.Board;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * As H2 DB doesn't really allow storing the "grid" IE the 2d arrays we will
 * need some trickery to map between the DTOs and entities.
 * Any ideas how to make it smarter?
 */

@Component
public class SudokuMapper {

    private final ModelMapper modelMapper;

    public SudokuMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper
                .createTypeMap(Board.class, BoardDTO.class)
                .setPostConverter(c -> c.getDestination().setGrid(cellsToGrid(c.getSource().getCells())));
        this.modelMapper
                .createTypeMap(NewBoardDTO.class, Board.class)
                .setPostConverter(c -> {
                    c.getDestination().setCells(gridToCells(c.getSource().getGrid()));
                    return c.getDestination();
                });
    }
    private static List<Integer> gridToCells(int[][] grid){
        if(grid == null){
            return new ArrayList<>();
        }
        return Arrays.stream(grid)
                .flatMap(e -> Arrays.stream(e).boxed())
                .collect(Collectors.toList());
    }
    private static int[][] cellsToGrid(List<Integer> cells) {
        if (cells == null || cells.size() != Constants.BOARD_SIZE * Constants.BOARD_SIZE) {
           return new int[0][0];
        }
        int[][] grid = new int[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        int cnt = 0;
        for (Integer cell : cells) {
            int column = cnt / Constants.BOARD_SIZE;
            int row = cnt % Constants.BOARD_SIZE;
            grid[column][row] = cell;
            cnt++;
        }
        return grid;
    }

    public BoardDTO boardToBoardDto(Board board){
        return modelMapper.map(board, BoardDTO.class);
    }

    public Board newBoardDtoToBoard(NewBoardDTO newBoardDTO){
        return modelMapper.map(newBoardDTO, Board.class);
    }
}
