package se.uhr.simone.core.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SqlScriptReader {

	private final InputStream is;

	public SqlScriptReader(InputStream is) {
		this.is = is;
	}

	public List<String> getStatements() {
		List<String> res = new ArrayList<>();
		Scanner scanner = new Scanner(is);
		try {
			scanner.useDelimiter(";");
			while (scanner.hasNext()) {
				String line = scanner.next();
				if (line.trim().length() > 0) {
					res.add(line);
				}
			}
		} finally {
			scanner.close();
		}

		return res;
	}
}
