package texas.logic;

import blackjack.deck.Card;
import blackjack.deck.Rank;
import java.util.*;

public class PokerHandEvaluator {

    /** Đại diện giá trị của một hand để so sánh. */
    public static class HandValue implements Comparable<HandValue> {
        // rankType: 9=RoyalFlush,8=StraightFlush,7=FourKind,6=FullHouse,5=Flush,
        // 4=Straight,3=ThreeKind,2=TwoPair,1=OnePair,0=HighCard
        public final int rankType;
        // kickerRanks dùng để so tie-breaker, sorted giảm dần
        public final List<Integer> kickerRanks;

        public HandValue(int rankType, List<Integer> kickerRanks) {
            this.rankType = rankType;
            this.kickerRanks = kickerRanks;
        }

        @Override
        public int compareTo(HandValue o) {
            if (this.rankType != o.rankType)
                return Integer.compare(this.rankType, o.rankType);
            // so từng kicker
            for (int i = 0; i < this.kickerRanks.size(); i++) {
                int cmp = Integer.compare(this.kickerRanks.get(i), o.kickerRanks.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "HandValue{" + "type=" + rankType + ", kickers=" + kickerRanks + '}';
        }
    }

    /** Tìm hand 5 lá mạnh nhất trong 7 lá (hole + community). */
    public static HandValue evaluateBest(List<Card> hole, List<Card> community) {
        // gom chung vào ArrayList để sinh tổ hợp:
        List<Card> all = new ArrayList<>(hole.size() + community.size());
        all.addAll(hole);
        all.addAll(community);

        HandValue best = null;
        int n = all.size(); // thường là 7
        // sinh tất cả C(n,5) tổ hợp
        for (int a = 0; a < n - 4; a++) {
            for (int b = a + 1; b < n - 3; b++) {
                for (int c = b + 1; c < n - 2; c++) {
                    for (int d = c + 1; d < n - 1; d++) {
                        for (int e = d + 1; e < n; e++) {
                            List<Card> combo = List.of(
                                all.get(a), all.get(b), all.get(c),
                                all.get(d), all.get(e)
                            );
                            HandValue hv = evaluate5(combo);
                            if (best == null || hv.compareTo(best) > 0) {
                                best = hv;
                            }
                        }
                    }
                }
            }
        }
        return best;
    }

    /** Đánh giá 1 hand 5 lá và trả về HandValue. */
    private static HandValue evaluate5(List<Card> cards) {
        // copy về ArrayList để sort được
        List<Card> sorted = new ArrayList<>(cards);
        sorted.sort(Comparator.comparingInt(c -> c.getRank().getIndexValue()));

        boolean flush = isFlush(sorted);
        boolean straight = isStraight(sorted);
        Map<Rank, Integer> counts = getRankCounts(sorted);
        List<Map.Entry<Rank, Integer>> byCount = new ArrayList<>(counts.entrySet());
        byCount.sort((e1, e2) -> {
            int c = Integer.compare(e2.getValue(), e1.getValue());
            if (c != 0) return c;
            // tie-breaker: rank cao hơn trước
            return Integer.compare(e2.getKey().getIndexValue(), e1.getKey().getIndexValue());
        });

        // chuẩn bị kicker list
        List<Integer> kickers = new ArrayList<>();

        // Royal / Straight Flush
        if (flush && straight && sorted.get(4).getRank() == Rank.A) {
            return new HandValue(9, List.of()); // Royal Flush
        }
        if (flush && straight) {
            kickers.add(sorted.get(4).getRank().getIndexValue());
            return new HandValue(8, kickers); // Straight Flush
        }

        // Four of a Kind
        if (byCount.get(0).getValue() == 4) {
            kickers.add(byCount.get(0).getKey().getIndexValue());
            // kicker còn lại:
            for (Card c : sorted)
                if (c.getRank() != byCount.get(0).getKey())
                    kickers.add(c.getRank().getIndexValue());
            return new HandValue(7, kickers);
        }

        // Full House
        if (byCount.get(0).getValue() == 3 && byCount.get(1).getValue() >= 2) {
            kickers.add(byCount.get(0).getKey().getIndexValue());
            kickers.add(byCount.get(1).getKey().getIndexValue());
            return new HandValue(6, kickers);
        }

        // Flush
        if (flush) {
            // kicker: top 5 ranks giảm dần
            for (int i = 4; i >= 0; i--)
                kickers.add(sorted.get(i).getRank().getIndexValue());
            return new HandValue(5, kickers);
        }

        // Straight
        if (straight) {
            kickers.add(sorted.get(4).getRank().getIndexValue());
            return new HandValue(4, kickers);
        }

        // Three of a Kind
        if (byCount.get(0).getValue() == 3) {
            kickers.add(byCount.get(0).getKey().getIndexValue());
            // add two highest kickers
            for (Card c : sorted)
                if (c.getRank() != byCount.get(0).getKey())
                    kickers.add(c.getRank().getIndexValue());
            Collections.reverse(kickers);
            return new HandValue(3, kickers);
        }

        // Two Pair
        if (byCount.get(0).getValue() == 2 && byCount.get(1).getValue() == 2) {
            kickers.add(byCount.get(0).getKey().getIndexValue());
            kickers.add(byCount.get(1).getKey().getIndexValue());
            // kicker còn lại:
            for (Card c : sorted)
                if (c.getRank() != byCount.get(0).getKey() && c.getRank() != byCount.get(1).getKey())
                    kickers.add(c.getRank().getIndexValue());
            return new HandValue(2, kickers);
        }

        // One Pair
        if (byCount.get(0).getValue() == 2) {
            kickers.add(byCount.get(0).getKey().getIndexValue());
            for (int i = 4; i >= 0; i--) {
                Rank r = sorted.get(i).getRank();
                if (r != byCount.get(0).getKey())
                    kickers.add(r.getIndexValue());
            }
            return new HandValue(1, kickers);
        }

        // High Card
        for (int i = 4; i >= 0; i--)
            kickers.add(sorted.get(i).getRank().getIndexValue());
        return new HandValue(0, kickers);
    }

    /** Kiểm tra flush (5 lá cùng chất) */
    private static boolean isFlush(List<Card> cards) {
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).getSuit() != cards.get(0).getSuit())
                return false;
        }
        return true;
    }

    /** Kiểm tra straight (5 lá liên tiếp) */
    /** Kiểm tra straight (5 lá liên tiếp) */
    private static boolean isStraight(List<Card> cards) {
        // chú ý A-2-3-4-5 (low ace)
        boolean lowAce = cards.get(4).getRank() == Rank.A &&
                         cards.get(0).getRank() == Rank.TWO &&
                         cards.get(1).getRank() == Rank.THREE &&
                         cards.get(2).getRank() == Rank.FOUR &&
                         cards.get(3).getRank() == Rank.FIVE;
        if (lowAce) return true;

        for (int i = 1; i < cards.size(); i++) {
            int prev = cards.get(i - 1).getRank().getIndexValue();
            int cur = cards.get(i).getRank().getIndexValue();
            if (cur != prev + 1) return false;
        }
        return true;
    }
    /** Đếm số lá mỗi rank trong hand 5 lá */
    private static Map<Rank, Integer> getRankCounts(List<Card> cards) {
        Map<Rank, Integer> cnt = new EnumMap<>(Rank.class);
        for (Card c : cards) {
            cnt.put(c.getRank(), cnt.getOrDefault(c.getRank(), 0) + 1);
        }
        return cnt;
    }
}
