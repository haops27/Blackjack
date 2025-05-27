package cards;

public enum Suit {
    C(Color.BLACK),
    D(Color.RED),
    H(Color.RED),
    S(Color.BLACK);
	
	enum Color {
	    RED, BLACK;
	}

    private final Color color;

    Suit(Color color) {
        this.color = color;
    }

    Color getColor() {
        return color;
    }
}
