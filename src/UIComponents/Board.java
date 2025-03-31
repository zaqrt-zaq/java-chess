package UIComponents;

import Pieces.ChessBoard;
import Pieces.ChessPieces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class Board extends JFrame {
    private static Board board;
    private JButton[][] grid;
    private ChessPieces currentPieceSelected;
    private ChessBoard chessBoard;

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

    private void    initializeGame() {
        chessBoard = ChessBoard.getChessBoard();
        updateBoardUI();
    }

    private void updateBoardUI() {
        String[][] stingChessBoard = chessBoard.getBoardToString();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j].setText(stingChessBoard[i][j]);
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
        if (chessBoard.doesPieceBelongToCurrentPlayer(row,col)) {
            clearHighlights();

            currentPieceSelected = chessBoard.getChessPieceFromPosition(row,col);

            List<int[]> moves = currentPieceSelected.getPossibleMoves();
            highlightPossibleMoves(moves);
            highlightSpecialMoves(currentPieceSelected, moves);
            return;
        }

        if (currentPieceSelected != null) {
            if (ChessBoard.isSpecialMove(currentPieceSelected, row, col)) {
                chessBoard.makeSpecialMove(currentPieceSelected, row, col);
                currentPieceSelected = null;
                updateBoardUI();
            } else if (ChessBoard.isValidMove(currentPieceSelected, row, col)) {
                chessBoard.makeMove(currentPieceSelected, row, col);
                currentPieceSelected = null;
                updateBoardUI();
            } else if (!chessBoard.doesPieceBelongToCurrentPlayer(row, col)) {
                currentPieceSelected = null;
            }
            clearHighlights();
        }

    }

    private void highlightSpecialMoves(ChessPieces piece, List<int[]> moves) {
        for (int[] move : moves) {
            if (ChessBoard.isSpecialMove(piece, move[0], move[1])) {
                grid[move[0]][move[1]].setBackground(Color.GREEN);
            }
        }
    }

    private void highlightPossibleMoves(List<int[]> moves) {
        for (int[] move : moves) {
            if (!chessBoard.isSquareEmpty(move[0], move[1])) {
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
        chessBoard.resetGameState();
        updateBoardUI();
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
