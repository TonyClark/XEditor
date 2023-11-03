package console;

import java.util.Arrays;

import repl.CallConsumer;

public class Message {

	private String message;
	private Object[] args;
	private CallConsumer consumer;

	public Message(String message, Object[] args, CallConsumer consumer) {
		super();
		this.message = message;
		this.args = args;
		this.consumer = consumer;
	}

	public String getMessage() {
		return message;
	}

	public Object[] getArgs() {
		return args;
	}

	public CallConsumer getConsumer() {
		return consumer;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + "]";
	}

}
