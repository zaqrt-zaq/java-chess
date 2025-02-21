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
    public List<int[]> getPossibleMoves(boolean skipCheck) {
        ArrayList<int[]> moves = new ArrayList<>();
        moves.addAll(moveByMore(Bishop.moves));
        moves.addAll(moveByMore(Rook.moves));

        if (skipCheck) return moves;

        return validateMoves(moves);
    }
}
