package repl;

public interface CallConsumer {

	public default void consume(Object threadOwner, Object value) {
		synchronized(threadOwner) {
			consume(value);
			threadOwner.notifyAll();
		}
	}

	public void consume(Object value);

}
