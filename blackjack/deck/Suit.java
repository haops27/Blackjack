package blackjack.deck;

enum Color {
    RED, BLACK;
}

public enum Suit {
    C(Color.BLACK),
    D(Color.RED),
    H(Color.RED),
    S(Color.BLACK);

    private final Color color;

    Suit(Color color) {
        this.color = color;
    }

    Color getColor() {
        return color;
    }
}
