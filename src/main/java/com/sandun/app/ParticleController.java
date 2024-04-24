package com.sandun.app;

import com.sandun.app.model.ParticleEffect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DecimalFormat;

public class ParticleController {
    private ParticleEffect effect;
    private static DecimalFormat df = new DecimalFormat("#.##");
    @FXML
    private Slider numParticlesSlider;
    @FXML
    private Slider speedSlider;
    @FXML
    private Slider lengthSlider;
    @FXML
    private Slider lineWidthSlider;
    @FXML
    private Slider radiusSlider;
    @FXML
    private Button programStateBtn;
    @FXML
    private Label numOfParticles;
    @FXML
    private Label maxLength;
    @FXML
    private Label particleRadius;
    @FXML
    private Label lineWidthText;
    @FXML
    private CheckBox randomCheckBox;
    @FXML
    private CheckBox connectionCheckBox;
    @FXML
    private CheckBox blurCheckBox;
    @FXML
    private CheckBox glowCheckBox;
    @FXML
    private CheckBox randomColorCheckBox;
    @FXML
    private CheckBox randomConnectionColorBox;
    @FXML
    private ColorPicker particleColorPicker;
    @FXML
    private ColorPicker connectionColorPicker;
    @FXML
    private ColorPicker backgroundColorpicker;

    @FXML
    public void onNumParticleDrag() {
        synchronized (effect) {
            int value = (int) (1990.0 / 100 * numParticlesSlider.getValue()) + 10;
            effect.setNumParticles(value);
            effect.initializeParticles();
            numOfParticles.setText(String.valueOf(value));
        }
    }

    @FXML
    public void onSpeedDrag() {
        synchronized (effect) {
            effect.setSpeed((int) (100 - speedSlider.getValue()) / 3);
        }
    }

    @FXML
    public void onMaxLengthDrag() {
        synchronized (effect) {
            int value = ((int) (210.0 / 100 * lengthSlider.getValue()) + 40);
            effect.setMaxDistance(value);
            maxLength.setText(String.valueOf(value));
        }
    }

    @FXML
    public void onRadiusSliderDrag() {
        synchronized (effect) {
            int value = (int) ((29.0 / 100.0) * radiusSlider.getValue()) + 1;
            effect.setParticleRadius(value);
            particleRadius.setText(String.valueOf(value));
        }
    }

    @FXML
    public void onRandomCheckBox(ActionEvent e) {
        synchronized (effect) {
            boolean state = randomCheckBox.isSelected();
            if (state) {
                effect.setRandomRadius(true);
            } else {
                effect.setRandomRadius(false);
            }
            radiusSlider.setDisable(state);
        }
    }

    @FXML
    public void onParticleColorPicker(ActionEvent e) {
        synchronized (effect) {
            effect.setParticleColor(particleColorPicker.getValue());
        }
    }

    @FXML
    public void onConnectionColorPicker(ActionEvent e) {
        synchronized (effect) {
            effect.setConnectionColor(connectionColorPicker.getValue());
        }
    }

    @FXML
    public void onBackgroungColorPicker(ActionEvent e) {
        synchronized (effect) {
            effect.setBackgroundColor(backgroundColorpicker.getValue());
        }
    }

    @FXML
    public void onLineWidth() {
        synchronized (effect) {
            double value = (9.0 / 100) * lineWidthSlider.getValue() + 1;
            effect.setLineWidth(value);
            lineWidthText.setText(df.format(value));
        }
    }

    @FXML
    public void onConnectionCheckBox(ActionEvent e) {
        synchronized (effect) {
            if (connectionCheckBox.isSelected()) {
                effect.setConnectionOn(true);
            } else {
                effect.setConnectionOn(false);
            }
        }
    }

    @FXML
    public void onBlurCheckBox(ActionEvent e) {
        synchronized (effect) {
            boolean state = blurCheckBox.isSelected();
            if (state) {
                glowCheckBox.setSelected(false);
            }
            effect.manageBlueEffectState(state);
        }
    }

    @FXML
    public void onGlowCheckBox(ActionEvent e) {
        synchronized (effect) {
            boolean state = glowCheckBox.isSelected();
            if (state) {
                blurCheckBox.setSelected(false);
            }
            effect.manageGlowEffectState(state);
        }
    }

    @FXML
    public void onRandomColorBox(ActionEvent e) {
        synchronized (effect) {
            boolean state = randomColorCheckBox.isSelected();
            effect.manageRandomColorState(state);

            particleColorPicker.setDisable(state);
            connectionColorPicker.setDisable(state);
            randomConnectionColorBox.setDisable(!state);
            if (!state) {
                effect.setParticleColor(particleColorPicker.getValue());
                effect.setConnectionColor(connectionColorPicker.getValue());
                randomConnectionColorBox.setSelected(false);
                effect.manageRandomConnectionColorState(false);
            }
        }
    }

    @FXML
    public void onConnectionRandomColorBox(ActionEvent e) {
        if (randomColorCheckBox.isSelected()) {
            synchronized (effect) {
                effect.manageRandomConnectionColorState(randomConnectionColorBox.isSelected());
            }
        }
    }

    @FXML
    public void onClose(ActionEvent e) {
        Platform.exit();
        effect.manageRandomColorState(false);
    }

    @FXML
    public void onPause(ActionEvent e) {
        effect.changeProgramState();
        if (programStateBtn.getText().equals("Pause")) {
            programStateBtn.setText("Resume");
        } else {
            programStateBtn.setText("Pause");
        }
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;

    }

}
