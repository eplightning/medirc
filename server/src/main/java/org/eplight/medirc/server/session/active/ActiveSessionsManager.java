package org.eplight.medirc.server.session.active;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.repository.SessionRepository;
import org.eplight.medirc.server.user.User;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class ActiveSessionsManager {

    private SessionRepository repo;

    private Set<Session> sessions;

    @Inject
    public ActiveSessionsManager(SessionRepository repo) {
        this.repo = repo;
        sessions = repo.findActive();
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public Set<Session> findForUser(User user) {
        return sessions.stream()
                .filter(p -> p.getOwner().equals(user) || p.getParticipants().contains(user))
                .collect(Collectors.toSet());
    }
}
