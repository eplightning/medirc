package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.User;

public class HardcodedUserFactory implements UserFactory {

    @Override
    public User create(int id) {
        if (id == 1)
            return new User(1, "Testowy1");

        return new User(2, "Testowy2");
    }
}
