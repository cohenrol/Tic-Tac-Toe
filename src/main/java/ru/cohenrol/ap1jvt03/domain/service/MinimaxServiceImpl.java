package ru.cohenrol.ap1jvt03.domain.service;

import org.springframework.stereotype.Service;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;

import static ru.cohenrol.ap1jvt03.domain.enums.CellStatus.*;

@Service
public class MinimaxServiceImpl implements MinimaxService {

    public void findAndApplyBestMove(GameEntity gameEntity) {
        CellStatus[][] board = gameEntity.getCells();
        int size = board.length;

        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == EMPTY) {

                    board[i][j] = O;
                    int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }

        if (bestRow != -1 && bestCol != -1) {
            board[bestRow][bestCol] = O;
        }
    }

    private int minimax(CellStatus[][] board, int depth, boolean isMax, int alpha, int beta) {
        if (depth > 5 && board.length > 3) return 0;

        if (hasWinnerForMinimax(board, O)) return 10 - depth;
        if (hasWinnerForMinimax(board, X)) return -10 + depth;
        if (isBoardFullForMinimax(board)) return 0;

        int size = board.length;

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = O;
                        int eval = minimax(board, depth + 1, false, alpha, beta);
                        board[i][j] = EMPTY;
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) break;
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = X;
                        int eval = minimax(board, depth + 1, true, alpha, beta);
                        board[i][j] = EMPTY;
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) break;
                    }
                }
            }
            return minEval;
        }
    }

    public boolean isBoardFullForMinimax(CellStatus[][] board) {
        for (CellStatus[] row : board) {
            for (CellStatus cell : row) {
                if (cell == EMPTY) return false;
            }
        }
        return true;
    }

    public boolean hasWinnerForMinimax(CellStatus[][] board, CellStatus player) {
        int size = board.length;

        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != player) { rowWin = false; break; }
            }
            if (rowWin) return true;
        }

        for (int j = 0; j < size; j++) {
            boolean colWin = true;
            for (int i = 0; i < size; i++) {
                if (board[i][j] != player) { colWin = false; break; }
            }
            if (colWin) return true;
        }

        boolean mainDiagWin = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) { mainDiagWin = false; break; }
        }
        if (mainDiagWin) return true;

        boolean sideDiagWin = true;
        for (int i = 0; i < size; i++) {
            if (board[i][size - 1 - i] != player) { sideDiagWin = false; break; }
        }
        return sideDiagWin;
    }
}
