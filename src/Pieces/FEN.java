package Pieces;

public class FEN {
    private FEN(){}

    static String DEFAULT_FEN_STRING ="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static String getFENString(ChessBoard chessBoard){
        StringBuilder fenString = new StringBuilder();
        ChessPieces[][] chessPieces = chessBoard.getCurrentPosition();
        for (int i =0;i<8;i++) {
            int consequenceEmptySpaces=0;
            for(ChessPieces piece : chessPieces[i]){
                if (piece == null) {
                    consequenceEmptySpaces++;
                    continue;
                }
                if (consequenceEmptySpaces>0)
                    fenString.append(consequenceEmptySpaces);

                fenString.append(piece.getFENSymbol());
            }
            fenString.append("/");
        }
        fenString.deleteCharAt(fenString.length() - 1);

        fenString.append(" ");
        fenString.append(chessBoard.isCurrentPlayerWhite() ? "w" : "b");

        fenString.append(" ");
        for (Castles castle : Castles.values()){
            fenString.append(chessBoard.isCastleAllowed(castle)?castle.getCharacter():"");
        }
        if (fenString.charAt(fenString.length() - 1) == ' ') {
            fenString.append("-");
        }

        fenString.append(" ");
        if (chessBoard.getPossibleEnPassant() != null) {
            ChessPieces piece =  chessBoard.getPossibleEnPassant();
            fenString.append(
                    (char) ('a' + piece.getPositionY()) + ( 8 - piece.getPositionX())
            );
        }else {
            fenString.append("-");
        }


        return fenString.toString();
    }

    public static Object[] createChessPiecesArray(String fenString){
        ChessPieces[][] chessPieces = new ChessPieces[8][8];
        String[] rows = fenString.split(" ")[0].split("/");
        int[][] kingPositions = new int[2][2];

        for (int i = 0; i < 8; i++) {
            int col = 0;
            for (char c : rows[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += c - '0';
                } else {
                    boolean isWhite = Character.isUpperCase(c);
                    switch (Character.toLowerCase(c)) {
                        case 'p': chessPieces[i][col] = new Pawn(i, col, isWhite); break;
                        case 'r': chessPieces[i][col] = new Rook(i, col, isWhite); break;
                        case 'n': chessPieces[i][col] = new Knight(i, col, isWhite); break;
                        case 'b': chessPieces[i][col] = new Bishop(i, col, isWhite); break;
                        case 'q': chessPieces[i][col] = new Queen(i, col, isWhite); break;
                        case 'k': {
                            chessPieces[i][col] = new King(i, col, isWhite);
                            kingPositions[isWhite?0:1][0] = i;
                            kingPositions[isWhite?0:1][1] = col;
                            break;
                        }
                    }
                    col++;
                }
            }
        }
        return new Object[]{chessPieces, kingPositions};
    }

    public static boolean getIsWhite(String fenString){
        return fenString.contains("w");
    }

    public static boolean[] getPossibleCastles(String fenString){
        String castleString = fenString.split(" ")[2];
        boolean[] possibleCastles = new boolean[4];

        for (Castles castle : Castles.values()) {
            if (castleString.contains(String.valueOf(castle.getCharacter()))) {
                possibleCastles[castle.id] = true;
            }
        }

        return possibleCastles;
    }

    public static ChessPieces getPossibleEnPassant(String fenString,ChessPieces[][] chessPieces){
        String pieceString = fenString.split(" ")[3];
        if (pieceString.equals("-")) {
            return null;
        }
        int col = (pieceString.charAt(0) - 'a');
        int row = (8 - pieceString.charAt(1));
        return chessPieces[row][col];
    }

}
