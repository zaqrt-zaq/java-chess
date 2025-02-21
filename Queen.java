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

        ArrayList<int[]> possibleMoves = new ArrayList<>(moveByMore(Bishop.moves));
        possibleMoves.addAll(moveByMore(Rook.moves));
        return possibleMoves;
    }
}
