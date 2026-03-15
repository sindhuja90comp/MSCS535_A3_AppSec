import java.security.SecureRandom;

public class OneTimePadDemo {

    // These are the only characters this demo uses.
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";

    public static void main(String[] args) {
        // This is the message we want to protect.
        String plaintext = "MY NAME IS UNKNOWN";

        // Make a random key, lock the message, then unlock it again.
        String key = generateRandomKey(plaintext.length());
        String ciphertext = encrypt(plaintext, key);
        String decrypted = decrypt(ciphertext, key);

        // Show each step so the result is easy to follow.
        System.out.println("Plaintext : " + plaintext);
        System.out.println("Key       : " + key);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Decrypted : " + decrypted);
    }

    public static String generateRandomKey(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        // Pick random letters to build a key the same size as the message.
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
            // Match each message letter with the key letter in the same spot.
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
            // Use the same key to turn the secret text back into the original message.
            int c = ALPHABET.indexOf(ciphertext.charAt(i));
            int k = ALPHABET.indexOf(key.charAt(i));
            int p = (c - k + ALPHABET.length()) % ALPHABET.length();
            plaintext.append(ALPHABET.charAt(p));
        }

        return plaintext.toString();
    }
}
