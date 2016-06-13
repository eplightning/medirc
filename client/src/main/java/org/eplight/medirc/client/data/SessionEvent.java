package org.eplight.medirc.client.data;

import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionUserFlag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

public class SessionEvent {

    private int id;
    private LocalDateTime timestamp;
    private String type;
    private int imageId;
    private int userId;
    private double zoom;
    private double colorRed;
    private double colorGreen;
    private double colorBlue;
    private String fragType;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private EnumSet<SessionUserFlag> flags;
    private String reason;
    private String message;
    private Main.Session.State state;
    private String name;
    private boolean autoVoice;

    public SessionEvent(int id, String timestamp, String type, int imageId, int userId, double zoom, double colorRed,
                        double colorGreen, double colorBlue, String fragType, int x1, int x2, int y1, int y2, int flags,
                        String reason, String message, int state, String name, int autoVoice) {
        this.id = id;
        this.timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.type = type;
        this.imageId = imageId;
        this.userId = userId;
        this.zoom = zoom;
        this.colorRed = colorRed;
        this.colorGreen = colorGreen;
        this.colorBlue = colorBlue;
        this.fragType = fragType;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.flags = SessionUserFlag.fromProtobuf(flags);
        this.reason = reason;
        this.message = message;
        this.state = state == 0 ? Main.Session.State.SettingUp :
                (state == 1 ? Main.Session.State.Started : Main.Session.State.Finished);
        this.name = name;
        this.autoVoice = autoVoice == 1;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public int getImageId() {
        return imageId;
    }

    public int getUserId() {
        return userId;
    }

    public double getZoom() {
        return zoom;
    }

    public double getColorRed() {
        return colorRed;
    }

    public double getColorGreen() {
        return colorGreen;
    }

    public double getColorBlue() {
        return colorBlue;
    }

    public String getFragType() {
        return fragType;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public EnumSet<SessionUserFlag> getFlags() {
        return flags;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    public Main.Session.State getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public boolean isAutoVoice() {
        return autoVoice;
    }
}
