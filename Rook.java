import java.util.List;

public class Rook extends ChessPieces {

    public Rook(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    static final int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


    @Override
    protected char getWhiteSymbol() {
        return '♖';
    }

    @Override
    public List<int[]> getPossibleMoves(boolean skipCheck) {
        if (skipCheck) return moveByMore(Rook.moves);

        return validateMoves(moveByMore(Rook.moves));
    }
}
