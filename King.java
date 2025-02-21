import java.util.ArrayList;
import java.util.List;

public class King extends ChessPieces {
    public King(int positionX, int positionY, boolean isWhite) {
        super(positionX, positionY, isWhite);
    }

    static final int[][] moves = {
            {1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };

    @Override
    protected char getWhiteSymbol() {
        return '♔';
    }

    public List<int[]> getPossibleMoves(boolean skipCheck) {
        return validateMoves(getPossibleMoves());
    }

    private List<int[]> getPossibleMoves() {


        ArrayList<int[]> possibleMoves = new ArrayList<>(this.moveByOne(King.moves));
        if (canCastle(Board.board.getCurrentBoard()[this.getPositionX()][7]))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() + 2});
        if (canCastle(Board.board.getCurrentBoard()[this.getPositionX()][0]))
            possibleMoves.add(new int[]{this.getPositionX(), this.getPositionY() - 2});

        return possibleMoves;
    }

    private boolean canCastle(ChessPieces piece) {
        if (!(piece instanceof Rook rook) || piece.hasMoved() || this.hasMoved()) return false;
        if (isKingInCheck(this)) return false;

        int step = (piece.getPositionY() == 7) ? 1 : -1;

        int y = getPositionY() + step;
        while(y != piece.getPositionY()){
            if (Board.board.getCurrentBoard()[getPositionX()][y] != null) return false;
            if (isKingInCheck(this.getPositionX(),y,this.isWhite(),Board.board.getCurrentBoard())) return false;
            y+=step;
        }

        return true;
    }

    public void castle(boolean isLongCastle) {
        ChessPieces rook = Board.board.getCurrentBoard()[this.getPositionX()][isLongCastle ? 0 : 7];

        Board.board.makeMove(rook, this.getPositionX(), isLongCastle ? 3 : 5);
        Board.board.makeMove(this, this.getPositionX(), isLongCastle ? 2 : 6);
    }
    protected void movePiece(int x, int y) {
        super.movePiece(x, y);
        if (isWhite())
            Board.board.setWhiteKing(new int[]{x, y});
        else
            Board.board.setBlackKing(new int[]{x, y});
    }
}
