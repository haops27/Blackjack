package blackjack.deck;

public enum Rank {
    A(1, 11),
    TWO(2, 2),
    THREE(3, 3),
    FOUR(4, 4),
    FIVE(5, 5),
    SIX(6, 6),
    SEVEN(7, 7),
    EIGHT(8, 8),
    NINE(9, 9),
    TEN(10, 10),
    J(11, 10),
    Q(12, 10),
    K(13, 10);

    private final int blackjackValue;
    private final int indexValue;

    Rank(int indexValue, int blackjackValue) {
        this.blackjackValue = blackjackValue;
        this.indexValue = indexValue;
    }

	public int getBlackjackValue() {
		return blackjackValue;
	}

	public int getIndexValue() {
		return indexValue;
	}

	@Override
	public String toString() {
        return switch (this) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            default -> super.toString();
        };
	}
	
}
