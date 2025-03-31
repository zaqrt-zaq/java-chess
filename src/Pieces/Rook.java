package Pieces;

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
    public List<int[]> getPossibleMoves() {
        if (this.canSkipCheck()) return moveByMore(Rook.moves);
        return validateMoves(moveByMore(Rook.moves));
    }

    @Override
    public char getFENSymbol() {
        return isWhite() ? 'R' : 'r';
    }

    @Override
    protected void movePiece(int x, int y) {
        super.movePiece(x, y);
        ChessBoard chessBoard = ChessBoard.getChessBoard();
        Castles castleToDisable = isWhite()
                ? (Castles.WHITE_SHORT_CASTLE.rookCol == this.positionY ? Castles.WHITE_LONG_CASTLE : Castles.WHITE_SHORT_CASTLE)
                : (Castles.BLACK_SHORT_CASTLE.rookCol == this.positionY ? Castles.BLACK_LONG_CASTLE : Castles.BLACK_SHORT_CASTLE);

        chessBoard.disableCastle(castleToDisable);

    }
}
