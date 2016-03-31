package org.eplight.medirc.server.event.events;

import org.eplight.medirc.server.network.ServerType;

/**
 * Created by EpLightning on 30.03.2016.
 */
public interface ServerEvent extends Event {

    public ServerType getType();
}
