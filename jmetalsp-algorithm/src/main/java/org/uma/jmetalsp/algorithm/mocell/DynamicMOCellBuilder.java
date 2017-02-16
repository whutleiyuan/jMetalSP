package org.uma.jmetalsp.algorithm.mocell;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetalsp.algorithm.DynamicAlgorithmBuilder;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.updatedata.AlgorithmData;
import org.uma.jmetalsp.util.Observable;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicMOCellBuilder<
				S extends Solution<?>,
				P extends DynamicProblem<S, ?>,
				O extends Observable<AlgorithmData>> implements DynamicAlgorithmBuilder<DynamicMOCell<S, O>, P> {

	private int maxEvaluations;
	private int populationSize;
	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private SelectionOperator<List<S>, S> selectionOperator;
	private SolutionListEvaluator<S> evaluator;

	protected Neighborhood<S> neighborhood ;
	protected BoundedArchive<S> archive ;

	private O observable ;
	public DynamicMOCellBuilder(CrossoverOperator<S> crossoverOperator,
	                            MutationOperator<S> mutationOperator,
															O observable) {
		this.crossoverOperator = crossoverOperator ;
		this.mutationOperator = mutationOperator;
		this.maxEvaluations = 25000 ;
		this.populationSize = 100 ;
		this.selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>()) ;
		neighborhood = new C9<S>((int)Math.sqrt(populationSize), (int)Math.sqrt(populationSize)) ;
		evaluator = new SequentialSolutionListEvaluator<S>();
		archive = new CrowdingDistanceArchive<>(populationSize) ;
		this.observable = observable ;
	}

	public DynamicMOCellBuilder<S,P,O> setMaxEvaluations(int maxEvaluations) {
		if (maxEvaluations < 0) {
			throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
		}
		this.maxEvaluations = maxEvaluations;

		return this;
	}

	public DynamicMOCellBuilder<S,P,O> setPopulationSize(int populationSize) {
		if (populationSize < 0) {
			throw new JMetalException("Population size is negative: " + populationSize);
		}

		this.populationSize = populationSize;

		return this;
	}

	public DynamicMOCellBuilder<S,P,O> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
		if (selectionOperator == null) {
			throw new JMetalException("selectionOperator is null");
		}
		this.selectionOperator = selectionOperator;

		return this;
	}

	public DynamicMOCellBuilder<S,P,O> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
		if (evaluator == null) {
			throw new JMetalException("evaluator is null");
		}
		this.evaluator = evaluator;

		return this;
	}

	public DynamicMOCellBuilder<S,P,O> setArchive(BoundedArchive<S> archive) {
		this.archive = archive ;

		return this;
	}

	public DynamicMOCellBuilder<S,P,O> setNeighborhood(Neighborhood<S> neighborhood) {
		this.neighborhood = neighborhood;

		return this;
	}

	@Override
	public DynamicMOCell<S,O> build(P problem) {
		return new DynamicMOCell<>(problem, maxEvaluations, populationSize, archive, neighborhood,
						crossoverOperator, mutationOperator, selectionOperator, evaluator, observable);
	}
}
