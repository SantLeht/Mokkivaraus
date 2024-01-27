package com.example.mokkivaraus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Varaus {

    private int varaus_id;
    private int asiakas_id;
    private int mokki_mokki_id;
    private Date varattu_pvm;
    private Date vahvistus_pvm;
    private Date varattu_alkupvm;
    private Date varattu_loppupvm;

    private static final VBox grid = new VBox();
    private static final Stage stage = new Stage();
    private static final Scene scene = new Scene(grid, 550, 500);
    private static final HBox buttonit = new HBox();
    private static final Label varausLabel = new Label("Varaus Id: ");
    private static final Label asiakasLabel = new Label("Asikas Id: ");
    private static final Label mokkiLabel = new Label("Mökki Id: ");
    private static final Label varattuPVMLabel = new Label("Varattu päivämäärä: ");
    private static final Label vahvistusPVMLabel = new Label("Vahvistus päivämäärä: ");
    private static final Label varattualkuPVMLabel = new Label("Alkamis päivämäärä: ");
    private static final Label varattuloppuPVMLabel = new Label("Loppumis päivämäärä: ");

    private static final TextField varausField = new TextField();
    private static final TextField asiakasField = new TextField();
    private static final TextField mokkiField = new TextField();
    private static final TextField varattuPVMField = new TextField();
    private static final TextField vahvistusPVMField = new TextField();
    private static final TextField varattualkuPVMField = new TextField();
    private static final TextField varattuloppuPVMField = new TextField();

    private static final Popup ok = new Popup();
    private static final Popup virhe = new Popup();

    private static final Button vahvistaButton = new Button("Vahvista");
    private static final Button peruutaButton = new Button("Peruuta");
    private static final Button okButton = new Button("Lisäys onnistui");
    private static final Button virheButton = new Button("Virhe: täydennä puuttuvat tiedot.");

    public Varaus() {
    }

    public static void muokkaaVaraus(Varaus varaus) {

        luoPohjat();
        vahvistaButton.setText("Muokkaa");
        stage.setTitle("Village Newbies hallintajärjestelmä - muokkaa varausta");

        varausField.setText(String.valueOf(Integer.valueOf(varaus.getVaraus_id())));
        asiakasField.setText(String.valueOf(Integer.valueOf(varaus.getAsiakas_id())));
        mokkiField.setText(String.valueOf(Integer.valueOf(varaus.getMokki_mokki_id())));
        varattuPVMField.setText(String.valueOf(varaus.getVarattu_pvm()));
        vahvistusPVMField.setText(String.valueOf(varaus.getVahvistus_pvm()));
        varattualkuPVMField.setText(String.valueOf(varaus.getVarattu_alkupvm()));
        varattuloppuPVMField.setText(String.valueOf(varaus.getVarattu_loppupvm()));

        vahvistaButton.setOnAction(event -> {
            if (
                    varausField.getText().isEmpty() ||
                            asiakasField.getText().isEmpty() ||
                            mokkiField.getText().isEmpty() ||
                            varattuPVMField.getText().isEmpty() ||
                            vahvistusPVMField.getText().isEmpty() ||
                            varattualkuPVMField.getText().isEmpty()||
                            varattuloppuPVMField.getText().isEmpty()




            ) {
                virhe.show(stage);

            } else {
                int varaus_id = Integer.parseInt(varausField.getText());
                int asiakas_id = Integer.parseInt(asiakasField.getText());
                int mokki_mokki_id = Integer.parseInt(mokkiField.getText());
                String varattuPVM = varattuPVMField.getText();
                String vahvistusPVM = vahvistusPVMField.getText();
                String varattualkuPVM = varattualkuPVMField.getText();
                String varattuloppuPVM = varattuloppuPVMField.getText();


                try {
                    String sql = "UPDATE varaus SET varaus_id=?, asiakas_id=?, mokki_mokki_id=?, varattu_pvm=?, vahvistus_pvm=?, varattu_alkupvm=?, varattu_loppupvm=? WHERE varaus_id=?";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);

                    stmt.setInt(1,varaus_id);
                    stmt.setInt(2,asiakas_id);
                    stmt.setInt(3,mokki_mokki_id);
                    stmt.setString(4, varattuPVM);
                    stmt.setString(5, vahvistusPVM);
                    stmt.setString(6,varattualkuPVM);
                    stmt.setString(7,varattuloppuPVM);



                    ok.show(stage);
                    stmt.setInt(8, varaus_id);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void lisaaVaraus() {
        luoPohjat();
        vahvistaButton.setText("Lisää");
        stage.setTitle("Village Newbies hallintajärjestelmä - lisää uusi varaus");

        varausField.clear();
        asiakasField.clear();
        mokkiField.clear();
        varattuPVMField.clear();
        vahvistusPVMField.clear();
        varattualkuPVMField.clear();
        varattuloppuPVMField.clear();

        //määritellään Lisää-nappulan toiminta
        vahvistaButton.setOnAction(event -> {
            if (
                    varausField.getText().isEmpty() ||
                            asiakasField.getText().isEmpty() ||
                            mokkiField.getText().isEmpty() ||
                            varattuPVMField.getText().isEmpty() ||
                            vahvistusPVMField.getText().isEmpty() ||
                            varattualkuPVMField.getText().isEmpty()||
                            varattuloppuPVMField.getText().isEmpty()
            ) {
                virhe.show(stage);
            } else {
                int varaus_id = Integer.parseInt(varausField.getText());
                int asiakas_id = Integer.parseInt(asiakasField.getText());
                int mokki_id = Integer.parseInt(mokkiField.getText());
                String varattuPVM = varattuPVMField.getText();
                String vahvistusPVM = vahvistusPVMField.getText();
                String varattualkuPVM = varattualkuPVMField.getText();
                String varattuloppuPVM = varattuloppuPVMField.getText();
                try {
                    String sql = "INSERT INTO varaus (varaus_id, asiakas_id, mokki_mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm) VALUES (?,?,?,?,?,?,?)";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);
                    stmt.setInt(1,varaus_id);
                    stmt.setInt(2,asiakas_id);
                    stmt.setInt(3, mokki_id);
                    stmt.setString(4, varattuPVM);
                    stmt.setString(5, vahvistusPVM);
                    stmt.setString(6,varattualkuPVM);
                    stmt.setString(7,varattuloppuPVM);

                    ok.show(stage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void poistaVaraus(Varaus id_luku) throws SQLException {
        int id_lukuString = id_luku.getVaraus_id();
        try {
            String sql = "DELETE FROM varaus WHERE varaus_id = (?)";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuString);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void luoPohjat() {

        //määritellään pohjan ominaisuudet
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setStyle("-fx-background-color: #FBF4D9;");

        //määritellään tekstialueiden koot ja muut ominaisuudet
        varausField.setMaxWidth(250);
        asiakasField.setMaxWidth(250);
        mokkiField.setMaxWidth(250);
        varattuPVMField.setMaxWidth(300);
        vahvistusPVMField.setMaxWidth(300);
        varattualkuPVMField.setMaxWidth(300);
        varattuloppuPVMField.setMaxWidth(300);

        buttonit.setPadding(new Insets(10, 0, 0, 0));
        buttonit.setSpacing(15);

        //lisätään elementit pohjaan
        grid.getChildren().clear();
        buttonit.getChildren().clear();
        grid.getChildren().add(varausLabel);
        grid.getChildren().add(varausField);
        grid.getChildren().add(asiakasLabel);
        grid.getChildren().add(asiakasField);
        grid.getChildren().add(mokkiLabel);
        grid.getChildren().add(mokkiField);
        grid.getChildren().add(varattuPVMLabel);
        grid.getChildren().add(varattuPVMField);
        grid.getChildren().add(vahvistusPVMLabel);
        grid.getChildren().add(vahvistusPVMField);
        grid.getChildren().add(varattualkuPVMLabel);
        grid.getChildren().add(varattualkuPVMField);
        grid.getChildren().add(varattuloppuPVMLabel);
        grid.getChildren().add(varattuloppuPVMField);
        buttonit.getChildren().addAll(vahvistaButton, peruutaButton);
        grid.getChildren().add(buttonit);

        //Lisätään sisältö pop uppeihin ja määritellään buttonien toiminnallisuus
        ok.getContent().clear();
        virhe.getContent().clear();
        ok.getContent().add(okButton);
        virhe.getContent().add(virheButton);

        okButton.setOnAction(actionEvent -> stage.close());
        virheButton.setOnAction(actionEvent -> virhe.hide());
        peruutaButton.setOnAction(e -> stage.close());


        //Alue -listan haku ja teko
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM varaus");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Retrieve the values of each column in the current row
                int id = rs.getInt("varaus_id");
                int name = rs.getInt("asiakas_id");

                // Add the object to the list

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



    public int getVaraus_id() {
        return varaus_id;
    }

    public void setVaraus_id(int varaus_id) {
        this.varaus_id = varaus_id;
    }

    public int getAsiakas_id() {
        return asiakas_id;
    }

    public void setAsiakas_id(int asiakas_id) {
        this.asiakas_id = asiakas_id;
    }

    public int getMokki_mokki_id() {
        return mokki_mokki_id;
    }

    public void setMokki_mokki_id(int mokki_mokki_id) {
        this.mokki_mokki_id = mokki_mokki_id;
    }

    public Date getVarattu_pvm() {
        return varattu_pvm;
    }

    public void setvarattu_pvm(Date varattu_pvm) {
        this.varattu_pvm = varattu_pvm;
    }

    public Date getVahvistus_pvm() {
        return vahvistus_pvm;
    }

    public void setVahvistus_pvm(Date vahvistus_pvm) {
        this.vahvistus_pvm = vahvistus_pvm;
    }

    public Date getVarattu_alkupvm() {
        return varattu_alkupvm;
    }

    public void setVarattu_alkupvm(Date varattu_alkupvm) {
        this.varattu_alkupvm=varattu_alkupvm;
    }
    public Date getVarattu_loppupvm(){
        return varattu_loppupvm;
    }
    public void setVarattu_loppupvm(Date varattu_loppupvm){
        this.varattu_loppupvm=varattu_loppupvm;
    }
}