package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

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

    public static long getRandomPackedScoreWithTurnTricks(int turnTricks1,
            int turnTricks2, SplittableRandom rng) {
        int turnPoints1 = rng.nextInt(257);
        int gamePoints1 = rng.nextInt(2000);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);
        int gamePoints2 = rng.nextInt(2000);
        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1,
                turnTricks2, turnPoints2, gamePoints2);
    }

    public static long getRandomPackedScoreWithTurnPoints(int turnPoints1,
            int turnPoints2, SplittableRandom rng) {
        int turnTricks1 = rng.nextInt(9);
        int gamePoints1 = rng.nextInt(2000);
        int turnTricks2 = rng.nextInt(9 - turnTricks1);
        int gamePoints2 = rng.nextInt(2000);

        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1,
                turnTricks2, turnPoints2, gamePoints2);
    }

    public static long getRandomPackedScoreWithGamePoints(int gamePoints1,
            int gamePoints2, SplittableRandom rng) {

        int tricks1 = rng.nextInt(9);
        int turnPoints1 = rng.nextInt(257);

        int tricks2 = rng.nextInt(9 - tricks1);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);

        return PackedScore.pack(tricks1, turnPoints1, gamePoints1, tricks2,
                turnPoints2, gamePoints2);
    }

    private static long getRandomInvalidPackedScore(
            SplittableRandom generator) {

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

    void isValidWorksWithValidRandomNumbers() {
        assertTrue(PackedScore.isValid(getRandomValidPackedScore(newRandom())));
    }

    void isValidFailsWithInvalidRandomNumbers() {
        assertFalse(
                PackedScore.isValid(getRandomInvalidPackedScore(newRandom())));
    }

    void turnTricksWorksWithRandomValidNumbers() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                for (int k = 0; k < 5000; ++k) {
                    assertEquals(i,
                            PackedScore
                                    .turnTricks(
                                            getRandomPackedScoreWithTurnTricks(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_1));
                    assertEquals(j,
                            PackedScore
                                    .turnTricks(
                                            getRandomPackedScoreWithTurnTricks(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_2));

                }
            }
        }
    }

    void turnPointsWorksWithRandomValidNumbers() {
        for (int i = 0; i < 257; ++i) {
            for (int j = 0; j < 257; ++j) {
                for (int k = 0; k < 100; ++k) {
                    assertEquals(i,
                            PackedScore
                                    .turnPoints(
                                            getRandomPackedScoreWithTurnPoints(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_1));
                    assertEquals(j,
                            PackedScore
                                    .turnPoints(
                                            getRandomPackedScoreWithTurnPoints(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_2));

                }
            }
        }
    }

    void gamePointsWorksWithRandomValidNumbers() {
        for (int i = 0; i < 2000; ++i) {
            for (int j = 0; j < 2000; ++j) {
                for (int k = 0; k < 10; ++k) {
                    assertEquals(i,
                            PackedScore
                                    .gamePoints(
                                            getRandomPackedScoreWithGamePoints(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_1));
                    assertEquals(j,
                            PackedScore
                                    .gamePoints(
                                            getRandomPackedScoreWithGamePoints(
                                                    i, j, newRandom()),
                                            TeamId.TEAM_2));

                }
            }
        }
    }

    public static void main(String[] args) {
        long s = PackedScore.INITIAL;
        System.out.println(PackedScore.toString(s));
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
            int p = (i == 0 ? 13 : 18);
            TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
            s = PackedScore.withAdditionalTrick(s, w, p);
            System.out.println(PackedScore.toString(s));
        }
        s = PackedScore.nextTurn(s);
        System.out.println(PackedScore.toString(s));
    }
}
