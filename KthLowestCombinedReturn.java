import java.util.PriorityQueue;

public class KthLowestCombinedReturn {
    public static void main(String[] args) {
        int[] returns1 = {2, 5};
        int[] returns2 = {3, 4};
        int k = 2;
        
        int result = kthLowestCombinedReturn(returns1, returns2, k);
        System.out.println("The " + k + "th lowest combined return is: " + result); // Improved output
    }

    public static int kthLowestCombinedReturn(int[] returns1, int[] returns2, int k) {
        int n = returns1.length;
        int m = returns2.length;

        // Min-heap to store the products and their indices
        PriorityQueue<Element> minHeap = new PriorityQueue<>((a, b) -> a.product - b.product);

        // Initialize the heap with the first element from returns1 and all elements from returns2
        for (int i = 0; i < Math.min(n, k); i++) {
            minHeap.offer(new Element(returns1[i] * returns2[0], i, 0));
        }

        // Extract the smallest element k times
        int currentProduct = 0;
        for (int i = 0; i < k; i++) {
            Element element = minHeap.poll();
            currentProduct = element.product;
            int index1 = element.index1;
            int index2 = element.index2;

            // If there's a next element in returns2, add the new product to the heap
            if (index2 + 1 < m) {
                minHeap.offer(new Element(returns1[index1] * returns2[index2 + 1], index1, index2 + 1));
            }
        }

        return currentProduct;
    }

    // Helper class to store the product and the indices
    static class Element {
        int product;
        int index1;
        int index2;

        public Element(int product, int index1, int index2) {
            this.product = product;
            this.index1 = index1;
            this.index2 = index2;
        }
    }
}