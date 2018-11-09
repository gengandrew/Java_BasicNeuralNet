import java.util.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
public class Neural {
	private static double testAccuracy(String filename, double[] weights) {
		double output = 0;
		File inputFile = null;
		try {
			inputFile = new File(filename);
			Scanner filescnr = new Scanner(inputFile);
			while(filescnr.hasNextLine()) {
				double x1 = 0;
				double x2 = 0;
				double y = 0;
				String placeHolder = filescnr.nextLine();
				Scanner scnr = new Scanner(placeHolder);
				while(scnr.hasNext()) {
					x1 = scnr.nextDouble();
					x2 = scnr.nextDouble();
					y = scnr.nextDouble();
				}
				double myOut = Neural.findVj('C', x1, x2, weights);
				double lowB = (double)y - 0.5;
				double highB = (double)y + 0.5;
				if(myOut > lowB && myOut < highB) {
					output = output + 1;
				}
			}
			return output;
		} catch(Exception E) {
			System.out.println("File Not Find.");
			return -1;
		}
	}
	
	private static double[] epoch(double[] myWeights, double n) {
		double[] weights = myWeights;
		File inputFile = null;
		try {
			inputFile = new File("input_data");
			Scanner filescnr = new Scanner(inputFile);
			while(filescnr.hasNextLine()) {
				double x1 = 0;
				double x2 = 0;
				double y = 0;
				String placeHolder = filescnr.nextLine();
				Scanner scnr = new Scanner(placeHolder);
				while(scnr.hasNext()) {
					x1 = scnr.nextDouble();
					x2 = scnr.nextDouble();
					y = scnr.nextDouble();
				}
				weights = Neural.updateW(x1, x2, y, n, weights);		
			}
			return weights;
		} catch(Exception E) {
			System.out.println("File Not Find.");
			return null;
		}
	}
	
	private static double[] readArgs(String[] args) {
		double[] weights = new double[9];
		for(int i = 1; i < 10; i++) {
			weights[i-1] = Double.parseDouble(args[i]);
		}
		return weights;
	}
	
	private static double findEvalSetError(String filename, double[] weights) {
		try {
			double vc = 0;
			File inputFile = new File(filename);
			Scanner filescnr = new Scanner(inputFile);
			while(filescnr.hasNextLine()) {
				double x1 = 0;
				double x2 = 0;
				double y = 0;
				String placeHolder = filescnr.nextLine();
				Scanner scnr = new Scanner(placeHolder);
				while(scnr.hasNext()) {
					x1 = scnr.nextDouble();
					x2 = scnr.nextDouble();
					y = scnr.nextDouble();
				}
				//System.out.println(placeHolder + " lol");
				double placeHolder2 = Neural.findVj('C', x1, x2, weights);
				placeHolder2 = placeHolder2 - (double)y;
				placeHolder2 = Math.pow(placeHolder2, 2);
				placeHolder2 = placeHolder2/(double)2;
				vc = vc + placeHolder2;
			}
			return vc;
		} catch(Exception E) {
			System.out.println("File Not Find.");
			return -10000;
		}
	}
	
	private static double[] updateW(double x1, double x2, double y, double n, double[] weights) {
		double dW;
		double[] out = new double[9];
		for(int i = 1; i < 10; i++) {
			dW = Neural.finddW(i, x1, x2, y, weights);
			dW = (double)n * dW;
			dW = (double)weights[i-1] - dW;
			out[i-1] = dW;
		}
		return out;
	}
	
	private static double finddW(int current, double x1, double x2, double y, double[] weights) {
		char input;
		if(current <= 3) {
			input = 'A';
		} else if (current <= 6) {
			input = 'B';
		} else {
			input = 'C';
		}
		double vi = Neural.findVi(x1, x2, current, weights);
		double dEU = Neural.finddU(input, x1, x2, y, weights);
		double dEW = (double)(vi*dEU);
		return dEW;
	}
	
	private static double finddU(char input, double x1, double x2, double y, double[] weights) {
		if(input == 'C') {
			double dEU = Neural.findVj('C', x1, x2, weights);
			dEU = 1 - dEU;
			dEU = (Neural.findVj('C', x1, x2, weights))*(dEU);
			double dEV = Neural.finddVj(input, x1, x2, y, weights);
			dEU = (dEV)*(dEU);
			return dEU;
		} else if (input == 'A') {
			double ua = Neural.findU('A', x1, x2, weights);
			if(ua > 0 || ua == 0) {
				ua = 1;
			} else {
				ua = 0;
			}
			double dEV = Neural.finddVj('A', x1, x2, y, weights);
			double output = (double)(dEV*ua);
			return output;
		} else {
			double ub = Neural.findU('B', x1, x2, weights);
			if(ub > 0 || ub == 0) {
				ub = 1;
			} else {
				ub = 0;
			}
			double dEV = Neural.finddVj('B', x1, x2, y, weights);
			double output = (double)(dEV*ub);
			return output;
		}
	}
	
	private static double finddVj(char input, double x1, double x2, double y, double[] weights) {
		if(input == 'C') {
			double dEV = Neural.findVj(input, x1, x2, weights);
			dEV = dEV - y;
			return dEV;
		} else if(input == 'A'){
			double dEU = Neural.finddU('C', x1, x2, y, weights);
			double dEV = dEU * (double)weights[7];
			return dEV;
		} else {
			double dEU = Neural.finddU('C', x1, x2, y, weights);
			double dEV = dEU * (double)weights[8];
			return dEV;
		}
	}
	
	private static double findE(char input, double x1, double x2, double y, double[] weights) {
		double E = Neural.findVj(input, x1, x2, weights);
		E = E - y;
		E = Math.pow(E, 2);
		E = E/(double)2;
		return E;
	}
	
	private static double findVj(char input, double x1, double x2, double[] weights) {
		if(input == 'A' || input == 'B') {
			double u = Neural.findU(input, x1, x2, weights);
			if(u > 0) {
				return u;
			} else {
				return 0;
			}
		} else {
			double u = Neural.findU(input, x1, x2, weights);
			u = u*(-1);
			u = Math.exp(u);
			u = 1 + u;
			u = (double)(1/u);
			return u;
		}
	}
	
	private static double findVi(double x1, double x2, int current, double[] weights) {
		if(current == 1 || current == 4 || current == 7) {
			return 1;
		}else if(current == 2 || current == 5) {
			return x1;
		}else if(current == 3 || current == 6) {
			return x2;
		}else if(current == 8){
			double output = Neural.findVj('A', x1, x2, weights);
			return output;
		}else {
			double output = Neural.findVj('B', x1, x2, weights);
			return output;
		}
	}
	
	private static double findU(char input, double x1, double x2, double[] weights) {
		double output = 0;
		if(input == 'A') {
			for(int i = 1; i < 4; i++) {
				double v = Neural.findVi(x1, x2, i, weights);
				double w = weights[i-1];
				double placeHolder = (double)w*v;
				output = output + placeHolder;
			}
		}else if(input == 'B') {
			for(int i = 4; i < 7; i++) {
				double v = Neural.findVi(x1, x2, i, weights);
				double w = weights[i-1];
				double placeHolder = (double)w*v;
				output = output + placeHolder;
			}
		} else {
			for(int i = 7; i < 10; i++) {
				double v = Neural.findVi(x1, x2, i, weights);
				double w = weights[i-1];
				double placeHolder = (double)w*v;
				output = output + placeHolder;
			}
		}
		return output;
	}
	
	public static void main(String[] args) {
		int flag = Integer.parseInt(args[0]);
		NumberFormat format = new DecimalFormat("#0.00000");
		if(flag == 100) {
			double[] weights = Neural.readArgs(args);
			double x1 = Double.parseDouble(args[10]);
			double x2 = Double.parseDouble(args[11]);
			char input = 'A';
			for(int i = 0; i < 3; i++) {
				input = (char)((int)input + i);
				double u = Neural.findU(input, x1, x2, weights);
				double v = Neural.findVj(input, x1, x2, weights);
				if(i < 2) {
					System.out.print(format.format(u) + " " + format.format(v) + " ");
				} else {
					System.out.print(format.format(u) + " " + format.format(v));
				}
			}
		}
		
		if(flag == 200) {
			double[] weights = Neural.readArgs(args);
			double x1 = Double.parseDouble(args[10]);
			double x2 = Double.parseDouble(args[11]);
			double y = Double.parseDouble(args[12]);

			double E = Neural.findE('C', x1, x2, y, weights);
			double dEV = Neural.finddVj('C', x1, x2, y, weights);
			double dEU = Neural.finddU('C', x1, x2, y, weights);			
			System.out.print(format.format(E) + " " + format.format(dEV) + " " + format.format(dEU));
		}
		
		if(flag == 300) {
			double[] weights = Neural.readArgs(args);
			double x1 = Double.parseDouble(args[10]);
			double x2 = Double.parseDouble(args[11]);
			double y = Double.parseDouble(args[12]);
			
			char input = 'A';
			for(int i = 0; i < 2; i++) {
				input = (char)((int)input + i);
				double dEV = Neural.finddVj(input, x1, x2, y, weights);
				double dEU = Neural.finddU(input, x1, x2, y, weights);
				if(i < 1) {
					System.out.print(format.format(dEV) + " " + format.format(dEU) + " ");
				} else {
					System.out.print(format.format(dEV) + " " + format.format(dEU));
				}
			}
		}
		
		if(flag == 400) {
			double[] weights = Neural.readArgs(args);
			double x1 = Double.parseDouble(args[10]);
			double x2 = Double.parseDouble(args[11]);
			double y = Double.parseDouble(args[12]);
			
			for(int i = 1; i < 10; i++) {
				double dEW = Neural.finddW(i, x1, x2, y, weights);
				if(i < 9) {
					System.out.print(format.format(dEW) + " ");
				} else {
					System.out.print(format.format(dEW));
				}
			}
		}
		
		if(flag == 500) {
			double[] weights = Neural.readArgs(args);
			double x1 = Double.parseDouble(args[10]);
			double x2 = Double.parseDouble(args[11]);
			double y = Double.parseDouble(args[12]);
			double n = Double.parseDouble(args[13]);
			
			for(int i = 0; i < weights.length; i++) {
				if(i < weights.length - 1) {
					System.out.print(format.format(weights[i]) + " ");
				} else {
					System.out.print(format.format(weights[i]) + "\n");
				}
			}
			double error = Neural.findE('C', x1, x2, y, weights);
			System.out.println(format.format(error));
			
			double[] out = Neural.updateW(x1, x2, y, n, weights);
			for(int i = 0; i < out.length; i++) {
				if(i < 8) {
					System.out.print(format.format(out[i]) + " ");
				} else {
					System.out.print(format.format(out[i]) + "\n");
				}
			}
			double newError = Neural.findE('C', x1, x2, y, out);
			System.out.println(format.format(newError));
		}
		
		if(flag == 600) {
			double[] weights = Neural.readArgs(args);
			double n = Double.parseDouble(args[10]);
			File inputFile = null;
			try {
				inputFile = new File("input_data");
				Scanner filescnr = new Scanner(inputFile);
				while(filescnr.hasNextLine()) {
					double x1 = 0;
					double x2 = 0;
					double y = 0;
					String placeHolder = filescnr.nextLine();
					Scanner scnr = new Scanner(placeHolder);
					while(scnr.hasNext()) {
						x1 = scnr.nextDouble();
						x2 = scnr.nextDouble();
						y = scnr.nextDouble();
					}
					System.out.println(format.format(x1) + " " + format.format(x2) + " " + format.format(y));
					weights = Neural.updateW(x1, x2, y, n, weights);
					for(int i = 0; i < weights.length; i++) {
						if(i < 8) {
							System.out.print(format.format(weights[i]) + " ");
						} else {
							System.out.print(format.format(weights[i]) + "\n");
						}
					}
					double vc = Neural.findEvalSetError("hw2_midterm_A_eval.txt", weights);
					System.out.println(format.format(vc));				}
			} catch(Exception E) {
				System.out.println("File Not Find.");
			}
		}
		
		if(flag == 700) {
			double[] weights = Neural.readArgs(args);
			double n = Double.parseDouble(args[10]);
			int T = Integer.parseInt(args[11]);
			
			for(int i = 0; i < T; i++) {
				weights = Neural.epoch(weights, n);
				double vc = Neural.findEvalSetError("input_data", weights);
				for(int j = 0; j < weights.length; j++) {
					if(j < 8) {
						System.out.print(format.format(weights[j]) + " ");
					} else {
						System.out.print(format.format(weights[j]) + "\n");
					}
				}
				System.out.println(format.format(vc));	
			}
		}
		
		if(flag == 800) {
			double[] weights = Neural.readArgs(args);
			double n = Double.parseDouble(args[10]);
			int T = Integer.parseInt(args[11]);
			double vc = 0;
			double prevVC = 100000000;
			int totLoops = 0;
			
			for(int i = 0; i < T; i++) {
				weights = Neural.epoch(weights, n);
				vc = Neural.findEvalSetError("input_data", weights);
				if(vc > prevVC) {
					totLoops = i + 1;
					break;
				} else {
					prevVC = vc;
				}
			}
			
			System.out.println(totLoops);
			for(int j = 0; j < weights.length; j++) {
				if(j < 8) {
					System.out.print(format.format(weights[j]) + " ");
				} else {
					System.out.print(format.format(weights[j]) + "\n");
				}
			}
			System.out.println(format.format(vc));
			double acc = Neural.testAccuracy("input_data", weights);
			acc = acc/(double)25;
			System.out.println(format.format(acc));
		}
	}
}
