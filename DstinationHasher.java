import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHasher {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <jar_file> <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase(); // Ensure roll number is lowercase.
        String filePath = args[1];

        try {
            // Parse the JSON file and get the destination value.
            String destinationValue = findDestination(filePath);

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string.
            String randomString = generateRandomString(8);

            // Concatenate values and generate the MD5 hash.
            String concatenatedValue = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedValue);

            // Output in the required format.
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Function to traverse JSON and find the first "destination" key.
    private static String findDestination(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(filePath));
        return traverseJSON(rootNode);
    }

    // Recursive function to traverse JSON.
    private static String traverseJSON(JsonNode node) {
        if (node.isObject()) {
            for (var entry : node.fields()) {
                if ("destination".equals(entry.getKey())) {
                    return entry.getValue().asText();
                }
                String value = traverseJSON(entry.getValue());
                if (value != null) return value;
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                String value = traverseJSON(item);
                if (value != null) return value;
            }
        }
        return null;
    }

    // Function to generate a random alphanumeric string of given length.
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    // Function to generate MD5 hash of a given string.
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hash = new StringBuilder();

        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }
}

