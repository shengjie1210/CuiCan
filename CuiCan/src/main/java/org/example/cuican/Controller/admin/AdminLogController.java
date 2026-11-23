package org.example.cuican.Controller.admin;

import org.example.cuican.Bean.Result;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理员日志控制器
 * 提供后台日志文件查看功能，可以列出所有日志文件并读取日志内容
 */
@RestController
@RequestMapping("/admin/log")
public class AdminLogController {

    /**
     * 日期格式化器，用于解析日志文件名中的日期
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 日志级别排序顺序：error优先，然后warn，最后info
     */
    private static final List<String> LEVEL_ORDER = Arrays.asList("error", "warn", "info");

    /**
     * 可能的日志目录候选列表，自动探测日志位置
     */
    private static final String[] LOG_DIR_CANDIDATES = {"logs", "CuiCan/logs"};

    /**
     * 获取所有日志文件列表
     * @return 包含日志文件信息（名称、大小、修改时间、日志级别）的列表
     */
    

    @GetMapping("/files")
    public Result files() {
        Map<String, File> fileMap = new LinkedHashMap<>();
        for (File dir : resolveLogDirectories()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".log"));
            if (files == null) continue;
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            for (File f : files) {
                fileMap.putIfAbsent(f.getName(), f);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (File f : fileMap.values()) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", f.getName());
            m.put("size", formatSize(f.length()));
            m.put("modified", new Date(f.lastModified()));
            m.put("level", detectLevel(f.getName()));
            list.add(m);
        }
        return Result.success(list);
    }

    /**
     * 读取指定日志文件的最后N行内容
     * @param file 日志文件名
     * @param lines 需要读取的行数，默认200行
     * @return 日志行列表
     */
    

    @GetMapping("/read")
    public Result read(@RequestParam String file, @RequestParam(defaultValue = "200") int lines) {
        if (!file.endsWith(".log")) return Result.fail("非法文件");
        try {
            return Result.success(readLogLines(file, lines));
        } catch (IOException e) {
            return Result.fail("读取失败: " + e.getMessage());
        }
    }

    /**
     * 读取日志行，处理特殊的all-xxx日志（合并不同级别的同一天日志）
     * @param file 日志文件名
     * @param lines 需要读取的行数
     * @return 日志行列表
     * @throws IOException 文件读取异常
     */
    private List<String> readLogLines(String file, int lines) throws IOException {
        if (file.startsWith("all-")) {
            String date = file.substring(4, file.length() - 4);
            List<String> merged = new ArrayList<>();
            for (String level : LEVEL_ORDER) {
                merged.addAll(readSingleLogLines(level + "-" + date + ".log", lines));
            }
            return trimTail(merged, lines);
        }
        return readSingleLogLines(file,lines);
    }

    /**
     * 读取单个日志文件的最后N行
     * @param file 日志文件名
     * @param lines 需要读取的行数
     * @return 日志行列表
     * @throws IOException 文件读取异常
     */
    private List<String> readSingleLogLines(String file, int lines) throws IOException {
        File f = resolveLogFile(file);
        if (f == null || !f.exists() || !f.isFile()) return Collections.emptyList();
        List<String> allLines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
        return trimTail(allLines, lines);
    }

    /**
     * 从尾部截取指定行数的日志
     * @param lines 所有日志行
     * @param limit 需要保留的行数
     * @return 截取后的最后N行日志
     */
    private List<String> trimTail(List<String> lines, int limit) {
        int start = Math.max(0, lines.size() - Math.max(limit, 0));
        return new ArrayList<>(lines.subList(start, lines.size()));
    }

    /**
     * 查找日志文件，在多个候选目录中查找
     * @param file 日志文件名
     * @return 找到的文件对象，如果没找到返回默认路径的文件对象
     */
    private File resolveLogFile(String file) {
        for (File dir : resolveLogDirectories()) {
            File candidate = new File(dir, file);
            if (candidate.exists() && candidate.isFile()) return candidate;
        }

        ParsedLogName parsed = ParsedLogName.parse(file);
        if (parsed == null) return new File(file);

        File fallback = resolveKnownFile(parsed.level + ".log");
        if (fallback.exists() && fallback.isFile()) return fallback;
        return fallback;
    }

    /**
     * 在候选目录中查找已知名称的文件
     * @param name 文件名
     * @return 找到的文件对象
     */
    private File resolveKnownFile(String name) {
        for (File dir : resolveLogDirectories()) {
            File candidate = new File(dir, name);
            if (candidate.exists() && candidate.isFile()) return candidate;
        }
        return new File(name);
    }

    /**
     * 解析所有存在的日志目录
     * @return 存在的日志目录列表
     */
    private List<File> resolveLogDirectories() {
        List<File> dirs = new ArrayList<>();
        for (String candidate : LOG_DIR_CANDIDATES) {
            File dir = new File(candidate);
            if (dir.exists() && dir.isDirectory()) dirs.add(dir);
        }
        if (dirs.isEmpty()) dirs.add(new File(LOG_DIR_CANDIDATES[0]));
        return dirs;
    }

    /**
     * 格式化文件大小为人类可读格式
     * @param bytes 字节数
     * @return 格式化后的大小字符串（B/KB/MB）
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fKB", bytes / 1024.0);
        return String.format("%.1fMB", bytes / (1024.0 * 1024));
    }

    /**
     * 根据文件名检测日志级别
     * @param name 文件名
     * @return 日志级别（ERROR/WARN/INFO）
     */
    private String detectLevel(String name) {
        if (name.contains("error")) return "ERROR";
        if (name.contains("warn")) return "WARN";
        return "INFO";
    }

    /**
     * 解析日志文件名内部类
     * 用于解析 level-date.log 格式的文件名
     */
    private static class ParsedLogName {
        private final String level;
        private final String date;

        private ParsedLogName(String level, String date) {
            this.level = level;
            this.date = date;
        }

        /**
         * 解析日志文件名
         * @param file 文件名
         * @return 解析后的ParsedLogName对象，如果格式不匹配返回null
         */
        private static ParsedLogName parse(String file) {
            if (file == null || !file.endsWith(".log")) return null;
            String base = file.substring(0, file.length() - 4);
            int dash = base.indexOf('-');
            if (dash <= 0) return null;
            String level = base.substring(0, dash);
            String date = base.substring(dash + 1);
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) return null;
            return new ParsedLogName(level, date);
        }
    }
}