package BoardControl;

import Pieces.ChessPieces;
import Pieces.King;
import Pieces.Pawn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class Board extends JFrame {
    private static Board board;
    private JButton[][] grid;
    private ChessPieces[][] currentPosition;
    private ChessPieces currentPieceSelected;

    public static Board getInstance() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }

    private Board() {
        super();
        initializeUI();
        initializeGame();
    }

    private void initializeUI() {
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        
        // Create menu bar
        JMenuBar menuBar = getBar();
        setJMenuBar(menuBar);
        
        // Game board
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 8));
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        this.add(panel);
        
        grid = new JButton[8][8];
        currentPosition = new ChessPieces[8][8];
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = new JButton();
                grid[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                grid[i][j].setMargin(new Insets(0, 0, 0, 0));
                grid[i][j].setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
                panel.add(grid[i][j]);
            }
        }
        createActionListeners();
        setVisible(true);
    }

    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem exit = new JMenuItem("Exit");

        newGame.addActionListener(e -> resetGame());
        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.addSeparator();
        gameMenu.add(exit);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> 
            JOptionPane.showMessageDialog(this,
                "Chess Game\nVersion 1.0\nCreated by zaqrt",
                "About",
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(about);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void initializeGame() {
        currentPosition = ChessPieces.initializeBoard();
        updateBoardUI();
    }

    private void updateBoardUI() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j].setText(currentPosition[i][j] != null ? currentPosition[i][j].toString() : "");
            }
        }
    }

    private void createActionListeners() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int row = i;
                final int col = j;
                grid[i][j].addActionListener(e -> handleClick(row, col));
            }
        }
    }

    private void handleClick(int row, int col) {
        // Jeśli kliknięto na nową figurę właściwego koloru
        if (currentPosition[row][col] != null && 
            currentPosition[row][col].isWhite() == ChessPieces.isCurrentPlayerWhite()) {
            // Wyczyść poprzednie zaznaczenie
            clearHighlights();
            // Zaznacz nową figurę
            currentPieceSelected = currentPosition[row][col];
            List<int[]> moves = currentPieceSelected.getPossibleMoves();
            highlightPossibleMoves(moves);
            highlightSpecialMoves(currentPieceSelected, moves);
            return;
        }

        // Jeśli figura jest zaznaczona i kliknięto na pole ruchu
        if (currentPieceSelected != null) {
            if (isSpecialMove(currentPieceSelected, row, col)) {
                ChessPieces.makeSpecialMove(currentPieceSelected, row, col, currentPosition);
                currentPieceSelected = null;
                updateBoardUI();
            } else if (ChessPieces.isValidMove(currentPieceSelected, row, col)) {
                ChessPieces.makeMove(currentPieceSelected, row, col, currentPosition);
                currentPieceSelected = null;
                updateBoardUI();
            } else if (grid[row][col].getText().isEmpty() ||
                      currentPosition[row][col].isWhite() != ChessPieces.isCurrentPlayerWhite()) {
                // Jeśli kliknięto na puste pole lub figurę przeciwnika, a nie jest to prawidłowy ruch
                currentPieceSelected = null;
            }
            clearHighlights();

        }

    }

    private boolean isSpecialMove(ChessPieces piece, int row, int col) {
        if (piece instanceof King) {
            return Math.abs(col - piece.getPositionY()) == 2;
        } else if (piece instanceof Pawn) {
            return col != piece.getPositionY() && currentPosition[row][col] == null;
        }
        return false;
    }

    private void highlightSpecialMoves(ChessPieces piece, List<int[]> moves) {
        for (int[] move : moves) {
            if (isSpecialMove(piece, move[0], move[1])) {
                grid[move[0]][move[1]].setBackground(Color.GREEN);
            }
        }
    }

    private void highlightPossibleMoves(List<int[]> moves) {
        for (int[] move : moves) {
            if (!grid[move[0]][move[1]].getText().isEmpty()) {
                grid[move[0]][move[1]].setBackground(Color.RED);
            } else {
                grid[move[0]][move[1]].setBackground(Color.YELLOW);
            }
        }
    }

    private void clearHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
            }
        }
    }

    private void resetGame() {
        currentPieceSelected = null;
        clearHighlights();
        ChessPieces.resetGame();
        currentPosition = ChessPieces.initializeBoard();
        updateBoardUI();
    }

    public ChessPieces[][] getCurrentBoard() {
        return currentPosition;
    }

    public void announceWinner(String winner){
        JOptionPane.showMessageDialog(null, winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    public int chosePromotion(){
        String[] options = {"Pieces.Queen", "Pieces.Rook", "Pieces.Bishop", "Pieces.Knight"};
        return JOptionPane.showOptionDialog(
                null,
                "Choose promotion:",
                "Pieces.Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
    }
}
