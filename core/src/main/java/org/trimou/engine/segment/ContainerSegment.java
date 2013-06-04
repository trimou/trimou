package org.trimou.engine.segment;

import java.util.List;

import org.trimou.annotations.Internal;

/**
 * Segment which contains other segments.
 *
 * @author Martin Kouba
 */
@Internal
public interface ContainerSegment extends Segment, Iterable<Segment> {

	/**
	 *
	 * @param segment
	 */
	public void addSegment(Segment segment);

	/**
	 *
	 * @return the list of contained segments
	 */
	public List<Segment> getSegments();

	/**
	 *
	 * @return the number of contained segments
	 */
	public int getSegmentsSize();

}
