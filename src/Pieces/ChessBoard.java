package Pieces;

import BoardControl.Board;

import java.util.Arrays;
import java.util.List;


public class ChessBoard {
    public static final int SIZE = 8;

    private final int[] whiteKing;
    private final int[] blackKing;
    private ChessPieces possibleEnPassant;
    private final boolean[] possibleCastles;
    private boolean isCurrentPlayerWhite = true;
    private final ChessPieces[][] currentPosition;
    private static ChessBoard chessBoard = null;

    public static ChessBoard getChessBoard() {
        if (chessBoard == null) {
            chessBoard = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        }
        return chessBoard;
    }


    private ChessBoard(String fenString) {
        Object[] result = FEN.createChessPiecesArray(fenString);
        currentPosition = (ChessPieces[][]) result[0];
        whiteKing = ((int[][])result[1])[0];
        blackKing = ((int[][])result[1])[1];
        possibleEnPassant = FEN.getPossibleEnPassant(fenString,currentPosition);
        possibleCastles = FEN.getPossibleCastles(fenString);
        isCurrentPlayerWhite = FEN.getIsWhite(fenString);
    }

    public ChessPieces getChessPieceFromPosition(int x, int y) {
        return currentPosition[x][y];
    }

    public boolean isSquareEmpty(int x, int y) {
        return getChessPieceFromPosition(x, y) == null;
    }

    public boolean doesPieceBelongToCurrentPlayer(int x, int y) {
        return !isSquareEmpty(x, y) && currentPosition[x][y].isWhite() == isCurrentPlayerWhite;
    }

    public boolean isCurrentPlayerWhite() {
        return isCurrentPlayerWhite;
    }


    public String[][] getBoardToString() {
        return Arrays.stream(currentPosition)
                .map(row -> Arrays.stream(row)
                        .map(chesspiece -> chesspiece != null ? chesspiece.toString() : "")
                        .toArray(String[]::new))
                .toArray(String[][]::new);
    }


    public void makeMove(ChessPieces piece, int newX, int newY) {
        possibleEnPassant = null;

        // Make the move
        movePiece(piece, newX, newY);

        // Handle pawn promotion`
        if (piece instanceof Pawn && (newX == 0 || newX == 7)) {
            promotePawn(piece, newX, newY);
        }

        // Set en passant flag if pawn moves two squares
        if (piece instanceof Pawn && Math.abs(newX - piece.getPositionX()) == 2) {
            possibleEnPassant = piece;
        }

        isCurrentPlayerWhite = !piece.isWhite();

        if (isCheckmate(isCurrentPlayerWhite)) {
            String winner = isCurrentPlayerWhite ? "Black" : "White";
            BoardControl.Board.getInstance().announceWinner(winner);
        }
    }
    private void movePiece(ChessPieces piece, int newX, int newY) {
        currentPosition[piece.getPositionX()][piece.getPositionY()] = null;
        currentPosition[newX][newY] = piece;
        piece.movePiece(newX, newY);
    }

    public void makeSpecialMove(ChessPieces piece, int x, int y) {
        if (piece instanceof King) {
            int rookCol = y == 2 ? 0 : 7;
            int rookNewCol = y == 2 ? 3 : 5;
            int kingNewCol = y == 2 ? 2 : 6;
            ChessPieces rook = currentPosition[piece.getPositionX()][rookCol];

            // Move rook
            movePiece(rook, rook.positionX, rookNewCol);
            // Move king
            movePiece(piece, piece.positionX, kingNewCol);

        } else if (piece instanceof Pawn) {
            // En passant capture
            currentPosition[piece.getPositionX()][y] = null; // Remove captured pawn
            currentPosition[piece.getPositionX()][piece.getPositionY()] = null;
            currentPosition[x][y] = piece;
            piece.movePiece(x, y);
        }

        isCurrentPlayerWhite = !piece.isWhite();

        if (isCheckmate(isCurrentPlayerWhite)) {
            String winner = isCurrentPlayerWhite ? "Black" : "White";
            Board.getInstance().announceWinner(winner);
        }
    }

    public static boolean isValidMove(ChessPieces piece, int newX, int newY) {
        List<int[]> possibleMoves = piece.getPossibleMoves();
        for (int[] move : possibleMoves) {
            if (move[0] == newX && move[1] == newY) {
                return true;
            }
        }
        return false;
    }

    public boolean executeWithTemporaryMove(int fromX, int fromY, Integer toX, Integer toY, BoardOperation operation) {
        ChessPieces piece = currentPosition[fromX][fromY];
        ChessPieces capturedPiece = (toX != null && toY != null) ? currentPosition[toX][toY] : null;

        currentPosition[fromX][fromY] = null;
        if (toX != null && toY != null) {
            currentPosition[toX][toY] = piece;
        }

        boolean result = operation.execute();

        if (toX != null && toY != null) {
            currentPosition[toX][toY] = capturedPiece;
        }
        currentPosition[fromX][fromY] = piece;

        return result;
    }


    private void promotePawn(ChessPieces piece, int row, int col) {

        int choice = Board.getInstance().chosePromotion();
        ChessPieces newPiece = switch (choice) {
            case 1 -> new Rook(row, col, piece.isWhite());
            case 2 -> new Bishop(row, col, piece.isWhite());
            case 3 -> new Knight(row, col, piece.isWhite());
            default -> new Queen(row, col, piece.isWhite());
        };

        currentPosition[row][col] = newPiece;
    }

    public boolean isCheckmate(boolean isWhite) {
        int[] kingPosition = isWhite ? whiteKing : blackKing;
        King king = (King) currentPosition[kingPosition[0]][kingPosition[1]];

        if (!isSquareUnderAttack(king.getPositionX(), king.getPositionY(), king.isWhite())) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPieces piece = currentPosition[i][j];
                if (piece != null && piece.isWhite() == isWhite && !piece.getPossibleMoves().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isSpecialMove(ChessPieces piece, int row, int col) {
        if (piece instanceof King) {
            return Math.abs(col - piece.getPositionY()) == 2;
        } else if (piece instanceof Pawn) {
            return col != piece.getPositionY() && chessBoard.isSquareEmpty(row, col);
        }
        return false;
    }

    public ChessPieces getPossibleEnPassant() {
        return possibleEnPassant;
    }

    public void setPossibleEnPassant(ChessPieces possibleEnPassant) {
        this.possibleEnPassant = possibleEnPassant;
    }

    public void removePossibleCastle(Castles castle) {
        possibleCastles[castle.id]=false;
    }

    public boolean isCastlePossible(Castles castle) {
        return possibleCastles[castle.id];
    }

    public void updateKingPosition(boolean isWhite, int[] position) {
        if (isWhite) {
            whiteKing[0] = position[0];
            whiteKing[1] = position[1];
        } else {
            blackKing[0] = position[0];
            blackKing[1] = position[1];
        }
    }
    public boolean isPieceTheSameColorAs(ChessPieces piece,int pieceRow,int PieceCol) {
        ChessPieces targetPiece = currentPosition[pieceRow][PieceCol];
        return targetPiece != null && piece.isWhite() == targetPiece.isWhite();
    }

    public void updateKingPosition(King king) {
        updateKingPosition(king.isWhite(),new int[]{king.getPositionX(),king.getPositionY()});
    }

    public int[] getKingPosition(boolean isWhite) {
        return isWhite ? whiteKing : blackKing;
    }

    public void resetGameState() {
        isCurrentPlayerWhite = true;
        updateKingPosition(true,new int[]{7, 4});
        updateKingPosition(false,new int[]{0, 4});
        possibleCastles[0]=true;
        possibleCastles[1]=true;
        possibleCastles[2]=true;
        possibleCastles[3]=true;
        possibleEnPassant=null;
    }

    public boolean isKingUnderAttack(boolean isWhite) {
        int[] kingPosition = isWhite ? whiteKing : blackKing;
        King king = (King) currentPosition[kingPosition[0]][kingPosition[1]];
        return isSquareUnderAttack(king.getPositionX(), king.getPositionY(), king.isWhite());
    }

    public ChessPieces[][] getCurrentPosition() {
        return currentPosition;
    }


    public static boolean isValidSquare(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isSquareUnderAttack(int x, int y, boolean isPlayerWhite) {
        int pawnDirection = isPlayerWhite ? -1 : 1;

        for (int[] move : Knight.moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValidSquare(newX, newY)) {
                ChessPieces piece = currentPosition[newX][newY];
                if (piece instanceof Knight && piece.isWhite() != isPlayerWhite) {
                    return true;
                }
            }
        }

        if ((isValidSquare(x + pawnDirection, y - 1) &&
                currentPosition[x + pawnDirection][y - 1] instanceof Pawn &&
                currentPosition[x + pawnDirection][y - 1].isWhite() != isPlayerWhite) ||
                (isValidSquare(x + pawnDirection, y + 1) &&
                        currentPosition[x + pawnDirection][y + 1] instanceof Pawn &&
                        currentPosition[x + pawnDirection][y + 1].isWhite() != isPlayerWhite)) {
            return true;
        }

        if (isSquareAttackedByX(x, y, isPlayerWhite, Rook.moves, Rook.class, Queen.class)) {
            return true;
        }

        if (isSquareAttackedByX(x, y, isPlayerWhite, Bishop.moves, Bishop.class, Queen.class)) {
            return true;
        }

        for (int[] move : King.moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValidSquare(newX, newY)) {
                ChessPieces targetPiece = currentPosition[newX][newY];
                if (targetPiece instanceof King && targetPiece.isWhite() != isPlayerWhite) {
                    return true;
                }
            }
        }

        return false;
    }

    @SafeVarargs
    private boolean isSquareAttackedByX(int x, int y, boolean isKingWhite, int[][] directions, Class<? extends ChessPieces>... attackingPieces) {
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = x + direction[0] * i;
                int newY = y + direction[1] * i;

                if (!isValidSquare(newX, newY)) break;

                ChessPieces targetPiece = currentPosition[newX][newY];
                if (targetPiece == null) continue;
                if (targetPiece.isWhite() == isKingWhite) break;

                for (Class<?> attackingPiece : attackingPieces) {
                    if (attackingPiece.isInstance(targetPiece)) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
}
