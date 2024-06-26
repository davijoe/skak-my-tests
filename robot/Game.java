package robot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Game {

    int[][] board = new int[8][8];

    int[][] moves = new int[1000][5];
    int[][] movesd1 = new int[1000][5];
    int[][] movesd2 = new int[1000][5];
    int[][] movesd3 = new int[1000][5];
    int[][] movesd4 = new int[1000][5];
    int[][] movesd5 = new int[1000][5];
    int[][] movesd6 = new int[1000][5];
    int[][] movesd7 = new int[1000][5];

    int generateMoveCounter = 0;
    int enPassant;
    boolean whiteLongCastle, whiteShortCastle, blackShortCastle, blackLongCastle = true;
    private int kingRow, kingCol;
    char currentPlayer;
    int heuristicValue;

    public Game() {

    }

    public boolean isTileEmpty(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8 && board[row][col] == 0;
    }

    public void generateMoves(int[][] moves) {
        if (currentPlayer == 'w') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == 1) {
                        generateRookMoves(row, col, moves);
                    }
                    if (board[row][col] == 2) {
                        generateKnightMoves(row, col, moves);
                    }
                    if (board[row][col] == 3) {
                        generateBishopMoves(row, col, moves);
                    }
                    if (board[row][col] == 4) {
                        generateQueenMoves(row, col, moves);
                    }
                    if (board[row][col] == 5) {
                        generateKingMoves(row, col, moves);
                    }
                    if (board[row][col] == 6 || board[row][col] == 7) {
                        generatePawnMoves(row, col, moves);
                    }
                }
            }
        }
        if (currentPlayer == 'b') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == 8) {
                        generateRookMoves(row, col, moves);
                    }
                    if (board[row][col] == 9) {
                        generateKnightMoves(row, col, moves);
                    }
                    if (board[row][col] == 10) {
                        generateBishopMoves(row, col, moves);
                    }
                    if (board[row][col] == 11) {
                        generateQueenMoves(row, col, moves);
                    }
                    if (board[row][col] == 12) {
                        generateKingMoves(row, col, moves);
                    }
                    if (board[row][col] == 13 || board[row][col] == 14) {
                        generatePawnMoves(row, col, moves);
                    }
                }
            }
        }
    }

    public void pieceMoveLogic(int[][] directions, int row, int col, boolean canSlide, int[][] moves) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if (board[newRow][newCol] > 7 && currentPlayer == 'w' || board[newRow][newCol] <= 7 && currentPlayer == 'b') {
                        addMove(row, col, newRow, newCol, board[row][col], moves);
                    }
                    break;
                } else {
                    addMove(row, col, newRow, newCol, board[row][col], moves);
                }
                if (!canSlide) {
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
    }

    public void generateRookMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateBishopMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateKnightMoves(int row, int col, int[][] moves) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        pieceMoveLogic(directions, row, col, false, moves);
    }

    public void generateQueenMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateKingMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, false, moves);
    }

    public void generatePawnMoves(int row, int col, int[][] moves) {
        int piece = board[row][col];
        int direction = (piece == 6) ? 1 : -1; // * 1 for white, -1 for black

        if (isTileEmpty(row + direction, col)) {
            if ((direction == 1 && row == 1) || (direction == -1 && row == 6)) {
                if (isTileEmpty(row + 2 * direction, col)) {
                    addMove(row, col, row + 2 * direction, col, piece, moves);
                }
            }
            addMove(row, col, row + direction, col, piece, moves);
        }

        if (col + 1 < 8 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col + 1) && board[row + direction][col + 1] / 8 != piece / 8) {
            addMove(row, col, row + direction, col + 1, piece, moves);
        }
        if (col - 1 >= 0 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col - 1) && board[row + direction][col - 1] / 8 != piece / 8) {
            addMove(row, col, row + direction, col - 1, piece, moves);
        }
    }

    private void addMove(int startRow, int startCol, int endRow, int endCol, int piece, int[][] moves) {
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        generateMoveCounter++;
    }

    public Game(Game currentGame) {
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(currentGame.board[i], 0, this.board[i], 0, board.length);
        }
        this.currentPlayer = currentGame.currentPlayer;
        this.heuristicValue = currentGame.heuristicValue;
        this.whiteLongCastle = currentGame.whiteLongCastle;
        this.whiteShortCastle = currentGame.whiteShortCastle;
        this.blackLongCastle = currentGame.blackLongCastle;
        this.blackShortCastle = currentGame.blackShortCastle;
    }

    public Game updateGameState(int moveIndex) {
        Game newGame = new Game(this);
        newGame.board[moves[moveIndex][0]][moves[moveIndex][1]] = 0;
        newGame.board[moves[moveIndex][2]][moves[moveIndex][3]] = moves[moveIndex][4];

        if (currentPlayer == 'w') {
            newGame.currentPlayer = 'b';
        } else {
            newGame.currentPlayer = 'w';
        }
        return newGame;
    }

    public int minimax(int depth, boolean isMaximizing) {
        if (checkForWin()) {
            return Integer.MAX_VALUE;
        }
        if (checkDraw()) {
            return 0;
        }
        if (isMaximizing) {
            int bestValue = -100;
            for (int i = 0; i < moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth + 1, false);
                if (heuristicValue > bestValue) {
                    bestValue = heuristicValue;
                }
            }
            return bestValue;
        } else {
            int bestValue = 100;
            for (int i = 0; i < moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth + 1, true);
                if (heuristicValue < bestValue) {
                    bestValue = heuristicValue;
                }
            }
            return bestValue;
        }
    }

    public boolean isGameFinished() {
        //return false;
        return isCheckmate() || checkDraw() || onlyKingLeft();
    }
    private boolean isCheckmate() {
        //is check can't escape
        return kingInCheck(kingRow, kingCol) && !canEscapeCheck(kingRow, kingCol);
    }

    private boolean kingInCheck(int kingRow, int kingCol) {
        int[] attacker = {kingRow, kingCol};
        if (kingSeeRook(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeeBishop(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeeKnight(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeePawn(kingRow, kingCol) != attacker) {
            return true;
        } else return false;
    }
    public int[] kingSeeBishop(int kingRow, int kingCol) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 11 || board[newRow][newCol] == 12) && currentPlayer == 'w' || (board[newRow][newCol] == 3 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return new int[]{kingRow, kingCol};
    }
    public int[] kingSeeKnight(int kingRow, int kingCol) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (board[newRow][newCol] == 2 && currentPlayer == 'b' || board[newRow][newCol] == 10 && currentPlayer == 'w') {
                    return new int[]{newRow, newCol};
                }
                break;
            }
        }
        return new int[]{kingRow, kingCol};
    }
    public int[] kingSeeRook(int kingRow, int kingCol) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 9 || board[newRow][newCol] == 12) && currentPlayer == 'w' || (board[newRow][newCol] == 1 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return new int[]{kingRow, kingCol};
    }
    public int[] kingSeePawn(int kingRow, int kingCol) {
        if (currentPlayer == 'w') {
            if (!isTileEmpty(kingRow + 1, kingCol + 1) || !isTileEmpty(kingRow + 1, kingCol - 1)) {

            }
        } else {

        }
        return new int[]{kingRow, kingCol};
    }

    private boolean canCaptureKing(int attackerRow, int attackerCol, int kingRow, int kingCol) {
        int attacker = board[attackerRow][attackerCol];
        int dy = Math.abs(kingRow - attackerRow);
        int dx = Math.abs(kingCol - attackerCol);

        return switch (attacker) {
            case 6, 7, 13, 14 -> // White PAWNS > Black PAWNS
                    (dy == 1 && dx == 1);
            case 2, 9 -> // White KNIGHTS > Black KNIGHTS
                    (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
            case 1, 8 -> // White ROOK > Black ROOK
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0);
            case 3, 10 -> // White BISHOPS > Black BISHOPS
                    (dy == dx);
            case 4, 11 -> // White QUEEN > Black QUEEN
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0) || (dy == dx);
            case 5, 12 -> // White KING > Black KING
                    (dy <= 1 && dx <= 1);
            default -> false;
        };
    }

    private boolean canEscapeCheck(int kingRow, int kingCol) {
        int[] attacker = new int[2];
        attacker = kingSeeRook(kingRow, kingCol);

        generateMoves(moves);
        for (int[] move : moves) {
            if (move[2] == attacker[0] && move[3] == attacker[1]) {
                return true;
            }
            if (move[4] == board[kingRow][kingCol] && !kingInCheck(move[2], move[3])) {
                return true;
            } else {
                makeMove(move[0], move[1], move[2], move[3]);
                if (!kingInCheck(kingRow, kingCol)) {
//                    undoMove(move[0],move[1],move[2],move[3],move[4]);
                    return true;
                }
//                else undoMove(move[0],move[1],move[2],move[3],move[4]);
            }
        }
        return false;
    }

    private boolean onlyKingLeft() {
        int numWhitePieces = 0;
        int numBlackPieces = 0;

        for (int[] row : board) {
            for (int piece : row) {
                if (piece == 1 || piece == 2 || piece == 3 || piece == 4 || piece == 5 || piece == 6 || piece == 7) {
                    numWhitePieces++;
                } else if (piece == 9 || piece == 10 || piece == 11 || piece == 12 || piece == 13 || piece == 14 || piece == 15) {
                    numBlackPieces++;
                }
            }
        }
        return numWhitePieces <= 1 && numBlackPieces <= 1;
    }

    public int evaluate() {
        int whiteScore = 0;
        int blackScore = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece != 0) {
                    int pieceValue = getPieceValue(piece);
                    if (piece <= 6) {
                        whiteScore += pieceValue + getPiecePositionValue(piece, row, col);
                    } else {
                        blackScore += pieceValue + getPiecePositionValue(piece, row, col);
                    }
                }
            }
        }
        return whiteScore - blackScore;
    }

    private int getPieceValue(int piece) {
        return switch (piece) {
            case 6, 7, 14, 15 -> // PAWNS
                    100;
            case 2, 10 ->  // KNIGHTS
                    300;
            case 3, 11 -> // BISHOPS
                    320;
            case 1, 9 ->  // ROOKS
                    540;
            case 4, 12 -> // QUEENS
                    900;
            case 5, 13 -> // KINGS
                    20000;
            default -> 0;
        };
    }

    private int getPiecePositionValue(int piece, int row, int col) {
        int value = 0;
        switch (piece) {
            case 6, 7, 13, 14 -> // PAWNS
                value = pawnPositionValue(row, col);
            case 2, 9 -> // KNIGHTS
                value = knightPositionValue(row, col);
            case 3, 10 -> // BISHOPS
                value = bishopPositionValue(row, col);
            case 1, 8 -> // ROOKS
                value = rookPositionValue(row, col);
            case 4, 11 -> // QUEENS
                value = queenPositionValue(row, col);
            case 5, 12 -> // KINGS
                value = kingPositionValue(row, col);
        }
        return value;
    }

    private int pawnPositionValue(int row, int col) {
        int[][] whitePawnPositionValues = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {50, 50, 50, 50, 50, 50, 50, 50},
                {10, 10, 20, 30, 30, 20, 10, 10},
                {5, 5, 10, 25, 25, 10, 5, 5},
                {0, 0, 0, 20, 20, 0, 0, 0},
                {5, -5, -10, 0, 0, -10, -5, 5},
                {5, 10, 10, -20, -20, 10, 10, 5},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        int[][] blackPawnPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whitePawnPositionValues[7 - i], 0, blackPawnPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whitePawnPositionValues[row][col] : blackPawnPositionValues[row][col];
    }

    private int knightPositionValue(int row, int col) {
        int[][] whiteKnightPositionValues = {
                {-50, -40, -30, -30, -30, -30, -40, -50},
                {-40, -20, 0, 0, 0, 0, -20, -40},
                {-30, 0, 10, 15, 15, 10, 0, -30},
                {-30, 5, 15, 20, 20, 15, 5, -30},
                {-30, 0, 15, 20, 20, 15, 0, -30},
                {-30, 5, 10, 15, 15, 10, 5, -30},
                {-40, -20, 0, 5, 5, 0, -20, -40},
                {-50, -40, -30, -30, -30, -30, -40, -50}
        };
        int[][] blackKnightPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackKnightPositionValues[i][j] = whiteKnightPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteKnightPositionValues[row][col] : blackKnightPositionValues[row][col];
    }

    private int bishopPositionValue(int row, int col) {
        int[][] whiteBishopPositionValues = {
                {-20, -10, -10, -10, -10, -10, -10, -20},
                {-10, 0, 0, 0, 0, 0, 0, -10},
                {-10, 0, 5, 10, 10, 5, 0, -10},
                {-10, 5, 5, 10, 10, 5, 5, -10},
                {-10, 0, 10, 10, 10, 10, 0, -10},
                {-10, 10, 10, 10, 10, 10, 10, -10},
                {-10, 5, 0, 0, 0, 0, 5, -10},
                {-20, -10, -10, -10, -10, -10, -10, -20}
        };
        int[][] blackBishopPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackBishopPositionValues[i][j] = whiteBishopPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteBishopPositionValues[row][col] : blackBishopPositionValues[row][col];
    }

    private int rookPositionValue(int row, int col) {
        int[][] whiteRookPositionValues = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {5, 10, 10, 10, 10, 10, 10, 5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {0, 0, 0, 5, 5, 0, 0, 0}
        };
        int[][] blackRookPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackRookPositionValues[i][j] = whiteRookPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteRookPositionValues[row][col] : blackRookPositionValues[row][col];
    }

    private int queenPositionValue(int row, int col) {
        int[][] whiteQueenPositionValues = {
                {-20, -10, -10, -5, -5, -10, -10, -20},
                {-10, 0, 0, 0, 0, 0, 0, -10},
                {-10, 0, 5, 5, 5, 5, 0, -10},
                {-5, 0, 5, 5, 5, 5, 0, -5},
                {0, 0, 5, 5, 5, 5, 0, -5},
                {-10, 5, 5, 5, 5, 5, 0, -10},
                {-10, 0, 5, 0, 0, 0, 0, -10},
                {-20, -10, -10, -5, -5, -10, -10, -20}
        };
        int[][] blackQueenPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackQueenPositionValues[i][j] = whiteQueenPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteQueenPositionValues[row][col] : blackQueenPositionValues[row][col];
    }

    private int kingPositionValue(int row, int col) {
        int[][] whiteKingPositionValues = {
                {-50, -40, -30, -20, -20, -30, -40, -50},
                {-30, -20, -10, 0, 0, -10, -20, -30},
                {-30, -10, 20, 30, 30, 20, -10, -30},
                {-30, -10, 30, 40, 40, 30, -10, -30},
                {-30, -10, 30, 40, 40, 30, -10, -30},
                {-30, -10, 20, 30, 30, 20, -10, -30},
                {-30, -30, 0, 0, 0, 0, -30, -30},
                {-50, -30, -30, -30, -30, -30, -30, -50}
        };
        int[][] blackKingPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackKingPositionValues[i][j] = whiteKingPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteKingPositionValues[row][col] : blackKingPositionValues[row][col];
    }

    public boolean checkForWin() {
        return false;
    }

    public boolean checkDraw() {
        return false;
    }

    public int getHeuristicMoveValue() {
        return 0;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getGenerateMoveCounter() {
        return generateMoveCounter;
    }

    public void setGenerateMoveCounter(int generateMoveCounter) {
        this.generateMoveCounter = generateMoveCounter;
    }

    public int getEnPassant() {
        return enPassant;
    }

    public void setEnPassant(int enPassant) {
        this.enPassant = enPassant;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void initializeBoard(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        for (int i = 0; i < 8; i++) {
            int col = 0;
            for (char c : rows[7 - i].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    int piece = pieceFromChar(c);
                    board[i][col] = piece;
                    col++;
                }
            }
        }

        currentPlayer = parts[1].charAt(0);
        findAndSetKing();
    }

    public void findAndSetKing() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = board[i][j];
                if ((currentPlayer == 'w' && piece == 5) || (currentPlayer == 'b' && piece == 12)) {
                    kingRow = i;
                    kingCol = j;
                    return;
                }
            }
        }
    }

    public int pieceFromChar(char c) {
        return switch (c) {
            case 'P' -> 6;  // white pawn
            case 'R' -> 1;  // white rook
            case 'N' -> 2;  // white knight
            case 'B' -> 3;  // white bishop
            case 'Q' -> 4;  // white queens
            case 'K' -> 5;  // white king

            case 'p' -> 14;  // black pawn
            case 'r' -> 8;  // black rook
            case 'n' -> 9;  // black knight
            case 'b' -> 10; // black bishop
            case 'q' -> 11; // black queen
            case 'k' -> 12; // black king

            default -> 0;   // empty square
        };
    }

    public void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(pieceToChar(board[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public char pieceToChar(int piece) {
        return switch (piece) {
            case 13, 14 -> 'p';  // black pawn

            case 9 -> 'n';  // black knight
            case 10 -> 'b';  // black bishop
            case 11 -> 'q';  // black queen
            case 12 -> 'k';  // black king
            case 8 -> 'r';  // black rook

            case 6, 7 -> 'P';  // white pawn
            case 1 -> 'R';  // white rook
            case 2 -> 'N';  // white knight
            case 3 -> 'B';  // white bishop
            case 4 -> 'Q';  // white queen
            case 5 -> 'K';  // white king
            default -> '.';  // empty square
        };
    }

    public int makeMove(int startRow, int startCol, int endRow, int endCol) {
        int capturedPiece = board[endRow][endCol];
        int piece = board[startRow][startCol];
        board[startRow][startCol] = 0;
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
            if ((piece == 5 && currentPlayer == 'w') || (piece == 13 && currentPlayer == 'b')) {
                kingRow = endRow;
                kingCol = endCol;
            }
        return capturedPiece;
    }

    public void undoMove(int startRow, int startCol, int endRow, int endCol, int piece) {
        board[startRow][startCol] = board[endRow][endCol];
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
    }

    public void resetMoves(int[][] moves) {
        for (int i = 0; i<moves.length; i++) {
            for (int j = 0; j<moves[i].length; j++) {
                moves[i][j]=0;
            }
        }
    }

    public int[][] createNewMoveLists() {
        return new int[1000][5];
    }

    public int[][] getMovesByDepth(int depth) {
        return switch (depth) {
            case 7 -> movesd7;  // black pawn
            case 6 -> movesd6;  // black knight
            case 5 -> movesd5;  // black bishop
            case 4 -> movesd4;  // black queen
            case 3 -> movesd3;  // black king
            case 2 -> movesd2;  // black rook
            case 1 -> movesd1;
            case 0 -> moves;
            default -> moves;
        };
    }

    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startRow >= 8 || startCol < 0 || startCol >= 8 ||
                endRow < 0 || endRow >= 8 || endCol < 0 || endCol >= 8) {
            return false;
        }
        return true;
    }

    public String getFEN() {
        StringBuilder fen = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(pieceToChar(board[i][j]));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i > 0) {
                fen.append('/');
            }
        }
        fen.append(" ");
        fen.append(currentPlayer);
        return fen.toString();
    }

        public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter FEN string:");
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//        String fen = scanner.nextLine(); // Example: rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1
//        System.out.println("Enter search depth for Minimax:");
//        int depth = scanner.nextInt();
        int depth = 7;
        System.out.println("Searching in depth " + depth + "...");
        
        Game game = new Game();
        game.initializeBoard(fen);
        game.printBoard();

        LocalDateTime startTime, endTime;

        startTime = LocalDateTime.now();
        //single-threaded test
        int[] bestMove = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        endTime = LocalDateTime.now();

        long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
        System.out.println("Single-threaded Minimax Time: " + singleThreadedTime + " milliseconds");

        System.out.println("bestmove: " + bestMove[1]+ bestMove[2]+ bestMove[3]+ bestMove[4]);
        game.makeMove(bestMove[1],bestMove[2],bestMove[3],bestMove[4]);
        game.printBoard();
        String newFEN = game.getFEN();
        System.out.println("New FEN string:");
        System.out.println(newFEN);
    }

    public static int[] parallelMinimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int parallelism = Runtime.getRuntime().availableProcessors() * 16;
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        ParallelMinimax task = new ParallelMinimax(game, depth, alpha, beta, maximizingPlayer);
        int[] result = pool.invoke(task);
        int nodeCount = task.getNodeCount();
        return new int[]{nodeCount, result[0], result[1], result[2], result[3]};

    }

    public static int[] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0)  {
            return new int[] {game.evaluate()};
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        int[][] moves = game.getMovesByDepth(depth);
        game.generateMoveCounter = 0;
        game.generateMoves(moves);

        for (int i = 0; i < game.generateMoveCounter; i++) {
            int[] move = moves[i];
            int previousMove;
            previousMove = game.makeMove(move[0], move[1], move[2], move[3]);

            int[] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer);
            game.undoMove(move[0],move[1],move[2],move[3],previousMove);

            if (result == null) {
                int score = game.evaluate();
                if (maximizingPlayer && score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                } else if (!maximizingPlayer && score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            } else {
                if (maximizingPlayer) {
                    if (result[0] > bestScore) {
                        bestScore = result[0];
                        bestMove = move;
                    }
                    alpha = Math.max(alpha, bestScore);
                } else {
                    if (result[0] < bestScore) {
                        bestScore = result[0];
                        bestMove = move;
                    }
                    beta = Math.min(beta, bestScore);
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestMove;
    }
}