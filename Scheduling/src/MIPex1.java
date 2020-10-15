
import ilog.concert.*;
import ilog.cplex.*;

public class MIPex1 {

	public static int n, nA, nB;
	public static int[] d;
	public static Double alpha, beta;
	public static Double[][] gamma, delta, epsilon;

	public static void main(String[] args) throws IloException {
		IloCplex cplex = new IloCplex();
		IloNumVar[][] x = new IloNumVar[n][];
		for (int i = 0; i < n; i++)
			x[i] = cplex.numVarArray(n, 0, Double.MAX_VALUE);

		IloLinearNumExpr fo = cplex.linearNumExpr();

		for (int t = 0; t < n; t++)
			for (int j = 0; j < n; j++) {
				fo.addTerm(gamma[j][t] * d[t], x[j][t]);
				fo.addTerm(-epsilon[j][t] * d[t], x[j][t]);
			}
		cplex.addMinimize(fo);

		// assignment
		for (int j = 0; j < n; j++) {
			cplex.addEq(cplex.sum(x[j]), 1);
		}

		for (int t = 0; t < n; t++) {
			IloLinearNumExpr t_th_constraints = cplex.linearNumExpr();
			for (int j = 0; j < n; j++)
				t_th_constraints.addTerm(1, x[j][t]);
			cplex.addEq(1, t_th_constraints);
		}
		// end assignment

//         if ( cplex.solve() ) {
//            double[] x     = cplex.getValues(var[0]);
//            double[] slack = cplex.getSlacks(rng[0]);
//
//            System.out.println("Solution status = " + cplex.getStatus());
//            System.out.println("Solution value  = " + cplex.getObjValue());
//
//            for (int j = 0; j < x.length; ++j) {
//               System.out.println("Variable " + j + ": Value = " + x[j]);
//            }
//
//            for (int i = 0; i < slack.length; ++i) {
//               System.out.println("Constraint " + i + ": Slack = " + slack[i]);
//            }
//         }
// 
//         cplex.exportModel("mipex1.lp");
	}
}
