package com.trackmybus.repo;

import com.trackmybus.user.User;
import java.util.Optional;

/** Storage abstraction: the service does not care whether users live in a file or a database. */
public interface UserRepository {
    Optional<User> find(String username);
    void save(User user);
}
