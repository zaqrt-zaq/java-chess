package Pieces;

import java.util.List;

public class Knight extends ChessPieces {
    Knight(int x, int y, boolean white) {
        super(x, y, white);
    }

    static final int[][] moves = {
            {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
            {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
    };

    @Override
    protected char getWhiteSymbol() {
        return '♘';
    }

    @Override
    public List<int[]> getPossibleMoves() {
        if (this.canSkipCheck()) return moveByOne(Knight.moves);
        return validateMoves(moveByOne(Knight.moves));
    }

    @Override
    public char getFENSymbol() {
        return isWhite() ? 'N' : 'n';
    }
}
