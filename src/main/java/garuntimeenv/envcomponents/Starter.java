package garuntimeenv.envcomponents;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.envcomponents.datalog.DataSeries;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.gacomponents.GAManager;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.interfaces.Property;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.ProblemLoader;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.System.exit;

public class Starter {

    final static MyLogger logger = MyLogger.getLogger(Starter.class);

    private static EnvConfig envConfig = EnvConfig.getInstance();
    // Test config holds the metadata for the tests
    private static TestManager testManager;
    // Loads the problems defined in the json format
    private static ProblemLoader problemLoader;
    // Manages the visual aspect of the runtime environment
    private static DataManager dataManager;
    private IProblem problem = null;

    /**
     * Parses the commandline options into a map and prints a formatted usage text if
     * the required parameters aren't set
     *
     * @param args The commandline options
     * @return The object map containing the set options and
     */
    public static CommandLine parseCmdLine(String[] args) {
        Options options = new Options();

        Option instance = new Option("i", "instance", true,
                "the respective instance in the json file if non is given a random one will be picked");
        options.addOption(instance);

        Option run = new Option("r", "run", true,
                "Set true if the runtime environment should only run to test hyper parameter's set false");
        run.setRequired(true);
        options.addOption(run);

        Option visual = new Option("v", "visual", false,
                "if flag is set enable visual");
        options.addOption(visual);

        Option savePictures = new Option("sp", "savePictures", false,
                "if flag is set saves the pictures from the UI if enabled");
        options.addOption(savePictures);

        Option writeData = new Option("w", "writeData", false,
                "If no data shall be written.");
        options.addOption(writeData);

        Option problem = new Option("p", "problem", true,
                "The problem name [Job_Shop_Scheduling]");
        problem.setRequired(true);
        options.addOption(problem);

        Option help = new Option("?", "help", false, "For help");
        options.addOption(help);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("garuntimeenv", options);
            System.out.println("\n\n To start the testing environment call: \n\t ./garuntimeenv -r true -p Job_Shop_Scheduling -v");
            exit(1);
        }

        if (cmd.hasOption("help")) {
            formatter.printHelp("utility-name", options);
            exit(0);
        }

        envConfig.setVisualEnabled(cmd.hasOption("visual"));
        envConfig.setWriteData(!cmd.hasOption("writeData"));
        envConfig.setSavePictures(!cmd.hasOption("savePictures"));

        return cmd;
    }

    /**
     * Constructor for the runtime environment
     * load the problem through the problem loader
     * and initilize some components
     *
     * @param problemInstance The index of the problem instance in the json list
     * @param problem         the problem instnace
     */
    public Starter(int problemInstance, String problem) throws FileNotFoundException, MalformedJsonException, DataTypeNotSupportedException {
        testManager = TestManager.getInstance();
        problemLoader = ProblemLoader.getInstance();
        dataManager = DataManager.getInstance();
        if (problemInstance == -1)
            this.problem = ProblemLoader.getInstance().loadProblem(problem);
        else
            this.problem = ProblemLoader.getInstance().loadProblem(problem, problemInstance);
    }

    /**
     * Entry point of the program
     *
     * @param args The commandline params
     */
    public static void main(String[] args) {
        CommandLine cmd = parseCmdLine(args);

        int instance = Integer.parseInt(cmd.getOptionValue("instance", String.valueOf(-1)));
        try {
            if (cmd.getOptionValue("run").equals("true")) {
                Starter starter = new Starter(instance, cmd.getOptionValue("problem"));
                starter.start();
            } else {
                Starter starter = new Starter(instance, cmd.getOptionValue("problem"));
                Config runConfiguration = new Config();
                logger.info("Starter", "Used configuration: " + runConfiguration.longString());
                dataManager.createNewDataSeries("Solution Series", runConfiguration, new Property() {
                    @Override
                    public String toString() {
                        return "Run set configuration:";
                    }
                });
                starter.startGAInstance(new Config());
            }
        } catch (DataTypeNotSupportedException e) {
            logger.error("Starter", "The data type is not supported: ");
            e.printStackTrace();
            exit(1);
        } catch (FileNotFoundException e) {
            logger.error("Starter", "The requested json problem file isn't found: " );
            e.printStackTrace();
            exit(1);
        } catch (MalformedJsonException e) {
            logger.error("Starter", "The loaded json is malformed: ");
            e.printStackTrace();
            exit(1);
        }
    }

    /**
     * Starts the runtime environment
     */
    public void start() {
        logger.info("Starter", "Runtime environment started");
        Config gaConfig = testManager.getCurrentConfig();

        // Do as long as the test manager creates configurations
        do {
            logger.info("Starter", "To be tested configuration: " + gaConfig);
            // For each configuration start each repetion or run
            for (int testRepetition = 0; testRepetition < TestManager.testRepetition; testRepetition++) {
                logger.info("Starter", "new run rep: " + (testRepetition + 1) + " of " + TestManager.testRepetition);
                // Create a new dataseries to save the data into
                DataSeries newDataSeries =
                        dataManager.createNewDataSeries("rep: " + testRepetition, gaConfig, testManager.getCurrTestedProperty());

                // Reset the time measurement stuff
                envConfig.resetPauseTime();
                long startTime = System.currentTimeMillis();

                // Start the genetic environment
                startGAInstance(gaConfig);

                // Stop the time measurement and save it in the dataseries
                long endTime = System.currentTimeMillis();
                newDataSeries.setRunTime((endTime - startTime) - envConfig.getPausedTime());
            }
            // Average all the runs an normalize the display graphs
            dataManager.averageCurrentRun(testManager.getCurrConfigLabel(), testManager.getCurrTestedProperty());
            dataManager.normalizeAllGraphs();
        } while ((gaConfig = testManager.getNextConfig()) != null);
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start a new genetic algorithm instance with the given configuration
     *
     * @param currentConfig The configuration the ga should run on
     */
    private void startGAInstance(Config currentConfig) {
        GAManager runtimeEnvironment = new GAManager(this.problem, currentConfig);
        runtimeEnvironment.startEnvironment();
    }
}
