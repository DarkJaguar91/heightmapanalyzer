package Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Vector;

/**
 * 
 * @author Brandon James Talbot
 *
 * This class loads the image
 * This dynamically loads in a height map file to a set type
 * <int x> <int y> <height>
 * It ignores non int values on a line eg. AAA A
 * it ignores extra spaces eg: __<int>_<int>_<int>____
 * it sorts and creates the height maps.
 *
 */

public class ImageLoader {
	private static BufferedReader reader;

	/**
	 * Loads a height map from a given file
	 * @param file The file to load
	 * @return the height map in the file
	 */
	public static HeightMap loadHeightMap(File file) {
		try {
			reader = new BufferedReader(new FileReader(
					file.getPath()));

			String line = "";
			Vector<xValue> sorter = new Vector<xValue>();

			// load the data
			while ((line = reader.readLine()) != null) {
				String[] array = line.trim().split("\\s+");

				if (array.length == 3) {
					xValue x = new xValue();
					yValue y = new yValue();

					x.value = Double.parseDouble(array[0]);
					y.value = Double.parseDouble(array[1]);
					y.height = Double.parseDouble(array[2]);
					if (sorter.contains(x)) {
						sorter.get(sorter.indexOf(x)).yPoints.add(y);
					} else {
						x.yPoints.add(y);
						sorter.add(x);
					}
				}
			}

			// sort all data for x
			Collections.sort(sorter);

			double[][] data = new double[sorter.size()][sorter.elementAt(0).yPoints
					.size()];
			double minH = 10000, maxH = -10000;

			for (int x = 0; x < sorter.size(); ++x) {
				//sort each x's y values
				xValue xval = sorter.elementAt(x);
				Collections.sort(xval.yPoints);

				// get the data
				for (int y = 0; y < xval.yPoints.size(); ++y) {
					yValue yval = xval.yPoints.elementAt(y);

					minH = yval.height < minH ? yval.height : minH;
					maxH = yval.height > maxH ? yval.height : maxH;

					data[x][y] = yval.height;
				}
			}

			// create the height map
			HeightMap map = new HeightMap(
					file.getName(),
					data,
					maxH,
					minH,
					sorter.elementAt(0).value,
					sorter.elementAt(sorter.size() - 1).value,
					sorter.elementAt(0).yPoints.elementAt(0).value,
					sorter.elementAt(0).yPoints.elementAt(sorter.elementAt(0).yPoints
							.size() - 1).value, sorter.elementAt(1).value
							- sorter.elementAt(0).value);

			return map;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}
}

/**
 * 
 * @author Brandon James Talbot
 *
 * The ability to compare x values
 * each x holds many y values
 */
class xValue implements Comparable<Object> {
	public double value;

	public Vector<yValue> yPoints;

	public xValue() {
		value = 0;
		yPoints = new Vector<yValue>();
	}

	public int compareTo(Object o) {
		if (o instanceof xValue) {
			xValue other = (xValue) o;
			return this.value < other.value ? -1 : this.value > other.value ? 1
					: 0;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof xValue) {
			return this.value == ((xValue) obj).value ? true : false;
		}
		return false;
	}
}

/**
 * 
 * @author Brandon James Talbot
 *
 * The ability to sort y values
 * Each y holds a single height value
 */
class yValue implements Comparable<Object> {
	public double value, height;

	public int compareTo(Object o) {
		if (o instanceof yValue) {
			yValue other = (yValue) o;
			return this.value < other.value ? -1 : this.value > other.value ? 1
					: 0;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof yValue) {
			return this.value == ((yValue) obj).value ? true : false;
		}
		return false;
	}
}