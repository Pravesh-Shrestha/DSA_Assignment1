-- Create the Tweets table
CREATE TABLE Tweets (
    user_id INT,
    tweet_id INT PRIMARY KEY,
    tweet VARCHAR(255),
    tweet_date DATE
);
Drop Table Tweets;

-- Insert sample data into the Tweets table
INSERT INTO Tweets (user_id, tweet_id, tweet, tweet_date) VALUES
(135, 13, 'Enjoying a great start to the day. #HappyDay #MorningVibes', '2024-02-01'),
(136, 14, 'Another #HappyDay with good vibes! #FeelGood', '2024-02-03'),
(137, 15, 'Productivity peaks! #WorkLife #ProductiveDay', '2024-02-05'),
(138, 16, 'Exploring new tech frontiers. #TechLife #Innovation', '2024-02-06'),
(139, 17, 'Gratitude for today moments. #HappyDay #Thankful', '2024-02-10'),
(140, 18, 'Innovation drives us. #TechLife #FutureTech', '2024-02-15'),
(141, 19, 'Connecting with nature serenity. #Nature #Peaceful', '2024-02-20');

SELECT * FROM Tweets WHERE tweet_date BETWEEN '2024-02-01' AND '2024-02-29';
SELECT
    t.tweet_id,
    SUBSTRING_INDEX(SUBSTRING_INDEX(t.tweet, '#', n.n), ' ', 1) AS hashtag
FROM
    Tweets t
JOIN 
    (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL 
     SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL 
     SELECT 9 UNION ALL SELECT 10) n
ON CHAR_LENGTH(t.tweet) - CHAR_LENGTH(REPLACE(t.tweet, '#', '')) >= n.n - 1
WHERE
    t.tweet_date BETWEEN '2024-02-01' AND '2024-02-29';
    
WITH Hashtags AS (
    SELECT
        tweet_id,
        REGEXP_SUBSTR(tweet, '#[^ ]+', 1, n.digit) AS hashtag
    FROM Tweets
    JOIN (
        SELECT 1 AS digit UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
        UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
    ) AS n
    WHERE tweet_date BETWEEN '2024-02-01' AND '2024-02-29'
),
FilteredHashtags AS (
    SELECT hashtag FROM Hashtags WHERE hashtag IS NOT NULL
)
SELECT
    hashtag,
    COUNT(*) AS count
FROM FilteredHashtags
GROUP BY hashtag
ORDER BY count DESC, hashtag DESC
LIMIT 3;




    

    


    
