package se.uhr.simone.core.feed.control;

import java.io.Serializable;
import java.util.UUID;

/**
 * Wrapper around java UUID.
 * When using uuid for id this should be used instead of java UUID. 
 *
 */
public class UniqueIdentifier implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final UniqueIdentifier ZERO_UNIQUE_IDENTIFIER = UniqueIdentifier.of(new UUID(0, 0));

	private final UUID uuid;

	private UniqueIdentifier(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public static UniqueIdentifier of(UUID uuid) {
		validateNotNull(uuid);
		return new UniqueIdentifier(uuid);
	}

	public static UniqueIdentifier of(byte[] bytes) {
		validateNotNull(bytes);
		return new UniqueIdentifier(UuidConverter.fromByteArray(bytes));
	}

	public static UniqueIdentifier of(String hexString) {
		validateNotNull(hexString);
		return new UniqueIdentifier(UuidConverter.fromHexString(hexString));
	}

	public static UniqueIdentifier randomUniqueIdentifier() {
		return new UniqueIdentifier(UUID.randomUUID());
	}

	public byte[] toByteArray() {
		return UuidConverter.toByteArray(uuid);
	}

	public String getValue() {
		return this.uuid.toString();
	}

	@Override
	public String toString() {
		return this.uuid.toString();
	}

	private static void validateNotNull(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Null is not valid");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
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
		UniqueIdentifier other = (UniqueIdentifier) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	//TODO does it ever make sense to compare UUID:s?
	public int compareTo(UniqueIdentifier uuid2) {
		return uuid.compareTo(uuid2.uuid);
	}

}
