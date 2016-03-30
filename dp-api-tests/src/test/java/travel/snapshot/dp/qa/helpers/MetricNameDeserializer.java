package travel.snapshot.dp.qa.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import travel.snapshot.dp.api.analytics.model.DefaultMetricName;
import travel.snapshot.dp.api.analytics.model.MetricName;

import java.io.IOException;

public class MetricNameDeserializer extends StdScalarDeserializer<MetricName> {

    private static final long serialVersionUID = 1L;

    public MetricNameDeserializer() {
        super(MetricName.class);
    }

    @Override
    public MetricName deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return new DefaultMetricName(parser.getCodec().readValue(parser, String.class));
    }

}
