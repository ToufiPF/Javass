package ch.epfl.javass;

/**
 * Preconditions 
 * Une classe non instanciable qui fournit des méthodes statiques
 * pour vérifier la validité d'un argument
 * 
 * @author Aurélien Clergeot (302592)
 */
public final class Preconditions {
    private Preconditions() {
    };

    /**
     * Methode utilitaire qui lance une exception
     * si l'argument passé vaut false
     * 
     * @param b (boolean) l'argument à vérifier
     * @throws IllegalArgumentException quand b vaut false
     */
    public static void checkArgument(boolean b) throws IllegalArgumentException {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Methode utilitaire qui vérifie la validité d'un index
     * càd index >= 0, et index < size.
     * Lance un IndexOutOfBoundsException sinon
     * 
     * @param index (int) l'index à vérifier
     * @param size (int) la taille à ne pas dépasser
     * @return (int) index, non modifié
     * @throws IndexOutOfBoundsException quand index < 0 ou index >= size
     */
    public static int checkIndex(int index, int size) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return index;
    }
}
