package Pieces;

public enum Castles {

    WHITE_SHORT_CASTLE(0,'K',7,7),
    WHITE_LONG_CASTLE(1,'Q',7,0),
    BLACK_SHORT_CASTLE(2,'k',0,7),
    BLACK_LONG_CASTLE(3,'q',0,0),;

    public final int id;
    private final char character;
    public final int row;
    public final int rookCol;


    Castles(int id,char character,int row,int rookCol) {
        this.id = id;
        this.character = character;
        this.row = row;
        this.rookCol = rookCol;
    }

    public char getCharacter() {
        return character;
    }
}
