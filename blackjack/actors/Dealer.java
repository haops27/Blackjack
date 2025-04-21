package blackjack.actors;

import java.util.ArrayList;

import blackjack.deck.Card;
import blackjack.deck.Deck;

public class Dealer {
    private ArrayList<Card> hand;      // Danh sách các lá bài của nhà cái
    private int sum;                   // Tổng điểm của nhà cái
    private int aceCount;              // Số lượng Ace trong tay nhà cái

    // Constructor
    public Dealer() {
        this.hand = new ArrayList<>();
        this.sum = 0;
        this.aceCount = 0;
    }

    // Thêm một lá bài vào tay nhà cái
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

    // Trả về tổng điểm của nhà cái
    public int getSum() {
        return sum;
    }

    // Trả về các lá bài của nhà cái
    public ArrayList<Card> getHand() {
        return hand;
    }

    // Kiểm tra nếu nhà cái đã "bust" (quá 21 điểm)
    public boolean isBust() {
        return sum > 21;
    }

    // Nhà cái rút bài cho đến khi có ít nhất 17 điểm
    public void playTurn(Deck deck) {
        // Nhà cái rút bài cho đến khi có ít nhất 17 điểm
        while (sum < 17) {
            Card card = deck.getDeck().remove(deck.getDeck().size() - 1);  // Lấy lá bài cuối cùng từ bộ bài
            addCard(card);
        }
    }

    // Reset tay bài của nhà cái (sau mỗi ván mới)
    public void reset() {
        hand.clear();
        sum = 0;
        aceCount = 0;
    }
}
