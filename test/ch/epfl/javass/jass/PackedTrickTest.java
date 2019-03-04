package ch.epfl.javass.jass;

import java.util.SplittableRandom;
import static ch.epfl.test.TestRandomizer.newRandom;
import org.junit.jupiter.api.Test;

class PackedTrickTest {
    
    private SplittableRandom rng = newRandom();
    
    @Test
    void testToString() {
        System.out.println("--------------------------");
        System.out.println("PackedTrickTest - toString");
        int pkTrick = PackedTrick.firstEmpty(Card.Color.DIAMOND, PlayerId.PLAYER_3);
        while (true) {
            while (!PackedTrick.isFull(pkTrick)) {
                pkTrick = PackedTrick.withAddedCard(pkTrick, generateRandomPackedCard());
                System.out.println(PackedTrick.toString(pkTrick));
            }
            
            if (PackedTrick.isLast(pkTrick))
                break;
            
            pkTrick = PackedTrick.nextEmpty(pkTrick);
        }
    }
    
    private int generateRandomPackedCard() {
        return PackedCard.pack(Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)), Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)));
    }
}
