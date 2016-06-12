package org.eplight.medirc.server.module.sessionrecorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.FunctionConsumer;
import org.eplight.medirc.server.event.events.session.*;
import org.eplight.medirc.server.image.fragments.RectImageFragment;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.session.SessionKickReason;
import org.eplight.medirc.server.user.Users;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class SessionRecorderModule implements Module {

    private static final Logger logger = LogManager.getLogger(SessionRecorderModule.class);

    @Inject
    private EventLoop loop;

    @Inject
    private Users users;

    @Inject
    private Connection connection;

    private String createTimestamp() {
        LocalDateTime now = LocalDateTime.now();

        return now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void onChangeFlags(ChangeFlagsSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`, `flags`) VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "flags");
            stmt.setInt(4, ev.getUser().getId());
            stmt.setInt(5, SessionUserFlag.toProtobuf(ev.getFlags()));

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onInvite(InviteSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "invite");
            stmt.setInt(4, ev.getInvitedUser().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onJoin(JoinSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "join");
            stmt.setInt(4, ev.getUser().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onKick(KickSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`, `reason`) VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "kick");
            stmt.setInt(4, ev.getKickedUser().getId());
            stmt.setString(5, ev.getReason() == SessionKickReason.Declined ? "decline" : "kick");

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onMessage(MessageSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`, `message`) VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "message");
            stmt.setString(5, ev.getText());


            if (ev.getUser() == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setInt(4, ev.getUser().getId());
            }

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onPart(PartSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `user_id`, `reason`) VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "part");
            stmt.setInt(4, ev.getUser().getId());
            stmt.setString(5, ev.getReason());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onSettings(SettingsSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `name`, `state`, `auto_voice`) VALUES (?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "settings");
            stmt.setString(4, ev.getData().getName());
            stmt.setInt(5, ev.getData().getState().getNumber());
            stmt.setInt(6, ev.getData().getAutoVoice() ? 1 : 0);

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onUploadImage(UploadImageSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "upload-image");
            stmt.setInt(4, ev.getImg().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onRemoveImage(RemoveImageSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "remove-image");
            stmt.setInt(4, ev.getImg().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onTransformImage(TransformImageSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`, `zoom`, `x1`, `y1`)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "transform-image");
            stmt.setInt(4, ev.getImg().getId());
            stmt.setDouble(5, ev.getTransformations().getZoom());
            stmt.setInt(6, ev.getTransformations().getFocusX());
            stmt.setInt(7, ev.getTransformations().getFocusY());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onAddImageFragment(AddImageFragmentSessionEvent ev) {
        if (!(ev.getImgFragment() instanceof RectImageFragment))
            return;

        RectImageFragment rectImageFragment = (RectImageFragment) ev.getImgFragment();

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`, `zoom`, `color_red`, `color_green`, `color_blue`," +
                    " `frag_type`, `x1`, `x2`, `y1`, `y2`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "add-image-fragment");
            stmt.setInt(4, ev.getImg().getId());
            stmt.setDouble(5, rectImageFragment.getZoom());
            stmt.setDouble(6, rectImageFragment.getColor().getR());
            stmt.setDouble(7, rectImageFragment.getColor().getG());
            stmt.setDouble(8, rectImageFragment.getColor().getB());
            stmt.setString(9, "rect");
            stmt.setInt(10, rectImageFragment.getX1());
            stmt.setInt(11, rectImageFragment.getX2());
            stmt.setInt(12, rectImageFragment.getY1());
            stmt.setInt(13, rectImageFragment.getY2());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onFocusImage(FocusImageSessionEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "focus-image");
            stmt.setInt(4, ev.getImg().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void onClearImageFragments(ClearImageFragmentsEvent ev) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO session_events" +
                    " (session_id, `timestamp`, `type`, `image_id`, `user_id`) VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, ev.getSession().getId());
            stmt.setString(2, createTimestamp());
            stmt.setString(3, "clear-image-fragments");
            stmt.setInt(4, ev.getImg().getId());
            stmt.setInt(5, ev.getUser() == null ? 0 : ev.getUser().getId());

            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void start() {
        loop.registerPostConsumer(new FunctionConsumer<>(ChangeFlagsSessionEvent.class, this::onChangeFlags));
        loop.registerPostConsumer(new FunctionConsumer<>(InviteSessionEvent.class, this::onInvite));
        loop.registerPostConsumer(new FunctionConsumer<>(JoinSessionEvent.class, this::onJoin));
        loop.registerPostConsumer(new FunctionConsumer<>(KickSessionEvent.class, this::onKick));
        loop.registerPostConsumer(new FunctionConsumer<>(MessageSessionEvent.class, this::onMessage));
        loop.registerPostConsumer(new FunctionConsumer<>(PartSessionEvent.class, this::onPart));
        loop.registerPostConsumer(new FunctionConsumer<>(SettingsSessionEvent.class, this::onSettings));
        loop.registerPostConsumer(new FunctionConsumer<>(UploadImageSessionEvent.class, this::onUploadImage));
        loop.registerPostConsumer(new FunctionConsumer<>(RemoveImageSessionEvent.class, this::onRemoveImage));
        loop.registerPostConsumer(new FunctionConsumer<>(TransformImageSessionEvent.class, this::onTransformImage));
        loop.registerPostConsumer(new FunctionConsumer<>(AddImageFragmentSessionEvent.class, this::onAddImageFragment));
        loop.registerPostConsumer(new FunctionConsumer<>(FocusImageSessionEvent.class, this::onFocusImage));
        loop.registerPostConsumer(new FunctionConsumer<>(ClearImageFragmentsEvent.class, this::onClearImageFragments));
    }

    @Override
    public void stop() {

    }
}
