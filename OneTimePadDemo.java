import java.security.SecureRandom;

public class OneTimePadDemo {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";

    public static void main(String[] args) {
        String plaintext = "MY NAME IS UNKNOWN";

        String key = generateRandomKey(plaintext.length());
        String ciphertext = encrypt(plaintext, key);
        String decrypted = decrypt(ciphertext, key);

        System.out.println("Plaintext : " + plaintext);
        System.out.println("Key       : " + key);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Decrypted : " + decrypted);
    }

    public static String generateRandomKey(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHABET.length());
            key.append(ALPHABET.charAt(index));
        }
        return key.toString();
    }

    public static String encrypt(String plaintext, String key) {
        if (plaintext.length() != key.length()) {
            throw new IllegalArgumentException("Key length must match plaintext length.");
        }

        StringBuilder ciphertext = new StringBuilder();

        for (int i = 0; i < plaintext.length(); i++) {
            int p = ALPHABET.indexOf(plaintext.charAt(i));
            int k = ALPHABET.indexOf(key.charAt(i));
            int c = (p + k) % ALPHABET.length();
            ciphertext.append(ALPHABET.charAt(c));
        }

        return ciphertext.toString();
    }

    public static String decrypt(String ciphertext, String key) {
        if (ciphertext.length() != key.length()) {
            throw new IllegalArgumentException("Key length must match ciphertext length.");
        }

        StringBuilder plaintext = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i++) {
            int c = ALPHABET.indexOf(ciphertext.charAt(i));
            int k = ALPHABET.indexOf(key.charAt(i));
            int p = (c - k + ALPHABET.length()) % ALPHABET.length();
            plaintext.append(ALPHABET.charAt(p));
        }

        return plaintext.toString();
    }
}