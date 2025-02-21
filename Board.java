import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class Board extends JFrame {
    static Board board;

    static Board getInstance() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }

    private final JButton[][] grid;
    static private final int[][] starting_position = {
            {0, 1, 2, 3, 4, 2, 1, 0},
            {5, 5, 5, 5, 5, 5, 5, 5},
            {-1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1},
            {5, 5, 5, 5, 5, 5, 5, 5},
            {0, 1, 2, 3, 4, 2, 1, 0}
    };
    public boolean isCurrentPlayerWhite = true;
    private final ChessPieces[][] currentPosition = new ChessPieces[8][8];
    private ChessPieces currentPieceSelected = null;
    private int[] whiteKing = {7, 4};
    private int[] blackKing = {0, 4};

    public void setBlackKing(int[] blackKing) {
        this.blackKing = blackKing;
    }

    public void setWhiteKing(int[] whiteKing) {
        this.whiteKing = whiteKing;
    }

    public int[] getWhiteKing() {
        return whiteKing;
    }

    public int[] getBlackKing() {
        return blackKing;
    }

    public ChessPieces getCurrentPieceSelected() {
        return currentPieceSelected;
    }

    public ChessPieces[][] getCurrentBoard() {
        return currentPosition;
    }


    private Board() {
        super();
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 8));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        this.add(panel);

        initializeBoard();
        grid = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = new JButton(currentPosition[i][j] != null ? currentPosition[i][j].toString() : "");
                grid[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                grid[i][j].setMargin(new Insets(0, 0, 0, 0));
                grid[i][j].setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
                panel.add(grid[i][j]);
            }
        }
        createActionListeners();
        setVisible(true);
    }


    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int pieceCode = starting_position[row][col];

                boolean isWhite = (row >= 6);

                currentPosition[row][col] = switch (pieceCode) {
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
    }

    private boolean canSkipCheck(ChessPieces piece, int row, int col) {
        //optimization in action
        boolean skipCheck = false;
        if (!(currentPieceSelected instanceof King) && !(currentPieceSelected instanceof Knight)) { // if it works dont fix it
            currentPosition[row][col] = null;
            King king = (King) (isCurrentPlayerWhite ? currentPosition[whiteKing[0]][whiteKing[1]]:currentPosition[blackKing[0]][blackKing[1]]);
            if (!(ChessPieces.isKingInCheck(king))){
                skipCheck = true;
            }
            currentPosition[row][col] = currentPieceSelected;
        }
        return skipCheck;
    }

    private void onPieceClick(int row, int col) {
        ChessPieces piece = currentPosition[row][col];
        currentPieceSelected = piece;
        resetBoardColors();

        List<int[]> possibleMoves = piece.getPossibleMoves(canSkipCheck(piece, row, col));

        for (int[] move : possibleMoves) {
            grid[move[0]][move[1]].setBackground(Color.GREEN);
            if (piece instanceof King && (move[1] - 2 == piece.getPositionY() || move[1] + 2 == piece.getPositionY()))
                grid[move[0]][move[1]].setBackground(Color.YELLOW);
            if (piece instanceof Pawn && move[1] != piece.getPositionY() && currentPosition[move[0]][move[1]] == null)
                grid[move[0]][move[1]].setBackground(Color.YELLOW);
        }

        createActionListeners();
    }

    private void resetBoardColors() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                grid[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);

    }

    public void makeMove(ChessPieces piece, int row, int col) {
        currentPosition[piece.getPositionX()][piece.getPositionY()] = null;
        currentPosition[row][col] = piece;
        grid[piece.getPositionX()][piece.getPositionY()].setText("");
        grid[row][col].setText(piece.toString());
        resetBoardColors();
        ChessPieces.removeUponsanuts();
        piece.movePiece(row, col);

        if (piece instanceof Pawn && (row == 0 || row == 7)) {
            promotePawn(piece, row, col);
        }

        if (currentPieceSelected != null)
            isCurrentPlayerWhite = !currentPieceSelected.isWhite();
        currentPieceSelected = null;

        resetBoardColors();
        createActionListeners();
        repaint();
    }

    private void createActionListeners() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                final int row = x;
                final int col = y;
                for (ActionListener al : grid[x][y].getActionListeners()) {
                    grid[x][y].removeActionListener(al);
                }
                if (currentPosition[x][y] != null && isCurrentPlayerWhite == currentPosition[x][y].isWhite())
                    grid[x][y].addActionListener(e -> onPieceClick(row, col));

                if (grid[x][y].getBackground() == Color.GREEN)
                    grid[x][y].addActionListener(e -> makeMove(currentPieceSelected, row, col));

                if (grid[x][y].getBackground() == Color.YELLOW)
                    grid[x][y].addActionListener(e -> ChessPieces.makeSpecialMove(currentPieceSelected, row, col));
            }
        }
    }

    private void promotePawn(ChessPieces piece, int row, int col) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                this,
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

        currentPosition[row][col] = newPiece;
        grid[row][col].setText(newPiece.toString());
    }

}
