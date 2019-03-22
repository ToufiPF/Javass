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

    private static int randomPkTrick(SplittableRandom rng) {
        int card1 = rng.nextInt(36);
        int card2 = rng.nextInt(35);
        int card3 = rng.nextInt(34);
        int card4 = rng.nextInt(33);
        int index = rng.nextInt(9);
        int joueur1 = rng.nextInt(4);
        int trump = rng.nextInt(4);
        return Bits32.pack(card1, 6, card2, 6, card3, 6, card4, 6, index, 4, joueur1, 2, trump, 2);
    }

    SplittableRandom rng = newRandom();
    TurnState turnStateTest = TurnState.initial(Color.ALL.get(rng.nextInt(Color.ALL.size())), 
            Score.ofPacked(randomPkScore(rng)), PlayerId.ALL.get(rng.nextInt(PlayerId.ALL.size())));

    @Test
    void initialWorksWithSomeRandomScore() {
        //Teste Initial et les getters

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
