package Pieces;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPieces {
    public Pawn(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    @Override
    protected char getWhiteSymbol() {
        return '♙';
    }


    @Override
    public List<int[]> getPossibleMoves() {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int newX = getPositionX() + direction;
        ChessBoard chessBoard = ChessBoard.getChessBoard();

        if (ChessBoard.isValidSquare(newX, this.getPositionY()))
            possibleMoves.add(new int[]{newX, this.getPositionY()});
        if (!hasMoved() && chessBoard.isSquareEmpty(positionX+2*direction, positionY))
            possibleMoves.add(new int[]{getPositionX() + 2 * direction, getPositionY()});

        addDiagonalCaptures(possibleMoves, newX);
        addEnPassantCaptures(possibleMoves, newX);

        if(this.canSkipCheck())
            return possibleMoves;


        return validateMoves(possibleMoves);
    }

    private void addDiagonalCaptures(List<int[]> possibleMoves, int newX) {
        for (int i = -1; i <= 1; i += 2) {
            int newY = getPositionY() + i;
            if (!(ChessBoard.isValidSquare(newX, newY)))
                continue;

            if (ChessBoard.getChessBoard().isPieceTheSameColorAs(this,newX,newY)) {
                possibleMoves.add(new int[]{newX, newY});
            }
        }
    }

    private void addEnPassantCaptures(List<int[]> possibleMoves, int newX) {
        Pawn pawn = (Pawn) ChessBoard.getChessBoard().getPossibleEnPassant();
        if (pawn == null || pawn.getPositionX() != this.getPositionX()){
            return;
        }

        if (this.getPositionY()+1==pawn.getPositionY()){
            possibleMoves.add(new int[]{newX, this.getPositionY()+1});
        }
        if (this.getPositionY()-1==pawn.getPositionY()){
            possibleMoves.add(new int[]{newX, this.getPositionY()+1});
        }

    }

    @Override
    protected void movePiece(int x, int y) {
        // Set canBeEnPassantCaptured to true if pawn moves two squares
        if (Math.abs(this.getPositionX() - x) == 2) ChessBoard.getChessBoard().setPossibleEnPassant(this);
        super.movePiece(x, y);
    }

    @Override
    public char getFENSymbol() {
        return isWhite()?'P':'p';
    }
}
