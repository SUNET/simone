package se.uhr.nya.integration.sim.server.control.extension;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import se.uhr.nya.integration.sim.extension.api.fileloader.FileLoaderDescriptor;

public class ExtensionManager {

	@Inject
	private Instance<FileLoaderDescriptor> availableJobDescriptors;

	public List<FileLoaderDescriptor> getFileExtensions(String fileName) {
		List<FileLoaderDescriptor> res = new ArrayList<>();

		for (FileLoaderDescriptor desc : availableJobDescriptors) {
			if (desc.accept(fileName)) {
				res.add(desc);
			}
		}

		return res;
	}
}
