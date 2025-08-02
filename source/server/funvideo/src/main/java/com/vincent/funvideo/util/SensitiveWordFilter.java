package com.vincent.funvideo.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class SensitiveWordFilter {
    private static List<String> sensitiveWords = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("sensitive-words.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        sensitiveWords.add(line);
                    }
                }
            }
        } catch (IOException e) {
            // 如果文件不存在，使用默认敏感词
            sensitiveWords.add("傻逼");
            sensitiveWords.add("垃圾");
            sensitiveWords.add("你妈");
            sensitiveWords.add("fuck");
        }
    }

    public static String filter(String text) {
        if (text == null) return null;
        String result = text;
        for (String word : sensitiveWords) {
            result = result.replaceAll(word, "***");
        }
        return result;
    }
} 