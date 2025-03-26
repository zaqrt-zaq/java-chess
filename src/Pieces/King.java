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
        ChessBoard chessBoard = ChessBoard.getChessBoard();

        ArrayList<int[]> possibleMoves = new ArrayList<>(this.moveByOne(King.moves));
        if (chessBoard.isCastlePossible(isWhite()?Castles.WHITE_LONG_CASTLE:Castles.BLACK_LONG_CASTLE))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() + 2});
        if (chessBoard.isCastlePossible(isWhite()?Castles.WHITE_SORT_CASTLE:Castles.BACK_SORT_CASTLE))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() - 2});

        return validateMoves(possibleMoves);
    }

    @Override
    protected void movePiece(int x, int y) {
        super.movePiece(x, y);
        ChessBoard.getChessBoard().updateKingPosition(this);
    }

    @Override
    public char getFENSymbol() {
        return isWhite()?'K':'k';
    }

    @Override
    protected boolean canSkipCheck() {
        return false;
    }

    @Override
    protected List<int[]> validateMoves(List<int[]> moves) {
        moves.removeIf(
                move -> ChessBoard.getChessBoard().isSquareUnderAttack(move[0],move[1],isWhite())
        );
        return moves;
    }
}
