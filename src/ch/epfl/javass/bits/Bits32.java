package ch.epfl.javass.bits;

public final class Bits32 {

    /**
     * Bits32 Une classe non instanciable contenant des méthodes permettant de
     * travailler sur des vecteurs de 32 bits
     */
    private Bits32() {
    };

    /**
     * @param start
     *            (int) l'emplacement du bit de départ
     * @param size
     *            (int) la taille du masque à créer
     * @return (int) un masque de taille size et de bit de départ start
     * @throws IllegalArgumentException
     *             lorsque start et size ne désignent pas un plage valide
     */
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

    /**
     * @param bits
     *            (int) la chaîne de bits de départ
     * @param start
     *            (int) le bit de départ de la chaîne de bits à extraire
     * @param size
     *            (int) la taille de la chaîne de bits à extraire
     * @return (int) un entier composé de la chaîne de bit extraite en poids
     *         faible
     * @throws IllegalArgumentException
     *             lorsque start et size ne désignent pas une plage valide
     */
    public static int extract(int bits, int start, int size)
            throws IllegalArgumentException {
        return (bits & mask(start, size)) >>> start;
    }

    /**
     * Méthode utilitaire permettant de vérifier si un couple d'entiers est
     * valide
     * 
     * @throws IllegalArgumentException
     *             si s1 n'est pas valide ou si la représentation en bits de v1
     *             est de taille supérieure à s1
     */
    private static void check(int v1, int s1) throws IllegalArgumentException {
        if ((s1 >= Integer.SIZE) || (s1 < 0) || (v1 >= Math.pow(2, s1))) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param v1
     *            (int)
     * @param s1
     *            (int)
     * @param v2
     *            (int)
     * @param s2
     *            (int)
     * @return un entier composé de v1 occupant les s1 bits de poids faible et
     *         de v2 occupant les s2 bits suivants
     * @throws IllegalArgumentException
     *             si la somme de s1 et s2 dépasse la taille d'un int
     */
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

    /**
     * Surcharge de pack fonctionnant pour 3 chiffres
     * 
     * @return une entier composé de v1 occupant les s1 bits de poids faible, de
     *         v2 occupant les s2 bits suivants et de v3 occupant les s3 bits
     *         suivants
     * @throws IllegalArgumentException
     *             si la taille totale de l'entier créé est supérieure à la
     *             taille d'un int
     */
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

    /**
     * Surcharge de pack fonctionnant pour 3 chiffres
     * 
     * @return une entier composé de v1 occupant les s1 bits de poids faible, de
     *         v2 occupant les s2 bits suivants, de v3 occupant les s3 bits
     *         suivants, etc.
     * @throws IllegalArgumentException
     *             si la taille totale de l'entier créé est supérieure à la
     *             taille d'un int
     */
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
