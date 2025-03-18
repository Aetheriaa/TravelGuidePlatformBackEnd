package top.aetheria.travelguideplatform.common.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TagExtractor {

    private static final Set<String> STOP_WORDS = new HashSet<>();

    // 加载停用词 (你可以根据你的实际情况修改停用词文件的路径)
    static {
        loadStopWords("stopwords/hit_stopwords.txt"); // 使用 / 分隔符
        loadStopWords("stopwords/cn_stopwords.txt");
        loadStopWords("stopwords/scu_stopwords.txt");
        loadStopWords("stopwords/my_stopwords.txt"); // 添加你自己的停用词文件
    }

    private static void loadStopWords(String filePath) {
        try (InputStream inputStream = TagExtractor.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                STOP_WORDS.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            //  处理异常
        }
    }

    public static List<String> extractTags(String content, int numTags) {
        // 1. 分词
        List<Term> terms = StandardTokenizer.segment(content);

        // 2. 过滤停用词和短词
        List<String> filteredWords = terms.stream()
                .map(term -> term.word)
                .filter(word -> word.trim().length() > 1 && !STOP_WORDS.contains(word.trim()))
                .collect(Collectors.toList());

        // 3. 统计词频
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : filteredWords) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

        // 4. 按词频排序 (降序)
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCounts.entrySet());
        sortedWords.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        // 5. 提取前 numTags 个关键词作为标签
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < Math.min(numTags, sortedWords.size()); i++) {
            tags.add(sortedWords.get(i).getKey());
        }

        return tags;
    }

    public static void main(String[] args) {
        String articleContent = "这里是一段测试文本，用于测试关键词提取功能。 关键词提取是自然语言处理中的一个重要任务。";
        List<String> tags = TagExtractor.extractTags(articleContent, 5);
        System.out.println(tags); // 输出：[关键词, 提取, 文本, 测试, 处理]
    }
}
