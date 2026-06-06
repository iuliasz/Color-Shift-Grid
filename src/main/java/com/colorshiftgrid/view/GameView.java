package com.colorshiftgrid.view;

import com.colorshiftgrid.controller.GameController;
import com.colorshiftgrid.model.Board;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

    private static final String BG_DARK      = "#0a0a14";
    private static final String BG_PANEL     = "#0f0f1e";
    private static final String BORDER_GLOW  = "#00ffcc";
    private static final String ACCENT_CYAN  = "#00e5ff";
    private static final String ACCENT_PINK  = "#ff2d78";
    private static final String TEXT_MAIN    = "#e0f7ff";
    private static final String TEXT_DIM     = "#4a7080";

    private static final Color[] BLOCK_COLORS = {
            Color.web("#ff2d78"),   // hot pink
            Color.web("#ffe600"),   // neon yellow
            Color.web("#00e676"),   // neon green
            Color.web("#2979ff"),   // electric blue
    };

    private final BorderPane root;
    private final GridPane gridPane;
    private final Rectangle[][] cells;

    private final Label statsLabel;
    private final Label hintLabel;

    private final Button undoBtn;
    private final Button restartBtn;
    private final Button hintBtn;
    private final Button targetBtn;
    private final Button autoStepBtn;

    private final ComboBox<String> modeSelector;

    public GameView(int gridSize) {
        root = new BorderPane();
        gridPane = new GridPane();
        cells = new Rectangle[gridSize][gridSize];

        // main background
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // title
        Label titleLabel = new Label("COLOR SHIFT GRID");
        titleLabel.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + ACCENT_CYAN + ";" +
                        "-fx-letter-spacing: 4px;"
        );
        DropShadow titleGlow = new DropShadow(20, Color.web(ACCENT_CYAN));
        titleGlow.setSpread(0.4);
        titleLabel.setEffect(titleGlow);

        HBox logoBlocks = buildLogoStrip();

        // mode selector
        modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll("Classic Mode", "Challenge Mode", "Pattern Mode");
        modeSelector.setValue("Classic Mode");
        modeSelector.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-color: #1a1a2e;" +
                        "-fx-border-color: " + ACCENT_CYAN + ";" +
                        "-fx-border-radius: 4px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-text-fill: " + TEXT_MAIN + ";"
        );

        // stats
        statsLabel = new Label("STEPS: 0  |  PROGRESS: 0%");
        statsLabel.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + ACCENT_CYAN + ";"
        );

        // hint
        hintLabel = new Label("▸  PRESS HINT WHEN STUCK");
        hintLabel.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: " + TEXT_DIM + ";"
        );

        // top panel
        VBox topBox = new VBox(8, titleLabel, logoBlocks, modeSelector, statsLabel, hintLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 22px 20px 12px 20px;");
        root.setTop(topBox);

        // grid cells
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.setStyle(
                "-fx-padding: 18px;" +
                        "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + BORDER_GLOW + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 6px;" +
                        "-fx-background-radius: 6px;"
        );

        DropShadow gridGlow = new DropShadow(30, Color.web(BORDER_GLOW));
        gridGlow.setSpread(0.15);
        gridPane.setEffect(gridGlow);


        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Rectangle rect = new Rectangle(90, 100);
                rect.setFill(Color.GRAY);
                rect.setArcWidth(4);

                rect.setArcHeight(4);
                rect.setStroke(Color.web("#000000"));
                rect.setStrokeWidth(2);

                cells[i][j] = rect;
                gridPane.add(rect, j, i);
            }
        }

        StackPane gridWrapper = new StackPane(buildScanlineOverlay(gridSize), gridPane);
        root.setCenter(gridWrapper);

        undoBtn     = buildButton("⟵ UNDO",     ACCENT_CYAN);
        restartBtn  = buildButton("↺ RESTART",  ACCENT_PINK);
        hintBtn     = buildButton("? HINT",      "#ffe600");
        autoStepBtn = buildButton("▶ AUTO",      "#00e676");
        targetBtn   = buildButton("◉ TARGET",   ACCENT_CYAN);
        targetBtn.setVisible(false);

        HBox bottomBox = new HBox(10, undoBtn, restartBtn, hintBtn, autoStepBtn, targetBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 16px 20px 22px 20px;");
        root.setBottom(bottomBox);
    }

    public Parent createLayout() {
        return root;
    }

    public void updateGrid(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Color c = getColor(grid[i][j]);
                cells[i][j].setFill(buildBlockFill(c));

                DropShadow glow = new DropShadow(18, c);
                glow.setSpread(0.3);
                Glow g = new Glow(0.6);
                g.setInput(glow);
                cells[i][j].setEffect(g);
            }
        }
    }

    public void updateStats(int steps, int progress, int moveLimit) {
        String limitText = moveLimit == -1 ? "unlimited" : String.valueOf(moveLimit);
        statsLabel.setText("STEPS: " + steps + " / " + limitText + "   |   PROGRESS: " + progress + "%");
    }

    public void updateHint(String text) {
        hintLabel.setText("▸  " + text.toUpperCase());
        hintLabel.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #ffe600;"
        );
    }

    public void highlightCell(int row, int col) {
        clearHintHighlight();

        cells[row][col].setStroke(Color.web("#ffe600"));
        cells[row][col].setStrokeWidth(4);

        ScaleTransition pulse = new ScaleTransition(Duration.millis(400), cells[row][col]);
        pulse.setFromX(1.0); pulse.setToX(1.08);
        pulse.setFromY(1.0); pulse.setToY(1.08);
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    public void clearHintHighlight() {
        for (Rectangle[] row : cells) {
            for (Rectangle cell : row) {
                cell.setStroke(Color.web("#000000"));
                cell.setStrokeWidth(2);
            }
        }

        hintLabel.setText("▸  PRESS HINT WHEN STUCK");
        hintLabel.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: " + TEXT_DIM + ";"
        );
    }

    public void bindClickHandler(java.util.function.BiConsumer<Integer, Integer> handler) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(e -> handler.accept(row, col));
            }
        }
    }

    public void bindController(GameController controller) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                final int row = i;
                final int col = j;

                cells[i][j].setOnMouseClicked(e -> {
                    animateClick(cells[row][col]);
                    controller.handleClick(row, col);
                });
            }
        }
        undoBtn.setOnAction(e  -> controller.undo());
        restartBtn.setOnAction(e -> controller.restart());
        hintBtn.setOnAction(e  -> controller.showHint());
        autoStepBtn.setOnAction(e -> controller.autoStep());
        modeSelector.setOnAction(e -> controller.changeMode(modeSelector.getValue()));
        targetBtn.setOnAction(e -> controller.showTargetPattern());
    }

    public void showTargetWindow(int[][] targetGrid) {
        Stage targetStage = new Stage();
        GridPane miniGrid = new GridPane();

        miniGrid.setAlignment(Pos.CENTER);
        miniGrid.setHgap(4);
        miniGrid.setVgap(4);
        miniGrid.setStyle(
                "-fx-padding: 20px;" +
                        "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-border-color: " + ACCENT_CYAN + ";" +
                        "-fx-border-width: 2px;"
        );

        for (int i = 0; i < targetGrid.length; i++) {
            for (int j = 0; j < targetGrid[0].length; j++) {
                Rectangle rect = new Rectangle(50, 50);

                Color c = getColor(targetGrid[i][j]);
                rect.setFill(buildBlockFill(c));
                rect.setArcWidth(4);
                rect.setArcHeight(4);
                DropShadow glow = new DropShadow(12, c);
                glow.setSpread(0.3);
                rect.setEffect(glow);
                miniGrid.add(rect, j, i);
            }
        }

        Label targetTitle = new Label("TARGET PATTERN");
        targetTitle.setStyle(
                "-fx-font-family: 'Courier New'; -fx-font-size: 16px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + ACCENT_CYAN + "; -fx-padding: 0 0 10 0;"
        );
        VBox wrap = new VBox(10, targetTitle, miniGrid);
        wrap.setAlignment(Pos.CENTER);
        wrap.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 20px;");
        DropShadow wrapGlow = new DropShadow(20, Color.web(ACCENT_CYAN));
        wrap.setEffect(wrapGlow);

        Scene targetScene = new Scene(wrap);
        targetScene.setFill(Color.web(BG_DARK));
        targetStage.setTitle("Target Pattern");
        targetStage.setScene(targetScene);
        targetStage.setAlwaysOnTop(true);
        targetStage.show();
    }
    public void setTargetButtonVisible(boolean visible) {
        targetBtn.setVisible(visible);
    }

    public void render(Board board) {
        updateGrid(board.getGrid());
    }

    // helper fcts
    private Color getColor(int value) {
        if (value >= 0 && value < BLOCK_COLORS.length) return BLOCK_COLORS[value];
        return Color.DARKGRAY;
    }

    private LinearGradient buildBlockFill(Color base) {
        Color bright = base.brighter().interpolate(Color.WHITE, 0.25);
        Color dark   = base.darker().darker();
        return new LinearGradient(0, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, bright),
                new Stop(0.5, base),
                new Stop(1.0, dark)
        );
    }

    private HBox buildLogoStrip() {
        HBox strip = new HBox(3);
        strip.setAlignment(Pos.CENTER);
        int[] seq = {0, 2, 1, 3, 0, 3, 1, 2};
        for (int v : seq) {
            Rectangle r = new Rectangle(14, 14);
            Color c = BLOCK_COLORS[v];
            r.setFill(buildBlockFill(c));
            r.setArcWidth(2); r.setArcHeight(2);
            DropShadow g = new DropShadow(6, c); g.setSpread(0.4);
            r.setEffect(g);
            strip.getChildren().add(r);
        }
        return strip;
    }

    private Pane buildScanlineOverlay(int gridSize) {
        Pane overlay = new Pane();
        int totalPx = gridSize * 90 + (gridSize - 1) * 6 + 36; // rough height
        overlay.setPrefSize(totalPx, totalPx);
        overlay.setMouseTransparent(true);
        for (int y = 0; y < totalPx; y += 4) {
            Rectangle line = new Rectangle(0, y, totalPx, 1);
            line.setFill(Color.color(0, 0, 0, 0.12));
            overlay.getChildren().add(line);
        }
        return overlay;
    }

    private Button buildButton(String text, String hexColor) {
        Button btn = new Button(text);
        Color c = Color.web(hexColor);
        String css =
                "-fx-font-family: 'Courier New';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + hexColor + ";" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + hexColor + ";" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 3px;" +
                "-fx-background-radius: 3px;" +
                "-fx-padding: 7px 14px;";
        btn.setStyle(css);

        DropShadow glow = new DropShadow(10, c);
        glow.setSpread(0.2);
        btn.setEffect(glow);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(css +
                    "-fx-background-color: " + hexColor + "33;" +
                    "-fx-cursor: hand;"
            );
            DropShadow hoverGlow = new DropShadow(20, c);
            hoverGlow.setSpread(0.5);
            btn.setEffect(hoverGlow);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(css);
            btn.setEffect(glow);
        });
        return btn;
    }

    private void animateClick(Rectangle cell) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), cell);
        st.setFromX(1.0); st.setToX(0.88);
        st.setFromY(1.0); st.setToY(0.88);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
}