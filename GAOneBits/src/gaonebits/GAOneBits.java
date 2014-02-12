package gaonebits;
/* 
 Program: GAOneBits
 Course: Bio-inspired Computing, Spring 2014
 Author: J. Gurka, edits and changes by Angel Preciado

 Description:
 A genetic algorithm (GA) program to illustrate the basic operations of
 GAs.  This program attempts to produce "bit strings" of all ones, from an initial
 population of random bit strings.  

 Versions.
 Version 1: Basic operations, parameters hard-coded, text output only.
 Version 2: Added sections to the original code which will implement the following changes:
 1) Individuals can now have the digits 1 - 5 and the goal is to have all 5's.
 2) The top 3 individuals will be copied over to the new population as clones
 3) The bottom 3 percent are removed from the population
       
 All other aspects, besides parameters, are largely unchanged.

 Input: None.

 Output: Tracking data of population progress (overall fitness).

 Future work:
 1. User-input values for GA parameters such as population size (and
 possibly command-line parameters?).
 2. Visual display of fitness progress.

 */

import java.util.Random;  // initial population values

public class GAOneBits {

    // GA parameters
    private final int POPULATION_SIZE,
            INDIVIDUAL_SIZE, // one member of population
            GENERATION_MAX;	// forced halting independent of fitness
    private final double MUTATION_RATE,
            GOOD_PARENT_RATE,
            FITNESS_GOAL, // population average fitness goal
            ELITE_SIZE;

    private int[][] population, newPopulation;
    private double[] fitness, newFitness;	// one value per population member
    private double averageFitness;			// entire population
    private int generation, // counter
            crossoverPoint;

    private Random rand;	// initial population, crossover point, parent selection

    public GAOneBits() {

        POPULATION_SIZE = 20;
        GENERATION_MAX = 500;
        MUTATION_RATE = 0.1;
        GOOD_PARENT_RATE = 0.8;
        INDIVIDUAL_SIZE = 30;
        FITNESS_GOAL = 0.95;
        ELITE_SIZE = 3;

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
                population[i][j] = rand.nextInt(5) + 1;  // zero or one		
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

    public int selectParent(boolean[] dontUse) {
        // printMethod("SelectParent");
        // select parent by mostly choosing above-average parents
        int parent;
        double totalFitness = 0, averageFitness;
        boolean found = false;

        if (rand.nextInt(POPULATION_SIZE) <= POPULATION_SIZE * GOOD_PARENT_RATE) {
            // select by fitness - choose above-average parent
            // System.out.println("selecting above-average parent");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                if(!dontUse[i]){
                totalFitness += fitness[i];
                }
            }
            averageFitness = (double) totalFitness / POPULATION_SIZE - this.ELITE_SIZE;
            // find random above-average parent
            do {
                parent = rand.nextInt(POPULATION_SIZE);
                if (fitness[parent] >= averageFitness && !dontUse[parent]) {
                    found = true;
                }
            } while (!found);
        } else {  //  choose random parent, ignore fitness///ADDED: also check its not from the bottom 3 individuals
            // System.out.println("selecting random parent");
            do {
                parent = rand.nextInt(POPULATION_SIZE);
                if (!dontUse[parent]) {
                    found = true;
                }
            } while (!found);
            parent = rand.nextInt(POPULATION_SIZE);
        }

        return parent;

    }  // select parent

    public double calculateFitness(int[] member) {
        // printMethod("calculateFitness");
        // fitness function: count one bits
        int fitness = 0;
        for (int i = 0; i < INDIVIDUAL_SIZE; i++) {
            if (member[i] == 5) {        //changed this to reflect fact that we are looking for all 5's
                fitness += 1;
            }
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

    ///CODE ADDED BY AP
    /**
     * Method will identify the weakest individuals and mark them so they will not be used in next generation.
     * @return boolean array identifying weakest individuals
     */
    public boolean[] cullTheWeak() {
        boolean[] theWeak = new boolean[this.POPULATION_SIZE];
        for (int z = 0; z < this.ELITE_SIZE; z++) {
            double lowestFitness = 99;
            int lowestIndex = 0;
            for (int i = 0; i < this.POPULATION_SIZE; i++) {
                if (this.calculateFitness(this.population[i]) <= lowestFitness
                        && !theWeak[i]) {
                    lowestFitness = this.calculateFitness(this.population[i]);
                    lowestIndex = i;
                }
            }
            theWeak[lowestIndex] = true;
        }
        return theWeak;
    }

    /**
     * Method identifies the elite among this population so they can be cloned into next generation
     * @return boolean array identifying the elite individuals
     */
    public boolean[] identifyElites() {
        boolean[] elites = new boolean[this.POPULATION_SIZE];
        for (int z = 0; z < this.ELITE_SIZE; z++) {
            double highestFitness = 0.0;
            int eliteIndex = 0;
            for (int i = 0; i < this.POPULATION_SIZE; i++) {
                if (this.calculateFitness(this.population[i]) >= highestFitness
                        && !elites[i]) {
                    highestFitness = this.calculateFitness(this.population[i]);
                    eliteIndex = i;
                }
            }
            elites[eliteIndex] = true;
        }
        return elites;
    }
    //////////////////////

    private void createNextGeneration() {
        //printMethod("createNextGeneration");
        int[] parent1, parent2,
                child = new int[INDIVIDUAL_SIZE];

        //////////////CODE ADDED - ap
        boolean[] remove = this.cullTheWeak();
        boolean[] elites = identifyElites();

        for (int i = 0; i < POPULATION_SIZE - this.ELITE_SIZE; i++) {

            parent1 = population[selectParent(remove)];
            parent2 = population[selectParent(remove)];

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
                child[position] = rand.nextInt(5) + 1; //altered to fit new values 1-5
            }
            // save child
            for (int j = 0; j < INDIVIDUAL_SIZE; j++) {
                newPopulation[i][j] = child[j];
            }
            newFitness[i] = calculateFitness(child);

        }  // create all new children
        ///////////////CODE ADDED BY AP
        //here we must now identify the top 3 and clone them into the next generation
        //note, that no mutation or crossover is done with these elites so a simple copy over
        //will be sufficient to implement this
        for (int i = this.POPULATION_SIZE - (int) this.ELITE_SIZE; i < this.POPULATION_SIZE; i++) {
            boolean found = false;
            for (int j = 0; j < this.POPULATION_SIZE; j++) {
                if (elites[j] && !found) {  //found an elite, now copy over
                    elites[j] = false;  //reset elite to false
                    found = true;
                    for (int k = 0; k < this.INDIVIDUAL_SIZE; k++) {
                        newPopulation[i][k] = this.population[j][k];
                        newFitness[i] = calculateFitness(this.population[j]);
                    }
                }
            }
        }

        ///////////////////END OF CODE ADDED
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

}
