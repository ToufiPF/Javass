package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;

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



    @Test
    void playableCardTestUnit() {
        System.out.println("--------------------------");
        System.out.println("PackedTrickTest - playableCardTestUnit");
        int pkTrick1 = PackedTrick.firstEmpty(Card.Color.SPADE,
                PlayerId.PLAYER_1);
        long pkHand1 = 0b0000_0000_0010_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000L;
        int pkTrick2 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand2 = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_1010_0000_0000_0010_0000L;
        int pkTrick3 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand3 = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_1010_0000L;
        int pkTrick4 = Bits32.pack(0b10_0110, 6, 0b0, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand4 = 0b0000_0000_0010_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_0010L;
        int pkTrick5 = Bits32.pack(0b11_0001, 6, 0b1_0011, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b1,
                2);
        long pkHand5 = 0b0000_0000_0000_0000_0000_0000_1000_0000_0000_0000_0000_0100_0000_0000_1000_0001L;
        int pkTrick6 = Bits32.pack(0b1_0101, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand6 = 0b0000_0000_0000_0001_0000_0000_0001_0000_0000_0000_1000_0000_0000_0000_0000_1000L;
        int pkTrick7 = Bits32.pack(0b11_0011, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b11,
                2);
        long pkHand7 = 0b0000_0000_0000_0000_0000_0000_1000_1000_0000_0000_0100_0000_0000_0001_0000_0000L;
        int pkTrick8 = Bits32.pack(0b1000, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand8 = 0b0000_0000_0000_0000_0000_0000_0010_0010_0000_0000_0100_0000_0000_0000_1000_0000L;
        int pkTrick9 = Bits32.pack(0b11_0100, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand9 = 0b0000_0000_0000_0010_0000_0000_0000_1000_0000_0000_0001_0001_0000_0000_0000_0000L;
        int pkTrick10 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand10 = 0b0000_0000_1000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0000L;
        int pkTrick11 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand11 = 0b0000_0000_0000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0100L;
        assertEquals(true,true);
        System.out.println("{\u266110 \u2663J} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick1, pkHand1)));
        System.out.println();
        System.out.println("{\u2660J \u26617 \u26619 \u266310} expected\n"
                + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick2, pkHand2)));
        System.out.println();
        System.out.println("{\u2660J \u2660K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick3, pkHand3)));
        System.out.println();
        System.out.println("{\u26607 \u266210} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick4, pkHand4)));
        System.out.println();
        System.out.println("{\u26606 \u2660K \u2662K} expected\n"
                + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick5, pkHand5)));
        System.out.println();
        System.out.println("{\u26609 \u2661K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick6, pkHand6)));
        System.out.println();
        System.out.println("{\u2660A \u2661Q \u26629 \u2662K} expected\n"
                + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick7, pkHand7)));
        System.out.println();
        System.out.println(
                "{\u2660K \u26627 \u2662J} expected\n" + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick8, pkHand8)));
        System.out.println();
        System.out.println("{\u26629 \u26637} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick9, pkHand9)));
        System.out.println();
        System.out.println("{\u2662J \u2662K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick10, pkHand10)));
        System.out.println();
        System.out.println("{\u2662J \u2662K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick11, pkHand11)));
    }
}
