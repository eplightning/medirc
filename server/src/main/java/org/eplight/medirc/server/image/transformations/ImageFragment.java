package org.eplight.medirc.server.image.transformations;

import com.google.protobuf.Message;
import org.eplight.medirc.protocol.SessionBasic;

public interface ImageFragment {

    SessionBasic.ImageFragment toProtobuf();
}
