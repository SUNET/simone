package se.uhr.simone.core.control.mbean;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class AbstractMBean {

	private final String domain;
	private final String name;

	private MBeanServer mbeanServer;
	private ObjectName objectName = null;

	public AbstractMBean(String domain) {
		super();
		this.domain = domain;
		this.name = this.getClass().getSimpleName();
	}

	@PostConstruct
	protected void startup() {

		try {
			objectName = new ObjectName(domain, "type", name);
			mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(this, objectName);
		} catch (Exception e) {
			throw new IllegalStateException("Error during registration of " + name + " into JMX:" + e, e);
		}
	}

	@PreDestroy
	protected void destroy() {
		try {
			mbeanServer.unregisterMBean(this.objectName);
		} catch (Exception e) {
			throw new IllegalStateException("Error during unregistration of " + name + " into JMX:" + e, e);
		}
	}
}
