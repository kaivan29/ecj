package ec.app.moobenchmarks;

import ec.*;
import ec.util.*;
import ec.vector.*;
import ec.simple.*;
import ec.multiobjective.*;

/**
 * 
 * ZDT-T3  The Pareto-optimal front is discrete, it consists
 * of several noncontiguous convex parts.
 * 
 * <p>The Pareto-optimal front is formed with g(X) = 1. There is discontinuity
 * in the front (due to sin) but no discontinuity in the parameter space.
 * 
 * <p>Zitzler, E., Deb, K., and Thiele, L., 2000, Comparison of Multiobjective Evolutionary
 * Algorithms: Empirical Results, Evolutionary Computation, Vol. 8, No. 2, pp173-195.
 * 
 * <p><b>Parameters</b><br>
 * <table>
 * <tr><td valign=top><i>base</i>.<tt>num-variables</tt><br>
 * <font size=-1>int (default=30)</font></td>
 * <td valign=top>The number of variables; genome-size is set to this value internally.</td></tr>
 * </table>
 * 
 * @author  Gabriel Balan
 */
public class ZDT3 extends Problem implements SimpleProblemForm
{
	public int numDecisionVars;
	public static final String P_NUMVARS = "num-variables"; 


    public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		Parameter def = defaultBase();		
		numDecisionVars = state.parameters.getIntWithDefault(base.push(P_NUMVARS), def.push(P_NUMVARS),30);
		if (numDecisionVars< 2)
			state.output.fatal("The number of variables must be an integer > 1", base.push(P_NUMVARS), def.push(P_NUMVARS));


		/**
		 * I want to force the following values:
		 * 
		 * pop.subpop.0.species = ec.vector.FloatVectorSpecies
		 * pop.subpop.0.species.ind = ec.vector.DoubleVectorIndividual
		 * pop.subpop.0.species.min-gene = 0
		 * pop.subpop.0.species.max-gene = 1
		 * pop.subpop.0.species.genome-size = numDecisionVars //could be anything, should be 30
		 */
		state.parameters.set(new Parameter("pop.subpop.0.species"),"ec.vector.FloatVectorSpecies");
		state.parameters.set(new Parameter("pop.subpop.0.species.ind"),"ec.vector.DoubleVectorIndividual");
		state.parameters.set(new Parameter("pop.subpop.0.species.genome-size"),""+numDecisionVars);
		state.parameters.set(new Parameter("pop.subpop.0.species.min-gene"),"0");
		state.parameters.set(new Parameter("pop.subpop.0.species.max-gene"),"1");
		state.parameters.set(new Parameter("pop.subpop.0.species.fitness.numobjectives"),"2");
	}
	private static final double TEN_PI = Math.PI*10;
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum)
	{
		if(ind.evaluated)
			return;
		double[] genome = ((DoubleVectorIndividual)ind).genome;
		float[] fitnesses = ((MultiObjectiveFitness)ind.fitness).multifitness;
		double f = genome[0];
		fitnesses[0] = (float)f;
		double sum = 0;
		for(int i = 1; i< numDecisionVars; ++i)
			sum += genome[i];
		double g = 1+9*sum/(numDecisionVars-1);
		double foverg = f/g;
		double h = 1-Math.sqrt(foverg) - foverg * Math.sin(TEN_PI * f);
		fitnesses[1] = (float)(g*h);
		ind.evaluated = true;
	}
    public void describe(final Individual ind, 
            final EvolutionState state, 
            final int subpopulation,
            final int threadnum,
            final int log,
            final int verbosity)
    {}	
}