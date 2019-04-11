package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Bits32 Une classe non instanciable contenant des méthodes permettant de
 * travailler sur des vecteurs de 32 bits
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class Bits32 {

    /**
     * Méthode utilitaire permettant de vérifier si un couple d'entiers est
     * valide
     *
     * @throws IllegalArgumentException
     *             si s1 n'est pas valide ou si la représentation en bits de v1
     *             est de taille supérieure à s1
     */
    private static void checkValidity(int v, int s)
            throws IllegalArgumentException {

        Preconditions.checkArgument(s >= 0 && s <= Integer.SIZE);
        if (s == Integer.SIZE)
            Preconditions.checkArgument(v <= -1);
        else
            Preconditions.checkArgument(v <= ((1 << s) - 1));
    };

    /**
     * Extrait de bits l'entier composé par l'ensemble des bits commençant en
     * start et de taille size
     *
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
     * Crée un masque sur un int, de taille size, et commençant au start-ième
     * bit Attention : le 1er bit est à start = 0
     *
     * @param start
     *            (int) l'emplacement du bit de départ
     * @param size
     *            (int) la taille du masque à créer
     * @return (int) un masque de taille size et de bit de départ start
     * @throws IllegalArgumentException
     *             lorsque start et size ne désignent pas une plage valide
     */
    public static int mask(int start, int size)
            throws IllegalArgumentException {

        Preconditions.checkArgument(
                start >= 0 && size >= 0 && start + size <= Integer.SIZE);

        if (size == Integer.SIZE)
            return -1;

        int mask = (1 << size) - 1;
        return mask << start;
    }

    /**
     * Permet de packer deux entiers en un seul en collant v1 sur les s1 bits de
     * poids faible, et v2 sur les s2 suivants
     *
     * @param v1
     *            (int) entier n°1 à packer dans l'entier retourné
     * @param s1
     *            (int) nombre de bits alloués pour packer v1
     * @param v2
     *            (int) entier n°2 à packer
     * @param s2
     *            (int) nombre de bits alloués pour packer v2
     * 
     * @return (int) un entier composé de v1 occupant les s1 bits de poids
     *         faible et de v2 occupant les s2 bits suivants
     * @throws IllegalArgumentException
     *             si la somme de s1 et s2 dépasse la taille d'un int, ou si
     *             v1/v2 ne sont pas representables en s1/s2 bits
     */
    public static int pack(int v1, int s1, int v2, int s2)
            throws IllegalArgumentException {

        Preconditions.checkArgument((s1 + s2) <= Integer.SIZE);
        checkValidity(v1, s1);
        checkValidity(v2, s2);

        int entier1 = v1 & mask(0, s1);
        int entier2 = v2 & mask(0, s2);
        return entier1 | (entier2 << s1);
    }

    /**
     * Surcharge de pack fonctionnant pour 3 chiffres
     *
     * @param v1
     *            (int) entier n°1 à packer dans l'entier retourné
     * @param s1
     *            (int) nombre de bits alloués pour packer v1
     * @param v2
     *            (int) entier n°2 à packer
     * @param s2
     *            (int) nombre de bits alloués pour packer v2
     * @param v3
     *            (int) entier n°3 à packer
     * @param s3
     *            (int) nombre de bits alloués pour packer v3
     * 
     * @return (int) un entier composé de v1 occupant les s1 bits de poids
     *         faible, de v2 occupant les s2 bits suivants et de v3 occupant les
     *         s3 bits suivants
     * @throws IllegalArgumentException
     *             si la taille totale de l'entier créé est supérieure à la
     *             taille d'un int, ou si les entiers vI donnés ne sont pas
     *             représentables par le nombre de bits sI correspondant
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3)
            throws IllegalArgumentException {

        Preconditions.checkArgument((s1 + s2 + s3) <= Integer.SIZE);

        checkValidity(v3, s3);

        int entier1 = pack(v1, s1, v2, s2);
        int entier3 = v3 & mask(0, s3);
        return entier1 | (entier3 << (s1 + s2));
    }

    /**
     * Surcharge de pack fonctionnant pour 7 chiffres
     *
     * @return (int) un entier composé de v1 occupant les s1 bits de poids
     *         faible, de v2 occupant les s2 bits suivants, de v3 occupant les
     *         s3 bits suivants, etc.
     * @throws IllegalArgumentException
     *             si la taille totale de l'entier créé est supérieure à la
     *             taille d'un int, ou si les entiers vI donnés ne sont pas
     *             représentables par le nombre de bits sI correspondant
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7)
            throws IllegalArgumentException {

        Preconditions.checkArgument(
                (s1 + s2 + s3 + s4 + s5 + s6 + s7) <= Integer.SIZE);

        int entier1 = pack(v1, s1, v2, s2, v3, s3);
        int entier2 = pack(v4, s4, v5, s5);
        int entier3 = pack(v6, s6, v7, s7);

        return entier1 | (entier2 << (s1 + s2 + s3))
                | (entier3 << (s1 + s2 + s3 + s4 + s5));
    }

    private Bits32() {
    }

}
