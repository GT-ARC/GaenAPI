package garuntimeenv.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.ProblemProperties;
import garuntimeenv.gacomponents.jobshop.JobShopProblem;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Random;

/**
 * Class that handel's all the problem loading from the problem resource
 */
public class ProblemLoader {

    private static Random rand = new Random(TestManager.getSeed());
    private final static MyLogger logger = MyLogger.getLogger(ProblemLoader.class);

    // The defined problems
    public enum problems {
        Job_Shop_Scheduling
    }

    // Singleton pattern
    private static final ProblemLoader INSTANCE = new ProblemLoader();

    public static ProblemLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Loads a random problem instance
     *
     * @param problem The problem kind
     * @return The problem object
     */
    public IProblem loadProblem(problems problem) throws FileNotFoundException, MalformedJsonException, DataTypeNotSupportedException {
        return loadProblem(problem.toString(), rand.nextInt(200));
    }

    /**
     * Loads a specific problem instance
     *
     * @param problem The problem kind
     * @return The problem object
     */
    public IProblem loadProblem(problems problem, int element) throws FileNotFoundException, MalformedJsonException, DataTypeNotSupportedException {
        return loadProblem(problem.toString(), element);
    }

    /**
     * Loads a random problem instance
     *
     * @param problem The problem kind
     * @return The problem object
     */
    public IProblem loadProblem(String problem) throws FileNotFoundException, MalformedJsonException, DataTypeNotSupportedException {
        return loadProblem(problem, rand.nextInt(200));
    }

    /**
     * Loads the problem and returns the problem object
     *
     * @param problemKind The problem kind
     * @param element     the index of the problem instance in the json
     * @return The problem object
     * @throws FileNotFoundException         If there is no file in which the json is stored
     * @throws MalformedJsonException        If the json is malformed
     * @throws DataTypeNotSupportedException If there is a non primitive type in the object json
     */
    public IProblem loadProblem(String problemKind, int element) throws FileNotFoundException, MalformedJsonException, DataTypeNotSupportedException {
        JsonArray problemArray = readFile(problemKind + "_Problem.json");
        int problemAmount = problemArray.size();
        JsonObject jsonData = problemArray.get(element % problemAmount).getAsJsonObject();

        IProblem problem = null;
        switch (problemKind) {
            case "Job_Shop_Scheduling":
                problem = new JobShopProblem();
                break;
        }

        putDataInObject(jsonData, problem);

        return problem;
    }

    /**
     * Puts the json data into the problem object
     *
     * @param jsonObject The json object
     * @param o          The problem object in which the data is stored
     */
    private void putDataInObject(JsonObject jsonObject, IProblem o) throws DataTypeNotSupportedException {
        ProblemProperties props = ProblemProperties.getINSTANCE();
        props.clearProperties();
        try {
            for (String key : jsonObject.keySet()) {
                JsonElement problemData = jsonObject.get(key);
                props.addProblem(key, problemData);
                Field f = null;
                try {
                    f = o.getClass().getDeclaredField(key);
                    f.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    continue;
                }
                if (problemData.isJsonPrimitive()) {
                    switch (f.getType().getTypeName()) {
                        case "java.lang.String":
                            f.set(o, problemData.getAsString());
                            break;
                        case "int":
                            f.setInt(o, problemData.getAsInt());
                            break;
                        case "double":
                            f.setDouble(o, problemData.getAsDouble());
                            break;
                        case "float":
                            f.setFloat(o, problemData.getAsFloat());
                            break;
                        case "boolean":
                            f.setBoolean(o, problemData.getAsBoolean());
                            break;
                        case "char":
                            f.setChar(o, problemData.getAsCharacter());
                            break;
                    }
                } else if (problemData.isJsonArray()) {
                    JsonArray jsonArray = problemData.getAsJsonArray();
                    // Check if its a map or a list
                    if (jsonArray.get(0).isJsonArray()) {
                        Object[][] dataArray = loadDataMap(problemData.getAsJsonArray(), f.getType());
                        f.set(o, dataArray);
                    } else {
                        Object[] dataArray = loadDataArray(problemData.getAsJsonArray(), f.getType());
                        f.set(o, dataArray);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a filepath to a problem and returns the json array
     *
     * @param filepath The filepath of the file that contains the json
     * @return The json array containing the problem instances
     * @throws FileNotFoundException  If the file is not found
     * @throws MalformedJsonException If the json couldn't be parsed
     */
    private JsonArray readFile(String filepath) throws FileNotFoundException, MalformedJsonException {

        JsonParser parser = new JsonParser();

        InputStream input = ProblemLoader.class.getResourceAsStream("/problems/" + filepath);
        if (input == null) {
            input = ProblemLoader.class.getClassLoader().getResourceAsStream(filepath);
        }

        if(input == null) {
            throw new FileNotFoundException("File " + filepath + " not found");
        }

        JsonElement jsonElement = parser.parse(
                new InputStreamReader(input)
        );

        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }

        throw new MalformedJsonException("The problem json wasn't an array.");
    }

    /**
     * Create a object array according to {@code type} out of the dataArray
     *
     * @param dataArray The data array holding the objects
     * @param type      The type of the data
     * @return A object array out of the json array
     * @throws DataTypeNotSupportedException If the {@code type} is not supported
     */
    private Object[] loadDataArray(JsonArray dataArray, Class<?> type) throws DataTypeNotSupportedException {

        int dataAmount = dataArray.size();

        // Create the Object array of correct type
        Object[] problemData;
        switch (type.getTypeName()) {
            case "java.lang.Integer[]":
                problemData = new Integer[dataAmount];
                break;
            case "java.lang.Double[]":
                problemData = new Double[dataAmount];
                break;
            case "java.lang.String[]":
                problemData = new String[dataAmount];
                break;
            case "java.lang.Float[]":
                problemData = new Float[dataAmount];
                break;
            case "java.lang.Boolean[]":
                problemData = new Boolean[dataAmount];
                break;
            case "java.lang.Character[]":
                problemData = new Character[dataAmount];
                break;
            default:
                throw new DataTypeNotSupportedException("Unexpected value: " + type.getTypeName());
        }

        // Go through each data array element and put it into the object array
        for (int i = 0; i < dataArray.size(); i++) {
            switch (type.getTypeName()) {
                case "java.lang.Integer[]":
                    problemData[i] = dataArray.get(i).getAsInt();
                    break;
                case "java.lang.Double[]":
                    problemData[i] = dataArray.get(i).getAsDouble();
                    break;
                case "java.lang.String[]":
                    problemData[i] = dataArray.get(i).getAsString();
                    break;
                case "java.lang.Float[]":
                    problemData[i] = dataArray.get(i).getAsFloat();
                    break;
                case "java.lang.Boolean[]":
                    problemData[i] = dataArray.get(i).getAsBoolean();
                    break;
                case "java.lang.Character[]":
                    problemData[i] = dataArray.get(i).getAsCharacter();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type.getTypeName());
            }

        }
        return problemData;
    }

    /**
     * Create a object array map according to {@code type} out of the dataArray
     * It is mandatory that inside the data array is another JsonArray
     *
     * @param dataArray The data array holding arrays of data
     * @param type      The type of the data
     * @return A object map or two dimensional array out of the json array
     * @throws DataTypeNotSupportedException If the {@code type} is not supported
     */
    private Object[][] loadDataMap(JsonArray dataArray, Class<?> type) throws DataTypeNotSupportedException {

        // Check if the data is correct
        if (!checkDataArray(dataArray)) {
            return null;
        }

        int dataAmount = dataArray.size();
        int dataArraySize = dataArray.get(0).getAsJsonArray().size();

        Object[][] problemData = null;
        switch (type.getTypeName()) {
            case "java.lang.Integer[][]":
                problemData = new Integer[dataAmount][dataArraySize];
                break;
            case "java.lang.Double[][]":
                problemData = new Double[dataAmount][dataArraySize];
                break;
            case "java.lang.String[][]":
                problemData = new String[dataAmount][dataArraySize];
                break;
            case "java.lang.Float[][]":
                problemData = new Float[dataAmount][dataArraySize];
                break;
            case "java.lang.Boolean[][]":
                problemData = new Boolean[dataAmount][dataArraySize];
                break;
            case "java.lang.Character[][]":
                problemData = new Character[dataAmount][dataArraySize];
                break;
            default:
                throw new DataTypeNotSupportedException("Unexpected value: " + type.getTypeName());
        }

        JsonArray dataRow;
        int i;
        for (i = 0; i < dataArray.size(); i++) {
            dataRow = dataArray.get(i).getAsJsonArray();
            int c;
            for (c = 0; c < dataRow.size(); c++) {
                switch (type.getTypeName()) {
                    case "java.lang.Integer[][]":
                        problemData[i][c] = dataRow.get(c).getAsInt();
                        break;
                    case "java.lang.Double[][]":
                        problemData[i][c] = dataRow.get(c).getAsDouble();
                        break;
                    case "java.lang.String[][]":
                        problemData[i][c] = dataRow.get(c).getAsString();
                        break;
                    case "java.lang.Float[][]":
                        problemData[i][c] = dataRow.get(c).getAsFloat();
                        break;
                    case "java.lang.Boolean[][]":
                        problemData[i][c] = dataRow.get(c).getAsBoolean();
                        break;
                    case "java.lang.Character[][]":
                        problemData[i][c] = dataRow.get(c).getAsCharacter();
                        break;
                }
            }
        }
        return problemData;
    }

    /**
     * Checks if the data array is correct
     *
     * @param data The data array to be checked
     * @return True if the data array correct
     */
    private boolean checkDataArray(JsonArray data) {
        if (data == null) {
            logger.error("ProblemLoader", "Data array is null");
            return false;
        }

        int nrMachine = data.size();

        if (nrMachine == 0) {
            logger.error("ProblemLoader", "Data array is empty");
            return false;
        }

        if (!data.get(0).isJsonArray()) {
            logger.error("ProblemLoader", "The data inside the array doesn't contains json arrays ");
            return false;
        }

        return true;
    }
}
