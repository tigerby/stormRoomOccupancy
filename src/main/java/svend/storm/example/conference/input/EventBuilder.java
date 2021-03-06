package svend.storm.example.conference.input;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import svend.storm.example.conference.LocationChangedEvent;
import backtype.storm.tuple.Values;

/**
 * Unmarshalls the json String into a {@link LocationChangedEvent} instance and add it to the tuple
 */
public class EventBuilder extends BaseFunction {

	private transient ObjectMapper mapper ;
	
	private static final long serialVersionUID = 1L;

	public void execute(TridentTuple tuple, TridentCollector collector) {
		String jsonEvent = (String) tuple.getValueByField("rawOccupancyEvent");

		if (jsonEvent != null && jsonEvent.length() > 0) {
			try {
				LocationChangedEvent event = getMapper().readValue(jsonEvent, LocationChangedEvent.class);
				collector.emit(new Values(event));
			} catch (IOException e) {
				// parsing error => asking Storm to retry would be pointless
				collector.reportError(e);
			}
		}
	}
	
	
	
	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

}