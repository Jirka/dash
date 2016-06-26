package cz.vutbr.fit.dash.util;

import java.io.File;
import java.io.FileFilter;

public class DashboardFileFilter implements FileFilter {

	private static final String[] fileExtensions = { ".bmp", ".gif", ".jpg", ".jpeg", ".png", ".xml" };

	@Override
	public boolean accept(File file) {
		if (file.isFile() && file.canRead()) {
			String name = file.getName().toLowerCase();
			for (String ext : fileExtensions) {
				if (name.endsWith(ext)) {
					return true;
				}
			}
		}
		return false;
	}

}