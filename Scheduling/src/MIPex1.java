
import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

public class MIPex1 {

	public static int n, nA, nB; //
	public static Double[] d;//
	public static Double alpha, beta;
	public static Double[][] gamma, delta, epsilon;
	public static int p[]; //
	public static Double w[][];

	public static int Pa, Pb;
	public static double x_val[][];

	public static double subgrad_alpha = 0., subgrad_beta = 0;
	public static double[][] subgrad_gamma, subgrad_delta, subgrad_epsilon;

	public static void main(String[] args) throws IloException {
		initParam();
		IloCplex cplex = new IloCplex();
		IloNumVar[][] x = new IloNumVar[n][];
		for (int i = 0; i < n; i++)
			x[i] = cplex.numVarArray(n, 0, Double.MAX_VALUE);

		IloLinearNumExpr fo = cplex.linearNumExpr();

		for (int t = 0; t < n; t++)
			for (int j = 0; j < n; j++) {
				fo.addTerm(gamma[j][t] * d[t], x[j][t]);
				fo.addTerm(-epsilon[j][t] * d[t], x[j][t]);
				for (int l = 0; l < n; l++)
					for (int k = 0; k < t - 1; k++) {
						fo.addTerm(gamma[j][t] * p[l], x[l][k]);
						fo.addTerm(-delta[j][t] * p[l], x[l][k]);
					}
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

		if (cplex.solve()) {
			updateOptimalW(cplex, x);
			updateSubGrad();

		}

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

	private static void updateSubGrad() {
		for (int t = 0; t < n; t++) {
			for (int j = 0; j < nA; j++)
				subgrad_alpha += w[j][t] / nA;
			for (int j = nA; j < n; j++)
				subgrad_alpha -= w[j][t] / nB;

		}
		subgrad_alpha += Pa / nA;
		subgrad_alpha -= Pb / nB;

		for (int t = 0; t < n; t++) {
			for (int j = 0; j < nA; j++)
				subgrad_beta -= w[j][t] / nA;
			for (int j = nA; j < n; j++)
				subgrad_beta += w[j][t] / nB;

		}
		subgrad_beta -= Pa / nA;
		subgrad_beta += Pb / nB;

		for (int t = 0; t < n; t++)
			for (int j = 0; j < n; j++) {
				subgrad_gamma[j][t] = d[t] * x_val[j][t];
				subgrad_gamma[j][t] -= (w[j][t] + d[t]);

				subgrad_delta[j][t] = w[j][t];

				subgrad_epsilon[j][t] = w[j][t] - d[t] * x_val[j][t];

				for (int l = 0; j < n; l++)
					for (int k = 0; k < t - 1; k++) {
						subgrad_gamma[j][t] += p[l] * x_val[l][k];

						subgrad_delta[j][t] -= p[l] * x_val[l][k];
					}
			}

	}

	private static void updateOptimalW(IloCplex cplex, IloNumVar[][] x) throws UnknownObjectException, IloException {
		for (int i = 0; i < n; i++)
			x_val[i] = cplex.getValues(x[i]);

		for (int t = 0; t < n; t++) {
			for (int j = 0; j < nA; j++)
				if (alpha / nA + beta / nB - gamma[j][t] + delta[j][t] + epsilon[j][t] > 0)
					w[j][t] = 0.;
				else
					w[j][t] = d[t];
			for (int j = nA; j < n; j++)
				if (beta / nB - alpha / nA - gamma[j][t] + delta[j][t] + epsilon[j][t] > 0)
					w[j][t] = 0.;
				else
					w[j][t] = d[t];
		}

	}

	private static void initParam() {
		alpha = 0.;
		beta = 0.;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				gamma[i][j] = 0.;
				delta[i][j] = 0.;
				epsilon[i][j] = 0.;
			}

		Pa = 0;
		Pb = 0;
		for (int i = 0; i < nA; i++)
			Pa += p[i];

		for (int i = nA; i < nA + nB; i++)
			Pb += p[i];

	}
}
