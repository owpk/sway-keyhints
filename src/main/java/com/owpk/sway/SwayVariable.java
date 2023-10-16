package com.owpk.sway;

public record SwayVariable(String name, String command, String icon) {
    public SwayVariable(String name, String command) {
        this(name, command, null);
    }
}
