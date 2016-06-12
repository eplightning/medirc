package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.session.repository.JDBCSessionRepository;
import org.eplight.medirc.server.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by EpLightning on 11.06.2016.
 */
public class JDBCSession extends AbstractSession {

    private JDBCSessionRepository repository;

    public JDBCSession(String name, User owner, JDBCSessionRepository repository) {
        this.id = -1;
        this.name = name;
        this.owner = owner;
        this.state = SessionState.SettingUp;

        this.repository = repository;

        this.flags.put(owner.getId(), EnumSet.of(SessionUserFlag.Owner));
    }

    public JDBCSession(ResultSet set, User owner, Map<Integer, EnumSet<SessionUserFlag>> flags,
                       Set<User> participants,
                       JDBCSessionRepository repository) throws SQLException {
        this.owner = owner;
        this.repository = repository;
        this.flags = flags;
        this.participants = participants;

        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.state = SessionState.fromNumber(set.getInt("state"));
        this.autoVoice = set.getInt("auto_voice") == 1;
    }

    @Override
    public void invite(User user) {
        if (getOwner().equals(user))
            return;

        participants.add(user);

        flags.put(user.getId(), EnumSet.of(SessionUserFlag.Invited));

        if (this.id != -1)
            repository.inviteUser(id, user.getId(), EnumSet.of(SessionUserFlag.Invited));
    }

    @Override
    public void kick(User user) {
        if (!participants.contains(user))
            return;

        participants.remove(user);

        if (flags.containsKey(user.getId()))
            flags.remove(user.getId());

        if (this.id != -1)
            repository.kickUser(id, user.getId());
    }

    @Override
    public void setName(String name) {
        this.name = name;

        if (this.id != -1)
            repository.setName(this.id, name);
    }

    @Override
    public void setFlags(User user, EnumSet<SessionUserFlag> flags) {
        if (this.id != -1) {
            if (getOwner().equals(user)) {
                repository.setOwnerFlags(id, flags);
            } else {
                repository.setUserFlags(id, user.getId(), flags);
            }
        }

        this.flags.put(user.getId(), flags);
    }

    @Override
    public void setState(SessionState state) {
        this.state = state;

        if (this.id != -1)
            repository.setState(this.id, state);
    }

    @Override
    public void setAutoVoice(boolean setting) {
        this.autoVoice = setting;

        if (this.id != -1)
            repository.setAutoVoice(this.id, setting);
    }

    public void persisted(int id) {
        this.id = id;
    }
}
