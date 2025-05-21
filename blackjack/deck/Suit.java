package blackjack.deck;

public enum Suit {
    C(Color.BLACK),
    D(Color.RED),
    H(Color.RED),
    S(Color.BLACK);

    public enum Color {
        RED, BLACK;
    }

    private final Color color;

    Suit(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
