package blackjack.actors;

import java.util.ArrayList;

import blackjack.deck.*;

public abstract class Actor {
    protected ArrayList<Card> hand;  // Danh sách các lá bài của người chơi hoặc nhà cái
    protected int sum;               // Tổng điểm
    protected int aceCount;          // Số lượng Ace trong tay

    // Constructor
    public Actor() {
        this.hand = new ArrayList<>();
        this.sum = 0;
        this.aceCount = 0;
    }

    // Thêm một lá bài vào tay
    public void addCard(Card card) {
        hand.add(card);
        sum += card.getValue();
        if (card.isAce()) {
            aceCount++;
        }
        adjustForAce();  // Điều chỉnh giá trị Ace nếu cần
    }

    // Điều chỉnh giá trị của Ace (A) nếu tổng điểm vượt quá 21
    private void adjustForAce() {
        while (sum > 21 && aceCount > 0) {
            sum -= 10;  // Chuyển Ace từ 11 xuống 1
            aceCount--;
        }
    }

    // Trả về tổng điểm
    public int getSum() {
        return sum;
    }

    // Trả về các lá bài của actor
    public ArrayList<Card> getHand() {
        return hand;
    }

    // Kiểm tra nếu actor đã "bust" (quá 21 điểm)
    public boolean isBust() {
        return sum > 21;
    }

    // Abstract method để xử lý hành vi của Actor (Player hoặc Dealer)
    public abstract void takeTurn(Deck deck);

    // Reset tay bài của actor (sau mỗi ván mới)
    public void reset() {
        hand.clear();
        sum = 0;
        aceCount = 0;
    }
}

