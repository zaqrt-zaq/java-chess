package UIComponents;

import javax.swing.*;
import java.awt.*;

public class SelectionScreen extends JPanel {
    private JFrame frame;
    private JTextField fenInput;
    private JLabel statusLabel;

    public SelectionScreen() {
        frame = new JFrame("Chess Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 1));

        JButton newGameButton = new JButton("Nowa gra");
        JButton fenButton = new JButton("Wpisz FEN string");
        JButton exitButton = new JButton("Wyjście");
        fenInput = new JTextField();
        statusLabel = new JLabel("Wybierz opcję", SwingConstants.CENTER);

        newGameButton.addActionListener(e -> startNewGame());
        fenButton.addActionListener(e -> loadFromFEN());
        exitButton.addActionListener(e -> System.exit(0));

        frame.add(newGameButton);
        frame.add(fenInput);
        frame.add(fenButton);
        frame.add(exitButton);
        frame.add(statusLabel);

        frame.setVisible(true);
    }

    private void startNewGame() {
        statusLabel.setText("Rozpoczynasz nową grę. FEN: startowy FEN");
    }

    private void loadFromFEN() {
        String fen = fenInput.getText();
        if (fen.isEmpty()) {
            statusLabel.setText("Proszę wpisać FEN string.");
        } else {
            statusLabel.setText("Gra załadowana z FEN: " + fen);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SelectionScreen::new);
    }
}

