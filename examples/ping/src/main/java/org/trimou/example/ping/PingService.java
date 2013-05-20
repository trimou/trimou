package org.trimou.example.ping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Kouba
 */
@Named("pingService")
@ApplicationScoped
public class PingService {

	private static final Logger logger = LoggerFactory
			.getLogger(PingService.class);

	private AtomicLong generator = new AtomicLong(System.currentTimeMillis());

	// Simple in-memory task store
	private ConcurrentMap<Long, Ping> pings = new ConcurrentHashMap<Long, Ping>();

	private Comparator<Ping> pingComparator = new Comparator<Ping>() {

		@Override
		public int compare(Ping o1, Ping o2) {
			return o1.getTime().compareTo(o2.getTime());
		}
	};

	/**
	 *
	 * @param remoteAddr
	 */
	public void ping(String remoteAddr) {
		Ping ping = new Ping(generator.incrementAndGet(), remoteAddr,
				new Date());
		pings.put(ping.getId(), ping);
		logger.info("{}", ping);
	}

	/**
	 *
	 * @return
	 */
	public List<Ping> getPings() {
		List<Ping> result = new ArrayList<Ping>();
		for (Ping task : pings.values()) {
			result.add(task);
		}
		Collections.sort(result, pingComparator);
		return result;
	}

	/**
	 *
	 * @return
	 */
	public Ping getPing(Long id) {
		return pings.get(id);
	}

}
