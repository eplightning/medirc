package org.eplight.medirc.server.session.active;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.repository.SessionRepository;
import org.eplight.medirc.server.user.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActiveSessionsManager {

    private SessionRepository repo;

    private Map<Integer, Session> sessions;

    @Inject
    public ActiveSessionsManager(SessionRepository repo) {
        this.repo = repo;

        sessions = repo.findActive().stream().
                collect(Collectors.toMap(Session::getId, Function.identity()));
    }

    public void addSession(Session session) {
        sessions.put(session.getId(), session);
    }

    public Set<Session> findForUser(User user) {
        return sessions.values().stream()
                .filter(p -> p.getOwner().equals(user) || p.getParticipants().contains(user))
                .collect(Collectors.toSet());
    }

    public Set<Session> findActiveForUser(User user) {
        return sessions.values().stream()
                .filter(a -> a.getActiveUsers().contains(user))
                .collect(Collectors.toSet());
    }

    public Session findById(int id) {
        return sessions.get(id);
    }
}
