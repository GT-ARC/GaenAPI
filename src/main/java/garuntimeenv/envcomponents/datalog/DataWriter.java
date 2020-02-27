package garuntimeenv.envcomponents.datalog;

import garuntimeenv.envcomponents.DataEvaluator;
import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.MyLogger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataWriter {

    final static MyLogger logger = MyLogger.getLogger(DataWriter.class);

    private final TestManager testManager = TestManager.getInstance();
    private final DataLogging dataLog;

    private List<String> dataNames;

    private String folderName;

    /**
     * Constructor for the data writer
     * Sets the folder name
     *
     * @param dataLog The data log to where the data is coming from
     */
    public DataWriter(DataLogging dataLog) {
        this.dataLog = dataLog;

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy_HH.mm.ss");

        this.folderName = dateFormat.format(date);
    }

    /**
     * Write the data to the {@code hyperProp} hyper parameter
     *
     * @param hyperProp The hyper parameter which should be saved persistently
     */
    public void writeData(HyperParameter hyperProp) {
        try {
            Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

            // Write all the data recorded data into the workbook
            writeData(hyperProp, workbook);

            // Write the eval property in the workbook
            writeEvalProperty(hyperProp, workbook);

            // Save the data into File
            saveDataInFile(hyperProp, workbook);

            // Closing the workbook
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the workbook into the file defined by the {@code folderName} and through the hyper property
     *
     * @param hyperProp The hyper property of the dataset
     * @param workbook  The workbook to be saved
     * @throws IOException If file cant be written or opened
     */
    private void saveDataInFile(HyperParameter hyperProp, Workbook workbook) throws IOException {
        String fileSeparator = System.getProperty("file.separator");
        String folder = "." + fileSeparator + "data" + fileSeparator + folderName + fileSeparator;
        String fileName = folder + hyperProp.getTestProperty() + ".xlsx";
        if (new File(folder).exists() || new File(folder).mkdirs()) {
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
        }

        if (EnvConfig.getInstance().isVisualEnabled() && EnvConfig.getInstance().savePictures()) {

            String imgFolder = folder + "img" + fileSeparator + hyperProp.getTestProperty() + fileSeparator;
            // Save Img for everything
            for (Property prop : hyperProp.getAllProperties()) {
                String propImgFolder = imgFolder + prop.toString() + fileSeparator;
                List<DataSeries> charts = dataLog.getDataSeriesOfProperty(prop);
                charts.add(dataLog.getAverageDataSeriesOfProperty(prop));
                // Save all charts in there respective folder
                for (DataSeries chart : charts) {
                    if (new File(propImgFolder).exists() || new File(propImgFolder).mkdirs()) {
                        BitmapEncoder.saveBitmapWithDPI(
                                chart.getXChart(),
                                propImgFolder + chart.getName()
                                        .replace(' ', '_')
                                        .replace(":", ""),
                                BitmapEncoder.BitmapFormat.PNG,
                                300
                        );
                    }
                }
            }
            // Save the eval charts
            for (Map.Entry<DataEvaluator.evaluationPoints, CategoryChart> chart : dataLog.getEval(hyperProp).getXCharts()) {
                String imgName = chart.getKey() == null ? "collective" : chart.getKey().name().replace(":", "");
                if (new File(imgFolder + "eval" + fileSeparator).exists() || new File(imgFolder + "eval" + fileSeparator).mkdirs())
                    BitmapEncoder.saveBitmapWithDPI(
                            chart.getValue(),
                            imgFolder + "eval" + fileSeparator + imgName,
                            BitmapEncoder.BitmapFormat.PNG,
                            300
                    );
            }
            BitmapEncoder.saveBitmapWithDPI(
                    dataLog.getEval(hyperProp).getScoreXChart(),
                    imgFolder + "eval" + fileSeparator + "score",
                    BitmapEncoder.BitmapFormat.PNG,
                    300
            );
        }
    }

    /**
     * Write the recorded data of the tested hyper property into a workbook
     *
     * @param hyperProp The hyper property under which the data is stored
     * @param workbook  The workbook the data is supposed to be written to
     */
    private void writeData(HyperParameter hyperProp, Workbook workbook) {
        for (Property prop : hyperProp.getAllProperties()) {
            List<DataSeries> toBeSavedSeries = this.dataLog.getDataSeriesOfProperty(prop);
            // Check if the data is empty
            if (toBeSavedSeries == null || toBeSavedSeries.isEmpty()) continue;

            // Create a sheet and the header rows
            Sheet worksheet = workbook.createSheet(prop.toString());
            Row repRow = worksheet.createRow(0);
            Row nameRow = worksheet.createRow(1);

            // Get the data names and contain for consistency
            List<String> dataNames = new ArrayList<>(toBeSavedSeries.get(0).getDataSetNames(false));
            if (this.dataNames == null) {
                this.dataNames = dataNames;
            } else {
                if (!this.dataNames.containsAll(dataNames)) {
                    logger.debug("writer", "Data Series data names dont match.");
                }
            }

            for (int cellIndex = 0; cellIndex < toBeSavedSeries.size(); cellIndex++) {
                Cell cell = repRow.createCell(cellIndex * dataNames.size());
                cell.setCellValue("Rep: " + cellIndex);
            }

            for (int cellIndex = 0; cellIndex < toBeSavedSeries.size() * dataNames.size(); cellIndex++) {
                Cell cell = nameRow.createCell(cellIndex);
                cell.setCellValue(this.dataNames.get(cellIndex % this.dataNames.size()));
            }

            HashMap<Number, Row> rows = new HashMap<>();
            HashMap<Row, List<Cell>> cells = new HashMap<>();

            List<Integer> offsets = new ArrayList<>();
            for (int i = 0; i < toBeSavedSeries.size(); i++)
                offsets.add(this.dataNames.size() * i);

            // Get the max x value
            createCells(toBeSavedSeries, worksheet, dataNames.size(), rows, cells);

            // Write all the data parallel into the worksheet
            toBeSavedSeries.parallelStream().forEach(
                    dataSeries -> {
                        int index = toBeSavedSeries.indexOf(dataSeries);
                        int myOffset = offsets.get(index);
                        this.dataNames.parallelStream().forEach(
                                dataName -> {
                                    int dataNameIndex = this.dataNames.indexOf(dataName);
                                    dataSeries.get(dataName).getXData().forEach(x -> {
                                        // Check if row exists
                                        if (!rows.containsKey(x))
                                            rows.put(x, worksheet.createRow(x.intValue() + 2));
                                        Row row = rows.get(x.intValue());
                                        if (row == null || cells.get(row) == null) return;
//                                            System.out.println(dataSeries + " " + dataName + " " + row);
                                        Cell cell = cells.get(row).get(myOffset + dataNameIndex);
                                        dataSeries.get(dataName).getDataPoint(x.intValue()).doubleValue();
                                        cell.setCellValue(
                                                dataSeries.get(dataName).getDataPoint(x.intValue()).doubleValue()
                                        );
                                    });
                                }
                        );
                    }
            );
        }
    }

    /**
     * Writes the evaluation data of the {@code hyperProp} into the {@code workbook}
     *
     * @param hyperProp The hyper property
     * @param workbook  the workbook in which the data has to be written
     */
    private void writeEvalProperty(HyperParameter hyperProp, Workbook workbook) {
        Sheet worksheet = workbook.createSheet("Evaluation");
        Row nameRow = worksheet.createRow(0);
        EvalSeries evalSeries = dataLog.getEval(hyperProp);
        if (evalSeries == null) return;
        HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> evalData = evalSeries.getAllData();

        List<Property> props = hyperProp.getAllProperties();
        props.forEach(prop -> {
            Cell cell = nameRow.createCell(props.indexOf(prop) + 1);
            cell.setCellValue(prop.toString());
        });

        int counter = 1;
        for (Map.Entry<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> entry : evalData.entrySet()) {
            LinkedHashMap<Property, Double> element = entry.getValue();
            Row row = worksheet.createRow(counter++);
            row.createCell(0).setCellValue(entry.getKey().toString());
            props.forEach(prop -> {
                Cell cell = row.createCell(props.indexOf(prop) + 1);
                cell.setCellValue(element.get(prop));
            });
        }

        Row scoreRow = worksheet.createRow(counter);
        scoreRow.createCell(0).setCellValue("score");
        props.forEach(prop -> {
            Cell cell = scoreRow.createCell(props.indexOf(prop) + 1);
            cell.setCellValue(evalSeries.getScore().get(prop));
        });

    }

    /**
     * Creates the rows and cells in which the data has to be stored in
     *
     * @param toBeSavedSeries The runs of the property for which the store unites have to be created
     * @param worksheet       The worksheet on which the rows have to be created
     * @param amountPerRun    Amount of recorded data
     * @param rows            The map in which the rows have to be created
     * @param cells           the map for the cells
     */
    private void createCells(List<DataSeries> toBeSavedSeries, Sheet worksheet, int amountPerRun, HashMap<Number, Row> rows, HashMap<Row, List<Cell>> cells) {
        int max = 0;
        for (DataSeries dataSeries : toBeSavedSeries) {
            for (DataSet dataSet : dataSeries.getDataSet(false, true)) {
                int currMax = dataSet.getXData().get(dataSet.getXData().size() - 1).intValue();
                if (currMax > max)
                    max = currMax;
            }
        }

        for (int i = 0; i < max; i++) {
            Row row = worksheet.createRow(i + 2);
            rows.put(i, row);
            List<Cell> cellList = new ArrayList<>();
            for (int c = 0; c < toBeSavedSeries.size() * dataNames.size(); c++) {
                cellList.add(row.createCell(c));
            }
            cells.put(row, cellList);
        }
    }
}
