package org.trimou.example.ping;

import java.util.Date;

/**
 *
 * @author Martin Kouba
 */
public class Ping {

	private Long id;

	private final String remoteAddr;

	private final Date time;

	/**
	 *
	 * @param remoteAddr
	 * @param time
	 */
	public Ping(Long id, String remoteAddr, Date time) {
		super();
		this.id = id;
		this.remoteAddr = remoteAddr;
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public String toString() {
		return String.format("Ping [id=%s, remoteAddr=%s, time=%s]", id,
				remoteAddr, time);
	}

}
