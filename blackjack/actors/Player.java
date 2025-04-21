package blackjack.actors;

import java.util.ArrayList;

import blackjack.deck.Card;

public class Player {
    private ArrayList<Card> hand;      // Danh sách các lá bài của người chơi
    private int sum;                   // Tổng điểm của người chơi
    private int aceCount;              // Số lượng Ace trong tay người chơi
    private int bet;                   // Số tiền cược của người chơi

    // Constructor
    public Player() {
        this.hand = new ArrayList<>();
        this.sum = 0;
        this.aceCount = 0;
        this.bet = 0;
    }

    // Thêm một lá bài vào tay người chơi
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

    // Trả về tổng điểm của người chơi
    public int getSum() {
        return sum;
    }

    // Trả về các lá bài của người chơi
    public ArrayList<Card> getHand() {
        return hand;
    }

    // Thiết lập số tiền cược
    public void setBet(int bet) {
        this.bet = bet;
    }

    // Lấy số tiền cược
    public int getBet() {
        return bet;
    }

    // Kiểm tra nếu người chơi đã "bust" (quá 21 điểm)
    public boolean isBust() {
        return sum > 21;
    }

    // Reset tay bài của người chơi (sau mỗi ván mới)
    public void reset() {
        hand.clear();
        sum = 0;
        aceCount = 0;
    }
}
