package ch.epfl.javass;


/**
 * Preconditions
 * Une classe non instanciable qui fournit des méthodes
 * pour vérifier la validité d'un argument
 * @author Aurélien Clergeot (302592)
 */
public final class Preconditions {
	private Preconditions() {};
	
	/**
	 * 
	 * @param arg
	 */
	public static void checkArguments(boolean arg) {
		if (!arg)
			throw new IllegalArgumentException();
	}
	/**
	 * @param index
	 * @param size
	 * @return
	 */
	public static int checkIndex(int index, int size) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		return index;
	}
}
