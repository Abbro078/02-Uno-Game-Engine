import java.util.*;
class UnoDeck {
    private List<UnoCard> cards;

    public UnoDeck() {
        cards = new ArrayList<>();

        for (UnoCard.Color color : UnoCard.Color.values()) {
            if (color == UnoCard.Color.WILD || color == UnoCard.Color.WILD_FOUR) {
                continue;
            }
            for (UnoCard.Value value : UnoCard.Value.values()) {
                if (value == UnoCard.Value.WILD || value == UnoCard.Value.WILD_FOUR) {
                    continue;
                }
                if (value == UnoCard.Value.ZERO) {
                    cards.add(new UnoCard(color, value));
                    continue;
                }
                cards.add(new UnoCard(color, value));
                cards.add(new UnoCard(color, value));
            }
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new UnoCard(UnoCard.Color.WILD, UnoCard.Value.WILD));
            cards.add(new UnoCard(UnoCard.Color.WILD_FOUR, UnoCard.Value.WILD_FOUR));
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public UnoCard drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        }
        return null;
    }

    public UnoCard[] drawCards(int numCards) {
        UnoCard[] drawnCards = new UnoCard[numCards];
        for (int i = 0; i < numCards; i++) {
            drawnCards[i] = drawCard();
        }
        return drawnCards;
    }

    public void replaceDeckWith(List<UnoCard> newCards) {
        cards = newCards;
    }
}