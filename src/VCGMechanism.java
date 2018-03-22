import java.util.Arrays;

public class VCGMechanism {
	
	private static double[][] dataMatrix = new double[10][10];
	private static double[] bidders = {0.93,1.27,1.31,1.49,1.24,1.45,0,0,0,0};
	private static double[] sellers = {0.5,1.13,1.18,0.89,1.16,0.96,1.16,1.14,0.91,1.08};
	
	//col: bidders
	//row: drivers
	
	public static void processData() {
		for(int i=0; i<sellers.length; i++) {
			for(int j=0; j<bidders.length; j++) {
				if((bidders[j] - sellers[i])>0)
					dataMatrix[i][j] = bidders[j] - sellers[i];
				else
					dataMatrix[i][j] = 0;
			}
		}			
	}
	
	public static double[][] cloneMatrix(double[][] matrix){
		double[][] tmp = new double[matrix.length][matrix.length];
		for(int row = 0; row < matrix.length; row++){
			tmp[row] = matrix[row].clone();
		}
		return tmp;
	}
	
	public static void removeBidder(double[][] matrix, int winner) {
		for(int row = 0; row < matrix.length; row++){
			matrix[row][winner] = 0;
		}
	}
	
	public static void removeSeller(double[][] matrix, int winner) {
		for(int row = 0; row < matrix.length; row++){
			matrix[winner][row] = 0;
		}
	}
	
	public static void removePair(double[][] matrix, int bidder, int seller) {
		bidder-=1;
		seller-=1;
		for(int row = 0; row < matrix.length; row++){
			for(int col = 0; col < matrix[0].length; col++) {
				if(col == bidder) {
					matrix[row][col] = 0;
				}
				if(row==seller) {
					matrix[row][col] = 0;
				}
			}
		}
	}
	
	public static double getWelfare(double[][] data, int[] pair) {
		double welfare = 0;
		for(int i=0; i<pair.length; i++) {
//			System.out.println("pair[" + pair[i] +"]["+ i +"] = " + data[pair[i]][i]);
			welfare += data[pair[i]][i];
		}
		
		return welfare;
	}
	
	public static double getWelfareExceptBidder(int[] pair, int bidder) {
		double welfare = 0;
		for(int i=0; i<pair.length; i++) {
//			System.out.println("pair[" + pair[i] +"]["+ i +"] = " + dataMatrix[pair[i]][i]);
			welfare += dataMatrix[pair[i]][i];
		}
		
		return welfare - dataMatrix[pair[bidder]][bidder];
	}
	
	public static double getWelfareExceptSeller(int[] pair, int seller) {
		double welfare = 0;
		for(int i=0; i<pair.length; i++) {
			welfare += dataMatrix[i][pair[i]];
		}	
		return welfare - dataMatrix[seller][pair[seller]];
	}
	
	public static void main(String[] args) {
		processData();
				
		for (double[] rows : dataMatrix) {
			for (double col : rows) {
				System.out.format("%.2f | ", col);
			}
			System.out.println();
		}
		System.out.println("-----");
		
		System.out.print("\nhttp://www.hungarianalgorithm.com/solve.php?c=");
		for (double[] rows : dataMatrix) {
			for (double col : rows) {
				System.out.format("%.2f-", col);
			}
			System.out.print("-");
		}
		System.out.print("&obj=max&steps=0\n");
		
//		HungAlgo alloc = new HungAlgo(dataMatrix);
//		int[] winners = alloc.execute();
//		System.out.println(Arrays.toString(winners));
//		System.out.println(getWelfare(dataMatrix, alloc.execute()));
		
		int bidderConsidered = 6 -1;
		int sellerConsidered = 6 -1;
		
		
		System.out.println("-----------Contribution of bidder-------------");
		double bidder[][] = cloneMatrix(dataMatrix);
		
		removeBidder(bidder, bidderConsidered);
		
		System.out.print("http://www.hungarianalgorithm.com/solve.php?c=");
		for (double[] rows : bidder) {
			for (double col : rows) {
				System.out.format("%.2f-", col);
			}
			System.out.print("-");
		}
		System.out.print("&obj=max&steps=0");
	
		
		System.out.println("\n-----------Contribution of seller-------------");
		double seller[][] = cloneMatrix(dataMatrix);
		
		removeSeller(seller, sellerConsidered);
		

		System.out.print("http://www.hungarianalgorithm.com/solve.php?c=");
		for (double[] rows : seller) {
			for (double col : rows) {
				System.out.format("%.2f-", col);
			}
			System.out.print("-");
		}
		System.out.print("&obj=max&steps=0");
		
		
		System.out.println("\n-----------Contribution of pair-------------");
		double pair[][] = cloneMatrix(dataMatrix);
		
		removePair(pair, 5,4);
		
		System.out.print("http://www.hungarianalgorithm.com/solve.php?c=");
		for (double[] rows : pair) {
			for (double col : rows) {
				System.out.format("%.2f-", col);
			}
			System.out.print("-");
		}
		System.out.print("&obj=max&steps=0");
	
	
	}
	
	
}
