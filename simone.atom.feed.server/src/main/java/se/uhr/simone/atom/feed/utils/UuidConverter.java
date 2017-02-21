package se.uhr.simone.atom.feed.utils;

import java.util.UUID;

public class UuidConverter {

	private UuidConverter() {
	}

	public static byte[] toByteArray(UUID uuid) {
		byte[] buffer = new byte[16];

		long msb = uuid.getMostSignificantBits();
		for (int n = 0; n < 8; n++) {
			buffer[n] = (byte) (msb >>> (8 * (7 - n)));
		}
		long lsb = uuid.getLeastSignificantBits();
		for (int n = 8; n < 16; n++) {
			buffer[n] = (byte) (lsb >>> (8 * (7 - n)));
		}

		return buffer;
	}

	public static UUID fromByteArray(byte[] bytes) {
		long msb = 0;
		long lsb = 0;
		assert bytes.length == 16;
		for (int i1 = 0; i1 < 8; i1++) {
			msb = (msb << 8) | (bytes[i1] & 0xff);
		}
		for (int i1 = 8; i1 < 16; i1++) {
			lsb = (lsb << 8) | (bytes[i1] & 0xff);
		}

		return new UUID(msb, lsb);
	}

	public static UUID fromHexString(String hexValue) {
		String uuidString = addDashesIfNeeded(hexValue);
		return UUID.fromString(uuidString);
	}

	private static String addDashesIfNeeded(String hexValue) {
		if (hexValue.indexOf('-') < 0) {
			String withoutSpaces = hexValue.replace(" ", "");
			return withoutSpaces.substring(0, 8) + "-" + withoutSpaces.substring(8, 12) + "-" + withoutSpaces.substring(12, 16) + "-"
					+ withoutSpaces.substring(16, 20) + "-" + withoutSpaces.substring(20);
		}

		return hexValue;
	}

}
