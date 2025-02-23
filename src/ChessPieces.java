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

    protected List<int[]> move(int[][] directions, boolean isLimited) {

        List<int[]> moves = new ArrayList<>();

        ChessPieces[][] board = Board.board.getCurrentBoard();

        for (int[] direction : directions) {
            for (int j = 1; j <= (isLimited ? 1 : 7); j++) {
                int newX = positionX + direction[0] * j;
                int newY = positionY + direction[1] * j;
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }
                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece == null) {
                    moves.add(new int[]{newX, newY});
                } else {
                    if (targetPiece.isWhite() != this.isWhite()) {
                        moves.add(new int[]{newX, newY});
                    }
                    break;
                }
            }
        }
        return moves;
    }

    protected List<int[]> moveByOne(int[][] directions) {
        return move(directions, true);
    }

    protected List<int[]> moveByMore(int[][] directions) {
        return move(directions, false);
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
            ((Pawn) piece).captureEnPassant(x,y);
    }

    static boolean isKingInCheck(int x, int y, boolean isKingWhite, ChessPieces[][] board) {
        int pawnDirection = isKingWhite ? -1 : 1;

        // Sprawdzenie ataku przez skoczka
        for (int[] move : Knight.moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                ChessPieces piece = board[newX][newY];
                if (piece instanceof Knight && piece.isWhite() != isKingWhite) {
                    return true;
                }
            }
        }

        // Sprawdzenie ataku przez pionki
        if ((x > 0 && y + pawnDirection >= 0 && y + pawnDirection < 8 &&
                board[x + pawnDirection][y + 1] instanceof Pawn &&
                board[x + pawnDirection][y + 1].isWhite() != isKingWhite) ||
                (x < 7 && y + pawnDirection >= 0 && y + pawnDirection < 8 &&
                        board[x + pawnDirection][y - 1] instanceof Pawn &&
                        board[x + pawnDirection][y - 1].isWhite() != isKingWhite)) {
            return true;
        }

        // Sprawdzenie ataku przez wieżę i królową
        for (int[] direction : Rook.moves) {
            for (int i = 1; i < 8; i++) {
                int newX = x + direction[0] * i;
                int newY = y + direction[1] * i;

                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;

                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece == null) continue;
                if (targetPiece.isWhite() == isKingWhite) break; // Zatrzymaj, jeśli napotkasz swoją figurę
                if (targetPiece instanceof Rook || targetPiece instanceof Queen) {
                    return true; // Król jest w szachu
                }
                break; // Zatrzymaj, jeśli napotkasz inną figurę
            }
        }

        // Sprawdzenie ataku przez gońca i królową
        for (int[] direction : Bishop.moves) {
            for (int i = 1; i < 8; i++) {
                int newX = x + direction[0] * i;
                int newY = y + direction[1] * i;

                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;

                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece == null) continue;
                if (targetPiece.isWhite() == isKingWhite) break; // Zatrzymaj, jeśli napotkasz swoją figurę
                if (targetPiece instanceof Bishop || targetPiece instanceof Queen) {
                    return true; // Król jest w szachu
                }
                break; // Zatrzymaj, jeśli napotkasz inną figurę
            }
        }

        // Sprawdzenie ataku przez króla
        for (int[] move : King.moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                ChessPieces targetPiece = board[newX][newY];
                if (targetPiece instanceof King && targetPiece.isWhite() != isKingWhite) {
                    return true; // Król przeciwnika atakuje
                }
            }
        }

        return false; // Król nie jest w szachu
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

        if (piece instanceof King) {
            isValid = !isKingInCheck(x,y,piece.isWhite(), board);
        }else
            isValid = !isKingInCheck(Board.getInstance().getCurrentKing(piece));

        board[oldX][oldY] = piece;
        board[x][y] = previousPiece;

        return isValid;
    }

    static void resetEnPassantFlags(){
        ChessPieces[][] board = Board.board.getCurrentBoard();
        // Reset en passant flags for all pawns
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] instanceof Pawn) {
                    ((Pawn) board[row][col]).setCanBeEnPassantCaptured(false);
                }
            }
        }
    }

    protected List<int[]> validateMoves(List<int[]> moves) {
        ArrayList<int[]> possibleMoves = new ArrayList<>();

        for (int[] move : moves)
            if(isMoveValid(this,move[0],move[1]))
                possibleMoves.add(move);

        return possibleMoves;
    }

    protected boolean canSkipCheck() {
        //optimization in action
        if (ChessPieces.isKingInCheck(Board.getInstance().getCurrentKing(this)))
            return false;
        boolean skipCheck = false;

        if (!(this instanceof King) && !(this instanceof Knight)) { // if it works dont fix it
            Board.getInstance().getCurrentBoard()[this.positionX][this.positionY] = null;
            if (!(ChessPieces.isKingInCheck(Board.getInstance().getCurrentKing(this)))){
                skipCheck = true;
            }
            Board.getInstance().getCurrentBoard()[this.positionX][this.positionY] = this;
        }
        return skipCheck;
    }


}
