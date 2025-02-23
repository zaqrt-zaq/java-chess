import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

abstract class ChessPieces {
    private int positionX;
    private int positionY;
    private final boolean isWhite;
    private boolean hasMoved;
    private static boolean isCurrentPlayerWhite = true;
    private static int[] whiteKing = {7, 4};
    private static int[] blackKing = {0, 4};
    
    private static final int[][] STARTING_POSITION = {
        {0, 1, 2, 3, 4, 2, 1, 0},
        {5, 5, 5, 5, 5, 5, 5, 5},
        {-1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1},
        {5, 5, 5, 5, 5, 5, 5, 5},
        {0, 1, 2, 3, 4, 2, 1, 0}
    };

    public ChessPieces(int positionX, int positionY, boolean isWhite) {
        this.isWhite = isWhite;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hasMoved = false;
    }

    public static ChessPieces[][] initializeBoard() {
        ChessPieces[][] board = new ChessPieces[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int pieceCode = STARTING_POSITION[row][col];
                boolean isWhite = (row >= 6);
                
                board[row][col] = switch (pieceCode) {
                    case 0 -> new Rook(row, col, isWhite);
                    case 1 -> new Knight(row, col, isWhite);
                    case 2 -> new Bishop(row, col, isWhite);
                    case 3 -> new Queen(row, col, isWhite);
                    case 4 -> new King(row, col, isWhite);
                    case 5 -> new Pawn(row, col, isWhite);
                    default -> null;
                };
            }
        }
        return board;
    }

    public static boolean isCurrentPlayerWhite() {
        return isCurrentPlayerWhite;
    }

    public static void makeMove(ChessPieces piece, int newX, int newY, ChessPieces[][] board) {
        resetEnPassantFlags();
        
        // Store the captured piece (if any) before making the move
        ChessPieces capturedPiece = board[newX][newY];
        
        // Make the move
        board[piece.getPositionX()][piece.getPositionY()] = null;
        board[newX][newY] = piece;
        piece.movePiece(newX, newY);
        
        // Handle pawn promotion
        if (piece instanceof Pawn && (newX == 0 || newX == 7)) {
            promotePawn(piece, newX, newY, board);
        }
        
        // Set en passant flag if pawn moves two squares
        if (piece instanceof Pawn && Math.abs(newX - piece.getPositionX()) == 2) {
            ((Pawn) piece).setCanBeEnPassantCaptured(true);
        }
        
        isCurrentPlayerWhite = !piece.isWhite();
        
        if (isCheckmate(isCurrentPlayerWhite, board)) {
            String winner = isCurrentPlayerWhite ? "Black" : "White";
            JOptionPane.showMessageDialog(null, winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void makeSpecialMove(ChessPieces piece, int x, int y, ChessPieces[][] board) {
        if (piece instanceof King) {
            int rookCol = x == 2 ? 0 : 7;
            ChessPieces rook = board[piece.getPositionX()][rookCol];
            
            // Move rook
            board[piece.getPositionX()][rookCol] = null;
            board[piece.getPositionX()][x == 2 ? 3 : 5] = rook;
            rook.movePiece(piece.getPositionX(), x == 2 ? 3 : 5);
            
            // Move king
            board[piece.getPositionX()][piece.getPositionY()] = null;
            board[piece.getPositionX()][x == 2 ? 2 : 6] = piece;
            piece.movePiece(piece.getPositionX(), x == 2 ? 2 : 6);
            
        } else if (piece instanceof Pawn) {
            // En passant capture
            board[piece.getPositionX()][y] = null; // Remove captured pawn
            board[piece.getPositionX()][piece.getPositionY()] = null;
            board[x][y] = piece;
            piece.movePiece(x, y);
        }
        
        isCurrentPlayerWhite = !piece.isWhite();
        
        if (isCheckmate(isCurrentPlayerWhite, board)) {
            String winner = isCurrentPlayerWhite ? "Black" : "White";
            JOptionPane.showMessageDialog(null, winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean isValidMove(ChessPieces piece, int newX, int newY) {
        List<int[]> possibleMoves = piece.getPossibleMoves();
        for (int[] move : possibleMoves) {
            if (move[0] == newX && move[1] == newY) {
                return true;
            }
        }
        return false;
    }

    private static void promotePawn(ChessPieces piece, int row, int col, ChessPieces[][] board) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        ChessPieces newPiece = switch (choice) {
            case 1 -> new Rook(row, col, piece.isWhite());
            case 2 -> new Bishop(row, col, piece.isWhite());
            case 3 -> new Knight(row, col, piece.isWhite());
            default -> new Queen(row, col, piece.isWhite());
        };

        board[row][col] = newPiece;
    }

    public static boolean isCheckmate(boolean isWhite, ChessPieces[][] board) {
        int[] kingPosition = isWhite ? whiteKing : blackKing;
        King king = (King) board[kingPosition[0]][kingPosition[1]];

        if (!isKingInCheck(king.getPositionX(), king.getPositionY(), king.isWhite(), board)) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPieces piece = board[i][j];
                if (piece != null && piece.isWhite() == isWhite && !piece.getPossibleMoves().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
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

    public static void updateKingPosition(boolean isWhite, int[] position) {
        if (isWhite) {
            whiteKing = position;
        } else {
            blackKing = position;
        }
    }

    public static int[] getKingPosition(boolean isWhite) {
        return isWhite ? whiteKing : blackKing;
    }

    public static void resetEnPassantFlags() {
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();
        // Reset en passant flags for all pawns
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] instanceof Pawn) {
                    ((Pawn) board[row][col]).setCanBeEnPassantCaptured(false);
                }
            }
        }
    }

    public static void resetGame() {
        isCurrentPlayerWhite = true;
        whiteKing = new int[]{7, 4};
        blackKing = new int[]{0, 4};
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

    static boolean isMoveValid(ChessPieces piece, int newX, int newY) {
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();
        boolean isValid = true;
        ChessPieces originalPiece = board[newX][newY];

        // Symuluj ruch
        board[piece.getPositionX()][piece.getPositionY()] = null;
        board[newX][newY] = piece;

        // Sprawdź czy król nie jest szachowany po ruchu
        int[] kingPos = getKingPosition(piece.isWhite());
        isValid = !isKingInCheck(kingPos[0], kingPos[1], piece.isWhite(), board);

        // Cofnij symulowany ruch
        board[piece.getPositionX()][piece.getPositionY()] = piece;
        board[newX][newY] = originalPiece;

        return isValid;
    }

    public boolean isInCheck() {
        int[] kingPos = getKingPosition(isWhite());
        return isKingInCheck(kingPos[0], kingPos[1], isWhite(), Board.getInstance().getCurrentBoard());
    }

    public boolean isValidCastling() {
        if (isInCheck()) {
            return false;
        }
        
        List<int[]> possibleMoves = getPossibleMoves();
        for (int[] move : possibleMoves) {
            if (!isKingInCheck(move[0], move[1], isWhite(), Board.getInstance().getCurrentBoard())) {
                return true;
            }
        }
        return false;
    }

    protected List<int[]> validateMoves(List<int[]> moves) {
        ChessPieces[][] board = Board.getInstance().getCurrentBoard();
        moves.removeIf(move -> {
            boolean isValid = true;
            ChessPieces originalPiece = board[move[0]][move[1]];
            board[move[0]][move[1]] = this;
            board[getPositionX()][getPositionY()] = null;

            int[] kingPos = getKingPosition(isWhite());
            isValid = !isKingInCheck(kingPos[0], kingPos[1], isWhite(), board);

            board[getPositionX()][getPositionY()] = this;
            board[move[0]][move[1]] = originalPiece;

            return !isValid;
        });
        return moves;
    }

    protected boolean canSkipCheck() {
        int[] kingPos = getKingPosition(isWhite());
        if (isKingInCheck(kingPos[0], kingPos[1], isWhite(), Board.getInstance().getCurrentBoard())) {
            return false;
        }
        return true;
    }
}
