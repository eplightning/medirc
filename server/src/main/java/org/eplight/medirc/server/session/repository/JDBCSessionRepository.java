package org.eplight.medirc.server.session.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.session.JDBCSession;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.SessionState;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.factory.UserRepository;

import javax.inject.Inject;
import java.sql.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by EpLightning on 11.06.2016.
 */
public class JDBCSessionRepository implements SessionRepository {

    private static final Logger logger = LogManager.getLogger(JDBCSessionRepository.class);

    @Inject
    private Connection connection;

    @Inject
    private UserRepository userRepository;

    @Override
    public Session create(String name, User owner) {
        return new JDBCSession(name, owner, this);
    }

    @Override
    public Set<Session> findActive() {
        try {
            ResultSet st = connection.createStatement().executeQuery("SELECT * FROM sessions WHERE `state` < 2");

            return findFromResultSet(st);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return new HashSet<>();
    }

    @Override
    public Set<Session> findArchived() {
        try {
            ResultSet st = connection.createStatement().executeQuery("SELECT * FROM sessions WHERE `state` = 2");

            return findFromResultSet(st);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return new HashSet<>();
    }

    @Override
    public void persist(Session session) throws SessionRepositoryException {
        if (session instanceof JDBCSession) {
            JDBCSession jdbcSession = (JDBCSession) session;

            if (jdbcSession.getId() != -1)
                return;

            try {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO sessions (owner_id, owner_flags, " +
                        "name, state, auto_voice) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                stmt.setInt(1, jdbcSession.getOwner().getId());
                stmt.setInt(2, SessionUserFlag.toProtobuf(jdbcSession.getFlags(jdbcSession.getOwner())));
                stmt.setString(3, jdbcSession.getName());
                stmt.setInt(4, jdbcSession.getState().getNumber());
                stmt.setInt(5, jdbcSession.getAutoVoice() ? 1 : 0);

                stmt.execute();

                // TODO: Persist users


                ResultSet generatedKeys = stmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    jdbcSession.persisted(generatedKeys.getInt(1));
                } else {
                    throw new SessionRepositoryException("Persist couldn't retrieve generated key");
                }
            } catch (SQLException e) {
                throw new SessionRepositoryException(e.getMessage());
            }
        }
    }

    private Set<Session> findFromResultSet(ResultSet set) {
        Set<Session> output = new HashSet<>();

        try {
            while (set.next()) {
                User owner = userRepository.findById(set.getInt("owner_id"));

                if (owner == null)
                    continue; // tood: log it

                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM session_users WHERE session_id = ?");
                stmt.setInt(1, set.getInt("id"));

                ResultSet users = stmt.executeQuery();

                HashSet<User> userSet = new HashSet<>();
                HashMap<Integer, EnumSet<SessionUserFlag>> flags = new HashMap<>();

                flags.put(owner.getId(), SessionUserFlag.fromProtobuf(set.getInt("owner_flags")));

                while (users.next()) {
                    userSet.add(userRepository.findById(users.getInt("user_id")));
                    flags.put(users.getInt("user_id"), SessionUserFlag.fromProtobuf(users.getInt("flags")));
                }

                output.add(new JDBCSession(set, owner, flags, userSet, this));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return output;
    }

    public void setName(int id, String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE sessions SET name = ? WHERE id = ?");

            stmt.setString(1, name);
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void setAutoVoice(int id, boolean autoVoice) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE sessions SET auto_voice = ? WHERE id = ?");

            stmt.setInt(1, autoVoice ? 1 : 0);
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void setState(int id, SessionState state) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE sessions SET `state` = ? WHERE id = ?");

            stmt.setInt(1, state.getNumber());
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void setOwnerFlags(int id, EnumSet<SessionUserFlag> flags) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE sessions SET `owner_flags` = ? WHERE id = ?");

            stmt.setInt(1, SessionUserFlag.toProtobuf(flags));
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void setUserFlags(int id, int userId, EnumSet<SessionUserFlag> flags) {
        try {
            PreparedStatement select = connection.prepareStatement("SELECT * FROM session_users WHERE session_id = ?" +
                    " AND user_id = ?");

            select.setInt(1, id);
            select.setInt(2, userId);

            ResultSet found = select.executeQuery();

            PreparedStatement stmt;

            if (found.next()) {
                stmt = connection.prepareStatement("UPDATE session_users SET `flags` = ? WHERE session_id = ?" +
                        " AND user_id = ?");

                stmt.setInt(1, SessionUserFlag.toProtobuf(flags));
                stmt.setInt(2, id);
                stmt.setInt(3, userId);

                stmt.execute();
            } else {
                throw new RuntimeException("Invalid call to setUserFlags");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void kickUser(int id, int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM session_users WHERE session_id = ? AND " +
                    "user_id = ?");

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void inviteUser(int id, int userId, EnumSet<SessionUserFlag> flags) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_users (user_id, session_id, flags) " +
                    "VALUES (?, ?, ?)");

            stmt.setInt(1, userId);
            stmt.setInt(2, id);
            stmt.setInt(3, SessionUserFlag.toProtobuf(flags));

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
