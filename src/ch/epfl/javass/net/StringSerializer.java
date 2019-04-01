package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public final class StringSerializer {

    private StringSerializer() {}
    
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, 16);
    }
    
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s);
    }
    
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, 16);
    }
    
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s);
    }
    
    public static String serializeString(String s) {
        return Base64.encode(s.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String deserializeString(String s) {
        return new String(Base64.decode(s), StandardCharsets.UTF_8);
    }
    
    public static String join(CharSequence delimiter, CharSequence... elements) {
        return String.join(delimiter, elements);
    }
    
    public static String[] split(String delimiter, String toSplit) {
        return toSplit.split(delimiter);
    }
}
