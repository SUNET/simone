package se.uhr.simone.core.feed.control;

public class Person {

	private String name;

	private Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Person of(String name) {
		return new Person(name);
	}

}
