package svend.storm.example.conference.period;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.state.map.IBackingMap;
import storm.trident.state.map.NonTransactionalMap;
import svend.storm.example.conference.CassandraDB;
import backtype.storm.task.IMetricsContext;

/**
 * retrieves in Cassandra the list of existing {@link RoomPresencePeriod} at the beginning of a batch and updates them at the end of the
 * batch.
 */
public class PeriodBackingMap implements IBackingMap<RoomPresencePeriod> {

	public static StateFactory FACTORY = new StateFactory() {
		public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
			// our logic is fully idempotent => no Opaque map nor Transactional map required here...
			return NonTransactionalMap.build(new PeriodBackingMap());
		}
	};

	public List<RoomPresencePeriod> multiGet(List<List<Object>> keys) {
		return CassandraDB.DB.getPresencePeriods(toCorrelationIdList(keys));
	}

	public void multiPut(List<List<Object>> keys, List<RoomPresencePeriod> newOrUpdatedPeriods) {
		CassandraDB.DB.upsertPeriods(newOrUpdatedPeriods);
	}

	private List<String> toCorrelationIdList(List<List<Object>> keys) {
		List<String> structuredKeys = new LinkedList();
		for (List<Object> key : keys) {
			structuredKeys.add((String) key.get(0));
		}
		return structuredKeys;
	}

}
