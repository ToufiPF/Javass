package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.test.TestRandomizer;

class TurnStateTest {
    private static long randomPkScore(SplittableRandom rng) {
        int t1 = rng.nextInt(10);
        int p1 = rng.nextInt(158);
        int g1 = rng.nextInt(2000);
        int t2 = rng.nextInt(10 - t1);
        int p2 = rng.nextInt(158 - p1);
        int g2 = rng.nextInt(2000 - g1);
        return PackedScore.pack(t1, p1, g1, t2, p2, g2);
    }

    private static int randomNonFullPkTrick(SplittableRandom rng) {
        long cardSet = PackedCardSet.ALL_CARDS;
        int card1 = PackedCardSet.get(cardSet, rng.nextInt(PackedCardSet.size(cardSet)));
        cardSet = PackedCardSet.remove(cardSet, card1);
        int card2 = PackedCardSet.get(cardSet, rng.nextInt(PackedCardSet.size(cardSet)));
        cardSet = PackedCardSet.remove(cardSet, card2);
        int card3 = PackedCardSet.get(cardSet, rng.nextInt(PackedCardSet.size(cardSet)));
        cardSet = PackedCardSet.remove(cardSet, card3);
        
        int index = rng.nextInt(9);
        int joueur1 = rng.nextInt(4);
        int trump = rng.nextInt(4);
        return Bits32.pack(card1, 6, card2, 6, card3, 6, PackedCard.INVALID, 6, index, 4, joueur1, 2, trump, 2);
    }

    SplittableRandom rng = newRandom();
    TurnState turnStateTest = TurnState.initial(Color.ALL.get(rng.nextInt(Color.ALL.size())), 
            Score.ofPacked(randomPkScore(rng)), PlayerId.ALL.get(rng.nextInt(PlayerId.ALL.size())));

    @Test
    void initialWorksWithSomeRandomScore() {
        //Test Initial et les getters
        
        SplittableRandom rng = newRandom();

        for(int i = 0; i < Color.COUNT; ++i) {
            for(int j = 0; j < PlayerId.COUNT; ++j) {
                for(int k = 0; k < TestRandomizer.RANDOM_ITERATIONS; ++ k) {
                    long score = randomPkScore(rng);
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).packedScore(), 
                            score);
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).packedTrick(),
                            Trick.firstEmpty(Color.ALL.get(i), PlayerId.ALL.get(j)).packed());
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).packedUnplayedCards(),
                            PackedCardSet.ALL_CARDS);
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).unplayedCards(),
                            CardSet.ofPacked(PackedCardSet.ALL_CARDS));
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).score(),
                            Score.ofPacked(score));
                    assertEquals(TurnState.initial(Color.ALL.get(i), Score.ofPacked(score), PlayerId.ALL.get(j)).trick(),
                            Trick.firstEmpty(Color.ALL.get(i), PlayerId.ALL.get(j)));
                }
            }
        }
    }
    
    @Test
    void ofPackedComponentsThrowsWhenComponentsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            TurnState.ofPackedComponents(-1L, PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            TurnState.ofPackedComponents(PackedScore.INITIAL, -1L, PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            TurnState.ofPackedComponents(PackedScore.INITIAL, PackedCardSet.ALL_CARDS, PackedTrick.INVALID);
        });
    }
    
    @Test
    void withCardPlayedThrowsWhenTrickFull() {
        TurnState st = TurnState.ofPackedComponents(PackedScore.INITIAL, PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1));
        st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.JACK));
        st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.NINE));
        st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.KING));
        st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.QUEEN));
        
        final TurnState st2 = st;
        assertThrows(IllegalStateException.class, () -> {
            st2.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.EIGHT));
        });
    }
    
    @Test
    void withTrickCollectedThrowsWhenTrickNotFull() {

        assertThrows(IllegalStateException.class, () -> {
            TurnState st = TurnState.ofPackedComponents(PackedScore.INITIAL, PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1));
            st = st.withTrickCollected();
        });
        
        assertThrows(IllegalStateException.class, () -> {
            TurnState st = TurnState.ofPackedComponents(PackedScore.INITIAL, PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1));
            st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.JACK));
            st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.NINE));
            st = st.withNewCardPlayed(Card.of(Card.Color.CLUB, Card.Rank.KING));
            st = st.withTrickCollected();
        });
    }
    
    @Test
    void normalTurnDoesNotThrowException() {
        SplittableRandom rng = new SplittableRandom();
        for (int i = 0 ; i < TestRandomizer.RANDOM_ITERATIONS ; ++i) {
            int trick = randomNonFullPkTrick(rng);
            
            long trickCards = PackedCardSet.EMPTY;
            for (int j = 0 ; j < PackedTrick.size(trick) ; ++j)
                trickCards = PackedCardSet.add(trickCards, PackedTrick.card(trick, j));
            
            TurnState st = TurnState.ofPackedComponents(PackedScore.INITIAL, PackedCardSet.difference(PackedCardSet.ALL_CARDS,  trickCards), trick);
            while (!st.isTerminal())
                st = st.withNewCardPlayedAndTrickCollected(st.unplayedCards().get(rng.nextInt(st.unplayedCards().size())));
        }
    }
    } 

    

    @Test 
    void nextPlayerThrowsExceptionWhenFull() {
        for(int i = 0; i < 4; ++ i) {
            System.out.println(PackedTrick.player(turnStateTest.packedTrick(), PackedTrick.size(turnStateTest.packedTrick())));

            turnStateTest = turnStateTest.withNewCardPlayed(Card.of(Color.ALL.get(rng.nextInt(Color.ALL.size())), Rank.ALL.get(rng.nextInt(Rank.ALL.size()))));
        }
        assertThrows(IllegalStateException.class, () -> {
           turnStateTest.nextPlayer();
        });
    }
}
