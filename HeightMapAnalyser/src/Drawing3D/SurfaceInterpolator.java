package Drawing3D;

public class SurfaceInterpolator {
	/**
	 * Linearly interpolates a surface (adding "steps" number of indices between the 4 corners)
	 * @param topLeft Top Left Height
	 * @param bottomLeft Bottom Left Height
	 * @param bottomRight Bottom Right Height
	 * @param topRight Top Right Height
	 * @param steps
	 * @return 2D grid containing interpolation
	 */
	public static double[][] interpolateSurface(double topLeft, double bottomLeft, 
			double bottomRight, double topRight, int steps){
		float intrp = (steps+1); 
		double[][] result = new double[steps+2][steps+2];
		double diffE1A = (topLeft - bottomLeft);
		double diffE2A = (topLeft - topRight);
		double diffE1B = (bottomLeft - bottomRight);
		double diffE2B = (topRight - bottomRight);
		double E1AStep = (diffE1A/intrp);
		double E2AStep = (diffE2A/intrp);
		double E1BStep = (diffE1B/intrp);
		double E2BStep = (diffE2B/intrp);
		
		result[0][0] = topLeft;
		result[steps+1][0] = bottomLeft;
		result[steps+1][steps+1] = bottomRight;
		result[0][steps+1] = topRight;
		for (int y = 0; y < steps + 2; ++y)
			for (int x = 0; x < steps + 2; ++x)
			{
				if ((y == 0 && x == 0) || (y == steps + 1 && x == 0) || (y == steps + 1 && x == steps + 1) || (y == 0 && x == steps + 1))
					continue;
				if (x == 0)
					result[y][0] = (topLeft - E1AStep*y);
				else if (x == steps+1)
					result[y][steps+1] = (topRight - E2BStep*y);
				else if (y == 0)
					result[0][x] = (topLeft - E2AStep*x);
				else if (y == steps+1)
					result[steps+1][x] = (bottomLeft - E1BStep*x);
				else result[y][x] = (result[y-1][x]+result[y][x-1])*0.5; 
			}
		return result;
	}
}
