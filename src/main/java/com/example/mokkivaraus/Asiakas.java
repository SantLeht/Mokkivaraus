package com.example.mokkivaraus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;


import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Asiakas {
    private int asiakas_id;
    private String postinro;
    private String etunimi;
    private String sukunimi;

    private String lahiosoite;

    private String email;

    private String puhelinnro;


    private static final VBox grid = new VBox();
    private static final Stage stage = new Stage();
    private static final Popup virhe = new Popup();
    private static final Button vahvistaButton = new Button();



    public Asiakas(){
    }



    public static void lisaaAsiakas() throws Exception {
        Stage asiakasStage = new Stage();
        VBox vboksi = new VBox();
        vboksi.setPadding(new Insets(10, 10, 10, 10));
        vboksi.setStyle("-fx-background-color: #FBF4D9;");

        Label postinroLabel = new Label("Postinumero: ");
        TextField postinroTF = new TextField();
        Label etunimiLabel = new Label("Asiakkaan etunimi:");
        TextField etunimiTF = new TextField();
        Label sukunimiLabel = new Label("Asiakkaan sukunimi:");
        TextField sukunimiTF = new TextField();
        Label lahiosoiteLabel = new Label("Lähiosoite: ");
        TextField lahiosoiteTF = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailTF = new TextField();
        Label puhelinnumeroLabel = new Label("Puhelinnumero:");
        TextField puhelinnumeroTF = new TextField();


        vboksi.getChildren().add(postinroLabel);
        vboksi.getChildren().add(postinroTF);
        vboksi.getChildren().add(etunimiLabel);
        vboksi.getChildren().add(etunimiTF);
        vboksi.getChildren().add(sukunimiLabel);
        vboksi.getChildren().add(sukunimiTF);
        vboksi.getChildren().add(lahiosoiteLabel);
        vboksi.getChildren().add(lahiosoiteTF);
        vboksi.getChildren().add(emailLabel);
        vboksi.getChildren().add(emailTF);
        vboksi.getChildren().add(puhelinnumeroLabel);
        vboksi.getChildren().add(puhelinnumeroTF);


        Button lisaysBtn = new Button("Lisää");
        Button peruutaBtn = new Button("Peruuta");
        Button okButton = new Button("Lisäys ok");


        vboksi.getChildren().add(lisaysBtn);
        vboksi.getChildren().add(peruutaBtn);
        vboksi.getChildren().add(okButton);

        Scene asiakasScene = new Scene(vboksi, 720, 600);
        asiakasStage.setTitle("Village Newbies hallintajärjestelmä");
        asiakasStage.getIcons().add(new Image("/VN-logo.png"));
        asiakasStage.setScene(asiakasScene);
        asiakasStage.show();


        Popup ok = new Popup();
        ok.getContent().add(okButton);
        okButton.setOnAction(actionEvent -> asiakasStage.close());



        lisaysBtn.setOnAction(event -> {

            String postinumero = postinroTF.getText();
            String etunimi = etunimiTF.getText();
            String sukunimi = sukunimiTF.getText();
            String lahiosoite = lahiosoiteTF.getText();
            String email = emailTF.getText();
            String puhelinnro = puhelinnumeroTF.getText();


            try {
                String sql = "INSERT INTO asiakas (postinro, etunimi, sukunimi, lahiosoite, email, puhelinnro) VALUES (?,?,?,?,?,?)";
                PreparedStatement stmt = VN.conn.prepareStatement(sql);
                stmt.setString(1, postinumero);
                stmt.setString(2, etunimi);
                stmt.setString(3, sukunimi);
                stmt.setString(4, lahiosoite);
                stmt.setString(5, email);
                stmt.setString(6, puhelinnro);
                stmt.executeUpdate();
                ok.show(asiakasStage);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        peruutaBtn.setOnAction(e -> asiakasStage.close());
    }

    public static void poistaAsiakas(Asiakas id_luku) throws SQLException {

        int id_lukuString = id_luku.getAsiakas_id();
        Label teksti= new Label(String.valueOf(id_lukuString));
        try {
            String sql = "DELETE FROM asiakas WHERE asiakas_id = (?)";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuString);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }
    public static void muokkaaAsiakas(Asiakas asiakas){

        Stage asiakasStage = new Stage();
        VBox vboksi = new VBox();
        vboksi.setPadding(new Insets(10, 10, 10, 10));
        vboksi.setStyle("-fx-background-color: #FBF4D9;");

        Label postinroLabel = new Label("Postinumero: ");
        TextField postinroTF = new TextField();
        Label etunimiLabel = new Label("Asiakkaan etunimi:");
        TextField etunimiTF = new TextField();
        Label sukunimiLabel = new Label("Asiakkaan sukunimi:");
        TextField sukunimiTF = new TextField();
        Label lahiosoiteLabel = new Label("Lähiosoite: ");
        TextField lahiosoiteTF = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailTF = new TextField();
        Label puhelinnumeroLabel = new Label("Puhelinnumero:");
        TextField puhelinnroTF = new TextField();

        vboksi.getChildren().add(postinroLabel);
        vboksi.getChildren().add(postinroTF);
        vboksi.getChildren().add(etunimiLabel);
        vboksi.getChildren().add(etunimiTF);
        vboksi.getChildren().add(sukunimiLabel);
        vboksi.getChildren().add(sukunimiTF);
        vboksi.getChildren().add(lahiosoiteLabel);
        vboksi.getChildren().add(lahiosoiteTF);
        vboksi.getChildren().add(emailLabel);
        vboksi.getChildren().add(emailTF);
        vboksi.getChildren().add(puhelinnumeroLabel);
        vboksi.getChildren().add(puhelinnroTF);

        Button muokkaaBtn = new Button("Muokkaa");
        Button peruutaBtn = new Button("Peruuta");
        Button okButton = new Button("Muokkaus ok");


        vboksi.getChildren().add(muokkaaBtn);
        vboksi.getChildren().add(peruutaBtn);
        vboksi.getChildren().add(okButton);

        Scene asiakasScene = new Scene(vboksi, 720, 600);
        asiakasStage.setTitle("Village Newbies - muokkaa asiakastietoja");
        asiakasStage.getIcons().add(new Image("/VN-logo.png"));
        asiakasStage.setScene(asiakasScene);
        asiakasStage.show();


        muokkaaBtn.setText("Muokkaa");
        stage.setTitle("Village Newbies hallintajärjestelmä - muokkaa palvelua");

        Popup ok = new Popup();
        ok.getContent().add(okButton);
        okButton.setOnAction(actionEvent -> asiakasStage.close());

        postinroTF.setText(String.valueOf(asiakas.getPostinro()));
        etunimiTF.setText(asiakas.getEtunimi());
        sukunimiTF.setText(asiakas.getSukunimi());
        lahiosoiteTF.setText(asiakas.getLahiosoite());
        emailTF.setText(asiakas.getEmail());
        puhelinnroTF.setText(String.valueOf(asiakas.getPuhelinnro()));


        muokkaaBtn.setOnAction(event -> {
            if (
                    postinroTF.getText().isEmpty() ||
                            etunimiTF.getText().isEmpty()||
                            sukunimiTF.getText().isEmpty() ||
                            lahiosoiteTF.getText().isEmpty() ||
                            emailTF.getText().isEmpty()||
                            puhelinnroTF.getText().isEmpty()
            ) {
                virhe.show(stage);

            } else {
                int AsiakasID = asiakas.getAsiakas_id();
                String postinro = postinroTF.getText();
                String etunimi = etunimiTF.getText();
                String sukunimi = sukunimiTF.getText();
                String lahiosoite = lahiosoiteTF.getText();
                String email = emailTF.getText();
                String puhelinnro = puhelinnroTF.getText();

                try {
                    String sql = "UPDATE asiakas SET postinro=?, etunimi=?, sukunimi=?, lahiosoite=?, email=?, puhelinnro=? WHERE asiakas_id=?";
                    PreparedStatement stmt = VN.conn.prepareStatement(sql);

                    stmt.setString(1, postinro);
                    stmt.setString(2, etunimi);
                    stmt.setString(3, sukunimi);
                    stmt.setString(4, lahiosoite);
                    stmt.setString(5, email);
                    stmt.setString(6, puhelinnro);
                    stmt.setInt(7,AsiakasID);

                    ok.show(asiakasStage);

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        peruutaBtn.setOnAction(e -> asiakasStage.close());
    }



    public int getAsiakas_id(){
        return asiakas_id;
    }
    public void setAsiakas_id(int asiakas_id){
        this.asiakas_id = asiakas_id;
    }

    public String getPostinro(){
        return postinro;
    }
    public void setPostinro(String postinro){
        this.postinro = postinro;
    }
    public String getEtunimi(){
        return etunimi;
    }
    public void setEtunimi(String etunimi){
        this.etunimi=etunimi;
    }
    public String getSukunimi(){
        return sukunimi;
    }
    public void setSukunimi(String sukunimi){
        this.sukunimi=sukunimi;
    }
    public String getLahiosoite(){
        return lahiosoite;
    }
    public void setLahiosoite(String lahiosoite){
        this.lahiosoite=lahiosoite;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPuhelinnro(){
        return puhelinnro;
    }
    public void setPuhelinnro(String puhelinnro){
        this.puhelinnro = puhelinnro;
    }




}