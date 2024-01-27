package com.example.mokkivaraus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Mokki {

    private String mokkinimi;
    private String katuosoite;
    private String postinro;
    private double hinta;
    private String kuvaus;
    private int henkilomaara;
    private String varustelu;
    private Integer mokki_id;
    private String alue_id;

    private static final VBox grid = new VBox();
    private static final Stage stage = new Stage();
    private static final Scene scene = new Scene(grid, 550, 500);
    private static final HBox buttonit = new HBox();

    private static final Label nameLabel = new Label("Mökin nimi: ");
    private static final Label postinroLabel = new Label("Postinumero: ");
    private static final Label alueLabel = new Label("Alue: ");
    private static final Label hintaLabel = new Label("Hinta: ");
    private static final Label katuosoiteLabel = new Label("Osoite: ");
    private static final Label henkMaaraLabel = new Label("Henkilömäärä: ");
    private static final Label kuvausLabel = new Label("Kuvaus: ");
    private static final Label varusteluLabel = new Label("Varustelu: ");
    private static final TextField nameField = new TextField();
    private static final TextArea varusteluField = new TextArea();
    private static final TextField postinroField = new TextField();
    private static final TextField katuosoiteField = new TextField();
    private static final TextField hintaField = new TextField();
    private static final TextField henkMaaraField = new TextField();
    private static final TextArea kuvausField = new TextArea();
    private static final ChoiceBox<String> valintaBox = new ChoiceBox<>();
    private static final Popup ok = new Popup();
    private static final Popup virhe = new Popup();
    private static final Popup virhe2 = new Popup();
    private static final ArrayList<Integer> listID = new ArrayList<>();
    private static final ArrayList<String> listNimi = new ArrayList<>();
    private static final Button vahvistaButton = new Button();
    private static final Button peruutaButton = new Button("Peruuta");
    private static final Button okMuokkaus = new Button("Muokkaus onnistui");
    private static final Button virheButton = new Button("Virhe: täydennä puuttuvat tiedot.");
    private static final Button virhe2Button = new Button("Käytä olemassa olevaa postinumrtoa");

    public Mokki() {
    }

    public static void lisaaMokki() {
        Stage mokkiStage = new Stage();
        VBox grid = new VBox();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");

        Label nameLabel = new Label("Mökin nimi:");
        TextField nameField = new TextField();
        Label valintaLabel = new Label("Valitse alue");
        ChoiceBox<String> valintaBox = new ChoiceBox<>();
        Label katuLabel = new Label("Katuosoite: ");
        TextField katuField = new TextField();
        Label postinroLabel = new Label("Postinumero: ");
        TextField postinroField = new TextField();
        Label hintaLabel = new Label("Hinta");
        TextField hintaField = new TextField();
        Label henkLabel = new Label("Henkilömäärä");
        TextField henkField = new TextField();
        Label kuvausLabel = new Label("Kuvaus");
        TextField kuvausField = new TextField();
        Label varusteluLabel = new Label("Varustelu");
        TextField varusteluField = new TextField();

        grid.getChildren().add(nameLabel);
        grid.getChildren().add(nameField);
        grid.getChildren().add(valintaLabel);
        grid.getChildren().add(valintaBox);
        grid.getChildren().add(katuLabel);
        grid.getChildren().add(katuField);
        grid.getChildren().add(postinroLabel);
        grid.getChildren().add(postinroField);
        grid.getChildren().add(hintaLabel);
        grid.getChildren().add(hintaField);
        grid.getChildren().add(henkLabel);
        grid.getChildren().add(henkField);
        grid.getChildren().add(kuvausLabel);
        grid.getChildren().add(kuvausField);
        grid.getChildren().add(varusteluLabel);
        grid.getChildren().add(varusteluField);

        Button addButton = new Button("Lisää");
        Button cancelButton = new Button("Peruuta");
        Button okButton = new Button("Lisäys ok");

        grid.getChildren().add(addButton);
        grid.getChildren().add(cancelButton);
        grid.getChildren().add(okButton);

        Scene mokkiScene = new Scene(grid, 720, 600);
        mokkiStage.setTitle("Village Newbies hallintajärjestelmä");
        mokkiStage.getIcons().add(new Image("/VN-logo.png"));
        mokkiStage.setScene(mokkiScene);
        mokkiStage.show();

        //Vahvistus että lisäys on ok
        Popup ok = new Popup();
        ok.getContent().add(okButton);
        okButton.setOnAction(actionEvent -> mokkiStage.close());

        //Alue listan haku ja teko
        ArrayList<Integer> listID = new ArrayList<>();
        ArrayList<String> listNimi = new ArrayList<>();

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
        valintaBox.getItems().addAll(listNimi);


        // Lisäys napin toiminta
        addButton.setOnAction(event -> {

            String name = nameField.getText();

            String katu = katuField.getText();
            valintaBox.getItems().addAll(listNimi);
            int indexi = listNimi.indexOf(valintaBox.getValue());
            int valinta = listID.get(indexi);
            String postinro = (postinroField.getText());
            int hinta = Integer.parseInt(hintaField.getText());
            int henk = Integer.parseInt(henkField.getText());
            String kuvaus = kuvausField.getText();
            String varustelu = varusteluField.getText();

            try {
                String sql = "INSERT INTO mokki (alue_id, postinro, mokkinimi, katuosoite, hinta, kuvaus, henkilomaara, varustelu) VALUES (?,?,?,?,?,?,?,?)";
                PreparedStatement stmt = VN.conn.prepareStatement(sql);
                stmt.setString(3, name);
                stmt.setInt(1, valinta);
                stmt.setString(4, katu);
                stmt.setString(2, postinro);
                stmt.setDouble(5, hinta);
                stmt.setString(6, kuvaus);
                stmt.setInt(7, henk);
                stmt.setString(8, varustelu);
                stmt.executeUpdate();
                ok.show(mokkiStage);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(e -> mokkiStage.close());
    }

    public static void poistaMokki(Mokki id_luku) throws SQLException {
        Stage mokkiPoistoStage = new Stage();
        VBox grid = new VBox();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");
        int id_lukuString = id_luku.getMokki_id();
        Label teksti = new Label("Mökki poistettu!");
        try {
            String sql = "DELETE FROM mokki WHERE mokki_id = (?)";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuString);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Scene mokkiScene = new Scene(grid, 720, 600);
        grid.getChildren().add(teksti);
        mokkiPoistoStage.setTitle("Village Newbies hallintajärjestelmä");
        mokkiPoistoStage.getIcons().add(new Image("/VN-logo.png"));
        mokkiPoistoStage.setScene(mokkiScene);
        mokkiPoistoStage.show();
    }

    public static void muokkaaMokkia(Mokki mokki) throws SQLException {

        luoPohja();
        vahvistaButton.setText("Muokkaa");
        stage.setTitle("Village Newbies hallintajärjestelmä - muokkaa mökkiä");


        nameField.setText(mokki.getMokkinimi());
        postinroField.setText(mokki.getPostinro());
        hintaField.setText(String.valueOf(mokki.getHinta()));
        henkMaaraField.setText(String.valueOf(mokki.getHenkilomaara()));
        katuosoiteField.setText(mokki.getKatuosoite());
        kuvausField.setText(mokki.getKuvaus());
        varusteluField.setText(mokki.getVarustelu());
        valintaBox.setValue(mokki.getAlue_id());

        vahvistaButton.setOnAction(event -> {
            if (
                    nameField.getText().isEmpty() ||
                            postinroField.getText().isEmpty() ||
                            valintaBox.getValue() == null ||
                            varusteluField.getText().isEmpty() ||
                            hintaField.getText().isEmpty() ||
                            henkMaaraField.getText().isEmpty() ||
                            katuosoiteField.getText().isEmpty() ||
                            kuvausField.getText().isEmpty()
            ) {
                virhe.show(stage);
            } else if (!mokki.postiHaku(postinroField.getText())) {
                virhe2.show(stage);

            } else {
                String nimi = nameField.getText();
                valintaBox.getItems().addAll(listNimi);
                int indexi = listNimi.indexOf(valintaBox.getValue());
                int valinta = listID.get(indexi);
                double hinta = Double.parseDouble(hintaField.getText());
                int henkMaara = Integer.parseInt(henkMaaraField.getText());
                String postinro = postinroField.getText();
                String katuosoite = katuosoiteField.getText();
                String kuvaus = kuvausField.getText();
                String varustelu = varusteluField.getText();
                int mokkiID = mokki.getMokki_id();

                try {
                    String sql = "UPDATE mokki SET alue_id=?, mokkinimi=?, kuvaus=?, hinta=?, henkilomaara=?, katuosoite=?, postinro=?, varustelu=? WHERE mokki_id=?";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);
                    stmt.setInt(1, valinta);
                    stmt.setString(2, nimi);
                    stmt.setString(3, kuvaus);
                    stmt.setDouble(4, hinta);
                    stmt.setInt(5, henkMaara);
                    stmt.setString(6, katuosoite);
                    stmt.setString(7, postinro);
                    stmt.setString(8, varustelu);
                    stmt.setInt(9, mokkiID);

                    ok.show(stage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void luoPohja() {

        //määritellään pohjan ominaisuudet
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");

        //määritellään tekstialueiden koot ja muut ominaisuudet
        nameField.setMaxWidth(200);
        hintaField.setMaxWidth(50);
        henkMaaraField.setMaxWidth(50);
        katuosoiteField.setMaxWidth(200);
        postinroField.setMaxWidth(150);
        kuvausField.setMaxWidth(400);
        kuvausField.setPrefRowCount(3);
        varusteluField.setMaxWidth(400);
        varusteluField.setPrefRowCount(3);
        kuvausField.setWrapText(true);
        salliVainDouble(hintaField);
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
        grid.getChildren().add(katuosoiteLabel);
        grid.getChildren().add(katuosoiteField);
        grid.getChildren().add(postinroLabel);
        grid.getChildren().add(postinroField);
        grid.getChildren().add(henkMaaraLabel);
        grid.getChildren().add(henkMaaraField);
        grid.getChildren().add(kuvausLabel);
        grid.getChildren().add(kuvausField);
        grid.getChildren().add(varusteluLabel);
        grid.getChildren().add(varusteluField);
        buttonit.getChildren().addAll(vahvistaButton, peruutaButton);
        grid.getChildren().add(buttonit);

        //Lisätään sisältö pop uppeihin ja määritellään buttonien toiminnallisuus

        ok.getContent().clear();
        virhe.getContent().clear();
        virhe2.getContent().clear();
        ok.getContent().add(okMuokkaus);
        virhe.getContent().add(virheButton);
        virhe2.getContent().add(virhe2Button);

        okMuokkaus.setOnAction(actionEvent -> stage.close());
        virheButton.setOnAction(actionEvent -> virhe.hide());
        virhe2Button.setOnAction(actionEvent -> virhe2.hide());
        peruutaButton.setOnAction(e -> stage.close());

        listID.clear();
        listNimi.clear();

        //Alue -listan haku ja teko
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM alue");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Retrieve the values of each column in the current row
                int id = rs.getInt("alue_id");
                String name = rs.getString("nimi");

                // Add the object to the list
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

    private boolean postiHaku(String haku) {
        try {
            String sql = "SELECT postinro FROM posti";

            Statement statement = VN.conn.createStatement();
            ResultSet result = statement.executeQuery(sql);

            while (result.next()){
                String postiHaku = result.getString("postinro");
                if (Objects.equals(postiHaku, haku)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public void setMokkiNimi(String mokkinimi) {
        this.mokkinimi = mokkinimi;
    }

    public  String getMokkinimi() {
        return mokkinimi;
    }

    public void setKatu(String katuosoite) {
        this.katuosoite = katuosoite;
    }

    public String getKatuosoite() {
        return katuosoite;
    }

    public void setPostiNro(String postinro) {
        this.postinro = postinro;
    }

    public String getPostinro() {
        return postinro;
    }

    public void setHinta(double hinta) {
        this.hinta = hinta;
    }

    public double getHinta() {
        return hinta;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setHenkMaara(int henkilomaara) {
        this.henkilomaara = henkilomaara;
    }


    public void setVarustelu(String varustelu) {
        this.varustelu = varustelu;
    }

    public String getVarustelu() {
        return varustelu;
    }

    public int getHenkilomaara() {
        return henkilomaara;
    }

    public void setMokkiID(int mokki_id) {
        this.mokki_id = mokki_id;
    }

    public Integer getMokki_id() {
        return mokki_id;
    }

    public void setAlue_id(String alue_id) {
        this.alue_id = alue_id;
    }

    public String getAlue_id() {
        return alue_id;
    }

}