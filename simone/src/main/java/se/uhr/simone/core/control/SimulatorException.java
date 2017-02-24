package se.uhr.simone.core.control;

public class SimulatorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SimulatorException(String msg, Exception e) {
		super(msg, e);
	}
}
