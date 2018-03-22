import java.text.DecimalFormat;
import java.util.Arrays;




public class TestHungAlgo {
	
	private static DecimalFormat form = new DecimalFormat("#.##");
	
	private static double[] bidders = {1.15,1.15,1.09,1.25,1.1,1.18,1.33,1.26,1.4,1,1.39,1.11,1.48,1.29,1.07};
	private static double[] sellers = {1.24,1.21,0.88,1.04,1.26,1.18,1.02,0.93,1.05,1.12};
	private static double[][] dataMatrix = new double[bidders.length][bidders.length];
	
	//Generate matrix from bidders and sellers
	public static void processData() {
		double[] newBidders;
		double[] newSellers;
		if(bidders.length > sellers.length) {
			newSellers = new double[bidders.length];
			Arrays.fill(newSellers, Double.MAX_VALUE);
			for(int i=0; i<sellers.length; i++) {
				newSellers[i] = sellers[i];
			}
			newBidders = bidders;
		}
		if(bidders.length < sellers.length) {
			newBidders = new double[sellers.length];
			Arrays.fill(newBidders, 0);
			for(int i=0; i<bidders.length; i++) {
				newBidders[i] = bidders[i];
			}
			newSellers = sellers;
		}
		else {
			newBidders = bidders;
			newSellers = sellers;
		}
		
		for(int i=0; i<newSellers.length; i++) {
			for(int j=0; j<newBidders.length; j++) {
				if((newBidders[j] - newSellers[i])>0)
					dataMatrix[i][j] = newBidders[j] - newSellers[i];
				else
					dataMatrix[i][j] = 0;
			}
		}			
	}

	//Clone matrix
	public static double[][] cloneMatrix(double[][] matrix){
		double[][] tmp = new double[matrix.length][matrix.length];
		for(int row = 0; row < matrix.length; row++){
			tmp[row] = matrix[row].clone();
		}
		return tmp;
	}
	
	//Generate matrix without bidder
	public static double[][] removeBidder(double[][] matrix, int winner) {
		for(int row = 0; row < matrix.length; row++){
			matrix[row][winner] = 0;
		}
		return matrix;
	}
	
	//Generate matrix without seller
	public static double[][] removeSeller(double[][] matrix, int winner) {
		for(int row = 0; row < matrix.length; row++){
			matrix[winner][row] = 0;
		}
		return matrix;
	}
	
	//Get welfare from winners
	public static double getWelfare(double[][] data, int[][] pair) {
		double welfare = 0;
		for(int i=0; i<pair.length; i++) {
//			System.out.println("contribution of : [" + pair[i][0] + "][" + pair[i][1] + "]: " +  data[pair[i][0]][pair[i][1]]);
			welfare += data[pair[i][0]][pair[i][1]];
		}
		
		return welfare;
	}
	
	//Get winners from result after running execute() function
	public static int[][] getWinners(double[][] data, int[][] pair){
		int count=0;
		for(int i=0; i<data[0].length; i++){
			if(data[pair[i][0]][i] > 0.0001) {
//				System.out.println(data[pair[i][0]][i]);
				count++;
			}
		}
		int[][] winners = new int[count][2];
		count = 0;
		for(int i=0; i<data[0].length; i++){
			if(data[pair[i][0]][i] > 0.0001) {
				winners[count][0] = pair[i][0];
				winners[count][1] = pair[i][1];
				count++;
			}
		}
		return winners;
	}
	
	//Print out pairs of winners and their price diff
	public static void printWinner(double[][] data, int[][] winners) {
		for(int i=0; i<winners.length; i++) {
			System.out.println("Bidder: " + bidders[winners[i][1]] + " - Seller: " + sellers[winners[i][0]]);
			System.out.println("Seller-Bidder: ["+ winners[i][0] +"]["+ winners[i][1] +"] = " + form.format(data[winners[i][0]][winners[i][1]]));
		}
	}
	
	//Print out matrix
	public static void printData(double[][] matrix) {
		for (double[] rows : matrix) {
			for (double col : rows) {
				System.out.format("%.2f | ", col);
			}
			System.out.println();
		}
	}
	
	//Get welfare when bidder(winner) was absent
	public static double getWelfareWhenBidderAbsent(double[][] data, int bidderID, double welfare) {
		//new data without bidder i  = removeBidder(cloneMatrix(data), bidderID)
		hungarian hung =  new hungarian(removeBidder(cloneMatrix(data), bidderID));
		int[][] newWinners = getWinners(removeBidder(cloneMatrix(data), bidderID), hung.execute());
//		printData(removeBidder(cloneMatrix(data), bidderID));
		return getWelfare(removeBidder(cloneMatrix(data), bidderID), newWinners);
	}
	
	//Get welfare when seller(winner) was absent
	public static double getWelfareWhenSellerAbsent(double[][] data, int sellerID, double welfare) {
		hungarian hung =  new hungarian(removeSeller(cloneMatrix(data), sellerID));
		int[][] newWinners = getWinners(removeSeller(cloneMatrix(data), sellerID), hung.execute());
//		printData(removeSeller(cloneMatrix(data), sellerID));
		return getWelfare(removeSeller(cloneMatrix(data), sellerID), newWinners);
	}
	
	//Get welfare of the others
	public static double getWelfareOfOthers(double[][] data, int sellerID, int bidderID, double welfare) {
		return (welfare - data[sellerID][bidderID]);
	}
	
	public static void main(String[] args) {
		processData();
		
		double[][] originalData = cloneMatrix(dataMatrix);
		
		printData(dataMatrix);
		
		hungarian test1 = new hungarian(dataMatrix);
		int[][] winners = getWinners(originalData,  test1.execute());
		System.out.println("=======================");
		printWinner(originalData, winners);
		double welfare = getWelfare(originalData, winners);
		System.out.println("welfare: " + form.format(welfare));
		System.out.println("=======================");
		
		for(int i=0; i<winners.length; i++) {
			double welfareWhenBidderAbsent = getWelfareWhenBidderAbsent(originalData, winners[i][1], welfare);
			double welfareWhenSellerAbsent = getWelfareWhenSellerAbsent(originalData, winners[i][0], welfare);
			double welfareOfOthers = getWelfareOfOthers(originalData, winners[i][0], winners[i][1], welfare);
			System.out.println("wefare when bidder "+ winners[i][1] + " absent:" + form.format(welfareWhenBidderAbsent));
			System.out.println("wefare when seller "+ winners[i][0] + " absent:" + form.format(welfareWhenSellerAbsent));
			System.out.println("welfare of others: " + form.format(welfareOfOthers));
			double bidderContribution = welfareWhenBidderAbsent-welfareOfOthers;
			double sellerContribution = welfareWhenSellerAbsent-welfareOfOthers;
			System.out.println("Contridbution of bidder:" + form.format(bidderContribution));
			System.out.println("Contridbution of seller:" + form.format(sellerContribution));
			System.out.println("Total contribution: " + form.format(bidderContribution + sellerContribution));
			System.out.println("------------------------");
		}
	}
	
}
