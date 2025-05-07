package blackjack.actors;

import java.util.ArrayList;
import blackjack.deck.*;

public class Player extends Actor {
    private float bet;  // Số tiền cược của người chơi
    private float sidebets; // Số tiền cược phụ của người chơi
    private float tokens = 2500f; // Tổng số tiền người chơi đang có, bao gồm cả tiền cược
    private String name; // Tên của người chơi
    private ArrayList<Card> splitHand = new ArrayList<>();
    private int splitSum = 0;
    private int splitAceCount = 0;
    
    //Split function
    public boolean canSplit() {
        return hand.size() == 2 && hand.get(0).getRank() == hand.get(1).getRank() && tokens >= bet;
    }
    
    public boolean split(Deck deck) {
        if (canSplit()) {
            Card splitCard = hand.remove(1);
            splitHand.add(splitCard);

            splitSum += splitCard.getValue();
            sum = sum - splitCard.getValue();

            addCard(deck.getCard()); // Add to original hand
            addCardToSplitHand(deck.getCard()); // Add to split hand

            System.out.println("Player has split the hand.");
            return true;
        }
        System.out.println("Split not allowed.");
        return false;
    }

    public int getSplitSum() {
        return splitSum;
    }
    
    public void addCardToSplitHand(Card card) {
        splitHand.add(card);
        splitSum += card.getValue();
        if (card.isAce()) splitAceCount++;
        while (splitSum > 21 && splitAceCount > 0) {
            splitSum -= 10;
            splitAceCount--;
        }
    }
    
    public ArrayList<Card> getSplitHand() {
        return splitHand;
    }


    // Constructor
    public Player(String name) {
        super();
        this.name = name;
    }

    public float getTokens() {
		return tokens;
	}

	public void setTokens(float tokens) {
		this.tokens = tokens;
	}

	// Thiết lập số tiền cược
    public void setBet(float bet) {
        this.bet = bet;
    }

    // Lấy số tiền cược
    public float getBet() {
        return bet;
    }

    public float getSidebets() {
        return sidebets;
    }

    public void setSidebets(float sidebets) {
        this.sidebets = sidebets;
    }
    
    public boolean hasBlackjack() {
    	return hand.size() == 2 && sum == 21;
    }

    public String getName() {
		return name;
	}
    
    
    //Double Down fucntionality
    public boolean canDouble() {
        return hand.size() == 2 && tokens >= bet;
    }

    public boolean doubleDown(Deck deck) {
        if (!canDouble()) {
            System.out.println("You cannot double down!");
            return false;
        }
        bet *= 2;
        addCard(deck.getCard());
        System.out.println("Doubled down successfully!");
        return true;
    }

	// Cài đặt hành vi cho người chơi: người chơi có thể "Hit" hoặc "Stay"
    @Override
    public void takeTurn(Deck deck) {
        // Người chơi sẽ tự quyết định hành vi của mình, ví dụ: chỉ đơn giản rút bài
        // Thực hiện hành động "Hit" cho đến khi quyết định "Stay"
        // Đây là ví dụ, bạn có thể thêm logic để người chơi quyết định
        Card card = deck.getCard();  // Lấy lá bài cuối cùng
        addCard(card);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player o) {
			return o.getName().equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.bet = 0;
		this.sidebets = 0;
	}

    public boolean isSplitBust(){
        return splitSum > 21;
    }

} 