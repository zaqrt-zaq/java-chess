import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPieces {
    public Pawn(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    @Override
    protected char getWhiteSymbol() {
        return '♙';
    }

    private boolean canBeEnPassantCaptured = false;

    public void setCanBeEnPassantCaptured(boolean canBeEnPassantCaptured) {
        this.canBeEnPassantCaptured = canBeEnPassantCaptured;
    }

    @Override
    public List<int[]> getPossibleMoves() {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();

        // Move forward one square
        if (getPositionX() + direction >= 0 && getPositionX() + direction < 8 &&
            board[getPositionX() + direction][getPositionY()] == null) {
            possibleMoves.add(new int[]{getPositionX() + direction, getPositionY()});

            // Move forward two squares from starting position
            if (!hasMoved() && board[getPositionX() + 2 * direction][getPositionY()] == null) {
                possibleMoves.add(new int[]{getPositionX() + 2 * direction, getPositionY()});
            }
        }

        // Capture diagonally
        for (int dy = -1; dy <= 1; dy += 2) {
            if (getPositionX() + direction >= 0 && getPositionX() + direction < 8 &&
                getPositionY() + dy >= 0 && getPositionY() + dy < 8) {
                
                // Normal capture
                ChessPieces targetPiece = board[getPositionX() + direction][getPositionY() + dy];
                if (targetPiece != null && targetPiece.isWhite() != isWhite()) {
                    possibleMoves.add(new int[]{getPositionX() + direction, getPositionY() + dy});
                }
                
                // En passant capture
                if (getPositionX() == (isWhite() ? 3 : 4)) {
                    ChessPieces adjacentPiece = board[getPositionX()][getPositionY() + dy];
                    if (adjacentPiece instanceof Pawn && 
                        adjacentPiece.isWhite() != isWhite() && 
                        ((Pawn) adjacentPiece).canBeEnPassantCaptured) {
                        possibleMoves.add(new int[]{getPositionX() + direction, getPositionY() + dy});
                    }
                }
            }
        }

        if(this.canSkipCheck()) return possibleMoves;
        return validateMoves(possibleMoves);
    }

    @Override
    protected void movePiece(int x, int y) {
        // Set canBeEnPassantCaptured to true if pawn moves two squares
        this.canBeEnPassantCaptured = Math.abs(this.getPositionX() - x) == 2;
        super.movePiece(x, y);
    }
}
