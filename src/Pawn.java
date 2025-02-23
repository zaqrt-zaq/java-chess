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

    public boolean isCanBeEnPassantCaptured() {
        return canBeEnPassantCaptured;
    }
    
    public void captureEnPassant(int positionX, int positionY) {
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();
        board[getPositionX()][positionY] = null;
        ChessPieces.makeMove(this, positionX, positionY, board);
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
        if (Math.abs(this.getPositionX() - x) == 2) {
            this.canBeEnPassantCaptured = true;
        } else {
            this.canBeEnPassantCaptured = false;
        }
        super.movePiece(x, y);
    }
}
