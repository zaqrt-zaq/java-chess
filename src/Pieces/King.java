package Pieces;

import java.util.ArrayList;
import java.util.List;
import BoardControl.Board;

public class King extends ChessPieces {
    public King(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    static final int[][] moves = {
            {1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };

    @Override
    protected char getWhiteSymbol() {
        return '♔';
    }


    public List<int[]> getPossibleMoves() {

        ArrayList<int[]> possibleMoves = new ArrayList<>(this.moveByOne(King.moves));
        if (canCastle(Board.getInstance().getCurrentBoard()[this.getPositionX()][7]))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() + 2});
        if (canCastle(Board.getInstance().getCurrentBoard()[this.getPositionX()][0]))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() - 2});

        return validateMoves(possibleMoves);
    }

    private boolean canCastle(ChessPieces piece) {
        if (!(piece instanceof Rook) || piece.hasMoved() || this.hasMoved()) return false;
        if (isKingUnderAttack(this)) return false;

        int step = (piece.getPositionY() == 7) ? 1 : -1;

        int y = getPositionY() + step;
        while(y != piece.getPositionY()){
            if (Board.getInstance().getCurrentBoard()[getPositionX()][y] != null) return false;
            if (isSquareUnderAttack(this.getPositionX(),y,this.isWhite(), Board.getInstance().getCurrentBoard())) return false;
            y+=step;
        }

        return true;
    }

    public void castle(boolean isLongCastle) {
        int rookCol = isLongCastle ? 0 : 7;
        ChessPieces rook = Board.getInstance().getCurrentBoard()[getPositionX()][rookCol];
        
        ChessPieces.makeMove(rook, this.getPositionX(), isLongCastle ? 3 : 5, Board.getInstance().getCurrentBoard());
        ChessPieces.makeMove(this, this.getPositionX(), isLongCastle ? 2 : 6, Board.getInstance().getCurrentBoard());
    }
    @Override
    protected void movePiece(int x, int y) {
        super.movePiece(x, y);
        ChessPieces.updateKingPosition(isWhite(), new int[]{x, y});
    }
}
