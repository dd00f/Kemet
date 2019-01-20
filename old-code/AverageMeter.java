/*
 */
package kemet.util;

/**
 * AverageMeter
 * 
 * @author Steve McDuff
 */
public class AverageMeter {
	private int count;
	private long sum;

	public AverageMeter() {
		super();
	}

	public void update(long l) {
		count++;
		sum +=l;
	}

	public long getAverage() {
		return sum/count;
	}
}
