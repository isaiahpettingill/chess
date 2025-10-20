package services;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.Repository;
import dto.RegisterPayload;
import models.User;

public final class UserService implements Service {
    private final Repository<User, Integer> _userRepository;

    public UserService(Repository<User, Integer> userRepository) {
        _userRepository = userRepository;
    }

    public boolean isAlreadyTaken(String username) {
        return _userRepository.exists(x -> username.equals(x.username()));
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean validLogin(String username, String password) {
        return _userRepository.exists(x -> x.username().equals(username) 
            && BCrypt.checkpw(password, x.passwordHash()));
    }

    public void saveUser(RegisterPayload user) {
        _userRepository.upsert(
                new User(null,
                        user.username(),
                        hashPassword(user.password()),
                        user.email()));
    }
}
