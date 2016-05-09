package org.eplight.medirc.client.instance.network.dispatcher;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.eplight.medirc.protocol.SessionResponses;

import java.util.Map;

/**
 * Created by EpLightning on 09.05.2016.
 */
public class GenericStatusDispatchFunction implements DispatchFunction<Message> {

    private DispatchFunction<SessionResponses.GenericResponse> dispatchFunction;

    public GenericStatusDispatchFunction(DispatchFunction<SessionResponses.GenericResponse> dispatchFunction) {
        this.dispatchFunction = dispatchFunction;
    }

    @Override
    public void handle(Message msg) {
        Map<Descriptors.FieldDescriptor,Object> fields = msg.getAllFields();

        for (Map.Entry<Descriptors.FieldDescriptor,Object> e : fields.entrySet()) {
            if (e.getValue() instanceof SessionResponses.GenericResponse)
                dispatchFunction.handle((SessionResponses.GenericResponse) e.getValue());

            break;
        }
    }
}
