import java.util.*;

abstract class Game {
    protected final String[] playerIds;
    protected final UnoDeck deck;
    protected final List<UnoCard> pile;
    protected final Map<String, List<UnoCard>> playerHands;
    protected int currentPlayer;
    protected boolean gameDirection;
    protected boolean gameOver;
    protected final Scanner scanner = new Scanner(System.in);

    public Game(String[] playerIds) {
        this.playerIds = playerIds;
        this.deck = new UnoDeck();
        this.pile = new ArrayList<>();
        this.playerHands = new HashMap<>();
        this.currentPlayer = 0;
        this.gameDirection = true;
        this.gameOver = false;
    }

    protected boolean isGameOver() {
        return gameOver;
    }

    protected String getCurrentPlayer() {
        return playerIds[currentPlayer];
    }

    protected List<UnoCard> getPlayerHand(String playerId) {
        return playerHands.get(playerId);
    }

    protected UnoCard getTopCard() {
        if (!pile.isEmpty()) {
            return pile.get(pile.size() - 1);
        }
        return null;
    }

    protected String getNextPlayer() {
        return playerIds[getNextPlayerIndex()];
    }

    protected int getNextPlayerIndex() {
        int increment = gameDirection ? 1 : -1;
        return (currentPlayer + increment + playerIds.length) % playerIds.length;
    }

    protected boolean isValidMove(UnoCard card, UnoCard.Color validColor, UnoCard.Value validValue) {
        return (card.getColor() == validColor || card.getValue() == validValue || card.getColor() == UnoCard.Color.WILD || card.getValue() == UnoCard.Value.WILD || card.getValue() == UnoCard.Value.WILD_FOUR);
    }

    protected void distributeCards() {
        for (String playerId : playerIds) {
            List<UnoCard> hand = new ArrayList<>(Arrays.asList(deck.drawCards(7)));
            playerHands.put(playerId, hand);
        }
    }

    protected void startCard(UnoCard topCard) {
        if (topCard.getValue() == UnoCard.Value.DRAW_TWO) {
            getPlayerHand(playerIds[currentPlayer]).addAll(Arrays.asList(deck.drawCards(2)));
            System.out.println(getCurrentPlayer() + " drew 2 cards");
            currentPlayer = getNextPlayerIndex();
        } else if (topCard.getValue() == UnoCard.Value.WILD_FOUR) {
            getPlayerHand(playerIds[currentPlayer]).addAll(Arrays.asList(deck.drawCards(4)));
            System.out.println(getCurrentPlayer() + " drew 4 cards");
            currentPlayer = getNextPlayerIndex();
        } else if (topCard.getValue() == UnoCard.Value.REVERSE) {
            gameDirection = !gameDirection;
            System.out.println("Game direction reversed");
        } else if (topCard.getValue() == UnoCard.Value.SKIP) {
            System.out.println(currentPlayer + " was skipped");
            currentPlayer = getNextPlayerIndex();
        }
    }

    protected void submitDraw(String playerId) {
        List<UnoCard> hand = playerHands.get(playerId);
        UnoCard card = deck.drawCard();
        if (card != null) {
            hand.add(card);
        } else {
            UnoCard topCard = pile.remove(pile.size() - 1);
            deck.replaceDeckWith(new ArrayList<>(pile));
            deck.shuffle();
            pile.clear();
            pile.add(topCard);
            card = deck.drawCard();
            hand.add(card);
        }
    }

    static String[] getPlayerIds(Scanner scanner, int numPlayers) {
        String[] playerIds = new String[numPlayers];
        for (int i = 0; i < playerIds.length; i++) {
            System.out.print("Enter the name of Player " + (i + 1) + ": ");
            playerIds[i] = scanner.nextLine();
        }
        return playerIds;
    }

    protected static int getNumberOfPlayers(Scanner scanner) {
        int numPlayers = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("Enter the number of players (2-4): ");
            String input = scanner.nextLine();
            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers >= 2 && numPlayers <= 4) {
                    validInput = true;
                } else {
                    System.out.println("Invalid number of players. Please enter a number between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return numPlayers;
    }

    public void start() {
        deck.shuffle();
        distributeCards();
        UnoCard topCard = deck.drawCard();
        pile.add(topCard);
        if (topCard.getValue() == UnoCard.Value.WILD || topCard.getValue() == UnoCard.Value.WILD_FOUR) {
            UnoCard.Color declaredColor = getValidColorInput(scanner);
            topCard = new UnoCard(declaredColor, topCard.getValue());
        }
        pile.add(topCard);
        startCard(topCard);
    }


    protected void playGame(Game game, Scanner scanner) {
        while (!game.isGameOver()) {
            String currentPlayer = game.getCurrentPlayer();
            List<UnoCard> hand = game.getPlayerHand(currentPlayer);
            UnoCard topCard = game.getTopCard();
            System.out.println("\n=== " + currentPlayer + "'s Turn ===");
            System.out.println("Top Card: " + topCard);
            System.out.println("Your Hand:");
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + "- " + hand.get(i));
            }
            System.out.println("Enter the card number to play (0 to draw):");
            int cardNumber = scanner.nextInt();
            scanner.nextLine();
            if (cardNumber == 0) {
                game.submitDraw(currentPlayer);
            } else if (cardNumber > 0 && cardNumber <= hand.size()) {
                UnoCard.Color declaredColor = null;
                UnoCard card = hand.get(cardNumber - 1);
                if (card.getColor() == UnoCard.Color.WILD || card.getColor() == UnoCard.Color.WILD_FOUR) {
                    declaredColor = game.getValidColorInput(scanner);
                }
                game.submitPlayerCard(currentPlayer, cardNumber - 1, declaredColor);
            } else {
                System.out.println("Invalid card number. Try again.");
            }
        }
        System.out.println("\n=== Game Over ===");
        System.out.println("Winner: " + game.getCurrentPlayer());
    }

    protected UnoCard.Color getValidColorInput(Scanner scanner) {
        UnoCard.Color declaredColor = null;
        boolean validColorInput = false;

        while (!validColorInput) {
            System.out.println("Choose a color:");
            System.out.println("1- RED");
            System.out.println("2- GREEN");
            System.out.println("3- BLUE");
            System.out.println("4- YELLOW");
            System.out.println("Enter the number corresponding to the color: ");
            String colorInput = scanner.nextLine();
            switch (colorInput) {
                case "1":
                    declaredColor = UnoCard.Color.RED;
                    validColorInput = true;
                    break;
                case "2":
                    declaredColor = UnoCard.Color.GREEN;
                    validColorInput = true;
                    break;
                case "3":
                    declaredColor = UnoCard.Color.BLUE;
                    validColorInput = true;
                    break;
                case "4":
                    declaredColor = UnoCard.Color.YELLOW;
                    validColorInput = true;
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number from 1 to 4.");
                    break;
            }
        }
        return declaredColor;
    }

    protected abstract void submitPlayerCard(String playerId, int cardIndex, UnoCard.Color declaredColor);
}