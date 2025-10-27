package service;

import java.sql.SQLException;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.Repository;
import dto.RegisterPayload;
import models.User;

public final class UserService implements Service {
    private final Repository<User, Integer> userRepository;

    public UserService(Repository<User, Integer> userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAlreadyTaken(String username) throws DataAccessException, SQLException {
        return this.userRepository.exists(x -> username.equals(x.username()));
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean validLogin(String username, String password) throws DataAccessException, SQLException {
        return this.userRepository.exists(x -> x.username().equals(username)
                && BCrypt.checkpw(password, x.passwordHash()));
    }

    public Optional<User> saveUser(RegisterPayload user) throws DataAccessException, SQLException {
        if (isAlreadyTaken(user.username())) {
            return Optional.empty();
        }
        return Optional.of(this.userRepository.upsert(
                new User(null,
                        user.username(),
                        hashPassword(user.password()),
                        user.email())));
    }
}
