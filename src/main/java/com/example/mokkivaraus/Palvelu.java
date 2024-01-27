package com.example.mokkivaraus;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Palvelu {

    private int palvelu_id;
    private String alue_id;
    private String nimi;
    private String kuvaus;
    private Double hinta;
    private Double alv;

    private static final VBox grid = new VBox();
    private static final Stage stage = new Stage();
    private static final Scene scene = new Scene(grid, 550, 500);
    private static final HBox buttonit = new HBox();
    private static final Label nameLabel = new Label("Palvelun nimi: ");
    private static final Label alueLabel = new Label("Alue: ");
    private static final Label hintaLabel = new Label("Hinta: ");
    private static final Label alvLabel = new Label("Alv: ");
    private static final Label kuvausLabel = new Label("Kuvaus: ");
    private static final TextField nameField = new TextField();
    private static final TextField hintaField = new TextField();
    private static final TextField alvField = new TextField();
    private static final TextArea kuvausField = new TextArea();
    private static final ChoiceBox<String> valintaBox = new ChoiceBox<>();
    private static final Popup ok = new Popup();
    private static final Popup virhe = new Popup();
    private static final ArrayList<Integer> listID = new ArrayList<>();
    private static final ArrayList<String> listNimi = new ArrayList<>();
    private static final Button vahvistaButton = new Button();
    private static final Button peruutaButton = new Button("Peruuta");
    private static final Button okButton = new Button("Lisäys onnistui");
    private static final Button virheButton = new Button("Virhe: täydennä puuttuvat tiedot.");

    public Palvelu() {
    }

    public static void muokkaaPalvelua(Palvelu palvelu) {

        luoPohja();
        okButton.setText("Muokkaus onnistui");
        vahvistaButton.setText("Muokkaa");
        stage.setTitle("Village Newbies hallintajärjestelmä - muokkaa palvelua");

        nameField.setText(palvelu.getNimi());
        hintaField.setText(String.valueOf(palvelu.getHinta()));
        alvField.setText(String.valueOf(palvelu.getAlv()));
        kuvausField.setText(palvelu.getKuvaus());
        valintaBox.setValue(palvelu.getAlue_id());
        vahvistaButton.setOnAction(event -> {
            if (
                    nameField.getText().isEmpty() ||
                            valintaBox.getValue() == null ||
                            hintaField.getText().isEmpty() ||
                            alvField.getText().isEmpty() ||
                            kuvausField.getText().isEmpty()
            ) {
                virhe.show(stage);

            } else {
                String nimi = nameField.getText();
                valintaBox.getItems().addAll(listNimi);
                int indexi = listNimi.indexOf(valintaBox.getValue());
                int valinta = listID.get(indexi);
                double hinta = Double.parseDouble(hintaField.getText());
                double alv = Double.parseDouble(alvField.getText());
                String kuvaus = kuvausField.getText();
                int palveluID = palvelu.getPalvelu_id();

                try {
                    String sql = "UPDATE palvelu SET alue_id=?, nimi=?, kuvaus=?, palvelu_hinta=?, alv=? WHERE palvelu_id=?";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);
                    stmt.setInt(1, valinta);
                    stmt.setString(2, nimi);
                    stmt.setString(3, kuvaus);
                    stmt.setDouble(4, hinta);
                    stmt.setDouble(5, alv);
                    stmt.setInt(6, palveluID);

                    ok.show(stage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void lisaaPalvelu() {
        luoPohja();
        okButton.setText("Lisäys onnistui");
        vahvistaButton.setText("Lisää");
        stage.setTitle("Village Newbies hallintajärjestelmä - lisää uusi palvelu");

        nameField.clear();
        hintaField.clear();
        alvField.clear();
        kuvausField.clear();

        //määritellään Lisää-nappulan toiminta
        vahvistaButton.setOnAction(event -> {
            if (
                    nameField.getText().isEmpty() ||
                            valintaBox.getValue() == null ||
                            hintaField.getText().isEmpty() ||
                            alvField.getText().isEmpty() ||
                            kuvausField.getText().isEmpty()
            ) {
                virhe.show(stage);
            } else {
                String nimi = nameField.getText();
                valintaBox.getItems().addAll(listNimi);
                int indexi = listNimi.indexOf(valintaBox.getValue());
                int valinta = listID.get(indexi);
                double hinta = Double.parseDouble(hintaField.getText());
                double alv = Double.parseDouble(alvField.getText());
                String kuvaus = kuvausField.getText();
                try {
                    String sql = "INSERT INTO palvelu (alue_id, nimi, kuvaus, palvelu_hinta, alv) VALUES (?,?,?,?,?)";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);
                    stmt.setInt(1, valinta);
                    stmt.setString(2, nimi);
                    stmt.setString(3, kuvaus);
                    stmt.setDouble(4, hinta);
                    stmt.setDouble(5, alv);

                    ok.show(stage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void poistaPalvelu(Palvelu id_luku) throws SQLException {
        int id_lukuString = id_luku.getPalvelu_id();
        try {
            String sql = "DELETE FROM palvelu WHERE palvelu_id = (?)";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuString);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void luoPohja() {

        //määritellään pohjan ominaisuudet
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");

        //määritellään tekstialueiden koot ja muut ominaisuudet
        nameField.setMaxWidth(250);
        hintaField.setMaxWidth(250);
        alvField.setMaxWidth(250);
        kuvausField.setMaxWidth(300);
        kuvausField.setPrefRowCount(5);
        kuvausField.setWrapText(true);
        salliVainDouble(hintaField);
        salliVainDouble(alvField);
        buttonit.setPadding(new Insets(10, 0, 0, 0));
        buttonit.setSpacing(15);

        //lisätään elementit pohjaan
        grid.getChildren().clear();
        buttonit.getChildren().clear();
        grid.getChildren().add(nameLabel);
        grid.getChildren().add(nameField);
        grid.getChildren().add(alueLabel);
        grid.getChildren().add(valintaBox);
        grid.getChildren().add(hintaLabel);
        grid.getChildren().add(hintaField);
        grid.getChildren().add(alvLabel);
        grid.getChildren().add(alvField);
        grid.getChildren().add(kuvausLabel);
        grid.getChildren().add(kuvausField);
        buttonit.getChildren().addAll(vahvistaButton, peruutaButton);
        grid.getChildren().add(buttonit);

        //Lisätään sisältö pop uppeihin ja määritellään buttonien toiminnallisuus
        ok.getContent().clear();
        virhe.getContent().clear();
        ok.getContent().add(okButton);
        virhe.getContent().add(virheButton);

        okButton.setOnAction(e -> stage.close());
        virheButton.setOnAction(e -> virhe.hide());
        peruutaButton.setOnAction(e -> stage.close());

        listID.clear();
        listNimi.clear();

        //Alue -listan haku ja teko
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM alue");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("alue_id");
                String name = rs.getString("nimi");
                listID.add(id);
                listNimi.add(name);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        valintaBox.getItems().clear();
        valintaBox.getItems().addAll(listNimi);

        //luodaan scene & stage
        stage.getIcons().add(new Image("/VN-logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void salliVainDouble(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(newValue.replaceAll("[^\\d*(\\.\\d*)?]", ""));
            }
        });
    }

    public int getPalvelu_id() {
        return palvelu_id;
    }

    public void setPalvelu_id(int palvelu_id) {
        this.palvelu_id = palvelu_id;
    }

    public String getAlue_id() {
        return alue_id;
    }

    public void setAlue_id(String alue_id) {
        this.alue_id = alue_id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Double getHinta() {
        return hinta;
    }

    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }

    public double getAlv() {
        return alv;
    }

    public void setAlv(Double alv) {
        this.alv = alv;
    }
}