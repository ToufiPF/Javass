package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class PackedScoreTest {
    private long getRandomValidPackedScore(SplittableRandom generator) {

        int tricks1 = generator.nextInt(9);
        int turnPoints1 = generator.nextInt(257);
        int gamePoints1 = generator.nextInt(2000);

        int tricks2 = generator.nextInt(9 - tricks1);
        int turnPoints2 = generator.nextInt(257 - turnPoints1);
        int gamePoints2 = generator.nextInt(2000);

        return PackedScore.pack(tricks1, turnPoints1, gamePoints1, tricks2,
                turnPoints2, gamePoints2);
    }

    private long getRandomInvalidPackedScore(SplittableRandom generator) {

        int tricks1 = generator.nextInt(6) + 10;
        int turnPoints1 = generator.nextInt(255) + 258;
        int gamePoints1 = generator.nextInt(48) + 2001;

        int tricks2 = generator.nextInt(6) + 10;
        int turnPoints2 = generator.nextInt(255) + 258;
        int gamePoints2 = generator.nextInt(48) + 2001;
        
        return PackedScore.pack(tricks1, turnPoints1, gamePoints1, tricks2,
                turnPoints2, gamePoints2);
    }

    @Test
    void InitialWorks() {
        assertEquals(0, PackedScore.INITIAL);
    }

    @Test
    void isValidWorksWithValidRandomNumbers() {
        assertTrue(PackedScore.isValid(getRandomValidPackedScore(newRandom())));
    }
    
    @Test
    void isValidFailsWithInvalidRandomNumbers() {
        assertFalse(PackedScore.isValid(getRandomInvalidPackedScore(newRandom())));
    }
    
    

}
