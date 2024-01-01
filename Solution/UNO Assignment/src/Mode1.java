import java.util.*;

/*
    in this game mode its basically the normal uno game from 2 to 4 people the only difference
    is that you keep drawing cards as much you want its just the variation that i grew up with
 */

class Mode1 extends Game {
    public Mode1(String[] playerIds) {
        super(playerIds);
    }

    protected void submitPlayerCard(String playerId, int cardIndex, UnoCard.Color declaredColor) {
        List<UnoCard> hand = playerHands.get(playerId);
        UnoCard card = hand.get(cardIndex);
        UnoCard.Color validColor = getTopCard().getColor();
        UnoCard.Value validValue = getTopCard().getValue();

        boolean isValidCard = isValidMove(card, validColor, validValue);

        if (!isValidCard) {
            System.out.println("Invalid card. Try again.");
            return;
        }

        if (card.getValue() == UnoCard.Value.WILD || card.getValue() == UnoCard.Value.WILD_FOUR) {
            hand.remove(card);
            card = new UnoCard(declaredColor, card.getValue());
        }

        pile.add(card);
        hand.remove(card);

        if (hand.isEmpty()) {
            gameOver = true;
        }
        if (card.getValue() == UnoCard.Value.DRAW_TWO) {
            String nextPlayer = getNextPlayer();
            getPlayerHand(nextPlayer).addAll(Arrays.asList(deck.drawCards(2)));
            System.out.println(nextPlayer + " drew 2 cards");
            currentPlayer = getNextPlayerIndex();
        } else if (card.getValue() == UnoCard.Value.WILD_FOUR) {
            String nextPlayer = getNextPlayer();
            getPlayerHand(nextPlayer).addAll(Arrays.asList(deck.drawCards(4)));
            System.out.println(nextPlayer + " drew 4 cards");
            currentPlayer = getNextPlayerIndex();
        } else if (card.getValue() == UnoCard.Value.REVERSE) {
            if (playerIds.length == 2) {
                currentPlayer = getNextPlayerIndex();
            } else {
                gameDirection = !gameDirection;
            }
            System.out.println("Game direction reversed");
        } else if (card.getValue() == UnoCard.Value.SKIP) {
            String skippedPlayer = getNextPlayer();
            System.out.println(skippedPlayer + " was skipped");
            currentPlayer = getNextPlayerIndex();
        }

        if (!gameOver) {
            currentPlayer = getNextPlayerIndex();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] playerIds = getPlayerIds(scanner, getNumberOfPlayers(scanner));
        Mode1 game = new Mode1(playerIds);
        game.start();
        game.playGame(game, scanner);
    }
}
