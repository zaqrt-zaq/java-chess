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
        ChessPieces[][] board = Board.board.getCurrentBoard();
        // Remove the captured pawn from its current position
        board[getPositionX()][positionY] = null;
        // Update UI for the captured pawn position
        Board.board.getGrid()[getPositionX()][positionY].setText("");
        // Move the capturing pawn to the new position
        Board.board.makeMove(this, positionX, positionY);
    }

    public List<int[]> getPossibleMoves() {
        List<int[]> moves = new ArrayList<>();
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();
        int direction = isWhite() ? -1 : 1;
        int x = getPositionX();
        int y = getPositionY();

        int newX = getPositionX() + direction;
        if (board[newX][y] == null) {
            moves.add(new int[]{newX, y});
            if (!this.hasMoved() && board[newX+direction][y] == null)
                moves.add(new int[]{newX + direction, y});
        }

        int[] directions = {-1, 1};
        for (int directionX : directions) {
            int newY = y + directionX;
            if (newY >= 0 && newY < 8) {
                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite())
                    moves.add(new int[]{newX, newY});
            }
        }

        if (getPositionX() == (isWhite()?3:4)) {
            // Check left side
            if (y>0 && board[x][y-1] instanceof Pawn && ((Pawn) board[x][y-1]).isCanBeEnPassantCaptured()){
                moves.add(new int[]{x+direction,y-1});
            }
            // Check right side
            if (y<7 && board[x][y+1] instanceof Pawn && ((Pawn) board[x][y+1]).isCanBeEnPassantCaptured()){
                moves.add(new int[]{x+direction,y+1});
            }
        }

        if(this.canSkipCheck()) return moves;
        return validateMoves(moves);
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
