package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> deck;
    private final List<Card> discardPile;
    private static final Card WILDCARD = new Card(null, null);
    private boolean wildCardReached = false;
    private int position;

    private static final Rank[] ranks = {
        Rank.A, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
        Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.J, Rank.Q, Rank.K
    };
    private static final Suit[] suits = {Suit.C, Suit.D, Suit.H, Suit.S};

    public Deck() {
        deck = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck(1);
    }

    public Deck(int n) {
        deck = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck(n);
    }

    private void initializeDeck(int n) {
        deck.clear();
        for (int k = 0; k < n; k++) {
            for (Suit s : suits) {
                for (Rank r : ranks) {
                    deck.add(new Card(r, s));
                }
            }
        }
        shuffleDeck();
        wildCardReached = false;
    }

    public void discard(Card card) {
        discardPile.add(card);
    }

    private void placeWildCard() {
        position = (int)(deck.size() * 0.25);
        deck.add(position, WILDCARD);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
        placeWildCard();
    }

    /**
     * Nếu deck hết bài hoặc đã gặp wildcard thì reset lại full bộ bài.
     * Trả về true nếu có reshuffle, false nếu không cần.
     */
    public boolean reshuffle() {
        if (deck.isEmpty() || wildCardReached) {
            reset();
            System.out.println("Deck gần hết bài hoặc đã gặp wildcard, tiến hành xào lại...");
            return true;
        }
        return false;
    }

    /**
     * Lấy bài từ bộ bài.
     * Nếu lấy phải wildcard thì đánh dấu đã gặp wildcard và lấy lại bài khác.
     * Nếu hết bài thì ném ra lỗi (nên gọi safeGetCard để an toàn).
     */
    public Card getCard() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Deck is empty!");
        }
        Card card = deck.remove(deck.size() - 1);
        if (card.equals(WILDCARD)) {
            wildCardReached = true;
            return getCard(); // lấy lại bài khác
        }
        return card;
    }

    public int size() {
        return deck.size();
    }

    public void clear() {
        deck.clear();
        discardPile.clear();
        wildCardReached = false;
    }

    /**
     * Reset bộ bài đầy đủ (52 lá), xóa bỏ discardPile, shuffle lại, reset trạng thái wildcard.
     */
    public void reset() {
        initializeDeck(1);
        discardPile.clear();
        wildCardReached = false;
    }
}