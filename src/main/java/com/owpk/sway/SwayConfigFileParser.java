package com.owpk.sway;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SwayConfigFileParser {
    private final List<String> res = new ArrayList<>();

    public List<String> parsePaths(Path path) throws IOException {
        Files.walkFileTree(replaceEnvVariable(path), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                var nextDir = replaceEnvVariable(dir);
                return super.preVisitDirectory(nextDir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                var lines = Files.readAllLines(replaceEnvVariable(file));
                for (String line : lines) {
                    line = line.strip();
                    if (line.contains(SwayConstants.INCLUDE)) {
                        var nextPath = line.split("\\s+");
                        if (nextPath.length > 1) {
                            var nextPathString = nextPath[1].replaceAll("[*]", "").strip();
                            var pathVar = replaceEnvVariable(Paths.get(nextPathString));
                            try {
                                parsePaths(pathVar);
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    } else
                        res.add(line);
                }
                return super.visitFile(file, attrs);
            }
        });
        return res;
    }

    public List<String> parsePaths(List<String> paths) throws IOException {
        Path tryToParse = null;
        for (String path : paths) {
            if (path != null && !path.isBlank()) {
                tryToParse = replaceEnvVariable(path);
                if (Files.exists(tryToParse))
                    break;
            }
        }
        return parsePaths(tryToParse);
    }

    private Path replaceEnvVariable(Path path) {
        return replaceEnvVariable(path.toString());
    }

    private Path replaceEnvVariable(String path) {
        if (path.contains("$HOME"))
            return Paths.get(path.replaceAll("\\$HOME", System.getenv().get("HOME")));
        return Paths.get(path);
    }

}
