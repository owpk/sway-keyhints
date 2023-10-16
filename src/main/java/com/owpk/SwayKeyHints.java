package com.owpk;

import com.owpk.sway.ConfigParserImpl;
import com.owpk.sway.SwayConfigFileParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SwayKeyHints {

    private static final String APPEND_CONT = "...";
    private static Integer MAX_COLUMN_WIDTH = 70;
    private static Integer MAX_COLUMN_HEIGHT = 36;
    private static final String ETC_CFG = "/etc/sway/config";
    private static final String HOME_CFG = "$HOME/.config/sway/config";

    public static void main(String[] args) {
        Map<Integer, String> mappedArgs = IntStream.range(0, args.length)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> args[i]));

        MAX_COLUMN_WIDTH = parseConfig(mappedArgs, "-w", MAX_COLUMN_WIDTH);
        MAX_COLUMN_HEIGHT = parseConfig(mappedArgs, "-h", MAX_COLUMN_HEIGHT);

        var configPath = mappedArgs.values().stream().findAny().orElse("");

        var fileParser = new SwayConfigFileParser();
        List<String> res;
        try {
            res = fileParser.parsePaths(List.of(configPath, ETC_CFG, HOME_CFG));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var configParser = new ConfigParserImpl();
        configParser.parse(res);

        var mapped = configParser.getBinds().stream()
                .map(bind -> String.format("%s %s - %s", bind.mod().icon(), bind.binding(), bind.command()))
                .collect(Collectors.toList());

        var binds = createParts(mapped);
        var sb = createTable(binds);
        System.out.println(sb);
    }

    private static Integer parseConfig(Map<Integer, String> mappedArgs, String arg, Integer defaultVal) {
        List<Integer> removeIdxs = new ArrayList<>();
        String result = null;
        for (int i = 0; i < mappedArgs.size(); i++) {
            if (mappedArgs.get(i).startsWith(arg)) {
                result = mappedArgs.get(i + 1);
                removeIdxs.add(i);
                removeIdxs.add(i + 1);
            }
        }
        for (var removeIdx : removeIdxs)
            mappedArgs.remove(removeIdx);
        return result != null ? Integer.parseInt(result) : defaultVal;
    }

    private static StringBuilder createTable(List<List<String>> binds) {
        Map<Integer, Integer> pads = new HashMap<>();
        var sb = new StringBuilder();

        for (int y = 0; y < binds.get(0).size(); y++) {
            for (int x = 0; x < binds.size(); x++) {
                var column = binds.get(x);
                var pad = pads.computeIfAbsent(x, i ->
                        Math.min(column.stream()
                                        .max(Comparator.comparingInt(String::length))
                                        .get().length(),
                                MAX_COLUMN_WIDTH));

                String part = "";
                if (column.size() > y)
                    part = column.get(y);

                var formatted = String.format("%-" + pad + "s" , part);
                if (formatted.length() > MAX_COLUMN_WIDTH)
                    formatted = formatted.substring(0, MAX_COLUMN_WIDTH - APPEND_CONT.length()) + APPEND_CONT;
                sb.append(String.format("%s"+ (x == binds.size() - 1 ? "" : " | "), formatted));
            }
            sb.append("\n");
        }
        return sb;
    }

    private static Path tryToGetPath(String path) {
        try {
            return Paths.get(path);
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> List<List<T>> createParts(List<T> keyBindings) {
        final int partSize = MAX_COLUMN_HEIGHT;
        final int maxSize = (keyBindings.size() + partSize - 1) / partSize;
        return IntStream.range(0, maxSize)
                .mapToObj(i -> keyBindings.subList(partSize * i, Math.min(partSize * i + partSize, keyBindings.size())))
                .collect(Collectors.toList());
    }
}
