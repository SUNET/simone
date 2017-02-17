package se.uhr.nya.integration.sim.server.control;

public class SimulatorException extends RuntimeException {

	public SimulatorException(String msg, Exception e) {
		super(msg, e);
	}
}
