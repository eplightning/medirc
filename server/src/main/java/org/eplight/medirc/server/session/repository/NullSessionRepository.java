package org.eplight.medirc.server.session.repository;

import org.eplight.medirc.server.session.MemorySession;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.factory.UserRepository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class NullSessionRepository implements SessionRepository {

    @Inject
    private UserRepository userRepository;

    @Override
    public Session create(String name, User owner) {
        return new MemorySession(name, owner);
    }

    @Override
    public Set<Session> findActive() {
        HashSet<Session> sessions = new HashSet<>();

        sessions.add(create("Testowa sesja", userRepository.create(1)));
        sessions.add(create("Testowa sesja 2", userRepository.create(2)));

        return sessions;
    }

    @Override
    public Set<Session> findArchived() {
        // TODO: Some default sessions
        return new HashSet<>();
    }

    @Override
    public void persist(Session session) {

    }
}
