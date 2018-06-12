package cz.vutbr.fit.dashapp.segmenation.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class BottomUpUtil {

	/**
	 * Method sets matrix to given values.
	 * 
	 * @param matrix
	 * @param value
	 */
	public static void setArray(int[][] matrix, int value) {
		int w = MatrixUtils.width(matrix);
		for (int i = 0; i < w; i++)
			Arrays.fill(matrix[i], value);
	}
	
	/**
	 * Method calculates the % of black pixels in matrix.
	 * 
	 * @param matrix
	 * @param w
	 * @param h
	 */
	public static float averageMatrix(int[][] matrix, int w, int h) {
		float avg = 0;
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (matrix[i][j] == 0)
					avg++;
		avg = avg / (w * h);
		return avg;
	}
	
	/**
	 * Method compare two matrices if they are different.
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @param w
	 * @param h
	 */	
	public static boolean differentMatrices(int[][] matrix1, int[][] matrix2, int w, int h) {
		boolean differ = false;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (matrix1[i][j] != matrix2[i][j]) {
					differ = true;
					break;
				}
			}
		}
		return differ;
	}
	
	/**
	 * Method creates list of potentially joinable regions beneath each other and according
	 * to the calculated threshold joins them to one region or leaves them separated.
	 * @param matrix
	 * @param w
	 * @param h
	 */	
	public static int[][] joinColumns(int[][] matrix, int w, int h) {
		List<Region> regions = getRegions(matrix, w, h);
		List<List<Region>> regionDoubleListTop = new ArrayList<List<Region>>();
		List<List<Region>> regionDoubleListBottom = new ArrayList<List<Region>>();

		regionDoubleListTop.add(new ArrayList<>());
		regionDoubleListTop.get(0).add(regions.get(0));
		regionDoubleListBottom.add(new ArrayList<>());
		regionDoubleListBottom.get(0).add(regions.get(0));

		for (int i = 1; i < regions.size(); i++) {
			boolean added = false;
			for (int j = 0; j < regionDoubleListTop.size(); j++) {
				List<Region> currentRegionList = regionDoubleListTop.get(j);
				if ((Math.abs(regions.get(i).x - currentRegionList.get(0).x) < 5)) {
					currentRegionList.add(new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width,
							regions.get(i).height, 0));
					added = true;
					break;
				}
			}
			if (!added) {
				regionDoubleListTop.add(new ArrayList<>());
				regionDoubleListTop.get(regionDoubleListTop.size() - 1).add(
						new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width, regions.get(i).height, 0));

			}
		}

		for (int i = 1; i < regions.size(); i++) {
			boolean added = false;
			for (int j = 0; j < regionDoubleListBottom.size(); j++) {
				List<Region> currentRegionList = regionDoubleListBottom.get(j);
				if ((Math.abs((regions.get(i).x + regions.get(i).width)
						- (currentRegionList.get(0).x + currentRegionList.get(0).width)) < 5)) {
					currentRegionList.add(new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width,
							regions.get(i).height, 0));
					added = true;
					break;
				}
			}
			if (!added) {
				regionDoubleListBottom.add(new ArrayList<>());
				regionDoubleListBottom.get(regionDoubleListBottom.size() - 1).add(
						new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width, regions.get(i).height, 0));

			}
		}

		regionDoubleListTop = sortDoubleList(regionDoubleListTop, 'v');
		regionDoubleListBottom = sortDoubleList(regionDoubleListBottom, 'v');

		int limitTop = getLimit(regionDoubleListTop, 'v', w, h);
		int limitBottom = getLimit(regionDoubleListBottom, 'v', w, h);

		List<Region> tmpRegionListTop = reDrawRowsOrColumns(regionDoubleListTop, limitTop, 'v', w, h);
		List<Region> tmpRegionListBottom = reDrawRowsOrColumns(regionDoubleListBottom, limitBottom, 'v', w, h);

		for (int x = 0; x < tmpRegionListTop.size(); x++)
			for (int i = 0; i < tmpRegionListTop.get(x).getWidth(); i++) {
				for (int j = 0; j < tmpRegionListTop.get(x).getHeight(); j++) {
					matrix[tmpRegionListTop.get(x).x + i][tmpRegionListTop.get(x).y + j] = 0;
				}

			}

		for (int x = 0; x < tmpRegionListBottom.size(); x++)
			for (int i = 0; i < tmpRegionListBottom.get(x).getWidth(); i++) {
				for (int j = 0; j < tmpRegionListBottom.get(x).getHeight(); j++) {
					matrix[tmpRegionListBottom.get(x).x + i][tmpRegionListBottom.get(x).y + j] = 0;
				}

			}

		return matrix;
	}

	/**
	 * Method creates list of potentially joinable regions beside each other and according
	 * to the calculated threshold joins them to one region or leaves them separated.
	 * @param matrix
	 * @param w
	 * @param h
	 */	
	public static int[][] joinRows(int[][] matrix, int w, int h) {
		List<Region> regions = getRegions(matrix, w, h);
		List<List<Region>> regionDoubleListTop = new ArrayList<List<Region>>();
		List<List<Region>> regionDoubleListBottom = new ArrayList<List<Region>>();

		regionDoubleListTop.add(new ArrayList<>());
		regionDoubleListTop.get(0).add(regions.get(0));
		regionDoubleListBottom.add(new ArrayList<>());
		regionDoubleListBottom.get(0).add(regions.get(0));

		for (int i = 1; i < regions.size(); i++) {
			boolean added = false;
			for (int j = 0; j < regionDoubleListTop.size(); j++) {
				List<Region> currentRegionList = regionDoubleListTop.get(j);
				if ((Math.abs(regions.get(i).y - currentRegionList.get(0).y) < 5)) {
					currentRegionList.add(new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width,
							regions.get(i).height, 0));
					added = true;
					break;
				}
			}
			if (!added) {
				regionDoubleListTop.add(new ArrayList<>());
				regionDoubleListTop.get(regionDoubleListTop.size() - 1).add(
						new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width, regions.get(i).height, 0));

			}
		}

		for (int i = 1; i < regions.size(); i++) {
			boolean added = false;
			for (int j = 0; j < regionDoubleListBottom.size(); j++) {
				List<Region> currentRegionList = regionDoubleListBottom.get(j);
				if ((Math.abs((regions.get(i).y + regions.get(i).height)
						- (currentRegionList.get(0).y + currentRegionList.get(0).height)) < 5)) {
					currentRegionList.add(new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width,
							regions.get(i).height, 0));
					added = true;
					break;
				}
			}
			if (!added) {
				regionDoubleListBottom.add(new ArrayList<>());
				regionDoubleListBottom.get(regionDoubleListBottom.size() - 1).add(
						new Region(regions.get(i).x, regions.get(i).y, regions.get(i).width, regions.get(i).height, 0));

			}
		}

		regionDoubleListTop = sortDoubleList(regionDoubleListTop, 'h');
		regionDoubleListBottom = sortDoubleList(regionDoubleListBottom, 'h');

		int limitTop = getLimit(regionDoubleListTop, 'h', w, h);
		int limitBottom = getLimit(regionDoubleListBottom, 'h', w, h);

		List<Region> tmpRegionListTop = reDrawRowsOrColumns(regionDoubleListTop, limitTop, 'h', w, h);
		List<Region> tmpRegionListBottom = reDrawRowsOrColumns(regionDoubleListBottom, limitBottom, 'h', w, h);

		for (int x = 0; x < tmpRegionListTop.size(); x++)
			for (int i = 0; i < tmpRegionListTop.get(x).getWidth(); i++) {
				for (int j = 0; j < tmpRegionListTop.get(x).getHeight(); j++) {
					matrix[tmpRegionListTop.get(x).x + i][tmpRegionListTop.get(x).y + j] = 0;
				}

			}

		for (int x = 0; x < tmpRegionListBottom.size(); x++)
			for (int i = 0; i < tmpRegionListBottom.get(x).getWidth(); i++) {
				for (int j = 0; j < tmpRegionListBottom.get(x).getHeight(); j++) {
					matrix[tmpRegionListBottom.get(x).x + i][tmpRegionListBottom.get(x).y + j] = 0;
				}

			}

		return matrix;
	}

	/**
	 * Method creates a list of Regions from the matrix.
	 * @param matrix
	 * @param w
	 * @param h
	 */	
	public static List<Region> getRegions(int[][] matrix, int w, int h) {
		List<Region> outRegions = new ArrayList<>();
		int[][] matrixCopy = new int[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				matrixCopy[i][j] = matrix[i][j];
			}
		}
		int x_, y_, w_, h_;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (matrixCopy[i][j] == 0) {
					x_ = i;
					y_ = j;
					h_ = h;
					w_ = 0;
					int k, l;
					for (k = x_; (k < w); k++) {
						if (matrixCopy[k][y_] != 0) {
							break;
						}
						for (l = y_; (l < h); l++) {
							if (matrixCopy[k][l] != 0) {
								break;
							}
						}
						h_ = (l - y_ < h_ ? l - y_ : h_);
					}
					w_ = k - x_;
					if ((w_ > 3 && h_ > 3)) {
						outRegions.add(new Region(x_, y_, w_, h_, 0));
						for (int _i = x_; _i < x_ + w_; _i++) {
							for (int _j = y_; _j < y_ + h_; _j++) {
								matrixCopy[_i][_j] = 255;
							}
						}
					}
				}
			}
		}
		return outRegions;
	}

	/**
	 * Method tries to connect as many regions as possible according to the max distance limit.
	 * @param regions
	 * @param inMatrix
	 * @param hMaxLineSize
	 * @param vMaxLineSize
	 * @param w
	 * @param h
	 */	
	public static int[][] connectSmallRegions(List<Region> regions, int[][] inMatrix, int hMaxLineSize,
			int vMaxLineSize, int w, int h) {
		int[][] outMatrix = new int[w][h];
		setArray(outMatrix, 255);

		int diff;
		// Search every direction for the closest Region. Note: "regions" contains only the small regions.
		for (int i = 0; i < regions.size(); i++) {
			diff = w > h ? w : h;
			int tmpX, tmpY, dirrection; // direction == 0 V dirrection == 1 H
			tmpX = tmpY = dirrection = 0;
			// ^
			// O
			for (int j = regions.get(i).x; j < regions.get(i).x + regions.get(i).width; j++) {
				int tmpDistance = 1;
				while (regions.get(i).y - tmpDistance > -1 && inMatrix[j][regions.get(i).y - tmpDistance] != 0)
					tmpDistance++;
				if (regions.get(i).y - tmpDistance > -1 && diff > tmpDistance) {
					diff = tmpDistance;
					tmpX = j;
					tmpY = regions.get(i).y - tmpDistance;
					dirrection = 0;
				}
			}

			// O >
			for (int j = regions.get(i).y; j < regions.get(i).y + regions.get(i).height; j++) {
				int tmpDistance = 1;
				while (regions.get(i).x + regions.get(i).width + tmpDistance < w
						&& inMatrix[regions.get(i).x + regions.get(i).width + tmpDistance][j] != 0)
					tmpDistance++;
				if (regions.get(i).x + regions.get(i).width + tmpDistance < w && diff > tmpDistance) {
					diff = tmpDistance;
					tmpX = regions.get(i).x + regions.get(i).width + tmpDistance;
					tmpY = j;
					dirrection = 1;
				}
			}

			// O
			// V
			for (int j = regions.get(i).x; j < regions.get(i).x + regions.get(i).width; j++) {
				int tmpDistance = 1;
				while (regions.get(i).y + regions.get(i).height + tmpDistance < h
						&& inMatrix[j][regions.get(i).y + regions.get(i).height + tmpDistance] != 0)
					tmpDistance++;
				if (regions.get(i).y + regions.get(i).height + tmpDistance < h && diff > tmpDistance) {
					diff = tmpDistance;
					tmpX = j;
					tmpY = regions.get(i).y + regions.get(i).height + tmpDistance;
					dirrection = 0;
				}
			}

			// < O
			for (int j = regions.get(i).y; j < regions.get(i).y + regions.get(i).height; j++) {
				int tmpDistance = 1;
				while (regions.get(i).x - tmpDistance > -1 && inMatrix[regions.get(i).x - tmpDistance][j] != 0)
					tmpDistance++;
				if (regions.get(i).x - tmpDistance > -1 && diff > tmpDistance) {
					diff = tmpDistance;
					tmpX = regions.get(i).x - tmpDistance;
					tmpY = j;
					dirrection = 1;
				}
			}
			// If the region should be connected to a bigger one, it is enlarged in the given direction.
			// Later the createRectangle function will form them into 1 region.
			if (diff <= ((dirrection == 1) ? hMaxLineSize * 3 : vMaxLineSize * 3)) {
				int newX, newY, newW, newH;
				newX = regions.get(i).x < tmpX ? regions.get(i).x : tmpX;
				newY = regions.get(i).y < tmpY ? regions.get(i).y : tmpY;
				newW = ((regions.get(i).x + regions.get(i).width - newX) > (tmpX - newX))
						? (regions.get(i).x + regions.get(i).width - newX)
						: (tmpX - newX);
				newH = ((regions.get(i).y + regions.get(i).height - newY) > (tmpY - newY))
						? (regions.get(i).y + regions.get(i).height - newY)
						: (tmpY - newY);
				for (int q = newX; q < newX + newW; q++)
					for (int p = newY; p < newY + newH; p++)
						outMatrix[q][p] = 0;
			}

		}

		return outMatrix;
	}

	/**
	 * Method calculates the minimum rectangle size for final regions.
	 * @param regions
	 */	
	public static int getMinRegionSize(List<Region> regions) {
		if (regions.size() == 0)
			return 0;
		int minRegionSize = 0;
		int[] tmpRegion = new int[regions.size()];
		for (int i = 0; i < regions.size(); i++) {
			tmpRegion[i] = (int) (regions.get(i).getWidth() * regions.get(i).getHeight());
		}
		minRegionSize = 0;
		for (int i = 1; i < regions.size(); i++) {
			minRegionSize += tmpRegion[i];
		}
		minRegionSize = minRegionSize / regions.size();
		minRegionSize *= 0.6;
		return minRegionSize;
	}

	/**
	 * Method connects rectangles closer than a given limit.
	 * @param inMatrix
	 * @param hMaxLineSize
	 * @param vMaxLineSize
	 * @param w
	 * @param h
	 */	
	public static int[][] reDrawRectangles(int[][] inMatrix, int hMaxLineSize, int vMaxLineSize, int w, int h) {
		List<Region> regions = getRegions(inMatrix, w, h);
		int[][] outMatrix = new int[w][h];
		
		setArray(outMatrix, 255);

		Rectangle tmp1, tmp2;

		for (int i = 0; i < regions.size(); i++) {
			for (int j = 0; j < regions.get(i).getWidth(); j++) {
				for (int k = 0; k < regions.get(i).getHeight(); k++) {
					outMatrix[j + regions.get(i).x][k + regions.get(i).y] = 0;
				}
			}
		}

		for (int i = 0; i < regions.size(); i++) {
			for (int j = i + 1; j < regions.size(); j++) {
				if (regions.get(i).x + regions.get(i).width > regions.get(j).x) {
					tmp1 = regions.get(j);
					tmp2 = regions.get(i);
				} else {
					tmp1 = regions.get(i);
					tmp2 = regions.get(j);
				}
				int diff = tmp2.x - (tmp1.x + (int) tmp1.getWidth());
				if (diff < (hMaxLineSize) && diff >= 0) {

					if ((tmp1.y >= tmp2.y && tmp1.y < (tmp2.y + tmp2.height))
							|| ((tmp1.y + tmp1.height) > tmp2.y && (tmp1.y + tmp1.height) <= (tmp2.y + tmp2.height))
							|| (tmp2.y >= tmp1.y && tmp2.y < (tmp1.y + tmp1.height))
							|| ((tmp2.y + tmp2.height) > tmp1.y && (tmp2.y + tmp2.height) <= (tmp1.y + tmp1.height)))

					{
						for (int o = tmp1.x; o < tmp2.x + tmp2.width; o++)
							for (int p = (tmp1.y < tmp2.y ? tmp1.y
									: tmp2.y); p < (((tmp1.y + tmp1.height) > (tmp2.y + tmp2.height))
											? (tmp1.y + tmp1.height)
											: (tmp2.y + tmp2.height)); p++)
								outMatrix[o][p] = 0;
					}
				}

			}

		}

		for (int i = 0; i < regions.size(); i++) {
			for (int j = i + 1; j < regions.size(); j++) {
				if (regions.get(i).y + regions.get(i).height > regions.get(j).y) {
					tmp1 = regions.get(j);
					tmp2 = regions.get(i);
				} else {
					tmp1 = regions.get(i);
					tmp2 = regions.get(j);
				}
				int diff = tmp2.y - (tmp1.y + (int) tmp1.getHeight());
				if (diff < (vMaxLineSize) && diff >= 0) {
					if ((tmp1.x >= tmp2.x && tmp1.x < (tmp2.x + tmp2.width))
							|| ((tmp1.x + tmp1.width) > tmp2.x && (tmp1.x + tmp1.width) <= (tmp2.x + tmp2.width))
							|| (tmp2.x >= tmp1.x && tmp2.x < (tmp1.x + tmp1.width))
							|| ((tmp2.x + tmp2.width) > tmp1.x && (tmp2.x + tmp2.width) <= (tmp1.x + tmp1.width))) {
						for (int o = tmp1.y; o < tmp2.y + tmp2.height; o++)
							for (int p = (tmp1.x < tmp2.x ? tmp1.x
									: tmp2.x); p < (((tmp1.x + tmp1.width) > (tmp2.x + tmp2.width))
											? (tmp1.x + tmp1.width)
											: (tmp2.x + tmp2.width)); p++)
								outMatrix[p][o] = 0;
					}
				}

			}

		}

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (outMatrix[i][j] == 0 || inMatrix[i][j] == 0)
					outMatrix[i][j] = 0;

		return outMatrix;

	}

	/**
	 * Method calculates max horizontal and vertical distance between joinable rectangles.
	 * @param inMatrix
	 * @param outArray
	 * @param w
	 * @param h
	 */	
	public static void getTreshold(int[][] inMatrix, int[] outArray, int w, int h) {
		int hMaxLineSize = 0;
		int vMaxLineSize = 0;
		//	horizontal
		{
			int[] tmpLongest = new int[w];
			//	loop trough whole image
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					//	find first white pixel
					if (inMatrix[j][i] == 255) {
						int tmp = 1;
						//	count the consecutive white pixels
						while ((j + tmp) < w && inMatrix[j + tmp][i] == 255) {
							tmp++;
						}
						//	leave out the distance between last black pixel and end of image
						//	TODO should have tested for the first sequence before the first black pixel
						if (tmp == w)
							continue;
						//	use the distance between two black pixels as index in the histogram
						//	and increase it by 1
						tmpLongest[tmp] += 1;
						j += tmp;
					}

				}
			}

			int tmp = 0;
			float[] avgr = new float[w];
			for (int i = 0; i < w; i++) {
				tmp += (tmpLongest[i]);
				//	compute average of previous values up to "i" in the histogram
				avgr[i] = (float) tmp / (float) (i + 1);
			}
			tmp = 0;
			//	find the first decrease in the histogram
			for (int i = 0; i < w; i++) {
				if (avgr[tmp] < avgr[i])
					tmp = i;
			}
			//	in case the threshold is too small
			hMaxLineSize = tmp < 4 ? 4 : tmp;
		}

		//	vertical
		{
			int[] tmpLongest = new int[h];
			for (int j = 0; j < w; j++) {
				for (int i = 0; i < h; i++) {

					if (inMatrix[j][i] == 255) {
						int tmp = 1;
						while ((i + tmp) < h && inMatrix[j][i + tmp] == 255) {
							tmp++;
						}
						if (tmp == h)
							continue;
						tmpLongest[tmp] += 1;
						i += tmp;
					}

				}
			}
			int tmp = 0;
			float[] avgr = new float[h];
			for (int i = 0; i < h; i++) {
				tmp += (tmpLongest[i]);
				avgr[i] = (float) tmp / (float) (i + 1);
			}
			tmp = 0;

			for (int i = 0; i < h; i++) {
				if (avgr[tmp] < avgr[i])
					tmp = i;
			}

			vMaxLineSize = tmp < 4 ? 4 : tmp;
		}
		outArray[0] = hMaxLineSize;
		outArray[1] = vMaxLineSize;
	}

	/**
	 * Method sorts List of regions contained in List according to their
	 * x or y coordinate.
	 * @param inRegionDoubleList
	 * @param direction
	 */	
	private static List<List<Region>> sortDoubleList(List<List<Region>> inRegionDoubleList, char direction) {
		List<List<Region>> outRegionDoubleList = new ArrayList<List<Region>>();
		int[] tmpSortArray;
	
		for (int i = 0; i < inRegionDoubleList.size(); i++) {
			tmpSortArray = new int[inRegionDoubleList.get(i).size()];
			for (int j = 0; j < inRegionDoubleList.get(i).size(); j++) {
				if (direction == 'h')
					tmpSortArray[j] = inRegionDoubleList.get(i).get(j).x;
				if (direction == 'v')
					tmpSortArray[j] = inRegionDoubleList.get(i).get(j).y;
			}
			Arrays.sort(tmpSortArray);
			List<Region> tmpSortedRegionList = new ArrayList<>();
			for (int j = 0; j < inRegionDoubleList.get(i).size(); j++) {
				for (int f = 0; f < inRegionDoubleList.get(i).size(); f++) {
					if (direction == 'h')
						if (tmpSortArray[j] == inRegionDoubleList.get(i).get(f).x) {
							tmpSortedRegionList.add(new Region(inRegionDoubleList.get(i).get(f).x, inRegionDoubleList.get(i).get(f).y,
									inRegionDoubleList.get(i).get(f).width, inRegionDoubleList.get(i).get(f).height, 0));
							break;
						}
					if (direction == 'v')
						if (tmpSortArray[j] == inRegionDoubleList.get(i).get(f).y) {
							tmpSortedRegionList.add(new Region(inRegionDoubleList.get(i).get(f).x, inRegionDoubleList.get(i).get(f).y,
									inRegionDoubleList.get(i).get(f).width, inRegionDoubleList.get(i).get(f).height, 0));
							break;
						}
				}
			}
			outRegionDoubleList.add(tmpSortedRegionList);
		}
		return outRegionDoubleList;
	}

	/**
	 * Method connects regions in a rows or columns, according to the direction
	 *  give, if the distance is smaller than the limit.
	 * @param inRegionList
	 * @param limit
	 * @param direction
 	 * @param w
	 * @param h
	 */	
	private static List<Region> reDrawRowsOrColumns(List<List<Region>> inRegionList, int limit, char direction, int w, int h) {
		List<Region> outRegionList = new ArrayList<>();
		for (int o = 0; o < inRegionList.size(); o++) {
			if (inRegionList.get(o).size() > 1) {
				int x1, y1, x2, y2;
				x1 = w;
				y1 = h;
				x2 = y2 = 0;
				int first = 0;
	
				List<Region> currentList = inRegionList.get(o);
				// While the distance between the 2 regions next to each other is smaller than the limit
				// go to the next. (Find "first" to "current" sequence of regions to join).
				for (int r = 1; r < currentList.size(); r++) {
					// If r == currentList.size() - 1 don't search further, but join the found sequence.
					if (direction == 'h' && ((currentList.get(r).x
							- ((currentList.get(r - 1).x + currentList.get(r - 1).width)) < limit)
							&& r != currentList.size() - 1)) {
						continue;
					} else if (direction == 'v' && ((currentList.get(r).y
							- ((currentList.get(r - 1).y + currentList.get(r - 1).height)) < limit)
							&& r != currentList.size() - 1)) {
						continue;
					}
					// The distance between the last two regions checked were bigger than limit, or
					// it is the end of the list.
					else {
						// Check if the r - 1 region equals to "first", which means there is no sequence
						// to add, only the r-1. In case the current region is the last one, add it to the return list.
						if (r - 1 == first) {
							outRegionList.add(new Region(currentList.get(r - 1).x, currentList.get(r - 1).y,
									currentList.get(r - 1).width, currentList.get(r - 1).height, 0));
							if (r == currentList.size() - 1) {
								outRegionList.add(new Region(currentList.get(r).x, currentList.get(r).y,
										currentList.get(r).width, currentList.get(r).height, 0));
								continue;
							}
							first = r;
						}
						// Compute x, y, width and height of the new region. first = r <- start a new search
						else {
							x1 = w;
							y1 = h;
							x2 = y2 = 0;
							// TODO check if r is the last region, and if its part of the sequence.
							for (int u = first; u < r; u++) {
								x1 = currentList.get(u).x < x1 ? currentList.get(u).x : x1;
								y1 = currentList.get(u).y < y1 ? currentList.get(u).y : y1;
								x2 = currentList.get(u).x + currentList.get(u).width > x2
										? currentList.get(u).x + currentList.get(u).width
										: x2;
								y2 = currentList.get(u).y + currentList.get(u).height > y2
										? currentList.get(u).y + currentList.get(u).height
										: y2;
							}
							outRegionList.add(new Region(x1, y1, x2 - x1, y2 - y1, 0));
							first = r;
						}
					}
				}
			}
		}
		return outRegionList;
	}

	/**
	 * Method calculates limit of distance between rows and columns
	 * according to the direction given.
	 * @param inRegionList
	 * @param direction
 	 * @param w
	 * @param h
	 */	
	private static int getLimit(List<List<Region>> inRegionList, char direction, int w, int h) {
		int limit, count;
		limit = count = 0;
		for (int i = 0; i < inRegionList.size(); i++) {
			if (inRegionList.get(i).size() > 1) {
				List<Region> currentList = inRegionList.get(i);
				for (int j = 1; j < currentList.size(); j++) {
					int diff = 0;
					if (direction == 'h')
						diff = currentList.get(j).x - (currentList.get(j - 1).x + currentList.get(j - 1).width);
					if (direction == 'v')
						diff = currentList.get(j).y - (currentList.get(j - 1).y + currentList.get(j - 1).height);
					if (diff > 0 && ((direction == 'h' && diff < w * 0.3) || (direction == 'v' && diff < h * 0.3))) {
						limit += diff;
						count++;
					}
				}
			}
		}
		if (count == 0)
			return 0;
		limit = limit / count;
		if (direction == 'v')
			limit *= 0.5;
		if (direction == 'h')
			limit *= 0.5;
		return limit;
	}

	/**
	 * Reuse of GrayMatrix.createRectangles(matrix, createNew) with different limitations.
	 * @param matrix
	 */
	public static int[][] createRectangles(int[][] matrix) {
		int w = MatrixUtils.width(matrix);
		int h = MatrixUtils.height(matrix);
	
		// process matrix
		int color = -1;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (matrix[i][j] != GrayMatrix.WHITE) {
					processSeedPixel(i, j, color, matrix);
					color--;
				}
			}
		}
	
		// convert colors to black
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (matrix[i][j] < 0) {
					matrix[i][j] = GrayMatrix.BLACK;
				}
			}
		}
	
		return matrix;
	}

	private static void processSeedPixel(int i, int j, int color, int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);

		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(i, j));
		int x1 = i, x2 = i, y1 = j, y2 = j;
		while (!queue.isEmpty()) {
			Point p = queue.remove();
			if ((p.x >= 0) && (p.x < mW) && (p.y >= 0) && (p.y < mH)) {
				if (matrix[p.x][p.y] == GrayMatrix.BLACK) {
					matrix[p.x][p.y] = color;

					// update min/max points
					if (p.x < x1) {
						x1 = p.x;
					} else if (p.x > x2) {
						x2 = p.x;
					}
					if (p.y < y1) {
						y1 = p.y;
					} else if (p.y > y2) {
						y2 = p.y;
					}

					// add neighbour points
					queue.add(new Point(p.x + 1, p.y));
					queue.add(new Point(p.x - 1, p.y));
					queue.add(new Point(p.x, p.y + 1));
					queue.add(new Point(p.x, p.y - 1));

					queue.add(new Point(p.x + 1, p.y + 1));
					queue.add(new Point(p.x + 1, p.y - 1));
					queue.add(new Point(p.x - 1, p.y + 1));
					queue.add(new Point(p.x - 1, p.y - 1));
				}
			}
		}

		x2++;
		y2++;

		// test rectangle size
		if (!(x2 - x1 > mW * 0.9 && y2 - y1 > mH * 0.9)) {
			// create rectangle
			if (!(x2 - x1 > mW / 2 && y2 - y1 < 15) && !(y2 - y1 > mW / 2 && x2 - x1 < 15))
				for (int x = x1; x < x2; x++) {
					for (int y = y1; y < y2; y++) {
						if (((x2 - x1) * (y2 - y1)) > 7) {
							matrix[x][y] = color;
						}
					}
				}
		}
	}

}
