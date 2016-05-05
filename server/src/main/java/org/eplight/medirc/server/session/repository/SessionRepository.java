package org.eplight.medirc.server.session.repository;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;

import java.util.Set;

public interface SessionRepository {

    Session create(String name, User owner);

    Set<Session> findActive();

    Set<Session> findArchived();

    void persist(Session session) throws SessionRepositoryException;
}
