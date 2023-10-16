package com.owpk.sway;

import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigParserImpl implements SwayConfigParser {
    @Getter
    private final List<SwayKeyBinding> binds = new ArrayList<>();
    private final Map<String, String> vars = new HashMap<>();
    @Override
    public void parse(List<String> lines) {
        var filtered = lines.stream().map(String::strip).filter(l -> !l.startsWith("#")).collect(Collectors.toList());
        var modVar = findModVariableAndFillOthers(filtered);
        var bindsymBlock = false;
        for (String line : filtered) {
            if (line.contains("}"))
                bindsymBlock = false;

            if (bindsymBlock && line.contains(modVar.name()))
                binds.add(createBind(modVar, line));

            if (line.contains(SwayConstants.KEY_BIND) && !line.contains("{")) {
                if (line.contains(modVar.name()))
                    binds.add(createBind(modVar, line.substring(line.indexOf(modVar.name()))));
            } else if (line.contains(SwayConstants.KEY_BIND) && line.contains("{")) {
                bindsymBlock = true;
            }
        }
    }

    private SwayKeyBinding createBind(SwayVariable modVar, String line) {
        var split = splitLine(line);
        var bind = split.get(0);
        bind = bind.substring(bind.indexOf("+") + 1);
        bind = Arrays.stream(bind.split("\\+"))
                .map(this::searchVariableRecursively)
                .collect(Collectors.joining(" "));
        split.entrySet().forEach(e -> e.setValue(searchVariableRecursively(e.getValue())));
        var command = split.values().stream().skip(1).collect(Collectors.joining(" "));
        return new SwayKeyBinding(modVar, bind, command);
    }

    private String searchVariableRecursively(String varName) {
        var val = vars.get(varName);
        if (val != null) {
            if (val.startsWith("$"))
                return searchVariableRecursively(val);
            return val;
        }
        return varName;
    }

    private SwayVariable findModVariableAndFillOthers(List<String> lines) {
        SwayVariable modVar = null;
        var notResolved = new HashMap<String, Map<Integer, String>>();
        for (String line : lines) {
            if (line.startsWith(SwayConstants.SET)) {
                var split = splitLine(line);
                var name = split.get(1);
                var command = split.values().stream().skip(2)
                        .collect(Collectors.joining(" "));
                if (command.contains(SwayConstants.MOD)) {
                    modVar = new SwayVariable(name, command, SwayConstants.modIcons.get(command));
                }
                notResolved.put(name, new HashMap<>(split.entrySet().stream().skip(2)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
            }
        }

        var resolved = notResolved.entrySet().stream().peek((e) ->
                e.getValue().forEach((idx, cmd) -> {
                    var def = notResolved.get(cmd);
                    if (def != null)
                        e.getValue().put(idx, String.join(" ", def.values()));
                })).collect(Collectors.toMap(Map.Entry::getKey,
                e -> String.join(" ", e.getValue().values())));

        vars.putAll(resolved);
        return modVar;
    }

//                    if (composed.length() > MAX_COLUMN_SIZE) {
//        composed = composed.substring(0, MAX_COLUMN_SIZE) + "..."l
//    }

    private Map<Integer, String> splitLine(String line) {
        var map = new HashMap<Integer, String>();
        var split = line.split("\\s+");
        IntStream.range(0, split.length).forEach(i -> map.put(i, split[i]));
        return map;
    }

}
