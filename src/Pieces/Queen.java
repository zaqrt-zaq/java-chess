package Pieces;

import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPieces {

    public Queen(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    @Override
    protected char getWhiteSymbol() {
        return '♕';
    }

    @Override
    public List<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        moves.addAll(moveByMore(Bishop.moves));
        moves.addAll(moveByMore(Rook.moves));

        if (this.canSkipCheck()) return moves;

        return validateMoves(moves);
    }

    @Override
    public char getFENSymbol() {
        return isWhite()?'Q':'q';
    }
}
