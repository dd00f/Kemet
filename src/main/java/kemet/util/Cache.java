package kemet.util;

import java.util.ArrayList;
import java.util.List;

import kemet.Options;

public class Cache<T> {

	private List<T> cache = new ArrayList<>();

	private Creator<T> creator;

	private long createNewCount = 0;
	private long createCount = 0;
	private long reuseCount = 0;
	private long releaseCount = 0;
	private String className;
	public Cache(Creator<T> creator) {
		
		this.creator = creator;

		T create = create();
		className = create.getClass().getName();
		release(create);
	}

	public T create() {
		createCount++;
		if( Options.USE_CACHE ) {
			if (! cache.isEmpty()) {
				reuseCount++;
				return cache.remove(cache.size() - 1);
			}
		}
		createNewCount++;
		if (Options.PRINT_CREATION_COUNT && createNewCount % Options.CREATION_PRINT_COUNT == 0) {
			System.out.println("Cache created " + createNewCount + " of " + className + ", released " + releaseCount
					+ " reused " + reuseCount + " of " + createCount + " calls");
		}		
		return creator.create();
	}

	public void release(T release) {

		if( Options.USE_CACHE ) {
			cache.add(release);
			releaseCount++;
		}
	}

}
