package ch.epfl.javass.bits;

public final class Bits32 {

    private Bits32() {
    };

    public static int mask(int start, int size)
            throws IllegalArgumentException {
        if (start < 0 || start + size >= Integer.SIZE || size < 0) {
            throw new IllegalArgumentException();
        }

        int entier = 0;
        for (int i = start; i < start + size; ++i) {
            entier |= 1 << i;
        }
        return entier;
    }

    public static int extract(int bits, int start, int size)
            throws IllegalArgumentException {
        return (bits & mask(start, size)) >>> start;
    }

    private static void check(int v1, int s1) throws IllegalArgumentException {
        if ((s1 >= Integer.SIZE) || (s1 < 0) || (v1 >= Math.pow(2, s1))) {
            throw new IllegalArgumentException();
        }
    }

    public static int pack(int v1, int s1, int v2, int s2)
            throws IllegalArgumentException {
        if ((s1 + s2) >= Integer.SIZE) {
            throw new IllegalArgumentException();
        }
        check(v1, s1);
        check(v2, s2);

        int entier1 = v1 & mask(0, s1);
        int entier2 = v2 & mask(0, s2);
        return entier1 | (entier2 << s1);

    }

    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3)
            throws IllegalArgumentException {
        if ((s1 + s2 + s3) >= Integer.SIZE) {
            throw new IllegalArgumentException();
        }

        check(v3, s3);

        int entier1 = pack(v1, s1, v2, s2);
        int entier3 = v3 & mask(0, s3);
        return entier1 | (entier3 << (s1 + s2));
    }

    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7)
            throws IllegalArgumentException {
        if (s1 + s2 + s3 + s4 + s5 + s6 + s7 >= Integer.SIZE) {
            throw new IllegalArgumentException();
        }

        int entier1 = pack(v1, s1, v2, s2, v3, s3);
        int entier2 = pack(v4, s4, v5, s5);
        int entier3 = pack(v6, s6, v7, s7);

        return entier1 | (entier2 << (s1 + s2 + s3))
                | (entier3 << (s1 + s2 + s3 + s4 + s5));
    }
}
