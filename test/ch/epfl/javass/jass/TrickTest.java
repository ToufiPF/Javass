package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;

class TrickTest {


    //@Test
    void ofPackedThrowsExceptionWhenUnvalid() {
        for (int i = 0; i <= 8; ++i) {
            for (int j = 0; j < 56; ++j) {
                for (int k = 0; k < 56; ++k) {
                    for (int l = 0; l < 56; ++l) {
                        for (int m = 0; m < 56; ++m) {
                            if (((!PackedCard.isValid(j) && PackedCard.isValid(k) && PackedCard.isValid(l) && PackedCard.isValid(m))) 
                                    || (!PackedCard.isValid(j) && !PackedCard.isValid(k) && PackedCard.isValid(l) && PackedCard.isValid(m))
                                    || (!PackedCard.isValid(j) && !PackedCard.isValid(k) && !PackedCard.isValid(l) && PackedCard.isValid(m))
                                    || (PackedCard.isValid(j) && !PackedCard.isValid(k) && PackedCard.isValid(l) && !PackedCard.isValid(m))
                                    || (!PackedCard.isValid(j) && PackedCard.isValid(k) && !PackedCard.isValid(l) && PackedCard.isValid(m))
                                    || (!PackedCard.isValid(j) && PackedCard.isValid(k) && PackedCard.isValid(l) && !PackedCard.isValid(m)))
                            {
                                int pkTrick = Bits32.pack(j, 6, k, 6, l, 6, m, 6, i, 4, 1, 2, 1, 2);
                                assertThrows(IllegalArgumentException.class, () -> {
                                    Trick.ofPacked(pkTrick);
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void nextEmptyThrowsExceptionWhenNotFull() {

        Trick trick1 = Trick.ofPacked(0b01100011_111111_100010_010011_100100);
        Trick trick2 = Trick.ofPacked(0b00000000_111111_111111_000000_000000);
        Trick trick3 = Trick.ofPacked(0b00000000_111111_111111_111111_000000);
        Trick trick4 = Trick.ofPacked(0b00000000_111111_111111_111111_111111);

        assertThrows(IllegalStateException.class, () -> {
            trick1.nextEmpty();
        });
        assertThrows(IllegalStateException.class, () -> {
            trick2.nextEmpty();
        });
        assertThrows(IllegalStateException.class, () -> {
            trick3.nextEmpty();
        });
        assertThrows(IllegalStateException.class, () -> {
            trick4.nextEmpty();
        });
    }

    @Test
    void playerThrowsExceptionWhenIndexIsNotValid() {
        Trick trick1 = Trick.ofPacked(0b01100011_110110_100010_010011_100100);


        assertThrows(IndexOutOfBoundsException.class, () -> {
            trick1.player(-20);
        });
    }

}


