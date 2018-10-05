package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.image.floodfill.BasicFloodFill;
import cz.vutbr.fit.dashapp.image.floodfill.SeedPixelUtil;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.segmenation.methods.DashboardSegmentation;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BUBasicUtil;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BUJoinLineUtil;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * Help methods for the bottom-up strategy used to process dashboard layout.
 * 
 * @author Jiri Hynek
 *
 */
public class BottomUpAnalysisUtil {
	
	/**
	 * Help class which stores lists of main and candidate regions.
	 * It is used for the bottom-up analysis.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class Regions {
		
		public static final int CLR_WHITE_SPACE = GrayMatrix.WHITE; 
		public static final int CLR_MAIN_REGION = GrayMatrix.BLACK;
		public static final int CLR_CANDIDATE_REGION = (GrayMatrix.WHITE+GrayMatrix.BLACK)/2;
		
		/**
		 * Layout root
		 */
		private TreeNode<Region> root;
		
		/**
		 * main regions
		 */
		public List<Region> main;
		
		/**
		 * candidate regions which can be joined
		 */
		public List<Region> candidate;
		
		public Regions(TreeNode<Region> root) {
			this.root = root;
		}

		public Regions shallowCopy() {
			Regions r = new Regions(root);
			r.main = this.main;
			r.candidate = this.candidate;
			return r;
		}
		
		public int[][] printMatrix(int color) {
			int matrix[][] = GrayMatrix.newMatrix(root.data.width, root.data.height, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, color, true);
			}
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, color, true);
			}
			
			return matrix;
		}
		
		public int[][] printMatrix() {
			int matrix[][] = GrayMatrix.newMatrix(root.data.width, root.data.height, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, CLR_MAIN_REGION, true);
			}
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, CLR_CANDIDATE_REGION, true);
			}
			
			return matrix;
		}
		
		public int[][] printMainRegions() {
			int matrix[][] = GrayMatrix.newMatrix(root.data.width, root.data.height, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, GrayMatrix.BLACK, true);
			}
			
			return matrix;
		}
		
		public int[][] printCandidateRegions() {
			int matrix[][] = GrayMatrix.newMatrix(root.data.width, root.data.height, CLR_WHITE_SPACE);
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, GrayMatrix.BLACK, true);
			}
			
			return matrix;
		}

		public List<Region> getAll() {
			List<Region> all = new LinkedList<>();
			all.addAll(main);
			all.addAll(candidate);
			return all;
		}

		public int size() {
			return main.size()+candidate.size();
		}
	}
	
	/**
	 * Help class which represents information about possible join with neighboring region.
	 * The join is represented by the Point p located
	 * in a neighboring region which can be joined with actual region. 
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class CandidateJoinPoint {
		
		/**
		 * region
		 */
		public Region region;
		
		/**
		 * region
		 */
		public Regions regions;
		
		/**
		 * intersection point located in candidate join region
		 */
		public Point p;
		
		/**
		 * distance between regions
		 */
		public int d;
		
		/**
		 * CLR_MAIN_REGION / CLR_CANDIDATE_REGION
		 */
		public int clr;
		
		/**
		 * candidate region
		 */
		private Region candidateRegion;
		
		/**
		 * region after join
		 */
		private Region joinRegion;
		
		/**
		 * share of area of 2 regions and join region
		 */
		private double joinAreaShare;
		
		public CandidateJoinPoint(Region region, Regions regions, Point p, int d, int clr) {
			this.region = region;
			this.regions = regions;
			this.p = p;
			this.d = d;
			this.clr = clr;
		}
		
		/**
		 * Method takes region and candidate join point, find the candidate region
		 * and calculates and share of regions area in join area 
		 * 
		 * @return
		 */
		public Region getCandidateRegion() {
			if(candidateRegion != null) {
				return candidateRegion;
			}
			
			joinAreaShare = 0;
			double actShare;
			
			// first we search in the list of candidate regions
			for (Region region2 : regions.candidate) {
				if(region2.intersects(p.x, p.y)) {
					actShare = region.shareOfJoinArea(region2);
					if(actShare > joinAreaShare) {
						candidateRegion = region2;
						joinAreaShare = actShare;
					}
				}
			}
			
			// then we try to search in the list of main regions 
			if(candidateRegion == null) {
				for (Region r2 : regions.main) {
					if(r2.intersects(p.x, p.y)) {
						actShare = region.shareOfJoinArea(r2);
						if(actShare > joinAreaShare) {
							candidateRegion = r2;
							joinAreaShare = actShare;
						}
					}
				}
			}
			
			return candidateRegion;
		}
		
		public Region getJoinRegion() {
			if(joinRegion != null) {
				return joinRegion;
			}
			return joinRegion = region.joinWith(getCandidateRegion());
		}
		
		public double getJoinAreaShare() {
			if(candidateRegion == null) {
				// join area share is measured during search of candidate region
				getCandidateRegion();
			}
			return joinAreaShare;
		}
	}
	
	/**
	 * Help class which stores information about affected regions by the join operation.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class AffectedRegionsOfJoin {
		
		/**
		 * affected regions
		 */
		List<Region> regions;
		
		/**
		 * Boundary of join region and other affected regions
		 */
		Rectangle boundary;
	}
	
	/**
	 * Method finds remaining small regions which could be clustered into larger main regions (Gestalt law of proximity).
	 * 
	 * @param mainRegions
	 * @param root
	 * @param alg
	 * @return
	 */
	public static List<Region> clusterRemainingSmallRegions(List<Region> mainRegions, TreeNode<Region> root, DashboardSegmentation alg) {
		Regions regions = new Regions(root);
		
		// get main regions (filter small main regions)
		regions.main = filterMainRegions(mainRegions, root, alg);
		//alg.debug("main regions", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));
		
		// find candidate regions (they don't intersects main regions)
		regions.candidate = findCandidateRegions(regions, alg);
		//alg.debug("main + candidate regions", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));

		// join small regions ordered in lines (update candidate regions)
		regions = joinLines(regions, alg);
		//alg.debug("joined lines", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));

		// try to join candidate regions (update main and candidate regions)
		regions = connectSmallRegionsIterative(regions, alg);
		//alg.debug("connected", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));
		
		// TODO: test number of regions

		// TODO: filter small candidate nodes

		return regions.getAll();
	}

	/**
	 * The method takes main regions and compares the area they represent with area of dashboard
	 * If main regions represent small share of dashboard's area, method filter small main regions.
	 * We expect that small main regions should be additionally clustered with other small regions (Gestalt law of proximity).
	 * 
	 * @param mainRegions
	 * @param root
	 * @param alg
	 */
	private static List<Region> filterMainRegions(List<Region> mainRegions, TreeNode<Region> root, DashboardSegmentation alg) {
		List<Region> resultRegions = new LinkedList<>();
		
		double shareOfMainRegionsArea = (double) area(mainRegions) / root.data.area();
		System.out.println("segmentation: bootom-up: main regions share: " + shareOfMainRegionsArea);
		
		// filter only if there is a lot of empty space area
		if (shareOfMainRegionsArea < 0.50) {
			int limit_w = (int) (root.data.width * 0.75);
			int limit_h = (int) (root.data.height * 0.75);
			int limit_a = (int) (root.data.area() * 0.10);
			for (Region mainRegion : mainRegions) {
				if (mainRegion.width > limit_w || mainRegion.height > limit_h || mainRegion.area() > limit_a) {
					resultRegions.add(mainRegion);
				} else {
					mainRegion.category = Region.CATEGORY_CANDIDATE;
				}
			}
		} else {
			// else add all main regions
			//resultRegions.addAll(mainRegions);
			int limit_a = (int) (root.data.area() * 0.8);
			for (Region mainRegion : mainRegions) {
				if(mainRegion.area() < limit_a) {
					resultRegions.add(mainRegion);
				} else {
					mainRegion.category = Region.CATEGORY_CANDIDATE;
				}
			}
		}
		
		return resultRegions;
	}
	
	/**
	 * 
	 * @param regions
	 * @return
	 */
	private static int area(List<Region> regions) {
		int a = 0;
		for (Region region : regions) {
			a += region.area();
		}
		return a;
	}
	
	/**
	 * Method finds small regions which does not intersects main regions.
	 * 
	 * @param matrix
	 * @param root
	 * @param alg
	 * @return
	 */
	private static List<Region> findCandidateRegions(Regions mainRegions, DashboardSegmentation alg) {
		List<Region> candidateRegions = new LinkedList<>();
		processEmptySpaceNodes(mainRegions.root, mainRegions.printMatrix(), candidateRegions);
		return candidateRegions;
	}
	
	/**
	 * Recursive method which process layout node and its children and finds candidate regions.
	 * 
	 * @param node
	 * @param mainRegionsMatrix
	 * @param candidateRegions
	 */
	private static void processEmptySpaceNodes(TreeNode<Region> node, int[][] mainRegionsMatrix, List<Region> candidateRegions) {
		if(intersectsMainRegions(node.data, mainRegionsMatrix) || isBigCandidateRegion(node.data, mainRegionsMatrix)) {
			// if node intersects main region or it is a large region then look for children
			for (TreeNode<Region> child : node.children) {
				processEmptySpaceNodes(child, mainRegionsMatrix, candidateRegions);
			}
		} else {
			candidateRegions.add(node.data);
		}
	}

	/**
	 * Heuristics for the search of candidates regions.
	 * It decides if the candidate region is big. 
	 * 
	 * @param region
	 * @param matrix
	 * @return
	 */
	private static boolean isBigCandidateRegion(Region region, int[][] matrix) {
		if(region.area() > MatrixUtils.area(matrix)*0.8) {
			return true;
		}
		
		return false;
	}

	/**
	 * Method tests if the region intersects a main region.
	 * 
	 * @param region
	 * @param matrix
	 * @return
	 */
	private static boolean intersectsMainRegions(Region region, int[][] matrix) {
		int x1 = region.x;
		int x2 = region.x+region.width;
		int y1 = region.y;
		int y2 = region.y+region.height;
		
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				if(matrix[i][j] == Regions.CLR_MAIN_REGION) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	/**
	 * Method joins vertical and horizontal lines
	 * 
	 * @param matrix
	 * @param candidateRegions
	 * @param mainRegions
	 * @param alg
	 * @return
	 */
	private static Regions joinLines(Regions regions, DashboardSegmentation alg) {
		Regions resultRegions = regions.shallowCopy();
		
		// print regions to matrix
		int[][] matrix = regions.printCandidateRegions();
		
		// 1. rows
		joinLine(resultRegions, matrix, Constants.X, 10, regions.root.data.width/100, alg);
		
		// 2. columns
		joinLine(resultRegions, matrix, Constants.Y, 10, regions.root.data.height/100, alg);
		
		// get regions from matrix
		resultRegions.candidate = BUBasicUtil.getRegions(matrix);
		return resultRegions;
	}
	
	/**
	 * Method joins line in X/Y direction using Mejia's joinLine method.
	 * Result is returned in the form of matrix.
	 * 
	 * @param regions
	 * @param matrix
	 * @param direction
	 * @param offsetLimit
	 * @param joinLimit
	 * @param alg
	 */
	private static void joinLine(Regions regions, int[][] matrix, int direction, int offsetLimit, int joinLimit, DashboardSegmentation alg) {
		//System.out.println("rowLimit: " + rowLimit);
		
		// -- join line using Mejia's algorithm
		int[][] newMatrix = MatrixUtils.copy(matrix);
		BUJoinLineUtil.joinLine(newMatrix, direction, offsetLimit, joinLimit);
		
		// -- perform the flood-fill-based "Create rectangles" method until it is possible to join regions 
		// (Gestalt law of closure)
		do {
			//alg.debug("rows", GrayMatrix.printMatrixToImage(null, actMatrix));
			MatrixUtils.copy(matrix, newMatrix);
			newMatrix = new JoinLinesRectangleFloodFill(newMatrix, false, GrayMatrix.BLACK, regions.main).process();
			//alg.debug("rows-rect", GrayMatrix.printMatrixToImage(null, actMatrix));
		} while (!MatrixUtils.equals(matrix, newMatrix));
	}
	
	/**
	 * Flood-fill-based algorithm which take black-and white matrix
	 * and creates rectangles from the areas of black pixels.
	 * 
	 * Comparing to SimpleRectangleFloodFill it does not delete the very large rectangles.
	 * It just ignores them.
	 * 
	 * It corresponds with Gestalt law of closure.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class JoinLinesRectangleFloodFill extends BasicFloodFill {
		
		private List<Region> mainRegions;

		public JoinLinesRectangleFloodFill(int[][] matrix, boolean createNew, int refColor, List<Region> mainRegions) {
			super(matrix, createNew, refColor);
			this.mainRegions = mainRegions;
		}

		@Override
		protected int[][] postProcessMatrix() {
			int[][] resultMatrix = super.postProcessMatrix();
			// convert colors to black
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					if (resultMatrix[i][j] < 0) {
						resultMatrix[i][j] = GrayMatrix.BLACK;
					}
				}
			}
			return resultMatrix;
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2) {
			super.postProcessSeedPixel(x1, y1, x2, y2);
			// test rectangle size
			int rW = x2 - x1;
			int rH = y2 - y1;
			
			// calculate number of main regions in rectangle
			int mainRegionsCount = 0;
			for (Region mainRegion : mainRegions) {
				if(mainRegion.intersects(x1, y1, rW, rH)) {
					mainRegionsCount++;
				}
			}
			
			// filter very large rectangles
			if ((rW > mW / 2 || rH > mH / 2) && (double) area/(rW*rH) < 0.6 || mainRegionsCount > 1) {
			//if(mainRegionsCount > 1) {
				// delete area
				// update: don't delete area
				/*for (int x = 0; x < mW; x++) {
					for (int y = 0; y < mH; y++) {
						if (matrix[x][y] == markColor) {
							matrix[x][y] = GrayMatrix.WHITE;
						}
					}
				}*/
			} else {
				// create rectangle
				MatrixUtils.drawPixels(matrix, x1, y1, x2, y2, markColor);
			}
		}
	}
	
	/**
	 * Method iteratively increase join limit and tries to connect neighboring small regions.
	 * 
	 * @param regions
	 * @param alg
	 * @return
	 */
	private static Regions connectSmallRegionsIterative(Regions regions, DashboardSegmentation alg) {
		int distanceThreshold = 5;//Math.max(regions.w, regions.h)/160;
		int change = distanceThreshold;
		int maxDistanceThreshold = 20;
		//System.out.println(regions.w + "px " + regions.h + "px : distance limit: " + maxDistanceLimit);
		while(regions.size() > 10 && distanceThreshold <= 5
				|| regions.size() > 15 && distanceThreshold <= 10 
				|| regions.size() > 20 && distanceThreshold <= maxDistanceThreshold) {
			regions = connectSmallRegions(regions, alg, distanceThreshold);
			distanceThreshold += change;
		}
		
		return regions;
	}
	
	/**
	 * Method connects small regions with neighbors.
	 * 
	 * @param matrix
	 * @param candidateRegions
	 * @param mainRegions
	 * @param alg
	 * @param threshold 
	 * @return
	 */
	private static Regions connectSmallRegions(Regions regions, DashboardSegmentation alg, int threshold) {
		// create working copy
		Regions resultRegions = regions.shallowCopy();
		
		// actual regions matrix
		int regionsMatrix[][] = regions.printMatrix();
		
		// create result matrix
		int resultMatrix[][] = GrayMatrix.newMatrix(MatrixUtils.width(regionsMatrix), MatrixUtils.height(regionsMatrix), GrayMatrix.WHITE);
		
		// sort candidate regions
		Collections.sort(regions.candidate, new Comparator<Region>() {

			@Override
			public int compare(Region r1, Region r2) {
				return r1.area()-r2.area();
			}
		});
		
		resultRegions.candidate = new LinkedList<>(regions.candidate);
		resultRegions.main = new LinkedList<>(regions.main);
		
		// initialize help variables
		CandidateJoinPoint[] nearestJoinPoints = null;
		List<CandidateJoinPoint> candidateJoinPoints;
		Region actJoinRegion = null;
		boolean joinPerformed;
		AffectedRegionsOfJoin affectedRegions;
		//int a_limit = (int) (MatrixUtils.area(matrix)*0.1);
		int unchangedCount = 0;
		int candidatesCount = resultRegions.candidate.size();
		
		// go through all regions and try to join them with larger
		// until the number of unchanged regions is lower than the number of candidates region
		// -> when we perform some join operation, we need to go through all candidates regions and test again.
		while (!resultRegions.candidate.isEmpty() && unchangedCount < candidatesCount) {
			Region region = resultRegions.candidate.remove(0);
			candidatesCount--;
			
			//if(region.area() < a_limit || region.type == Region.JOIN_TYPE) {
				nearestJoinPoints = findNearestJoinPoints(region, resultRegions, regionsMatrix, alg);
				candidateJoinPoints = chooseCandidateJoinPoints(nearestJoinPoints, region, resultRegions, regionsMatrix, threshold, alg);
				
				joinPerformed = false;
				// go through candidate join points and analyze affected area
				for (CandidateJoinPoint candidateJoinPoint : candidateJoinPoints) {
					actJoinRegion = candidateJoinPoint.getJoinRegion();
					
					// analyze affected regions
					affectedRegions = getAffectedRegions(resultRegions, actJoinRegion);
					if(!isAffectingImportantRegions(region, candidateJoinPoint, affectedRegions, resultRegions)) {
						// test what kind of region we should join with and update lists appropriately
						if(candidateJoinPoint.candidateRegion.category == Region.CATEGORY_MAIN) {
							resultRegions.main.remove(candidateJoinPoint.candidateRegion);
							// update the Regions object
							actJoinRegion.category = Region.CATEGORY_MAIN;
							resultRegions.main.add(actJoinRegion);
							MatrixUtils.drawRectangle(regionsMatrix, actJoinRegion, Regions.CLR_MAIN_REGION, false);
							//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultRegions.printMatrix()));
						} else {
							// draw region
							//MatrixUtils.drawRectangle(resultMatrix, joinRegion, GrayMatrix.BLACK, false);
							resultRegions.candidate.remove(candidateJoinPoint.candidateRegion);
							// update the Regions object
							actJoinRegion.category = Region.CATEGORY_CANDIDATE;
							resultRegions.candidate.add(actJoinRegion);
							MatrixUtils.drawRectangle(regionsMatrix, actJoinRegion, Regions.CLR_CANDIDATE_REGION, false);
							// draw connection region and point
							//connectRegionWithPoint(resultMatrix, region, candidate.p);
						}
						unchangedCount = 0;
						joinPerformed = true;
						
						//alg.debug("join", GrayMatrix.printMatrixToImage(null, resultRegions.printMatrix()));
						
						break;
					}
				}
				
				if(!joinPerformed) {
					resultRegions.candidate.add(region);
					//MatrixUtils.drawRectangle(resultMatrix, region, GrayMatrix.BLACK, false);
					candidatesCount++;
					unchangedCount++;
				}
			/*} else {
				if(region.type == Region.JOIN_TYPE) {
					continue;
				} else {
					break;
				}
			}*/
		}
		
		// print remaining candidate regions
		resultMatrix = resultRegions.printCandidateRegions();
		//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultMatrix));
		
		// apply rectangle flood fill (Gestalt law of closure)
		int[][] prevMatrix = new int[MatrixUtils.width(regionsMatrix)][MatrixUtils.height(regionsMatrix)];
		do {
			MatrixUtils.copy(prevMatrix, resultMatrix);
			resultMatrix = new JoinLinesRectangleFloodFill(resultMatrix, false, GrayMatrix.BLACK, regions.main).process();
		} while (!MatrixUtils.equals(resultMatrix, prevMatrix));
		
		//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultMatrix));
		
		resultRegions.candidate = BUBasicUtil.getRegions(resultMatrix);
		
		// TODO filter candidate regions stored in main regions
		
		return resultRegions;
	}

	/**
	 * Methods finds the nearest join points in 4 directions (up, down, left, right).
	 * It uses black and white matrix.
	 * 
	 * @param region
	 * @param regions 
	 * @param regionsMatrix
	 * @param alg
	 * @return
	 */
	private static CandidateJoinPoint[] findNearestJoinPoints(Region region, Regions regions, int[][] regionsMatrix, DashboardSegmentation alg) {
		// dimensions of dashboard
		int mW = regions.root.data.width;
		int mH = regions.root.data.height;
		
		// dimension of region
		int x1 = region.x;
		int x2 = region.x2();
		int y1 = region.y;
		int y2 = region.y2();
		
		// find candidate points in all directions 
		CandidateJoinPoint[] candidates = new CandidateJoinPoint[4];
		
		// iteratively moves up
		int actDistance = 1;
		for (int y = y1-1; y >= 0 && candidates[0] == null; y--) {
			// check one line
			for (int x = x1; x < x2; x++) {
				if(regionsMatrix[x][y] != Regions.CLR_WHITE_SPACE) {
					candidates[0] = new CandidateJoinPoint(region, regions, new Point(x, y), actDistance, regionsMatrix[x][y]);
					break;
				}
			}
			
			// move up
			actDistance++;
		}
		
		// iteratively moves down
		actDistance = 1;
		for (int y = y2+1; y < mH && candidates[1] == null; y++) {
			// check one line
			for (int x = x1; x < x2; x++) {
				if(regionsMatrix[x][y] != Regions.CLR_WHITE_SPACE) {
					candidates[1] = new CandidateJoinPoint(region, regions, new Point(x, y), actDistance, regionsMatrix[x][y]);
					break;
				}
			}
			
			// move down
			actDistance++;
		}
		
		// iteratively moves left
		actDistance = 1;
		for (int x = x1-1; x >= 0 && candidates[2] == null; x--) {
			// check one line
			for (int y = y1; y < y2; y++) {
				if(regionsMatrix[x][y] != Regions.CLR_WHITE_SPACE) {
					candidates[2] = new CandidateJoinPoint(region, regions, new Point(x, y), actDistance, regionsMatrix[x][y]);
					break;
				}
			}
			
			// move left
			actDistance++;
		}
		
		// iteratively moves right
		actDistance = 1;
		for (int x = x2+1; x < mW && candidates[3] == null; x++) {
			// check one line
			for (int y = y1; y < y2; y++) {
				if(regionsMatrix[x][y] != Regions.CLR_WHITE_SPACE) {
					candidates[3] = new CandidateJoinPoint(region, regions, new Point(x, y), actDistance, regionsMatrix[x][y]);
					break;
				}
			}
			
			// move right
			actDistance++;
		}
		
		return candidates;
	}

	/**
	 * Method takes the nearest points and choose candidate join points.
	 * 
	 * It considers distance threshold.
	 * 
	 * It calculates result region after join.
	 * 
	 * @param candidates
	 * @param region
	 * @param regions
	 * @param regionsMatrix
	 * @param threshold 
	 * @param alg
	 * @return
	 */
	private static List<CandidateJoinPoint> chooseCandidateJoinPoints(CandidateJoinPoint[] candidates, Region region, Regions regions,
			int[][] regionsMatrix, int threshold, DashboardSegmentation alg) {		
		List<CandidateJoinPoint> resultJoinPoints = new LinkedList<>();
		
		//double a = regions.w*regions.h;
		double joinAreaShare;
		
		// go through all candidates and find the optimal one
		for (int i = 0; i < candidates.length; i++) {
			if(candidates[i] != null) {
				if(candidates[i].d > threshold) {
					// filter big distances
					candidates[i] = null;
				} else {					
					if(candidates[i] != null) {						
						// get region from the join point and measure join attributes
						joinAreaShare = candidates[i].getJoinAreaShare();
						
						// apply heuristics...
						
						// experiments
						//if(candidates[i].share < 0.75)
						//if(share[i] < (0.01*((double) r2.area()/r.area()))) {
						//if(candidates[i].share < 0.4 || candidates[i].share < 0.75 && candidates[i].joinRegion.area()/a > 0.15) {
						
						if(joinAreaShare < 0.4) {
							// filter connection of small region with a very large one
							candidates[i] = null;
							
							// debug
							//System.out.println(points[i].x + "," + points[i].y);
							//System.out.println(r + " " + r.area() + " " + r2 + " " + r2.area());
							//System.out.println("share: " + candidates[i].share);
						} else {
							// append candidate to the list of result join points
							// keep the list sorted according to joinAreaShare
							int j = 0;
							for (CandidateJoinPoint sortedJoinPoint : resultJoinPoints) {
								if(joinAreaShare > sortedJoinPoint.getJoinAreaShare()) {
									break;
								}
								j++;
							}
							resultJoinPoints.add(j, candidates[i]);
						}
					}
				}
			}
		}
		
		// TODO we sort region according to the share variable
		// heuristics could be improved
		
		return resultJoinPoints;
	}
	
	/**
	 * Method returns object representing affected regions and boundary of affected area.
	 * 
	 * @param regions
	 * @param joinRegion
	 * @return
	 */
	private static AffectedRegionsOfJoin getAffectedRegions(Regions regions, Region joinRegion) {
		// perform flood-fill algorithm for the reference region in matrix
		
		// print all regions
		int[][] matrix = regions.printMatrix(GrayMatrix.BLACK);
		
		// iteratively add regions affected by join rectangle until there will no other affected region 
		Rectangle r_prev, r = joinRegion;
		do {
			// draw the join region in black color
			MatrixUtils.drawRectangle(matrix, r, GrayMatrix.BLACK, false);
			r_prev = r;
			// perform flood-fill algorithm to get boundary of the black area (Gestalt law of closure)
			// if the join region intersects regions the new rectangle will be higher than previous join rectangle
			r = SeedPixelUtil.processSeedPixel(r_prev.x, r_prev.y, GrayMatrix.BLACK, Regions.CLR_CANDIDATE_REGION, matrix);
		} while (!r.equals(r_prev));
		
		// create affected regions object
		AffectedRegionsOfJoin affectedRegions = new AffectedRegionsOfJoin();
		affectedRegions.boundary = r;
		
		// find regions that intersects the flood-fill region
		affectedRegions.regions = new LinkedList<>();
		for (Region actRegion : regions.candidate) {
			if(r.intersects(actRegion)) {
				affectedRegions.regions.add(actRegion);
			}
		}
		for (Region actRegion : regions.main) {
			if(r.intersects(actRegion)) {
				affectedRegions.regions.add(actRegion);
			}
		}
		
		return affectedRegions;
	}
	
	private static boolean isAffectingImportantRegions(Region region, CandidateJoinPoint joinPoint, AffectedRegionsOfJoin affectedRegions, Regions regions) {
		// variables for heuristics
		int a_root = regions.root.data.area();
		int a_affected = affectedRegions.boundary.width*affectedRegions.boundary.height;
		int a_region_and_candidate = region.area()+joinPoint.candidateRegion.area();
		//int a_join = joinPoint.joinRegion.area();
		//System.out.println(a + " " + (double )a_affected/a + " " + (double) a_affected/a_sum);
		
		// heuristics comparing affected area with area of dashboard and sum of the areas of region and candidate region 
		if(((double) a_affected/a_root > 0.2 && (double) a_affected/a_region_and_candidate > 3)
			|| ((double) a_affected/a_root > 0.4 && (double) a_affected/a_region_and_candidate > 1.5)
			/* || (double) a_affected/a_join > 3*/) {
			return true;
		}
		
		// it is not good to affect main regions
		//int limit = (int) (a_root*0.01);
		for (Region affectedRegion : affectedRegions.regions) {
			if(affectedRegion != region && affectedRegion != joinPoint.candidateRegion) {
				if(affectedRegion.category == Region.CATEGORY_MAIN) {
					return true;
				}
				
				// TODO improve heuristics
				// test how much it affect the main region
				// experiments
				/*if(region.area() > limit) {
					return true;
				}*/
			}
		}
		
		return false;
	}

	/**
	 * For experimental purposes.
	 * 
	 * @param matrix
	 * @param point
	 * @param color
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Region getRectangle(int[][] matrix, Point point, int color) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		Region r = new Region(0,0,0,0,Region.TYPE_RECT_FILL);
		
		// x1
		int i = point.x;
		while(i > 0 && matrix[i][point.y] == color) {
			i--;
		}
		r.x = ++i;
		
		// y1
		i = point.y;
		while(i > 0 && matrix[point.x][i] == color) {
			i--;
		}
		r.y = ++i;
		
		// x2
		i = point.x;
		while(i < mW && matrix[i][point.y] == color) {
			i++;
		}
		r.width = i-r.x;
		
		// y2
		i = point.y;
		while(i < mH && matrix[point.x][i] == color) {
			i++;
		}
		r.height = i-r.y;
		
		return r;
	}
	
	/**
	 * For experimental and debug purposes.
	 * 
	 * @param matrix
	 * @param region
	 * @param p
	 */
	@SuppressWarnings("unused")
	private static void connectRegionWithPoint(int[][] matrix, Region region, Point p) {
		int x1, x2, y1, y2;
		if(p.x >= region.x && p.x < region.x2()) { // x
			x1 = x2 = p.x;
			if(p.y < region.y) {
				y1 = p.y;
				y2 = region.y;
			} else {
				y1 = region.y2();
				y2 = p.y;
			}
		} else { // y
			y1 = y2 = p.y;
			if(p.x < region.x) {
				x1 = p.x;
				x2 = region.x;
			} else {
				x1 = region.x2();
				x2 = p.x;
			}
		}
		
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				matrix[i][j] = Color.RED.getRGB();
			}
		}
	}

}
