package com.trackmybus.repo;

import com.trackmybus.user.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Keeps users in memory (collections) and persists them to a tab separated text file. */
public class FileUserRepository implements UserRepository {
    private final Path file;
    private final Map<String, User> users = new LinkedHashMap<>();

    public FileUserRepository(Path file) {
        this.file = file;
        try {
            if (Files.exists(file)) for (String line : Files.readAllLines(file)) {
                String[] p = line.split("\t", -1);
                if (p.length < 7) continue;
                User u = p[0].equals("DRIVER") ? new Driver(p[1], p[2], p[3], p[4], p[5])
                        : new Student(p[1], p[2], p[3], p[4], Integer.parseInt(p[5]), Integer.parseInt(p[6]));
                users.put(u.getUsername(), u);
            }
        } catch (IOException e) { throw new IllegalStateException("Cannot read " + file, e); }
    }
    @Override public synchronized Optional<User> find(String username) { return Optional.ofNullable(users.get(username)); }
    @Override public synchronized void save(User u) {
        users.put(u.getUsername(), u);
        List<String> lines = new ArrayList<>();
        for (User x : users.values()) {
            String[] e = x.extras();
            lines.add(String.join("\t", x.role().name(), x.getUsername(), x.getName(), x.getSalt(), x.getHash(), e[0], e[1]));
        }
        try { Files.createDirectories(file.toAbsolutePath().getParent()); Files.write(file, lines); }
        catch (IOException ex) { throw new IllegalStateException("Cannot write " + file, ex); }
    }
}
