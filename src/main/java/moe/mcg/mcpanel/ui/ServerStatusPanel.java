package moe.mcg.mcpanel.ui;

import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslationManager;
import moe.mcg.mcpanel.api.minecraft.ServerStatus;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerStatusPanel extends VBox implements IPanel<List<ServerStatus>>, ITranslatable {
    private static final Component X_AXIS = Component.translatable("main.status.x");
    private static final Component Y_AXIS = Component.translatable("main.status.y");
    private static final Component AVERAGE_TICK = Component.translatable("main.status.average");
    private static final Component TITLE = Component.translatable("main.status.title");

    @Getter
    private final Queue<ServerStatus> tickQueue = new ConcurrentLinkedQueue<>();
    private final LineChart<Number, Number> lineChart;
    private final Label averageTickLabel;
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private float averageTick = 20.0f;

    public ServerStatusPanel() {
        TranslationManager.register(this);

        xAxis.setLabel(X_AXIS.getString());
        yAxis.setLabel(Y_AXIS.getString());
        lineChart = new LineChart<>(xAxis, yAxis);
        series.setName(TITLE.getString());
        lineChart.getData().add(series);
        lineChart.setAnimated(false);
        getChildren().setAll(lineChart);
        averageTickLabel = new Label(AVERAGE_TICK.getString() + averageTick);
        getChildren().add(averageTickLabel);
    }

    @Override
    public void refresh(List<ServerStatus> data) {
        tickQueue.addAll(data);
        updateChart();
    }

    private void updateChart() {
        float totalTick = 0;
        int count = tickQueue.size();
        for (ServerStatus status : tickQueue) {
            totalTick += status.tick();
        }
        if (count > 0) {
            averageTick = totalTick / count;
        }
        averageTickLabel.setText(AVERAGE_TICK.getString() + String.format("%.2f", averageTick));
        XYChart.Series<Number, Number> series = lineChart.getData().getFirst();
        series.getData().clear();
        int time = 0;
        for (ServerStatus status : tickQueue) {
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(time++, status.tick());
            Group transparentGroup = new Group();
            dataPoint.setNode(transparentGroup);
            series.getData().add(dataPoint);
        }
    }

    @Override
    public void translate() {
        xAxis.setLabel(X_AXIS.getString());
        yAxis.setLabel(Y_AXIS.getString());
        series.setName(TITLE.getString());
        averageTickLabel.setText(AVERAGE_TICK.getString() + String.format("%.2f", averageTick));
    }
}