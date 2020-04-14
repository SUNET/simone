package se.uhr.simone.core.control.extension;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import se.uhr.simone.api.fileloader.FileLoaderDescriptor;

@Dependent
public class ExtensionManager {

	@Inject
	Instance<FileLoaderDescriptor> availableJobDescriptors;

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
