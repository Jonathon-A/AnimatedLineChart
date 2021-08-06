package AnimatedLineChart;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class AnimatedLineChart extends Application {

    private static final int MAX_DATA_POINTS = 50;
    private int xSeriesData = 0;
    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
    private ExecutorService executor;
    private final ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Number> dataQ3 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Number> dataQ4 = new ConcurrentLinkedQueue<>();

    private NumberAxis xAxis;

    public double Currency1 = 0;
    public double Currency2 = 0;
    public double Currency3 = 0;
    public double Currency4 = 0;


    private void init(Stage primaryStage) {

        xAxis = new NumberAxis();

        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("USD");

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis) {

            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        lineChart.setTitle("Accurate Live Currency Values");
        lineChart.setHorizontalGridLinesVisible(true);

       
        series1.setName("Theoretical Currency 1");
        series2.setName("Theoretical Currency 2");
        series3.setName("Theoretical Currency 3");
        series4.setName("Theoretical Currency 4");
        lineChart.getData().addAll(series1, series2, series3, series4);
        primaryStage.setScene(new Scene(lineChart));
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Animated Line Chart Sample");
        init(stage);
        stage.show();

        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        AddToQueue addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        prepareTimeline();
    }

    private class AddToQueue implements Runnable {

        public void run() {
            try {
                web();
            } catch (IOException ex) {
                Logger.getLogger(AnimatedLineChart.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                dataQ1.add(Currency1);
                dataQ2.add(Currency2);
                dataQ3.add(Currency3);
                dataQ4.add(Currency4);
                Thread.sleep(1000);
                executor.execute(this);
            } catch (Exception e) {

            }
        }
    }

    private void prepareTimeline() {

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) {
            if (dataQ1.isEmpty()) {
                break;
            }
            series1.getData().add(new XYChart.Data<>(xSeriesData++, dataQ1.remove()));
            series2.getData().add(new XYChart.Data<>(xSeriesData++, dataQ2.remove()));
            series3.getData().add(new XYChart.Data<>(xSeriesData++, dataQ3.remove()));
            series4.getData().add(new XYChart.Data<>(xSeriesData++, dataQ4.remove()));
        }

        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }

    public void web() throws IOException {
        
        Random rand = new Random();
        
     
        Currency1 = Currency1 + (rand.nextInt(10)-5);

      
        Currency2 = Currency2 + (rand.nextInt(10)-5);

     
        Currency3 = Currency3 + (rand.nextInt(10)-5);

     
        Currency4 = Currency4 + (rand.nextInt(10)-5);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
