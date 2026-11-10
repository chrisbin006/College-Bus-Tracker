package com.trackmybus.web;

import java.util.*;
import java.util.regex.*;

/** Tiny JSON writer and flat-object reader, so the project needs no external libraries. */
public final class Json {
    private Json() {}

    public static String write(Object o) {
        if (o == null) return "null";
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        if (o instanceof Map<?, ?> m) {
            StringJoiner j = new StringJoiner(",", "{", "}");
            m.forEach((k, v) -> j.add(write(String.valueOf(k)) + ":" + write(v)));
            return j.toString();
        }
        if (o instanceof Iterable<?> it) {
            StringJoiner j = new StringJoiner(",", "[", "]");
            for (Object x : it) j.add(write(x));
            return j.toString();
        }
        if (o instanceof double[] a) { StringJoiner j = new StringJoiner(",", "[", "]"); for (double d : a) j.add("" + d); return j.toString(); }
        String s = o.toString();
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\""); case '\\' -> b.append("\\\\"); case '\n' -> b.append("\\n");
                default -> b.append(c < 32 ? " " : String.valueOf(c));
            }
        }
        return b.append('"').toString();
    }

    private static final Pattern PAIR = Pattern.compile("\"(\\w+)\"\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|-?[0-9.eE+-]+|true|false|null)");

    /** Reads a flat object such as {"a":"x","b":12} into a map of strings. */
    public static Map<String, String> readFlat(String body) {
        Map<String, String> out = new HashMap<>();
        Matcher m = PAIR.matcher(body == null ? "" : body);
        while (m.find()) out.put(m.group(1), m.group(3) != null ? m.group(3).replace("\\\"", "\"").replace("\\\\", "\\") : m.group(2));
        return out;
    }
}
