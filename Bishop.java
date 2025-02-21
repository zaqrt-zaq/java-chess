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
        return moveByMore(Bishop.moves);
    }
}
