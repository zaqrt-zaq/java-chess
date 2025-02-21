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
        return moveByOne(Knight.moves);
    }

}
