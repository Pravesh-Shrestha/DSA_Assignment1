import java.util.*;

class Tweet {
    int tweetId;
    String tweet;
    String tweetDate; // Format: "YYYY-MM-DD"

    Tweet(int tweetId, String tweet, String tweetDate) {
        this.tweetId = tweetId;
        this.tweet = tweet;
        this.tweetDate = tweetDate;
    }
}

public class TopHashtags {
    public static List<String[]> findTopHashtags(List<Tweet> tweets) {
        // Step 1: Map to store hashtag counts
        Map<String, Integer> hashtagCount = new HashMap<>();

        // Step 2: Process each tweet
        for (Tweet tweet : tweets) {
            // Filter for February 2024
            if (tweet.tweetDate.startsWith("2024-02")) {
                // Split tweet into words
                String[] words = tweet.tweet.split("\\s+");
                for (String word : words) {
                    if (word.startsWith("#") && word.length() > 1) { // Valid hashtag check
                        hashtagCount.put(word, hashtagCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        // Step 3: Sort hashtags by count and name
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(hashtagCount.entrySet());
        sortedList.sort((a, b) -> {
            int countCompare = b.getValue().compareTo(a.getValue()); // Descending count
            if (countCompare == 0) {
                return b.getKey().compareTo(a.getKey()); // Descending hashtag
            }
            return countCompare;
        });

        // Step 4: Prepare result (top 3)
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sortedList.size()); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            result.add(new String[]{entry.getKey(), entry.getValue().toString()});
        }

        return result;
    }

    // Test the solution
    public static void main(String[] args) {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(13, "Celebrating #HappyDay", "2024-02-01"),
            new Tweet(14, "Love #HappyDay today", "2024-02-02"),
            new Tweet(15, "Busy with #WorkLife", "2024-02-03"),
            new Tweet(16, "Enjoying #TechLife", "2024-02-04"),
            new Tweet(17, "#HappyDay is great", "2024-02-05"),
            new Tweet(18, "#TechLife rocks", "2024-02-06")
        );

        List<String[]> topHashtags = findTopHashtags(tweets);
        System.out.println("hashtag\thashtag_count");
        for (String[] entry : topHashtags) {
            System.out.println(entry[0] + "\t" + entry[1]);
        }
    }
}