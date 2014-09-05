package net.stratfordpark.pco;

import java.util.Comparator;

/**
 *
 */
class ServiceDataComparator implements Comparator<ServiceData> {
	@Override
	public int compare( ServiceData o1, ServiceData o2 ) {
		long t1 = o1.getStartDate().getTime();
		long t2 = o2.getStartDate().getTime();

		return Long.compare( t1, t2 );
	}
}
