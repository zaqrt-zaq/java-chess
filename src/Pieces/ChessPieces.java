package Pieces;

import java.util.ArrayList;
import java.util.List;

import BoardControl.Board;


public abstract class ChessPieces {
    protected int positionX;
    protected int positionY;
    private final boolean isWhite;
    private boolean hasMoved;

    public ChessPieces(int positionX, int positionY, boolean isWhite) {
        this.isWhite = isWhite;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hasMoved = false;
    }

    @Override
    public String toString() {
        char whiteSymbol = getWhiteSymbol();
        return isWhite ? String.valueOf(whiteSymbol) : String.valueOf((char) (whiteSymbol + 6));
    }

    protected abstract char getWhiteSymbol();

    public abstract List<int[]> getPossibleMoves();

    public abstract char getFENSymbol();

    public boolean isWhite() {
        return isWhite;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    protected List<int[]> move(int[][] directions, boolean isLimited) {

        List<int[]> moves = new ArrayList<>();

        for (int[] direction : directions) {
            for (int j = 1; j <= (isLimited ? 1 : 7); j++) {
                int newX = positionX + direction[0] * j;
                int newY = positionY + direction[1] * j;
                if (!ChessBoard.isValidSquare(newX, newY)) {
                    break;
                }
                if (ChessBoard.getChessBoard().isSquareEmpty(newX, newY)) {
                    moves.add(new int[]{newX, newY});
                } else {
                    if (!ChessBoard.getChessBoard().doesPieceBelongToCurrentPlayer(newX, newY)) {
                        moves.add(new int[]{newX, newY});
                    }
                    break;
                }
            }
        }
        return moves;
    }

    protected List<int[]> moveByOne(int[][] directions) {
        return move(directions, true);
    }

    protected List<int[]> moveByMore(int[][] directions) {
        return move(directions, false);
    }

    protected void movePiece(int x, int y) {
        this.positionX = x;
        this.positionY = y;
        this.hasMoved = true;
    }


    protected List<int[]> validateMoves(List<int[]> moves) {
        ChessBoard chessBoard = ChessBoard.getChessBoard();
        moves.removeIf(move -> chessBoard.executeWithTemporaryMove(
               this.positionX, this.positionY,
               move[0],move[1],
               () -> chessBoard.isKingUnderAttack(this.isWhite)
        ));
        return moves;
    }

    protected boolean canSkipCheck() {
        ChessBoard chessBoard = ChessBoard.getChessBoard();
        return chessBoard.executeWithTemporaryMove(
                this.positionX,this.positionY,
                null,null,
                () -> !chessBoard.isKingUnderAttack(this.isWhite)
        );
    }
}
