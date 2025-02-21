import java.util.ArrayList;
import java.util.List;

abstract class ChessPieces {
    private int positionX;
    private int positionY;
    private final boolean isWhite;
    private boolean hasMoved;

    public ChessPieces(int positionX, int positionY, boolean isWhite) {
        this.isWhite = isWhite;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hasMoved = false;
    }

    @Override
    public String toString() {
        char whiteSymbol = getWhiteSymbol();
        return isWhite ? String.valueOf(whiteSymbol) : String.valueOf((char) (whiteSymbol + 6));
    }

    protected abstract char getWhiteSymbol();

    public abstract List<int[]> getPossibleMoves();

    public boolean isWhite() {
        return isWhite;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    private static List<int[]> moveByOne(int[][] directions, int x, int y, boolean isPieceWhite) {
        List<int[]> moves = new ArrayList<>();
        ChessPieces[][] board = Board.board.getCurrentBoard();

        for (int[] move : directions) {
            int newX = x + move[0];
            int newY = y + move[1];

            if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8)
                continue;


            ChessPieces targetPiece = board[newX][newY];
            if (targetPiece == null || targetPiece.isWhite() != isPieceWhite)
                moves.add(new int[]{newX, newY});

        }
        return moves;
    }

    protected List<int[]> moveByOne(int[][] directions) {
        return moveByOne(directions, positionX, positionY, isWhite);
    }

    private static List<int[]> moveByMore(int[][] directions, int x, int y, boolean isPieceWhite) {
        ChessPieces[][] board = Board.board.getCurrentBoard();
        ArrayList<int[]> moves = new ArrayList<>();

        for (int[] move : directions) {
            for (int j = 1; j < 8; j++) {
                int newX = x + move[0] * j;
                int newY = y + move[1] * j;

                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8)
                    break;

                ChessPieces targetPiece = board[newX][newY];

                if (targetPiece == null) {
                    moves.add(new int[]{newX, newY});
                } else {
                    if (targetPiece.isWhite != isPieceWhite) {
                        moves.add(new int[]{newX, newY});
                    }
                    break;
                }
            }
        }
        return moves;
    }


    protected List<int[]> moveByMore(int[][] directions) {
        return moveByMore(directions, positionX, positionY, isWhite);
    }

    protected void movePiece(int x, int y) {
        this.positionX = x;
        this.positionY = y;
        this.hasMoved = true;
    }


    public static void makeSpecialMove(ChessPieces piece, int x, int y) {
        if (piece instanceof King)
            ((King) piece).castle(x == 2);
        else if (piece instanceof Pawn)
            ((Pawn) piece).unpasoud(x,y);
            return;
    }

    static boolean isKingInCheck(int x, int y, boolean isKingWhite, ChessPieces[][] board) {
        int pawnDirection = isKingWhite ? -1 : 1;

        for (int[] move : moveByOne(Knight.moves, x, y, isKingWhite)) {
            ChessPieces piece = board[move[0]][move[1]];
            if (piece instanceof Knight && piece.isWhite != isKingWhite) {
                return true;
            }
        }

        //if it ain't broke don't fix it
        if ((x > 0 && y + pawnDirection >= 0 && y + pawnDirection < 8 &&
                board[x + pawnDirection][y + 1] instanceof Pawn &&
                board[x + pawnDirection][y + 1].isWhite != isKingWhite) ||
            (x < 7 && y + pawnDirection >= 0 && y + pawnDirection < 8 &&
                board[x + pawnDirection][y - 1] instanceof Pawn &&
                board[x + pawnDirection][y - 1].isWhite != isKingWhite)) {
            return true;
        }

        for (int[] direction : King.moves) {
            int newX = x, newY = y;
            for (int i = 1; i < 8; i++) {
                newX += direction[0];
                newY += direction[1];

                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;

                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece == null) continue;
                if (targetPiece.isWhite == isKingWhite) break;
                switch (targetPiece) {
                    case Queen queen -> { return true; }
                    case Rook rook when (direction[0] == 0 || direction[1] == 0) -> { return true; }
                    case Bishop bishop when (direction[0] != 0 && direction[1] != 0) -> { return true; }
                    default -> {}
                }
                break;
            }
        }

        for (int[] move : King.moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece instanceof King && targetPiece.isWhite != isKingWhite) {
                    return true;
                }
            }
        }

        return false;
    }



    static boolean isKingInCheck(King king) {
        return isKingInCheck(king.getPositionX(), king.getPositionY(), king.isWhite(), Board.board.getCurrentBoard());
    }

    static boolean isMoveValid(ChessPieces piece, int x, int y) {
        ChessPieces[][] board = Board.board.getCurrentBoard();

        int oldX = piece.getPositionX();
        int oldY = piece.getPositionY();
        boolean isValid;
        ChessPieces previousPiece = board[x][y];

        board[oldX][oldY] = null;
        board[x][y] = piece;

        if (piece instanceof King)
            isValid = !isKingInCheck(x, y, piece.isWhite, board);
        else {
            int[] king = piece.isWhite ? Board.board.getWhiteKing() : Board.board.getBlackKing();
            isValid = !isKingInCheck(king[0], king[1], piece.isWhite, board);
        }

        board[oldX][oldY] = piece;
        board[x][y] = previousPiece;

        return isValid;
    }

    static void removeUponsanuts(){
        ChessPieces[][] board = Board.board.getCurrentBoard();
        final int blackRow = 4;
        final int whiteRow = 5;
        for (int col = 0; col < 8; col++) {
            if (board[whiteRow][col] instanceof Pawn)
                ((Pawn) board[whiteRow][col]).setCanBeUmpasoundet(false);
            if (board[blackRow][col] instanceof Pawn)
                ((Pawn) board[blackRow][col]).setCanBeUmpasoundet(true);
        }
        return;
    }

}
