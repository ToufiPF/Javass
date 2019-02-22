package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.PackedScore;
import ch.epfl.javass.jass.Score;

class PackedScoreTest {
    public static long getRandomValidPackedScore(SplittableRandom generator) {

        int tricks1 = generator.nextInt(9);
        int turnPoints1 = generator.nextInt(257);
        int gamePoints1 = generator.nextInt(2000);

        int tricks2 = generator.nextInt(9 - tricks1);
        int turnPoints2 = generator.nextInt(257 - turnPoints1);
        int gamePoints2 = generator.nextInt(2000);

        return PackedScore.pack(tricks1, turnPoints1, gamePoints1, tricks2,
                turnPoints2, gamePoints2);
    }
    public static long getRandomPackedScoreWithTurnTricks(int turnTricks1, int turnTricks2, SplittableRandom rng) {
        int turnPoints1 = rng.nextInt(257);
        int gamePoints1 = rng.nextInt(2000);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);
        int gamePoints2 = rng.nextInt(2000);
        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2);
    }
    
    public static long getRandomPackedScoreWithTurnPoints(int turnPoints1, int turnPoints2, SplittableRandom rng) {
        int turnTricks1 = rng.nextInt(9);
        int gamePoints1 = rng.nextInt(2000);
        int turnTricks2 = rng.nextInt(9 - turnTricks1);
        int gamePoints2 = rng.nextInt(2000);
        
        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2);
    }
    
    public static long getRandomPackedScoreWithGamePoints(int gamePoints1, int gamePoints2, SplittableRandom rng) {

        int tricks1 = rng.nextInt(9);
        int turnPoints1 = rng.nextInt(257);

        int tricks2 = rng.nextInt(9 - tricks1);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);

        return PackedScore.pack(tricks1, turnPoints1, gamePoints1, tricks2,
                turnPoints2, gamePoints2);
    }

    private static long getRandomInvalidPackedScore(SplittableRandom generator) {

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
