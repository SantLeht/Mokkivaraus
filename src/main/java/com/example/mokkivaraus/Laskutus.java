package com.example.mokkivaraus;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Laskutus {
    int lasku_id;
    String etunimi;
    String sukunimi;
    int varaus_id;
    double summa;
    double alv;
    int muistutettu;
    int maksettu;
    LocalDate erapaiva;
    LocalDate maksupaiva;

    public static void merkkaaMuistutus(Laskutus lasku) throws SQLException {
        System.out.println("merkkaaMuistutus invoke");
        String sql = "UPDATE lasku SET muistutettu = 1 WHERE (lasku_id = ?);";
        PreparedStatement stmt = VN.conn.prepareStatement(sql);
        stmt.setInt(1, lasku.getLasku_id());
        stmt.executeUpdate();
        stmt.close();
    }

    public static void merkkaaMaksu(Laskutus lasku) throws SQLException {
        System.out.println("merkkaaMaksu invoke");
        String sql = "UPDATE lasku SET maksettu = 1, maksupaiva = ? WHERE (lasku_id = ?);";
        PreparedStatement stmt = VN.conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(LocalDate.now()));
        stmt.setInt(2, lasku.getLasku_id());
        stmt.executeUpdate();
        stmt.close();
    }

    public static void poistaLasku(Laskutus id_luku) throws SQLException {
        int id_lukuInt = id_luku.getLasku_id();
        try {
            String sql = "DELETE FROM lasku WHERE lasku_id = (?);";
            PreparedStatement stmt = VN.conn.prepareStatement(sql);
            stmt.setInt(1, id_lukuInt);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void luoLasku() throws SQLException {
        String sql =
                """
                        SELECT lasku.lasku_id, varaus.varaus_id\s
                        FROM varaus
                        LEFT OUTER
                        JOIN lasku ON lasku.varaus_id = varaus.varaus_id
                        WHERE lasku.varaus_id IS NULL;""";
        Statement stmt = VN.conn.createStatement();
        ResultSet tulos = stmt.executeQuery(sql);

        if (!tulos.next()) {
            tulos.close();
            stmt.close();
            Alert eiLaskuja = new Alert(Alert.AlertType.INFORMATION);
            eiLaskuja.setTitle("Huomio");
            eiLaskuja.setHeaderText("Ei lisättäviä laskuja");
            eiLaskuja.setContentText("Yhdenkään varauksen eräpäivä ei ole mennyt.");
            eiLaskuja.showAndWait();
        } else {
            ArrayList<Integer> listVarausId = new ArrayList<>();
            do {
                int varausId = tulos.getInt("varaus_id");
                listVarausId.add(varausId);
            } while (tulos.next());
            tulos.close();
            stmt.close();
            luoPohja(listVarausId);
        }
    }

    public static void luoPohja(ArrayList<Integer> listVarausId) {

        Stage stage = new Stage();
        VBox luontiPohja = new VBox();
        HBox valintaPohja = new HBox();
        HBox buttonPohja = new HBox();
        GridPane laskuPohja = new GridPane();
        Button btnLuoLasku = new Button("Lisää Lasku");
        Button btnPeruuta = new Button("Peruuta");
        luontiPohja.getChildren().addAll(valintaPohja, laskuPohja, buttonPohja);

        luontiPohja.setPadding(new Insets(20));
        luontiPohja.setStyle("-fx-background-color: #FBF4D9;");
        valintaPohja.setSpacing(15);
        laskuPohja.setPadding(new Insets(30,0,20,0));
        laskuPohja.setHgap(10);
        laskuPohja.setVgap(15);
        buttonPohja.setSpacing(15);

        Label tekstiVaraus = new Label("Luo lasku varaukselle:");
        Label tekstiAsiakas = new Label("Asiakas:");
        Label tekstiMokki = new Label("Varattu mökki:");
        Label tekstiPalvelu = new Label("Lisäpalvelut:");
        Label tekstiSumma = new Label("Summa:");
        Label tekstiErapaiva = new Label("Eräpäivä:");

        Label sqlAsiakas = new Label();
        Label sqlMokki = new Label();
        Label sqlPalveluLista = new Label();
        Label sqlSumma = new Label();
        Label sqlErapaiva = new Label();
        Label sqlMokkiHinta = new Label();
        Label sqlPalveluHinta = new Label();
        Label sqlMokkipaivat = new Label();
        Label sqlPalveluMaara = new Label();
        sqlPalveluLista.setPrefWidth(150);
        sqlPalveluHinta.setPrefWidth(50);
        sqlPalveluMaara.setPrefWidth(50);

        ChoiceBox<Integer> valintaBox = new ChoiceBox<>();
        valintaBox.getItems().addAll(listVarausId);
        valintaPohja.getChildren().addAll(tekstiVaraus, valintaBox);

        btnPeruuta.setOnMouseClicked(e -> stage.close());

        valintaBox.setOnAction(e -> {
                    laskuPohja.getChildren().clear();
                    laskuPohja.add(tekstiAsiakas, 0, 0);
                    laskuPohja.add(tekstiMokki, 0, 1);
                    laskuPohja.add(tekstiPalvelu, 0, 2);
                    laskuPohja.add(tekstiSumma, 0, 3);
                    laskuPohja.add(tekstiErapaiva, 0, 4);

                    laskuPohja.add(sqlAsiakas,1,0);
                    laskuPohja.add(sqlMokki,1,1);
                    laskuPohja.add(sqlPalveluLista, 1, 2);
                    laskuPohja.add(sqlSumma,1,3);
                    laskuPohja.add(sqlErapaiva,1,4);

                    laskuPohja.add(sqlMokkiHinta, 2,1);
                    laskuPohja.add(sqlPalveluHinta, 2, 2);
                    laskuPohja.add(sqlMokkipaivat, 3, 1);
                    laskuPohja.add(sqlPalveluMaara, 3,2);

                    buttonPohja.getChildren().clear();
                    buttonPohja.getChildren().addAll(btnLuoLasku, btnPeruuta);

                    try {
                        int indeksi = valintaBox.getValue();
                        String query = "SELECT varaus.varaus_id, varaus.asiakas_id, asiakas.etunimi, asiakas.sukunimi, \n" +
                                "varaus.mokki_mokki_id, mokki.mokkinimi, mokki.hinta,\n" +
                                "varaus.varattu_alkupvm, varaus.varattu_loppupvm, \n" +
                                "varauksen_palvelut.palvelu_id, varauksen_palvelut.lkm, palvelu.nimi, palvelu.palvelu_hinta\n" +
                                "FROM varaus\n" +
                                "JOIN asiakas ON varaus.asiakas_id = asiakas.asiakas_id\n" +
                                "JOIN mokki ON varaus.mokki_mokki_id = mokki.mokki_id\n" +
                                "LEFT OUTER\n" +
                                "JOIN varauksen_palvelut ON varaus.varaus_id = varauksen_palvelut.varaus_id\n" +
                                "LEFT OUTER\n" +
                                "JOIN palvelu ON varauksen_palvelut.palvelu_id = palvelu.palvelu_id\n" +
                                "WHERE varaus.varaus_id="+indeksi+";";
                        PreparedStatement stmt = VN.conn.prepareStatement(query);
                        ResultSet tulos = stmt.executeQuery(query);
                        tulos.next();
                        LocalDate alkupvm = tulos.getDate("varattu_alkupvm").toLocalDate();
                        LocalDate loppupvm = tulos.getDate("varattu_loppupvm").toLocalDate();
                        int paivat = (int) ChronoUnit.DAYS.between(alkupvm, loppupvm);
                        int mokkiHinta = tulos.getInt("hinta");
                        int varausId = tulos.getInt("varaus_id");
                        double summa = 0;

                        sqlAsiakas.setText(tulos.getString("etunimi") + " " + tulos.getString("sukunimi"));
                        sqlMokki.setText(tulos.getString("mokkinimi"));
                        sqlMokkiHinta.setText(mokkiHinta + " € /pv");
                        sqlMokkipaivat.setText("Päivät: " + paivat + "  (" + alkupvm + " - " + loppupvm + ")");
                        sqlErapaiva.setText(String.valueOf(LocalDate.now().plusDays(14)));
                        sqlPalveluLista.setText("");
                        sqlPalveluHinta.setText("");
                        sqlPalveluMaara.setText("");

                        if (tulos.getBoolean("palvelu_id")) {
                            ArrayList<Integer> listPalveluId = new ArrayList<>();
                            ArrayList<String> listPalveluNimi = new ArrayList<>();
                            ArrayList<Integer> listPalveluMaara = new ArrayList<>();
                            ArrayList<Double> listPalveluHinta = new ArrayList<>();
                            TextArea valiId = new TextArea();
                            TextArea valiMaara = new TextArea();
                            TextArea valiHinta = new TextArea();
                            do {
                                int palveluId = tulos.getInt("palvelu_id");
                                listPalveluId.add(palveluId);
                                String palveluNimi = tulos.getString("nimi");
                                listPalveluNimi.add(palveluNimi);
                                int palveluMaara = tulos.getInt("lkm");
                                listPalveluMaara.add(palveluMaara);
                                double palveluHinta = tulos.getDouble("palvelu_hinta");
                                listPalveluHinta.add(palveluHinta);
                            } while (tulos.next());
                            for (int i= 0; i < listPalveluId.size(); i++){
                                String data = String.format("%s: %s", listPalveluId.get(i), listPalveluNimi.get(i));
                                valiId.appendText(data + "\n");
                                valiMaara.appendText("x " + listPalveluMaara.get(i) + "\n");
                                valiHinta.appendText(listPalveluHinta.get(i) + "€" + "\n");
                                summa = (summa + (listPalveluMaara.get(i) * listPalveluHinta.get(i)));
                            }

                            sqlPalveluLista.setText(valiId.getText());
                            sqlPalveluHinta.setText(valiHinta.getText());
                            sqlPalveluMaara.setText(valiMaara.getText());
                            summa = (summa + mokkiHinta * paivat);
                            sqlSumma.setText(summa + "€");

                        } else {
                            sqlPalveluLista.setText("Ei lisäpalveluita");
                            summa = mokkiHinta * paivat;
                            sqlSumma.setText(summa + "€");
                        }

                        stmt.close();
                        double finalSumma = summa;
                        btnLuoLasku.setOnMouseClicked(event -> {
                                    try {
                                        String sql = "INSERT INTO lasku (varaus_id, summa, alv, erapaiva) VALUES (?,?,?,?)";
                                        PreparedStatement stamt = VN.conn.prepareStatement(sql);
                                        stamt.setInt(1, varausId);
                                        stamt.setDouble(2, finalSumma);
                                        stamt.setDouble(3, 24);
                                        stamt.setDate(4, Date.valueOf(LocalDate.now().plusDays(14)));
                                        stamt.executeUpdate();
                                        stamt.close();
                                        stage.close();
                                    } catch (SQLException ev) {
                                        ev.printStackTrace();
                                    }
                                }
                        );
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
        );

        //luodaan scene & stage
        Scene scene = new Scene(luontiPohja, 550, 500);
        stage.getIcons().add(new Image("/VN-logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    public LocalDate getMaksupaiva() {
        return maksupaiva;
    }

    public void setMaksupaiva(LocalDate maksupaiva) {
        this.maksupaiva = maksupaiva;
    }

    public LocalDate getErapaiva() {
        return erapaiva;
    }

    public void setErapaiva(LocalDate erapaiva) {
        this.erapaiva = erapaiva;
    }

    public String getErapaivaString() {
        return erapaiva.toString();
    }

    public int getLasku_id() {
        return lasku_id;
    }

    public void setLasku_id(int lasku_id) {
        this.lasku_id = lasku_id;
    }

    public String getEtunimi() {
        return etunimi;
    }

    public void setEtunimi(String etunimi) {
        this.etunimi = etunimi;
    }

    public String getSukunimi() {
        return sukunimi;
    }

    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }

    public int getVaraus_id() {
        return varaus_id;
    }

    public void setVaraus_id(int varaus_id) {
        this.varaus_id = varaus_id;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }

    public double getAlv() {
        return alv;
    }

    public void setAlv(double alv) {
        this.alv = alv;
    }

    public int getMuistutettu() {
        return muistutettu;
    }

    public void setMuistutettu(int muistutettu) {
        this.muistutettu = muistutettu;
    }

    public int getMaksettu() {
        return maksettu;
    }

    public void setMaksettu(int maksettu) {
        this.maksettu = maksettu;
    }
}