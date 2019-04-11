package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Bits64 Une classe non instanciable, homologue à Bits32, contenant des
 * méthodes permettant de travailler sur des vecteurs de 64 bits
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class Bits64 {
    /**
     * Extrait de bits le long composé par l'ensemble des bits commençant en
     * start et de taille size
     *
     * @param bits
     *            (long) la chaîne de bits de départ
     * @param start
     *            (int) le bit de départ de la chaîne de bits à extraire
     * @param size
     *            (int) la taille de la chaîne de bits à extraire
     * @return (long) un long composé de la chaîne de bit extraite en poids
     *         faible
     * @throws IllegalArgumentException
     *             lorsque start et size ne désignent pas une plage valide
     */
    public static long extract(long bits, int start, int size) {
        return (bits & mask(start, size)) >>> start;
    }

    /**
     * Crée un masque sur un long, de taille size, et commençant au start-ième
     * bit Attention : le 1er bit est à start = 0
     *
     * @param start
     *            (int) l'emplacement du bit de départ
     * @param size
     *            (int) la taille du masque à créer
     * @return (long) un masque de taille size et de bit de départ start
     * @throws IllegalArgumentException
     *             lorsque start et size ne désignent pas une plage valide
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(
                start >= 0 && size >= 0 && start + size <= Long.SIZE);

        if (size == Long.SIZE)
            return -1L;

        long mask = (1L << size) - 1;
        return mask << start;
    }

    /**
     * Permet de packer deux long en un seul en collant v1 sur les s1 bits de
     * poids faible, et v2 sur les s2 suivants
     *
     * @param v1
     *            (long) long n°1 à packer dans l'entier retourné
     * @param s1
     *            (int) nombre de bits alloués pour packer v1
     * @param v2
     *            (long) long n°2 à packer
     * @param s2
     *            (int) nombre de bits alloués pour packer v2
     *
     * @return (long) un long composé de v1 occupant les s1 bits de poids faible
     *         et de v2 occupant les s2 bits suivants
     * @throws IllegalArgumentException
     *             si la somme de s1 et s2 dépasse la taille d'un long, ou si
     *             v1/v2 ne sont pas representables en s1/s2 bits
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);
        checkValidity(v1, s1);
        checkValidity(v2, s2);

        long l1 = v1 & mask(0, s1);
        long l2 = v2 & mask(0, s2);

        return l1 | (l2 << s1);
    }

    private static void checkValidity(long v, int s)
            throws IllegalArgumentException {
        Preconditions.checkArgument(s >= 0 && s <= Long.SIZE);
        if (s == Long.SIZE)
            Preconditions.checkArgument(v <= -1L);
        else
            Preconditions.checkArgument(v <= ((1L << s) - 1L));
    }

    private Bits64() {
    }
}
