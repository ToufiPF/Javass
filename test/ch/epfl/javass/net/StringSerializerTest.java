package ch.epfl.javass.net;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class StringSerializerTest {

    private SplittableRandom mRng = newRandom();
    
    @Test
    void serializeAndDeserializeIntWorks() {
        for(int i = -1000000; i < 1000000; ++ i) {
            assertEquals(i, StringSerializer.deserializeInt(StringSerializer.serializeInt(i)));
        }
    }
    
    @Test
    void serializeAndDeserializeLongWorks() {
        for(long l = -1000000; l < 1000000; ++l) {
            assertEquals(l,StringSerializer.deserializeLong(StringSerializer.serializeLong(l)));
        }
    }

    @Test
    void serializeAndDeserializeStringWorks() {
        StringBuilder s= new StringBuilder();
        for(int i = 0; i < mRng.nextInt(1000); ++ i) {
            s.append((char) mRng.nextInt(20, 122));
        }
        System.out.println(s.toString());
        System.out.println(StringSerializer.serializeString(s.toString()));
        assertEquals(s.toString(), StringSerializer.deserializeString(StringSerializer.serializeString(s.toString())));
    }
}
