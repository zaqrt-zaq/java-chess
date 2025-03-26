package Pieces;

public enum Castles {

    WHITE_SORT_CASTLE(0,'K'),
    WHITE_LONG_CASTLE(1,'Q'),
    BACK_SORT_CASTLE(2,'k'),
    BLACK_LONG_CASTLE(3,'q');

    public final int id;
    private final char character;


    Castles(int id,char character) {
        this.id = id;
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
