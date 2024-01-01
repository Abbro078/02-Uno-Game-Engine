import java.util.*;

/*
    in this game mode i added that the number 7 when played you can change cards with another player
    it is one of the many uno variations out ther.
 */

class Mode2 extends Game{

    public Mode2(String[] playerIds) {
        super(playerIds);
    }

    protected void submitPlayerCard(String playerId, int cardIndex, UnoCard.Color declaredColor) {
        List<UnoCard> hand = playerHands.get(playerId);
        UnoCard card = hand.get(cardIndex);
        UnoCard.Color validColor = getTopCard().getColor();
        UnoCard.Value validValue = getTopCard().getValue();

        boolean isValidCard = isValidMove(card,validColor,validValue);

        if (!isValidCard) {
            System.out.println("Invalid card. Try again.");
            return;
        }

        if (card.getValue() == UnoCard.Value.WILD || card.getValue() == UnoCard.Value.WILD_FOUR) {
            if (declaredColor == null) {
                System.out.println("Invalid declared color for Wild card. Try again.");
                return;
            }
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
        } else if (card.getValue() == UnoCard.Value.SEVEN) {
            System.out.println("Current Hand Sizes:");
            for (int i = 0; i < playerIds.length; i++) {
                String playerName = playerIds[i];
                List<UnoCard> playerHand = getPlayerHand(playerName);
                System.out.println((i + 1) + ". " + playerName + ": " + playerHand.size() + " cards");
            }
            boolean validPlayerNumber = false;
            while (!validPlayerNumber) {
                System.out.print("Enter the player number to switch cards with (1-" + playerIds.length + "): ");
                int targetPlayerNumber = scanner.nextInt();
                scanner.nextLine();
                if (targetPlayerNumber < 1 || targetPlayerNumber > playerIds.length) {
                    System.out.println("Invalid player number. Try again.");
                } else if (targetPlayerNumber == currentPlayer+1) {
                    System.out.println("You cannot switch cards with yourself. Try again.");
                } else {

                    String targetPlayer = playerIds[targetPlayerNumber - 1];
                    List<UnoCard> targetHand = getPlayerHand(targetPlayer);
                    List<UnoCard> temp = new ArrayList<>(hand);
                    hand.clear();
                    hand.addAll(targetHand);
                    targetHand.clear();
                    targetHand.addAll(temp);

                    System.out.println(playerId + " switched hands with " + targetPlayer);
                    validPlayerNumber=true;
                }
            }
        }

        if (!gameOver) {
            currentPlayer = getNextPlayerIndex();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] playerIds = getPlayerIds(scanner, getNumberOfPlayers(scanner));
        Mode2 game = new Mode2(playerIds);
        game.start();
        game.playGame(game, scanner);
    }
}
