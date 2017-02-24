package se.uhr.simone.core.admin.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ResponsePath {

	private final ArrayList<String> segments;

	private ResponsePath(String path) {
		this.segments = new ArrayList<>(Arrays.asList(trimPath(path).split("/")));
	}

	public static ResponsePath of(String path) {
		return new ResponsePath(path);
	}

	@Override
	public int hashCode() {
		return segments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ResponsePath other = (ResponsePath) obj;
		if (segments == null) {
			if (other.segments != null) {
				return false;
			}
		} else if (!segments.equals(other.segments)) {
			return false;
		}
		return true;
	}

	private String trimPath(String path) {
		String tmp = path.trim();

		int len = tmp.length();
		int st = 0;

		while ((st < tmp.length()) && (tmp.charAt(st) == '/')) {
			st++;
		}

		while ((st < len) && (tmp.charAt(len - 1) == '/')) {
			len--;
		}

		return ((st > 0) || (len < tmp.length())) ? tmp.substring(st, len) : tmp;
	}

	@Override
	public String toString() {
		Iterator<String> it = segments.iterator();
		StringBuilder sb = new StringBuilder();

		while (it.hasNext()) {
			sb.append(it.next());

			if (it.hasNext()) {
				sb.append('/');
			}
		}

		return sb.toString();
	}
}
