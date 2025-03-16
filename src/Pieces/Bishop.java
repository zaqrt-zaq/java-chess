package Pieces;

import java.util.List;

public class Bishop extends ChessPieces {

    public Bishop(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    static int[][] moves = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    @Override
    protected char getWhiteSymbol() {
        return '♗';
    }

    @Override
    public List<int[]> getPossibleMoves() {
        if (this.canSkipCheck()) return moveByMore(Bishop.moves);

        return validateMoves(moveByMore(Bishop.moves));
    }
}
