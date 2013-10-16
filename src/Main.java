public class Main {
    private static double[][] transition, emission, initial;
    private static int [] emissionSequence;
    private static Kattio io = new Kattio(System.in, System.out);
    private static int result = 0;

    public static void main(String[] args) {
        transition = handleInput();
        emission = handleInput();
        initial = handleInput();
        emissionSequence = handleSequenceInput();
        int[] stateSpace = spaceOf(emission);

        // Set initial state probability distribution as a one dimensional vector instead of a matrix.
        double[] pi = new double[initial[0].length];
        for(int i = 0; i < pi.length; i++)
            pi[i] = initial[0][i];

        int[] mostProbableSequence = viterbi(stateSpace, pi, emissionSequence, transition, emission);

        // Result to std out.
        for(int i = 0; i < mostProbableSequence.length; i++)
            System.out.print(mostProbableSequence[i] + " ");
        System.out.println();

        // Close IO-stream
        io.close();
    }

    private static int[] spaceOf(double[][] matrix) {
        int[] spaceSet = new int[matrix.length];
        for(int i = 0; i < spaceSet.length; i++)
            spaceSet[i] = i;
        return spaceSet;
    }

    private static int[] viterbi(int[] S, double[] pi, int[] Y, double[][] A, double[][] B) {
        int K = transition.length;
        int T = emissionSequence.length;
        double[][] T_1 = new double[K][T];
        int[][] T_2 = new int[K][T];

        for(int i = 0; i < K; i++) {
            T_1[i][0] = (pi[i] * B[i][Y[0]]);
            T_2[i][0] = 0;
        }

        for(int i = 1; i < T; i++) {
            for(int j = 0; j < K; j++) {
                double maxValue = 0;
                int maxArg = 0;
                for(int k = 0; k < K; k++){
                    double value = T_1[k][i-1] * A[k][j] * B[j][Y[i]];
                    if(value > maxValue) {
                        maxValue = value;
                        maxArg = k;
                    }
                }
                T_1[j][i] = maxValue;
                T_2[j][i] = maxArg;
            }
        }

        int[] Z = new int[T];
        int[] X = new int[T];
        double value = 0;

        for(int k = 0; k < K; k++) {
            if(T_1[k][T-1] > value)
                Z[T-1] = k;
        }

        X[T-1] = S[Z[T-1]];

        for(int i = T-1; i > 0; i--) {
            Z[i-1] = T_2[Z[i]][i];
            X[i-1] = S[Z[i-1]];
        }

        return X;

    }


    private static void forward(double[][] transition, double[][] emission, int[] sequence, int state, int index, double prob){
        if(emissionSequence.length == index) {
            result += prob;
            return;
        }

        for(int i = 0; i < transition.length; i++) {
            double current = prob * transition[state][i] * emission[i][emissionSequence[index]];
            if(current > 0)
                forward(transition, emission, sequence, i, index+1, current);
        }
    }

    private static double[][] handleInput() {
        int rows = io.getInt();
        int cols = io.getInt();
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = io.getDouble();
            }
        }
        return matrix;
    }

    private static int[] handleSequenceInput() {
        int rows = io.getInt();
        int[] sequence = new int[rows];
        for(int i = 0; i < rows; i++)
            sequence[i] = io.getInt();
        return sequence;
    }

    private static double forwardProc(int[] o, int numStates, double[] pi,double[][] a, double[][] b) {
        int T = o.length;
        double[][] fwd = new double[numStates][T];
        double lol = 0;

    /* initialization (time 0) */
        for (int i = 0; i < numStates; i++) {
            fwd[i][0] = pi[i] * b[i][o[0]];
            if(fwd[i][0] != 0)
                lol = fwd[i][0];
        }


    /* induction */
        for (int t = 0; t <= T-2; t++) {
            for (int j = 0; j < numStates; j++) {
                fwd[j][t+1] = 0;
                for (int i = 0; i < numStates; i++)
                    fwd[j][t+1] += (fwd[i][t] * a[i][j]);
                fwd[j][t+1] *= b[j][o[t+1]];
                if(fwd[j][t+1] != 0)
                    lol = fwd[j][t+1];
            }
        }

        return lol;
    }

    private static void printMatrix(double[][] matrix) {
        int rows = 0;
        int cols = 0;
        String line = "";
        for (double[] row : matrix) {
            rows++;
            for (double j : row) {
                cols++;
                line += (j + " ");
            }
        }
        System.out.println(rows + " " + cols + " " + line);
    }

    // Delete me!
    private static double[] convertMatrix(double[][] matrix) {
        double[] vector = new double[matrix[0].length];
        int i = 0;

        for (double[] row : matrix) {
            i++;
            for (double j : row) {
                vector[i] = j;
            }
        }
        return vector;
    }

    // Delete me!
    private static void printSequence(int[] sequence) {
        for(int i : sequence)
            System.out.print(i);
        System.out.println();
    }

    // Delete me!
    private static void printPi(double[] sequence) {
        for(double i : sequence)
            System.out.print(i);
        System.out.println();
    }

    // return C = A * B
    public static double[][] multiply(double[][] A, double[][] B) {
        int mA = A.length;
        int nA = A[0].length;
        int mB = B.length;
        int nB = B[0].length;
        if (nA != mB) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] C = new double[mA][nB];
        for (int i = 0; i < mA; i++)
            for (int j = 0; j < nB; j++)
                for (int k = 0; k < nA; k++)
                    C[i][j] += (A[i][k] * B[k][j]);
        return C;
    }
}