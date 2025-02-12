import java.util.*;

public class UniquePermutations {
    public static Set<String> generatePermutations(String str) {
        Set<String> result = new HashSet<>();
        permute(str.toCharArray(), 0, result);
        return result;
    }

    private static void permute(char[] chars, int index, Set<String> result) {
        if (index == chars.length) {
            result.add(new String(chars));
            return;
        }
        for (int i = index; i < chars.length; i++) {
            if (canSwap(chars, index, i)) {
                swap(chars, index, i);
                permute(chars, index + 1, result);
                swap(chars, index, i); // backtrack
            }
        }
    }

    private static boolean canSwap(char[] chars, int start, int current) {
        for (int i = start; i < current; i++) {
            if (chars[i] == chars[current]) return false;
        }
        return true;
    }

    private static void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }

    public static void main(String[] args) {
        String input = "AAB";
        Set<String> permutations = generatePermutations(input);
        System.out.println("Unique Permutations: " + permutations);
    }
}
