package Pieces;

import java.util.ArrayList;
import java.util.List;
import BoardControl.Board;

public class Pawn extends ChessPieces {
    public Pawn(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    @Override
    protected char getWhiteSymbol() {
        return '♙';
    }

    private boolean canBeEnPassantCaptured = false;

    public void setCanBeEnPassantCaptured(boolean canBeEnPassantCaptured) {
        this.canBeEnPassantCaptured = canBeEnPassantCaptured;
    }

    @Override
    public List<int[]> getPossibleMoves() {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int newX = getPositionX() + direction;
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();

        if (ChessPieces.isValidSquare(newX, this.getPositionY())) {
            possibleMoves.add(new int[]{newX, this.getPositionY()});
        }
        if (!hasMoved() && board[getPositionX() + 2 * direction][getPositionY()] == null) {
            possibleMoves.add(new int[]{getPositionX() + 2 * direction, getPositionY()});
        }
        addDiagonalCaptures(possibleMoves, newX, direction, board);
        addEnPassantCaptures(possibleMoves, newX, direction, board);

        if(this.canSkipCheck()) {
            return possibleMoves;
        }

        return validateMoves(possibleMoves);
    }

    private void addDiagonalCaptures(List<int[]> possibleMoves, int newX, int direction, ChessPieces[][] board) {
        for (int i = -1; i <= 1; i += 2) {
            int newY = getPositionY() + i;
            if (!(isValidSquare(newX, newY)))
                continue;

            ChessPieces targetPiece = board[newX][newY];
            if (targetPiece != null && targetPiece.isWhite() != isWhite()) {
                possibleMoves.add(new int[]{newX, newY});
            }
        }
    }

    private void addEnPassantCaptures(List<int[]> possibleMoves, int newX, int direction, ChessPieces[][] board) {
        if (!(getPositionX() == (isWhite() ? 3 : 4)))
            return;
        for (int i = -1; i <= 1; i += 2) {
            int newY = getPositionY() + i;
            ChessPieces adjacentPiece = board[getPositionX()][newY];
            if (adjacentPiece instanceof Pawn &&
                    adjacentPiece.isWhite() != isWhite() &&
                    ((Pawn) adjacentPiece).canBeEnPassantCaptured) {
                possibleMoves.add(new int[]{newX, newY});
            }
        }

    }

    @Override
    protected void movePiece(int x, int y) {
        // Set canBeEnPassantCaptured to true if pawn moves two squares
        this.canBeEnPassantCaptured = Math.abs(this.getPositionX() - x) == 2;
        super.movePiece(x, y);
    }
}
