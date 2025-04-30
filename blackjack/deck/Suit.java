package blackjack.deck;

public enum Suit {
    C("black"),
    D("red"),
    H("red"),
    S("black");

    private final String color;

    Suit(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
