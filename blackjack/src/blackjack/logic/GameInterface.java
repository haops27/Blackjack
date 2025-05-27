package blackjack.logic;

import blackjack.actor.Player;

import java.util.List;

public interface GameInterface {
    void initializePlayers(List<String> playerNames);
    void resetRound();
    void dealInitialCards();
    void playerAction(int playerIndex, String action, float amount);
    boolean nextPlayer();
    boolean isRoundOver();
    List<Player> getPlayers();
}
