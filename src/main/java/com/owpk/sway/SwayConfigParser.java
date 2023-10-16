package com.owpk.sway;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface SwayConfigParser {
    void parse(List<String> lines) throws IOException;
}
