package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TurnState;

/**
 * StringSerializer Une classe publique, finale et non instanciable contenant des méthodes de sérialisation
 *  des valeurs échangées entre le cliant et le serveur
 *  
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class StringSerializer {

    private StringSerializer() {}
    
    /**
     * Méthode publique et statique permettant de convertir un entier en la
     *  chaine de caractères le représentant en base 16
     * @param i (int) l'entier à convertir
     * @return (String) la chaîne de caractères représenant i en base 16
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, 16);
    }
    
    /**
     * Méthode publique et statique permettant de convertir
     *  une chaine de caractères en l'entier en base 10 qu'elle représente en base 16
     * @param s (String) la chaîne de caractères à convertir
     * @return (int) l'entier en base 10 représenté par s
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s);
    }
    
    /**
     * Méthode publique et statique permettant de convertir un long en la
     *  chaine de caractères le représentant en base 16
     * @param l (long) l'entier à convertir
     * @return (String) la chaîne de caractères représenant l en base 16
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, 16);
    }
    
    /**
     * Méthode publique et statique permettant de convertir
     *  une chaine de caractères en long en base 10 qu'elle représente en base 16
     * @param s (String) la chaîne de caractères à convertir
     * @return (long) le long en base 10 représenté par s
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s);
    }
    
    /**
     * Méthode publique et statique permettant de sérialiser une chaîne de caractères en base64 
     * @param s (String) la chaîne de caractères à convertir en base64
     * @return (String) la chaîne s en base64
     */
    public static String serializeString(String s) {
        return Base64.encode(s.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Méthode publique et statique permettant de désérialiser une chaîne de caractères en base64
     * @param s (String) la chaîne de caractères en base64 à désérialiser
     * @return (String) la version désérialisée de s
     */
    public static String deserializeString(String s) {
        return new String(Base64.decode(s), StandardCharsets.UTF_8);
    }
    
    /**
     * Méthode publique et statique permettant de joindre plusieurs
     *  chaînes de caractères en les séparant par un séparateur
     * @param delimiter (String) le séparateur
     * @param elements (String) les chaînes de caractères à joindre
     * @return (String) la chaîne de caractère composée des elements séparés par le delimiter
     */
    public static String join(String delimiter, String... elements) {
        return String.join(delimiter, elements);
    }
    
    /**
     * Méthode publique et static permettant de séparer une
     *  chaine de caractère par rapport au delimiter
     * @param delimiter (String) le séparateur par rapport auquel séparer la chaîne
     * @param toSplit (String) la chaîne de caractères à séparer
     * @return (String[]) un tableau contenant les différentes parties de toSplit
     */
    public static String[] split(String delimiter, String toSplit) {
        return toSplit.split(delimiter);
    }
    
    /**
     * Méthode permettant de sérialiser un TurnState facilement
     * @param state (TurnState) l'état du tour
     * @return (String) l'état du tour sérialisé
     */
    public static String serializeTurnState(TurnState state) {
        return join(",", serializeLong(state.packedScore()), serializeLong(state.packedUnplayedCards()), serializeInt(state.packedTrick()));
    }
    
    /**
     * Désérialise un String en TurnState
     * @param s (String) le string
     * @return (TurnState) le tour déserialisé
     */
    public static TurnState deserializeTurnState(String s) {
        String[] strs = split(",", s);
        return TurnState.ofPackedComponents(deserializeLong(strs[0]), deserializeLong(strs[1]), deserializeInt(strs[2]));
    }
    
    /**
     * Sérialise la map des noms des joueurs
     * @param map (Map<PlayerId, String>) la map des noms
     * @return (String) map sérialisée
     */
    public static String serializeMapNames(Map<PlayerId, String> map) {
        String[] strs = new String[PlayerId.COUNT];
        for (int i = 0 ; i < strs.length ; ++i)
            strs[i] = serializeString(map.get(PlayerId.ALL.get(i)));
        return join(",", strs);
    }
    /**
     * Déserialise la map des noms des joueurs
     * @param s (String) le string
     * @return (Map<PlayerId, Player>)
     */
    public static Map<PlayerId, String> deserializeMapNames(String s) {
        String[] strs = split(",", s);
        Map<PlayerId, String> map = new HashMap<>();
        for (int i = 0 ; i < PlayerId.COUNT ; ++i)
            map.put(PlayerId.ALL.get(i), deserializeString(strs[i]));
        return map;
    }
}
