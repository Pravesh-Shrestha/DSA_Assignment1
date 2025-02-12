import java.util.*;

public class KthPermutation {
    public static List<Integer> getKthPermutation(int n, int k) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            numbers.add(i);
        }

        List<Integer> result = new ArrayList<>();
        k--; // Convert k to zero-indexed
        int factorial = 1;

        for (int i = 1; i <= n; i++) {
            factorial *= i;
        }

        for (int i = 0; i < n; i++) {
            factorial /= (n - i);
            int index = k / factorial;
            result.add(numbers.get(index));
            numbers.remove(index);
            k %= factorial;
        }

        return result;
    }

    public static void main(String[] args) {
        int n = 4;
        int k = 9;
        List<Integer> result = getKthPermutation(n, k);
        System.out.println("K-th Permutation: " + result);
    }
}

