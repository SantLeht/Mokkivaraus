package com.example.mokkivaraus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class Alue {

    private String nimi;

    private Integer alue_id;

    private static final VBox grid = new VBox();
    private static final Stage stage = new Stage();
    private static final Scene scene = new Scene(grid, 550, 500);
    private static final HBox buttonit = new HBox();
    private static final Label nameLabel = new Label("Alueen nimi: ");
    private static final TextField nameField = new TextField();

    private static final Popup ok = new Popup();
    private static final Popup virhe = new Popup();
    private static final Button vahvistaButton = new Button();
    private static final Button peruutaButton = new Button("Peruuta");
    private static final Button okButton = new Button("Lisäys onnistui");
    private static final Button okMuokkaus = new Button("Muokkaus onnistui");
    private static final Button virheButton = new Button("Virhe: täydennä puuttuvat tiedot.");
    public Alue(){
    }
    public static void lisaaAlue(){
        Stage alueStage = new Stage();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);


        Label nameLabel = new Label("Alueen nimi:");
        TextField nameField = new TextField();


        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Button addButton = new Button("Lisää");
        Button cancelButton = new Button("Peruuta");
        Button okButton = new Button("Lisäys ok");

        grid.add(addButton, 0, 3);
        grid.add(cancelButton, 1, 3);
        grid.add(okButton, 3,3);

        Scene alueScene = new Scene(grid, 720, 600);
        alueStage.setTitle("Village Newbies hallintajärjestelmä");
        alueStage.getIcons().add(new Image("/VN-logo.png"));
        alueStage.setScene(alueScene);
        alueStage.show();
        grid.setStyle("-fx-background-color: #FBF4D9;");

        Popup ok = new Popup();
        ok.getContent().add(okButton);
        okButton.setOnAction(actionEvent -> alueStage.close());


        addButton.setOnAction(event -> {

            String name = nameField.getText();

            try  {
                String sql = "INSERT INTO alue (nimi) VALUES (?)";
                PreparedStatement stmt = VN.conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.executeUpdate();


                ok.show(alueStage);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> alueStage.close());
    }

    public static void muokkaaAlue(Alue alue) {

        luoPohja();
        vahvistaButton.setText("Muokkaa");
        stage.setTitle("Village Newbies hallintajärjestelmä - muokkaa aluetta");

        nameField.setText(alue.getNimi());

        vahvistaButton.setOnAction(event -> {
            if (
                    nameField.getText().isEmpty()
            ) {
                virhe.show(stage);

            } else {
                String nimi = nameField.getText();
                int alue_ID = alue.getAlue_id();


                try {
                    String sql = "UPDATE alue SET nimi=? WHERE alue_id=?";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);
                    stmt.setString(1, nimi );
                    stmt.setInt(2, alue_ID);


                    ok.show(stage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void poistaAlue(Alue id_luku) throws SQLException {
        Stage aluePoistoStage = new Stage();
        VBox grid = new VBox();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");
        int id_lukuString = id_luku.getAlue_id();
        Button poistoOK = new Button("Alue poistettu!");
        try {
            String sql = "DELETE FROM alue WHERE alue_id = (?)";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuString);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        Scene alueScene = new Scene(grid, 720, 600);
        grid.getChildren().add(poistoOK);
        aluePoistoStage.setTitle("Village Newbies hallintajärjestelmä");
        aluePoistoStage.getIcons().add(new Image("/VN-logo.png"));
        aluePoistoStage.setScene(alueScene);
        aluePoistoStage.show();
        poistoOK.setOnAction(actionEvent -> aluePoistoStage.close());
    }
    public static void luoPohja() {

        //määritellään pohjan ominaisuudet
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");

        //määritellään tekstialueiden koot ja muut ominaisuudet
        nameField.setMaxWidth(250);

        buttonit.setPadding(new Insets(10, 0, 0, 0));
        buttonit.setSpacing(15);

        //lisätään elementit pohjaan
        grid.getChildren().clear();
        buttonit.getChildren().clear();
        grid.getChildren().add(nameLabel);
        grid.getChildren().add(nameField);

        buttonit.getChildren().addAll(vahvistaButton, peruutaButton);
        grid.getChildren().add(buttonit);

        //Lisätään sisältö pop uppeihin ja määritellään buttonien toiminnallisuus
        ok.getContent().clear();
        virhe.getContent().clear();
        ok.getContent().add(okMuokkaus);
        virhe.getContent().add(virheButton);

        okMuokkaus.setOnAction(actionEvent -> stage.close());
        virheButton.setOnAction(actionEvent -> virhe.hide());
        peruutaButton.setOnAction(e -> stage.close());




        //Alue -listan haku ja teko
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM alue");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Retrieve the values of each column in the current row
                int id = rs.getInt("alue_id");
                String name = rs.getString("nimi");


            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        //luodaan scene & stage
        stage.getIcons().add(new Image("/VN-logo.png"));
        stage.setScene(scene);
        stage.show();
    }



    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getNimi() {

        return nimi;
    }

    public void setAlue_id(int alue_id) {
        this.alue_id=alue_id;
    }

    public int getAlue_id() {
        return alue_id;
    }
}