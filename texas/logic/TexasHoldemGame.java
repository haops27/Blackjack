package texas.logic;

import players.Player;
import cards.Card;
import cards.Deck;

import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldemGame {
    private final Deck deck = new Deck(1);
    private final List<Player> players = new ArrayList<>();
    private final List<Card> community = new ArrayList<>();

    // Betting state
    private float pot = 0;
    private float currentBet = 0;
    private final Map<Player, Float> contribution = new HashMap<>();
    private final Set<Player> folded = new HashSet<>();
    private int stage = 0; // 0=pre-flop,1=flop,2=turn,3=river,4=showdown
    private int currentPlayerIdx = 0;
    private int playersActedInRound = 0;

    private int dealerIndex = 0; // Vị trí dealer
    private static final float SMALL_BLIND = 1.0f;
    private static final float BIG_BLIND = 2.0f;

    private static final float EPSILON = 0.01f; // sai số so sánh float

    public void initializePlayers(List<String> names) {
        players.clear();
        for (String n : names) {
            players.add(new Player(n));
        }
    }
    private void ensureEnoughCards(int requiredCards) {
        if (deck.size() < requiredCards) {
            boolean reshuffled = deck.reshuffle();
            if (!reshuffled || deck.size() < requiredCards) {
                throw new IllegalStateException("Không đủ bài để chia, kể cả khi xào lại.");
            }
        }
    }

    /**
     * Lấy bài an toàn từ bộ bài, tự động xào lại nếu cần.
     */
    private Card safeGetCard() {
        if (deck.size() == 0) {
            boolean reshuffled = deck.reshuffle();
            if (!reshuffled) {
                throw new IllegalStateException("Deck is empty after reshuffle!");
            }
        }
        return deck.getCard();
    }

    public void resetRound() {
        deck.shuffleDeck();
        community.clear();
        pot = 0;
        currentBet = 0;
        contribution.clear();
        folded.clear();

        for (Player p : players) {
            p.reset(deck);
            contribution.put(p, 0f);
        }
        stage = 0;

        // Đặt cược blind tự động
        int smallBlindIndex = (dealerIndex + 1) % players.size();
        int bigBlindIndex = (dealerIndex + 2) % players.size();

        Player smallBlindPlayer = players.get(smallBlindIndex);
        Player bigBlindPlayer = players.get(bigBlindIndex);

        // Small blind
        if (smallBlindPlayer.getTokens() >= SMALL_BLIND) {
            smallBlindPlayer.setTokens(-SMALL_BLIND);
            contribution.put(smallBlindPlayer, SMALL_BLIND);
            pot += SMALL_BLIND;
        } else {
            folded.add(smallBlindPlayer);
            contribution.put(smallBlindPlayer, 0f);
            System.out.println(smallBlindPlayer.getName() + " folds (insufficient tokens for small blind)");
        }

        // Big blind
        if (bigBlindPlayer.getTokens() >= BIG_BLIND) {
            bigBlindPlayer.setTokens(-BIG_BLIND);
            contribution.put(bigBlindPlayer, BIG_BLIND);
            pot += BIG_BLIND;
            currentBet = BIG_BLIND;
        } else {
            folded.add(bigBlindPlayer);
            contribution.put(bigBlindPlayer, 0f);
            System.out.println(bigBlindPlayer.getName() + " folds (insufficient tokens for big blind)");
            currentBet = 0;
        }

        // Bắt đầu vòng cược từ người chơi tiếp theo big blind
        currentPlayerIdx = (bigBlindIndex + 1) % players.size();
        playersActedInRound = 0;

        // Di chuyển dealer cho vòng sau
        dealerIndex = (dealerIndex + 1) % players.size();
    }

    public void dealPreFlop() {
        resetRound();
        for (Player p : players) {
            if (!folded.contains(p)) {
                p.addCard(safeGetCard());
                p.addCard(safeGetCard());
            }
        }
    }

    public void dealFlop() {
        ensureEnoughCards(4);
        burn();
        community.add(safeGetCard());
        community.add(safeGetCard());
        community.add(safeGetCard());
        nextStage();
    }

    public void dealTurn() {
        ensureEnoughCards(2);
        burn();
        community.add(safeGetCard());
        nextStage();
    }

    public void dealRiver() {
        ensureEnoughCards(2);
        burn();
        community.add(safeGetCard());
        nextStage();
    }

    private void burn() {
        // Bước burn luôn cần 1 lá bài
        ensureEnoughCards(1);
        safeGetCard();
    }

    private void nextStage() {
        if (stage < 4) {
            stage++;
            currentBet = 0;
            for (Player p : players) contribution.put(p, 0f);
            currentPlayerIdx = 0;
            playersActedInRound = 0;
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return Collections.unmodifiableList(community);
    }

    public int getStage() {
        return stage;
    }

    public float getPot() {
        return pot;
    }

    public float getCurrentBet() {
        return currentBet;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIdx;
    }

    public void playerAction(String action, float amount) {
        if (stage >= 4) return;

        Player p = getCurrentPlayer();
        if (folded.contains(p)) {
            advancePlayer();
            return;
        }

        switch (action.toLowerCase()) {
            case "fold":
                folded.add(p);
                System.out.println(p.getName() + " folds");
                break;

            case "check":
                if (currentBet > 0 && contribution.get(p) < currentBet - EPSILON) {
                    System.out.println(p.getName() + " cannot check, must call or fold");
                    return;
                }
                System.out.println(p.getName() + " checks");
                break;

            case "call":
                float needed = currentBet - contribution.get(p);
                if (needed < 0) needed = 0;
                if (p.getTokens() >= needed) {
                    p.setTokens(-needed);
                    contribution.put(p, contribution.get(p) + needed);
                    pot += needed;
                    System.out.println(p.getName() + " calls " + needed);
                } else {
                    folded.add(p);
                    System.out.println(p.getName() + " folds (insufficient tokens to call)");
                }
                break;

            case "raise":
                needed = currentBet - contribution.get(p);
                if (needed < 0) needed = 0;
                float totalPut = needed + amount;
                if (p.getTokens() >= totalPut) {
                    p.setTokens(-totalPut);
                    contribution.put(p, contribution.get(p) + totalPut);
                    pot += totalPut;
                    currentBet = contribution.get(p);
                    System.out.println(p.getName() + " raises to " + currentBet);
                    playersActedInRound = 1;
                    advancePlayer();
                    return;
                } else {
                    folded.add(p);
                    System.out.println(p.getName() + " folds (insufficient tokens to raise)");
                }
                break;

            default:
                System.out.println("Invalid action");
                return;
        }

        playersActedInRound++;

        if (playersActedInRound >= players.size() - folded.size()) {
            if (allHaveCalled() || folded.size() == players.size() - 1) {
                switch (stage) {
                    case 0 -> dealFlop();
                    case 1 -> dealTurn();
                    case 2 -> dealRiver();
                    case 3 -> showdown();
                }
                playersActedInRound = 0;
            }
        }
        advancePlayer();
    }

    private void advancePlayer() {
        int cnt = 0;
        do {
            currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
            cnt++;
            if (cnt >= players.size()) break;
        } while (folded.contains(getCurrentPlayer()) && cnt < players.size());
    }

    private boolean allHaveCalled() {
        for (Player p : players) {
            if (folded.contains(p)) continue;
            if (!contribution.containsKey(p) || Math.abs(contribution.get(p) - currentBet) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    public void showdown() {
        nextStage();

        List<Player> winners = evaluateShowdown();
        if (!winners.isEmpty()) {
            float potShare = pot / winners.size();

            for (Player winner : winners) {
                winner.setTokens(potShare);
                System.out.println(winner.getName() + " nhận được " + potShare + " tokens");
            }
        } else {
            System.out.println("Không có người thắng (tất cả đều fold)");
        }
        pot = 0;
    }

    public List<Player> evaluateShowdown() {
        PokerHandEvaluator.HandValue best = null;
        List<Player> winners = new ArrayList<>();

        for (Player p : players) {
            if (folded.contains(p)) continue;
            PokerHandEvaluator.HandValue hv = PokerHandEvaluator.evaluateBest(
                p.getHoleCards(), community);
            if (best == null || hv.compareTo(best) > 0) {
                best = hv;
                winners.clear();
                winners.add(p);
            } else if (hv.compareTo(best) == 0) {
                winners.add(p);
            }
        }
        return winners;
    }
}
