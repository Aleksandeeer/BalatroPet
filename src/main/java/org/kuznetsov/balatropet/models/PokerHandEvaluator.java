package org.kuznetsov.balatropet.models;

import lombok.Getter;
import lombok.Setter;
import org.kuznetsov.balatropet.GameState;

import java.util.*;

public class PokerHandEvaluator {
    @Getter
    @Setter
    private static Played_hand playedHand;
    @Getter
    private static List<Card> scoringCards; // ? playedHand contains scoringCards, but not the other way around
    @Getter
    @Setter
    private static int score;

    public static void evaluateHand(List<Card> playedHandCards) {
        playedHandCards.sort(new CardNominalComparator());

        if (isStraightFlush(playedHandCards)) {
            if (isFromTenToAce(playedHandCards)) {
                playedHand = Played_hand.ROYAL_FLUSH;
            } else {
                playedHand = Played_hand.STRAIGHT_FLUSH;
            }
            scoringCards = new ArrayList<>(playedHandCards);
        } else if (isNOfAKind(playedHandCards, 4)) {
            playedHand = Played_hand.FOUR_OF_A_KIND;
            scoringCards = getCardsOfKind(playedHandCards, 4);
        } else if (isFullHouse(playedHandCards)) {
            playedHand = Played_hand.FULL_HOUSE;
            scoringCards = getFullHouseCards(playedHandCards);
        } else if (isFlush(playedHandCards)) {
            playedHand = Played_hand.FLUSH;
            scoringCards = new ArrayList<>(playedHandCards);
        } else if (isStraight(playedHandCards)) {
            playedHand = Played_hand.STRAIGHT;
            scoringCards = new ArrayList<>(playedHandCards);
        } else if (isNOfAKind(playedHandCards, 3)) {
            playedHand = Played_hand.THREE_OF_A_KIND;
            scoringCards = getCardsOfKind(playedHandCards, 3);
        } else if (isTwoPair(playedHandCards)) {
            playedHand = Played_hand.TWO_PAIR;
            scoringCards = getTwoPairCards(playedHandCards);
        } else if (isNOfAKind(playedHandCards, 2)) {
            playedHand = Played_hand.PAIR;
            scoringCards = getCardsOfKind(playedHandCards, 2);
        } else {
            playedHand = Played_hand.HIGH_CARD;
            scoringCards = List.of(playedHandCards.getLast());
        }

        scoreCards(scoringCards);
    }

    public static boolean isStraightFlush(List<Card> playedHand) {
        return isFlush(playedHand) && isStraight(playedHand);
    }

    public static boolean isFlush(List<Card> playedHand) {
        if (playedHand.size() != 5) return false;
        Suit suit = playedHand.getFirst().getSuit();
        for (int i = 1; i < playedHand.size(); i++)
            if (playedHand.get(i).getSuit() != suit)
                return false;
        return true;
    }

    public static boolean isStraight(List<Card> playedHand) {
        if (playedHand.size() != 5) return false;
        for (int i = 0; i < playedHand.size() - 1; i++) {
            if (Math.abs(playedHand.get(i).getNominal() - playedHand.get(i + 1).getNominal()) != 1)
                return false;
        }
        return true;
    }

    public static boolean isFromTenToAce(List<Card> playedHand) {
        if (playedHand.size() != 5) return false;
        for (int i = 0; i < 5; i++) {
            if (playedHand.get(i).getNominal() != 10 + i)
                return false;
        }
        return true;
    }

    public static boolean isFullHouse(List<Card> playedHand) {
        if (playedHand.size() != 5) return false;
        return (playedHand.get(0).getNominal() == playedHand.get(2).getNominal() &&
                playedHand.get(3).getNominal() == playedHand.get(4).getNominal()) ||
                (playedHand.get(0).getNominal() == playedHand.get(1).getNominal() &&
                        playedHand.get(2).getNominal() == playedHand.get(4).getNominal());
    }

    public static boolean isNOfAKind(List<Card> playedHand, int n) {
        if (playedHand.size() < n) return false;
        Map<Integer, Integer> cards = new HashMap<>();
        for (Card card : playedHand) {
            cards.merge(card.getNominal(), 1, Integer::sum);
        }
        return cards.containsValue(n);
    }

    public static boolean isTwoPair(List<Card> playedHand) {
        if (playedHand.size() < 4) return false;
        int pairCount = 0;
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < playedHand.size() - 1; i++) {
            if (playedHand.get(i).getNominal() == playedHand.get(i + 1).getNominal()
                    && !seen.contains(playedHand.get(i).getNominal())) {
                pairCount++;
                seen.add(playedHand.get(i).getNominal());
                i++; // skip next
            }
        }
        return pairCount == 2;
    }

    private static List<Card> getCardsOfKind(List<Card> cards, int n) {
        Map<Integer, List<Card>> grouped = new HashMap<>();
        for (Card c : cards) {
            grouped.computeIfAbsent(c.getNominal(), k -> new ArrayList<>()).add(c);
        }
        for (List<Card> group : grouped.values()) {
            if (group.size() == n)
                return group;
        }
        return List.of();
    }

    private static List<Card> getTwoPairCards(List<Card> cards) {
        List<Card> result = new ArrayList<>();
        Map<Integer, List<Card>> grouped = new HashMap<>();
        for (Card c : cards) {
            grouped.computeIfAbsent(c.getNominal(), k -> new ArrayList<>()).add(c);
        }
        int found = 0;
        for (List<Card> group : grouped.values()) {
            if (group.size() >= 2 && found < 2) {
                result.add(group.get(0));
                result.add(group.get(1));
                found++;
            }
            if (found == 2) break;
        }
        return result;
    }

    private static List<Card> getFullHouseCards(List<Card> cards) {
        Map<Integer, List<Card>> grouped = new HashMap<>();
        for (Card c : cards) {
            grouped.computeIfAbsent(c.getNominal(), k -> new ArrayList<>()).add(c);
        }
        List<Card> result = new ArrayList<>();
        List<Card> three = null;
        List<Card> two = null;
        for (List<Card> group : grouped.values()) {
            if (group.size() == 3 && three == null) three = group;
            else if (group.size() >= 2 && two == null) two = group;
        }
        if (three != null) result.addAll(three);
        if (two != null) result.addAll(two.subList(0, 2));
        return result;
    }

    // * SCORING
    private static void scoreCards(List<Card> cards) {
        int add_chips = 0;
        for (Card card : cards) {
            add_chips += card.getNominal();
        }

        int levelOfCombination = switch (playedHand) {
            case HIGH_CARD -> GameState.HIGH_CARD_LVL;
            case PAIR -> GameState.PAIR_LVL;
            case TWO_PAIR -> GameState.TWO_PAIR_LVL;
            case THREE_OF_A_KIND -> GameState.THREE_OF_A_KIND_LVL;
            case STRAIGHT -> GameState.STRAIGHT_LVL;
            case FLUSH -> GameState.FLUSH_LVL;
            case FULL_HOUSE -> GameState.FULL_HOUSE_LVL;
            case FOUR_OF_A_KIND -> GameState.FOUR_OF_A_KIND_LVL;
            case STRAIGHT_FLUSH -> GameState.STRAIGHT_FLUSH_LVL;
            case ROYAL_FLUSH -> GameState.FLUSH_ROYAL_LVL;
        };

        score = (GameState.chips[playedHand.ordinal()] * levelOfCombination + add_chips) *
                (GameState.multiply[playedHand.ordinal()] * levelOfCombination);
    }

    private static class CardNominalComparator implements Comparator<Card> {
        @Override
        public int compare(Card a, Card b) {
            return Integer.compare(a.getNominal(), b.getNominal());
        }
    }
}
