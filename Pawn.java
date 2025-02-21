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

    private boolean canBeUmpasoundet = false;

    public void setCanBeUmpasoundet(boolean canBeUmpasoundet) {
        this.canBeUmpasoundet = canBeUmpasoundet;
    }

    public boolean isCanBeUmpasoundet() {
        return canBeUmpasoundet;
    }
    public void unpasoud(int positionX, int positionY) {
        ChessPieces[][] board = Board.board.getCurrentBoard();
        board[positionX+1][positionY] = null;
        Board.board.makeMove(this, positionX, positionY);
    }

    public List<int[]> getPossibleMoves(boolean skipCheck){
        if(skipCheck) return getPossibleMoves();
        return validateMoves(getPossibleMoves());
    }
    private List<int[]> getPossibleMoves() {
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

        if (getPositionX() == (isWhite()?5:4)) {
            if (y>0 && board[x][y-1] instanceof Pawn && ((Pawn) board[x][y]).isCanBeUmpasoundet()){
                moves.add(new int[]{x+direction,y-1});
            }
        }

        return moves;
    }

    @Override
    protected void movePiece(int x, int y) {
        if (this.getPositionX() + 2 == x ) this.canBeUmpasoundet = false;
        super.movePiece(x, y);
        return;
    }
}
