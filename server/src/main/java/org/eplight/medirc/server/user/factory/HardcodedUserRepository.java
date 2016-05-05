package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.AbstractUser;
import org.eplight.medirc.server.user.User;

public class HardcodedUserRepository implements UserRepository {

    @Override
    public User create(int id) {
        if (id == 1)
            return new AbstractUser(1, "Testowy1");

        return new AbstractUser(2, "Testowy2");
    }
}
