package gaonebits;
/////// ASSIGNMENT BY ANGEL PRECIADO
/* 
 Program: GAOneBits
 Course: Bio-inspired Computing, Spring 2014
 Author: J. Gurka  Edited by Angel Preciado

 Description:
 A genetic algorithm (GA) program to illustrate the basic operations of
 GAs.  This program attempts to produce "bit strings" of all ones, from an initial
 population of random bit strings.

 Versions.
 Version 1: Basic operations, parameters hard-coded, text output only.

 Input: None.

 Output: Tracking data of population progress (overall fitness).

 Future work:
 1. User-input values for GA parameters such as population size (and
 possibly command-line parameters?).
 2. Visual display of fitness progress.

 */

import java.util.ArrayList;
import java.util.Random;  // initial population values

public class GAOneBits {

    // GA parameters
    private final int POPULATION_SIZE,
            INDIVIDUAL_SIZE, // one member of population
            GENERATION_MAX;	// forced halting independent of fitness
    private final double MUTATION_RATE,
            GOOD_PARENT_RATE,
            FITNESS_GOAL;  // population average fitness goal

    private int[][] population, newPopulation;
    private double[] fitness, newFitness;	// one value per population member
    private double averageFitness;			// entire population
    private int generation, // counter
            crossoverPoint;

    private Random rand;	// initial population, crossover point, parent selection

    public GAOneBits() {

        POPULATION_SIZE = 20;
        GENERATION_MAX = 200;
        MUTATION_RATE = 0.03;
        GOOD_PARENT_RATE = 0.8;
        INDIVIDUAL_SIZE = 20;
        FITNESS_GOAL = 0.95;

        generation = 1;
        rand = new Random();

        population = new int[POPULATION_SIZE][INDIVIDUAL_SIZE];
        newPopulation = new int[POPULATION_SIZE][INDIVIDUAL_SIZE];
        fitness = new double[POPULATION_SIZE];
        newFitness = new double[POPULATION_SIZE];

    }  // default constructor

    public void outputGAParameters() {
        System.out.println("GA OneBits program ...");
        System.out.println("Parameters:"
                + "\n   population size: " + POPULATION_SIZE
                + "\n   individual size: " + INDIVIDUAL_SIZE
                + "\n   generation count: " + GENERATION_MAX
                + "\n   mutation rate: " + MUTATION_RATE
                + "\n   good parent rate: " + GOOD_PARENT_RATE
                + "\n   fitness level: " + FITNESS_GOAL
                + "\n"
        );
    }  // outputProgramOverview

    public void createInitialPopulation() {

        // printMethod("createInitialPopulation");
        // fill population with random ones and zeros
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < INDIVIDUAL_SIZE; j++) {
                population[i][j] = rand.nextInt(2);  // zero or one		
            }  // one member of the population
        }  // entire population

        // calculate initial fitness
        for (int i = 0; i < POPULATION_SIZE; i++) {
            fitness[i] = calculateFitness(population[i]);
        }
        double totalFitness = 0.0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            totalFitness += fitness[i];
        }
        averageFitness = totalFitness / POPULATION_SIZE;

        printPopulation(population, "Initial Population");

    }  // createInitialPopulation

    public int selectParent(boolean[] alreadyUsed) {
        // printMethod("SelectParent");
        // select parent by mostly choosing above-average parents
        int parent;
        double totalFitness = 0, averageFitness;
        boolean found = false;

        if (rand.nextInt(POPULATION_SIZE) <= POPULATION_SIZE * GOOD_PARENT_RATE) {
            // select by fitness - choose above-average parent
            // System.out.println("selecting above-average parent");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                totalFitness += fitness[i];
            }
            averageFitness = (double) totalFitness / POPULATION_SIZE;
            // find random above-average parent
            do {
                parent = rand.nextInt(POPULATION_SIZE);
                if (fitness[parent] >= averageFitness) {
                    found = true;
                }
            } while (!found);
        } else {  //  choose random parent, ignore fitness
            // System.out.println("selecting random parent");
            parent = rand.nextInt(POPULATION_SIZE);
        }

        return parent;

    }  // select parent

    public double calculateFitness(int[] member) {
        // printMethod("calculateFitness");
        // fitness function: count one bits
        int fitness = 0;
        for (int i = 0; i < INDIVIDUAL_SIZE; i++) {
            fitness += member[i];
        }
        return fitness;
    }

    public void runGA() {
        generation = 1;
        while (generation < GENERATION_MAX
                && averageFitness < FITNESS_GOAL * INDIVIDUAL_SIZE) {
            createNextGeneration();
            printPopulation(population, "Generation " + generation);
            generation++;
        }
        if (generation == GENERATION_MAX) {
            System.out.println("stopped due to maximum generations reached, "
                    + "generation: " + generation);
        }
        if (averageFitness >= FITNESS_GOAL * INDIVIDUAL_SIZE) {
            System.out.println("stopped due to fitness goal reached, "
                    + "generation: " + generation);
        }
        printPopulation(population, "Last population");

    }  // runGA

    private void createNextGeneration() {
        //printMethod("createNextGeneration");
        ////////////////////////////////
        //////////////CODE ADDED BY ME
        boolean[] alreadyUsed = new boolean[POPULATION_SIZE]; //used to keep track of indiviudals
                                //already selected to be used as an elite, or culled from the bottom
        int eliteNumber = (int) (POPULATION_SIZE * .10);

        int[][] elites = cloneElites(alreadyUsed, eliteNumber);
        this.cullTheWeak(alreadyUsed, eliteNumber);
        //////////////////////////////////////^^^^^^
        int[] parent1, parent2,
                child = new int[INDIVIDUAL_SIZE];

        for (int i = 0; i < POPULATION_SIZE - eliteNumber; i++) {

            parent1 = population[selectParent(alreadyUsed)];
            parent2 = population[selectParent(alreadyUsed)];

            crossoverPoint = rand.nextInt(INDIVIDUAL_SIZE);

            // create child by copying parent 1 up to crossover point,
            // then parent2 from there to the end
            for (int j = 0; j < crossoverPoint; j++) {
                child[j] = parent1[j];
            }
            for (int j = crossoverPoint; j < INDIVIDUAL_SIZE; j++) {
                child[j] = parent2[j];
            }

            // possibly alter child using mutation
            if (rand.nextDouble() <= MUTATION_RATE) {
                // flip one bit
                int position = rand.nextInt(INDIVIDUAL_SIZE);
                child[position] = child[position] == 0 ? 1 : 0;
            }

            // save child
            for (int j = 0; j < INDIVIDUAL_SIZE; j++) {
                newPopulation[i][j] = child[j];
            }
            newFitness[i] = calculateFitness(child);

        }  // create all new children
        
        //add the elites into the new population
        for (int i = POPULATION_SIZE - eliteNumber; i < this.POPULATION_SIZE; i++) {
            for (int j = 0; j < this.INDIVIDUAL_SIZE; j++) {
                newPopulation[i][j] = elites[i][j];
            }
            newFitness[i] = this.getFitness(i);
        }
        
        

        ////////////////////////////////////////////////////
        //dont touch this for now
        // update with new generation		
        population = newPopulation;
        fitness = newFitness;
        double totalFitness = 0.0;
        averageFitness = 0.0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            totalFitness += fitness[i];
        }
        averageFitness = totalFitness / POPULATION_SIZE;

    }  // createNextGeneration


    /**
     * Helper method for sort, will calculate an individuals fitness based ont
     * he passed in index
     *
     * @param i individual
     * @return the individuals fitness
     */
    public int getFitness(int i) {
        int fitness = 0;
        for (int j = 0; j < this.INDIVIDUAL_SIZE; j++) {
            fitness += population[i][j];
        }
        return fitness;
    }

    private void printMember(int[] member) {
        for (int i = 0; i < INDIVIDUAL_SIZE - 1; i++) {
            System.out.print(member[i] + " ");
        }
        System.out.print(member[INDIVIDUAL_SIZE - 1]);
    }  // print population member

    private void printMethod(String name) {
        System.out.println("\n... in " + name);
    }  // printMethod

    private void printPopulation(int[][] pop, String popName) {
        // prints population and fitness of each member
        System.out.println("\n" + popName + " -- fitness");
        System.out.println("(average fitness = " + averageFitness + ")");

        for (int i = 0; i < POPULATION_SIZE; i++) {
            System.out.print("   ");
            for (int j = 0; j < INDIVIDUAL_SIZE; j++) {
                System.out.print(pop[i][j] + "  ");
            }
            System.out.println("  --  " + fitness[i]);
        }
    }  // printPopulation

    private double calculateAverageFitness() {
        double total = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            total += fitness[i];
        }
        return total / POPULATION_SIZE;
    }  // calculateAverageFitness

    /*
     driver for GAOneBits

     */
    public static void main(String args[]) {
        GAOneBits gaBits = new GAOneBits();
        gaBits.outputGAParameters();
        gaBits.createInitialPopulation();
        gaBits.runGA();
        //gaBits.reportResults();
    }

    private int[][] cloneElites(boolean[] alreadyUsed, int eliteNum) {
        int[][] elites = new int[this.POPULATION_SIZE][this.INDIVIDUAL_SIZE];
        for (int k = 0; k < eliteNum; k++) {
            int elite = 0;
            int highestFitness = 0;
            for (int i = 0; i < this.POPULATION_SIZE; i++) { //get highest fitness individual
                if (this.getFitness(i) > highestFitness || !alreadyUsed[i]) {
                    elite = i;
                    highestFitness = this.getFitness(i);
                }
            }
            System.arraycopy(this.population[elite], 0, elites[elite], 0, this.INDIVIDUAL_SIZE);
            alreadyUsed[elite] = true;
        }
        return elites;
    }

    private void cullTheWeak(boolean[] alreadyUsed, int bottom) {
        for (int i = 0; i < bottom; i++) {
            int lowestFitness = 999;
            int unFit = 0;
            for (int j = 0; j < this.POPULATION_SIZE; j++) {
                if(this.getFitness(j) < lowestFitness){
                    lowestFitness = this.getFitness(j);
                    unFit = j;
                }
            }
            alreadyUsed[unFit] = true;
        }
    }

}
