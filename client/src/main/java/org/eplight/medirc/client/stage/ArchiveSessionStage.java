package org.eplight.medirc.client.stage;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.eplight.medirc.client.data.AllowedActions;
import org.eplight.medirc.client.data.SessionEvent;
import org.eplight.medirc.client.data.SessionImage;
import org.eplight.medirc.client.data.SessionUser;
import org.eplight.medirc.client.image.ImageFragment;
import org.eplight.medirc.client.image.RectImageFragment;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionEvents;
import org.eplight.medirc.protocol.SessionRequests;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Consumer;

public class ArchiveSessionStage extends AbstractSessionStage {

    private Connection connection;

    private HashMap<Integer, String> users = new HashMap<>();
    private HashMap<Integer, SessionImage> images = new HashMap<>();
    private ArrayList<SessionEvent> events = new ArrayList<>();

    private TimelineStage timelineStage;

    private ListIterator<SessionEvent> eventIterator;

    private Timeline timeline;

    private int totalSeconds;
    private LocalDateTime startDate;

    private LocalDateTime currentDate;
    private Main.Session.State currentState;
    private HashMap<Integer, List<ImageFragment>> currentFragments = new HashMap<>();

    public ArchiveSessionStage(Consumer<AbstractSessionStage> onCloseRun, Basic.HandshakeAck ack, File dbFile) {
        super(onCloseRun, ack);

        try {
            connect(dbFile.getAbsolutePath());
            fetchUsers();
            fetchImages();
            fetchEvents();
            this.connection.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd podczas czytania pliku");
            alert.setContentText(e.getMessage());
            alert.showAndWait();

            this.close();
            this.onCloseRun.accept(this);
        }

        setupWindow("Nagrana sesja", ack.getName());
        setAllowedActions(EnumSet.noneOf(AllowedActions.class));
        disableUserInput(false);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timelineStage.valueProperty().set(timelineStage.valueProperty().get() + 1);
            }
        }));

        startDate = events.get(0).getTimestamp();

        totalSeconds = 0;

        if (events.size() > 1) {
            LocalDateTime last = events.get(events.size() - 1).getTimestamp();

            totalSeconds = (int) ChronoUnit.SECONDS.between(startDate, last);
        }

        totalSeconds += 1;

        show();

        timelineStage = new TimelineStage(this, totalSeconds, new Runnable() {
            @Override
            public void run() {
                onPlay();
            }
        });

        timelineStage.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    advance(newValue.intValue() - oldValue.intValue());
                } else {
                    reset();
                    advance(newValue.intValue());
                }
            }
        });

        timelineStage.show();

        reset();
    }

    private void advance(int seconds) {
        currentDate = currentDate.plusSeconds(seconds);

        while (eventIterator.hasNext()) {
            SessionEvent e = eventIterator.next();

            if (e.getTimestamp().isBefore(currentDate)) {
                handleEvent(e);
            } else {
                eventIterator.previous();
                break;
            }
        }
    }

    private void reset() {
        currentFragments = new HashMap<>();
        currentDate = startDate;
        currentState = Main.Session.State.SettingUp;
        eventIterator = events.listIterator();
        timeline.setCycleCount(totalSeconds);
        timeline.playFromStart();
    }

    private void onPlay() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
        } else {
            timeline.play();
        }
    }

    private String getUserName(int id) {
        if (users.containsKey(id))
            return users.get(id);

        return "Nieznany użytkownik";
    }

    private SessionUser getUser(int id) {
        return new SessionUser(id, getUserName(id));
    }

    private void handleEvent(SessionEvent e) {
        SessionImage img;

        switch (e.getType()) {
            case "join":
                joinUser(getUser(e.getUserId()));
                break;

            case "flags":
                updateUser(new SessionUser(e.getUserId(), getUserName(e.getUserId()), e.getFlags()));
                break;

            case "invite":
                inviteUser(getUser(e.getUserId()));
                break;

            case "kick":
                kickUser(getUser(e.getUserId()), e.getReason().equals("declined") ?
                        SessionEvents.Kicked.Reason.Declined : SessionEvents.Kicked.Reason.Kick);
                break;

            case "message":
                if (e.getUserId() > 0)
                    addMessage(getUser(e.getUserId()), e.getMessage());

                break;

            case "part":
                partUser(getUser(e.getUserId()));
                break;

            case "settings":
                if (e.getState() != currentState) {
                    setStateText(getStateButtonText(e.getState()));
                    setState(e.getState());
                }

                // TODO: auto voice
                // TODO: name
                break;

            case "upload-image":
                img = images.get(e.getImageId());

                if (img != null) {
                    addImageInfo(img.getName());
                    addImage(img);
                }

                break;

            case "remove-image":
                img = images.get(e.getImageId());

                if (img != null)
                    removeImage(img);

                break;

            case "transform-image":
                img = images.get(e.getImageId());

                if (img != null) {
                    updateImageTransform(img.getId(), e.getZoom(), e.getX1(), e.getY1());
                }

                break;

            case "add-image-fragment":
                img = images.get(e.getImageId());

                if (!e.getFragType().equals("rect"))
                    break;

                if (img != null) {
                    Color c = Color.color(e.getColorRed(), e.getColorGreen(), e.getColorBlue(), 1.0);

                    RectImageFragment f = new RectImageFragment(new Point2D(e.getX1(), e.getY1()),
                            new Point2D(e.getX2(), e.getY2()), e.getZoom(), c);

                    if (!currentFragments.containsKey(img.getId()))
                        currentFragments.put(img.getId(), new ArrayList<>());

                    currentFragments.get(img.getId()).add(f);

                    updateImageFragments(img.getId(), currentFragments.get(img.getId()));
                }

                break;

            case "clear-image-fragments":
                img = images.get(e.getImageId());

                if (img != null) {
                    if (!currentFragments.containsKey(img.getId()))
                        break;

                    List<ImageFragment> fragments = currentFragments.get(img.getId());

                    if (e.getUserId() == 0) {
                        fragments.clear();
                    } else {
                        fragments.removeIf(a -> a.getUserId() == e.getUserId());
                    }

                    updateImageFragments(img.getId(), currentFragments.get(img.getId()));
                }
                break;

            case "focus-image":
                img = images.get(e.getImageId());

                if (img != null) {
                    focusImage(img.getId());
                }

                break;
        }
    }

    private void connect(String path) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        this.connection = DriverManager.getConnection("jdbc:sqlite:"+path);
    }

    private void fetchEvents() throws SQLException {
        ResultSet set = connection.createStatement().executeQuery("SELECT * FROM session_events");

        while (set.next()) {
            SessionEvent ev = new SessionEvent(set.getInt("id"), set.getString("timestamp"), set.getString("type"),
                    set.getInt("image_id"), set.getInt("user_id"), set.getDouble("zoom"), set.getDouble("color_red"),
                    set.getDouble("color_green"), set.getDouble("color_blue"), set.getString("frag_type"),
                    set.getInt("x1"), set.getInt("x2"), set.getInt("y1"), set.getInt("y2"), set.getInt("flags"),
                    set.getString("reason"), set.getString("message"), set.getInt("state"), set.getString("name"),
                    set.getInt("auto_voice"));

            events.add(ev);
        }
    }

    private void fetchImages() throws SQLException {
        ResultSet set = connection.createStatement().executeQuery("SELECT * FROM images");

        while (set.next()) {
            SessionImage img = new SessionImage(set.getInt("id"), set.getBytes("data"), set.getString("name"),
                    Color.color(set.getDouble("color_red"), set.getDouble("color_green"), set.getDouble("color_blue")));

            images.put(img.getId(), img);
        }
    }

    private void fetchUsers() throws SQLException {
        ResultSet set = connection.createStatement().executeQuery("SELECT * FROM users");

        while (set.next()) {
            users.put(set.getInt("id"), set.getString("name"));
        }
    }

    @Override
    protected void onCloseRequest(WindowEvent event) {
        timeline.stop();

        super.onCloseRequest(event);
    }
}
