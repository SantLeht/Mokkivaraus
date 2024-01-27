package com.example.mokkivaraus;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class VN extends Application {

    private final BorderPane alku = new BorderPane();
    private final Pane naytto = new Pane();
    private final VBox valikko = new VBox();
    private final VBox napit = new VBox();


    private final Button aloitusBtn = new Button("Aloitus");
    private final Button varausBtn = new Button("Varaukset");
    private final Button laskuBtn = new Button("Laskutus");
    private final Button asiakasBtn = new Button("Asiakkaat");
    private final Button alueBtn = new Button("Alueet");
    private final Button mokkiBtn = new Button("Mökit");
    private final Button palveluBtn = new Button("Palvelu");
    Button[] buttons = new Button[7];

    public static Connection conn;
    private String query;
    private Statement stmt;
    private ResultSet tulos;

    @Override
    public void start(Stage stage) {

        ImageView vnlogo = new ImageView(new Image("/img.png", 230, 230, true, false));
        vnlogo.preserveRatioProperty().set(true);

        napit.getChildren().addAll(aloitusBtn, varausBtn, laskuBtn, asiakasBtn, alueBtn, mokkiBtn, palveluBtn);
        napit.setSpacing(5);
        napit.setPadding(new Insets(0, 0, 0, 50));

        valikko.setStyle("-fx-background-color: #608643;");
        valikko.getChildren().addAll(vnlogo, napit);
        valikko.setSpacing(30);
        valikko.setPadding(new Insets(30, 0, 0, 0));
        valikko.setAlignment(Pos.TOP_CENTER);

        valikko.setMinWidth(250);
        valikko.setPrefWidth(330);
        valikko.setMaxWidth(400);
        valikko.getStylesheets().add(Objects.requireNonNull(VN.class.getResource("tyylittely.css")).toExternalForm());

        naytto.setStyle("-fx-background-color: #FBF4D9;");

        alku.setLeft(valikko);
        alku.setCenter(naytto);

        buttons[0] = aloitusBtn;
        buttons[1] = varausBtn;
        buttons[2] =laskuBtn;
        buttons[3] =asiakasBtn;
        buttons[4] =alueBtn;
        buttons[5] =mokkiBtn;
        buttons[6] =palveluBtn;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://34.88.207.62/vn", "root", "k=2^3Kr92O7AOUj{");
        } catch (SQLException e) {
            System.out.println("Failed to connect to MySQL database: " + e.getMessage());
        }

        aloitusBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoAloitus();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        varausBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoVaraukset();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        laskuBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoLaskutus();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        asiakasBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoAsiakkaat();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        alueBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoAlueet();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            try {
                nayttoAlueet();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        mokkiBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoMokit();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        palveluBtn.setOnMouseClicked(e -> {
            Button clickedButton = (Button) e.getSource();
            alleviivausButton(clickedButton);
            try {
                nayttoPalvelut();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        try {
            nayttoAloitus();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        Scene scene = new Scene(alku, 1280, 720);
        stage.setTitle("Village Newbies hallintajärjestelmä");
        stage.getIcons().add(new Image("/VN-logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    private void alleviivausButton(Button button) {
        for (Button b : buttons) {
            b.setStyle("");
        }
        button.setStyle("-fx-underline: true; -fx-font: 45px \"Stencil\";");
    }

    public void nayttoAloitus() throws SQLException {
        naytto.getChildren().clear();
        VBox kehys = new VBox();
        HBox paneeli = new HBox();
        HBox ylaPaneeli = new HBox();
        VBox vasenPaneeli = new VBox();
        VBox oikeaPaneeli = new VBox();

        Label labelLaskut = new Label("Erääntyneet laskut:");
        Label labelVaraukset = new Label("Vahvistetut varaukset:");

        labelLaskut.setStyle("-fx-font: 14px \"Arial\";-fx-font-weight: bold;");
        labelVaraukset.setStyle("-fx-font: 14px \"Arial\";-fx-font-weight: bold;");

        TableView<Laskutus> listausVaraukset = new TableView<>();
        TableView<Laskutus> listausLaskut = new TableView<>();

        listausLaskut.setPrefWidth(320);
        listausVaraukset.setPrefWidth(300);

        // Varaus-sarakkeet & CellValueFactory
        TableColumn<Laskutus, Integer> clmVarausId = new TableColumn<>("Varaus ID");
        clmVarausId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getVaraus_id()).asObject());
        TableColumn<Laskutus, String> clmAsiakasV = new TableColumn<>("Asiakas");
        clmAsiakasV.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtunimi() + " " + data.getValue().getSukunimi()));
        TableColumn<Laskutus, LocalDate> clmPeruutuspaiva = new TableColumn<>("Peruutuspäivä");
        clmPeruutuspaiva.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getErapaiva()));

        // Lasku-sarakkeet & CellValueFactory
        TableColumn<Laskutus, Integer> clmLaskuId = new TableColumn<>("Lasku ID");
        clmLaskuId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLasku_id()).asObject());
        TableColumn<Laskutus, String> clmAsiakasL = new TableColumn<>("Asiakas");
        clmAsiakasL.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtunimi() + " " + data.getValue().getSukunimi()));
        TableColumn<Laskutus, Double> clmSumma = new TableColumn<>("Summa (€)");
        clmSumma.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSumma()).asObject());
        TableColumn<Laskutus, LocalDate> clmErapaiva = new TableColumn<>("Eräpäivä");
        clmErapaiva.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getErapaiva()));

        //Laskut Tableview
        ObservableList<Laskutus> dataLaskut = FXCollections.observableArrayList();
        query = """
                SELECT lasku.lasku_id, asiakas.etunimi, asiakas.sukunimi, lasku.summa, lasku.erapaiva, lasku.muistutettu
                FROM lasku\s
                JOIN varaus ON lasku.varaus_id = varaus.varaus_id
                JOIN asiakas ON varaus.asiakas_id = asiakas.asiakas_id
                WHERE lasku.maksettu=0 AND lasku.erapaiva <= curdate();""";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);

        while (tulos.next()) {
            Laskutus lasku = new Laskutus();
            lasku.setLasku_id(tulos.getInt("lasku_id"));
            lasku.setEtunimi(tulos.getString("etunimi"));
            lasku.setSukunimi(tulos.getString("sukunimi"));
            lasku.setSumma(tulos.getDouble("summa"));
            lasku.setMuistutettu(tulos.getInt("muistutettu"));
            lasku.setErapaiva(tulos.getDate("erapaiva").toLocalDate());
            dataLaskut.add(lasku);
        }
        listausLaskut.getColumns().addAll(clmLaskuId, clmAsiakasL, clmSumma, clmErapaiva);
        listausLaskut.setItems(dataLaskut);

        //Varaukset TableView
        ObservableList<Laskutus> dataVaraukset = FXCollections.observableArrayList();
        query = """
                SELECT varaus.varaus_id, asiakas.etunimi, asiakas.sukunimi, varaus.vahvistus_pvm
                FROM varaus
                LEFT OUTER
                JOIN lasku ON varaus.varaus_id = lasku.varaus_id
                JOIN asiakas ON varaus.asiakas_id = asiakas.asiakas_id
                WHERE lasku.lasku_id IS NULL AND varaus.vahvistus_pvm <= curdate();""";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);

        while (tulos.next()) {
            Laskutus lasku = new Laskutus();
            lasku.setVaraus_id(tulos.getInt("varaus_id"));
            lasku.setEtunimi(tulos.getString("etunimi"));
            lasku.setSukunimi(tulos.getString("sukunimi"));
            lasku.setErapaiva(tulos.getDate("vahvistus_pvm").toLocalDate());
            dataVaraukset.add(lasku);
        }
        listausVaraukset.getColumns().addAll(clmVarausId, clmAsiakasV, clmPeruutuspaiva);
        listausVaraukset.setItems(dataVaraukset);

        // asettelua
        vasenPaneeli.getChildren().addAll(labelVaraukset, listausVaraukset);
        oikeaPaneeli.getChildren().addAll(labelLaskut, listausLaskut);
        paneeli.getChildren().addAll(vasenPaneeli, oikeaPaneeli);
        kehys.getChildren().addAll(ylaPaneeli, paneeli);

        vasenPaneeli.setSpacing(5);
        oikeaPaneeli.setSpacing(5);
        paneeli.setSpacing(20);
        kehys.setPadding(new Insets(30,0, 0, 15));
        naytto.getChildren().add(kehys);
    }

    public void nayttoVaraukset() throws SQLException {
        naytto.getChildren().clear();

        BorderPane VarausNaytto = new BorderPane();
        Button btnLisaavaraus = new Button("Lisää uusi varaus");
        Button btnPaivita = new Button("Päivitä");
        Button btnPeruuta = new Button("Peruuta");
        TableView listausVaraus = new TableView();
        VBox kehys = new VBox();
        HBox buttonit = new HBox();

        //määritellään arvoja muuttujille
        listausVaraus.setEditable(true);
        listausVaraus.prefHeightProperty().bind(kehys.heightProperty());
        listausVaraus.prefWidthProperty().bind(kehys.widthProperty());
        kehys.getChildren().add(listausVaraus);
        kehys.setMinHeight(500);
        kehys.setPrefHeight(Region.USE_COMPUTED_SIZE);
        kehys.setMaxHeight(2000);
        kehys.setMinWidth(931);
        kehys.setPrefWidth(Region.USE_COMPUTED_SIZE);
        kehys.setMaxWidth(2000);
        VBox.setVgrow(listausVaraus, Priority.ALWAYS);

        //luodaan sarakkeet ja CellValueFactoryt tableviewiin
        TableColumn clmVarausId = new TableColumn<>("Varaus ID");
        clmVarausId.setCellValueFactory(new PropertyValueFactory<Varaus, Integer>("varaus_id"));
        TableColumn clmAsiakasId = new TableColumn<>("Asiakas ID");
        clmAsiakasId.setCellValueFactory(new PropertyValueFactory<Varaus, Integer>("asiakas_id"));
        TableColumn clmMokkiId = new TableColumn<>("Mökki ID");
        clmMokkiId.setCellValueFactory(new PropertyValueFactory<Varaus, Integer>("mokki_mokki_id"));
        TableColumn clmVarattuPVM = new TableColumn<>("Varaus päivämäärä");
        clmVarattuPVM.setCellValueFactory(new PropertyValueFactory<Varaus, String>("varattu_pvm"));
        TableColumn clmVahvistusPVM = new TableColumn<>("Vahvistus päivämäärä");
        clmVahvistusPVM.setCellValueFactory(new PropertyValueFactory<Varaus, String>("vahvistus_pvm"));
        TableColumn clmVarattualkuPVM = new TableColumn<>("Varauksen alkamispäiväämärä");
        clmVarattualkuPVM.setCellValueFactory(new PropertyValueFactory<Varaus, String>("varattu_alkupvm"));
        TableColumn clmVarattuloppuPVM = new TableColumn<>("Varauksen loppumispäivämäärä");
        clmVarattuloppuPVM.setCellValueFactory(new PropertyValueFactory<Varaus, String>("varattu_loppupvm"));

        TableColumn<Varaus, Void> clmMuokkaus = new TableColumn<>("Muokkaa");
        TableColumn<Varaus, Void> clmPoisto = new TableColumn<>("Poista");


        Callback<TableColumn<Varaus, Void>, TableCell<Varaus, Void>> cellFactoryVaraus = new Callback<>() {

            public TableCell<Varaus, Void> call(final TableColumn<Varaus, Void> param) {
                return new TableCell<>() {

                    private final Button button = new Button("Muokkaa");

                    {
                        button.setOnAction(event -> {
                            Varaus id_luku = getTableRow().getItem();
                            Varaus.muokkaaVaraus(id_luku);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        clmMuokkaus.setCellFactory(cellFactoryVaraus);

        Callback<TableColumn<Varaus, Void>, TableCell<Varaus, Void>> cellFactoryVarauspoisto = new Callback<>() {
            @Override
            public TableCell<Varaus, Void> call(final TableColumn<Varaus, Void> param) {
                return new TableCell<>() {

                    private final Button button = new Button("Poista");

                    {
                        button.setOnAction(event -> {

                            listausVaraus.getSelectionModel().getSelectedItem();
                            Varaus id_luku = getTableRow().getItem();
                            VBox poistoVahvistusPane = new VBox();
                            HBox poistoVahvistusBtn = new HBox();
                            poistoVahvistusPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            poistoVahvistusPane.setStyle("-fx-padding: 10;" +
                                    "-fx-border-style: solid inside;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-insets: 5;" +
                                    "-fx-border-radius: 5;" +
                                    "-fx-border-color: black;");
                            Popup varmistus = new Popup();
                            Button btnVahvista = new Button("Vahvista poisto");
                            Label vahvistusTeksti = new Label("Poistetaanko varaus " + id_luku.getVaraus_id() + "?");
                            poistoVahvistusBtn.getChildren().addAll(btnVahvista, btnPeruuta);
                            poistoVahvistusPane.getChildren().addAll(vahvistusTeksti, poistoVahvistusBtn);
                            varmistus.getContent().add(poistoVahvistusPane);
                            varmistus.show(getScene().getWindow());
                            btnPeruuta.setOnAction(e -> varmistus.hide());
                            btnVahvista.setOnAction(tapahtuu -> {
                                try {
                                    Varaus.poistaVaraus(id_luku);
                                    varmistus.hide();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        clmPoisto.setCellFactory(cellFactoryVarauspoisto);

        //määritellään sarakkeiden leveydet
        clmVarausId.setPrefWidth(75);
        clmAsiakasId.setPrefWidth(75);
        clmMokkiId.setPrefWidth(75);
        clmVarattuPVM.setPrefWidth(115);
        clmVahvistusPVM.setPrefWidth(130);
        clmVarattualkuPVM.setPrefWidth(165);
        clmVarattuloppuPVM.setPrefWidth(165);
        clmMuokkaus.setPrefWidth(75);
        clmPoisto.setPrefWidth(60);

        //Lisää palvelu -buttonin funktio
        btnLisaavaraus.setOnMouseClicked(e -> Varaus.lisaaVaraus());

        btnPaivita.setOnMouseClicked(e -> {
            try {
                nayttoVaraukset();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });


        ArrayList<Integer> listID = new ArrayList<>();
        ArrayList<String> listNimi = new ArrayList<>();
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM varaus");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("varaus_id");
                listID.add(id);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Tästä eteenpäin määritellään SQL-hakuun liittyvä toiminnallisuus
        ObservableList<Varaus> data = FXCollections.observableArrayList();

        //Luodaan SQL-haku palveluihin
        query = "SELECT varaus_id, asiakas_id, mokki_mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm FROM varaus";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);

        //luodaan uusi Palvelu-olio jokaiselle haetulle tiedolle
        while (tulos.next()) {
            Varaus varaus = new Varaus();
            varaus.setVaraus_id(tulos.getInt("varaus_id"));
            varaus.setAsiakas_id(tulos.getInt("asiakas_id"));
            varaus.setMokki_mokki_id(tulos.getInt("mokki_mokki_id"));
            varaus.setvarattu_pvm(tulos.getDate("varattu_pvm"));
            varaus.setVahvistus_pvm(tulos.getDate("vahvistus_pvm"));
            varaus.setVarattu_alkupvm(tulos.getDate("varattu_alkupvm"));
            varaus.setVarattu_loppupvm(tulos.getDate("varattu_loppupvm"));
            data.add(varaus);
        }
        TextField varaushakuTF = new TextField();
        varaushakuTF.setPromptText("Etsi varaus ID");
        FilteredList<Varaus> filteredData = new FilteredList<>(data, p -> true);

        varaushakuTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                varaushakuTF.clear();
            }
        });


        varaushakuTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(varaus -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                boolean isInt = newValue.matches("-?\\d+");
                int intChecker = isInt ? Integer.parseInt(newValue) : 0;


                if (varaus.getVaraus_id() == intChecker) {
                    return true;
                }
                return false;
            });
        });

        TextField mokkihakuTF = new TextField();
        mokkihakuTF.setPromptText("Etsi mökki ID");

        mokkihakuTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                mokkihakuTF.clear();
            }
        });

        mokkihakuTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(varaus -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                boolean isInt = newValue.matches("-?\\d+");
                int intChecker = isInt ? Integer.parseInt(newValue) : 0;


                if (varaus.getMokki_mokki_id() == intChecker) {
                    return true;
                }
                return false;
            });
        });


        listausVaraus.getColumns().addAll(clmVarausId, clmAsiakasId, clmMokkiId, clmVarattuPVM, clmVahvistusPVM, clmVarattualkuPVM, clmVarattuloppuPVM, clmMuokkaus, clmPoisto);
        listausVaraus.setItems(filteredData);

        buttonit.getChildren().addAll(btnLisaavaraus, btnPaivita, varaushakuTF, mokkihakuTF);
        buttonit.setSpacing(10);
        buttonit.setPadding(new Insets(0, 0, 10, 0));
        VarausNaytto.setTop(buttonit);
        VarausNaytto.setCenter(kehys);
        BorderPane.setMargin(btnLisaavaraus, new Insets(0, 15, 15, 0));
        VarausNaytto.setPadding(new Insets(15));

        BorderPane.setMargin(listausVaraus, new Insets(20, 0, 0, 0));
        naytto.getChildren().addAll(VarausNaytto);
    }

    public void nayttoLaskutus() throws SQLException {
        naytto.getChildren().clear();

        VBox laskutusNaytto = new VBox();
        HBox laskuValikko = new HBox();
        HBox buttonit = new HBox();
        VBox kehys = new VBox();

        Button avoimetLaskut = new Button("Avoimet laskut");
        Button maksetutLaskut = new Button("Maksetut laskut");
        Button btnLisaaLasku = new Button("Luo lasku varaukselle");

        TableView<Laskutus> listausLaskut = new TableView<>();

        //määritellään arvoja muuttujille
        listausLaskut.setEditable(true);
        listausLaskut.prefHeightProperty().bind(kehys.heightProperty());
        listausLaskut.prefWidthProperty().bind(kehys.widthProperty());

        kehys.getChildren().add(listausLaskut);
        kehys.setMinHeight(500);
        kehys.setPrefHeight(Region.USE_COMPUTED_SIZE);
        kehys.setMaxHeight(2000);
        kehys.setMinWidth(931);
        kehys.setPrefWidth(Region.USE_COMPUTED_SIZE);
        kehys.setMaxWidth(2000);
        VBox.setVgrow(listausLaskut, Priority.ALWAYS);

        //luodaan sarakkeet ja CellValueFactoryt tableviewiin
        TableColumn<Laskutus, Integer> clmLaskuId = new TableColumn<>("Lasku ID");
        clmLaskuId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLasku_id()).asObject());
        TableColumn<Laskutus, Integer> clmVarausId = new TableColumn<>("Varaus ID");
        clmVarausId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getVaraus_id()).asObject());
        TableColumn<Laskutus, String> clmAsiakasNimi = new TableColumn<>("Asiakas");
        clmAsiakasNimi.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtunimi() + " " + data.getValue().getSukunimi()));
        TableColumn<Laskutus, Double> clmSumma = new TableColumn<>("Summa (€)");
        clmSumma.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSumma()).asObject());
        TableColumn<Laskutus, Double> clmAlv = new TableColumn<>("Alv%");
        clmAlv.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAlv()).asObject());
        TableColumn<Laskutus, LocalDate> clmErapaiva = new TableColumn<>("Eräpäivä");
        clmErapaiva.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getErapaiva()));
        TableColumn<Laskutus, LocalDate> clmMaksupaiva = new TableColumn<>("Maksupäivä");
        clmMaksupaiva.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaksupaiva()));
        TableColumn<Laskutus, Integer> clmMuistutus = new TableColumn<>("Muistutus");
        clmMuistutus.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMuistutettu()).asObject());
        TableColumn<Laskutus, Integer> clmMaksu = new TableColumn<>("Maksettu");
        clmMaksu.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMaksettu()).asObject());
        TableColumn<Laskutus, Void> clmPoisto = new TableColumn<>("Poista lasku");

        clmErapaiva.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(date.toString());
                    if (date.isBefore(LocalDate.now())) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.BLACK);
                    }
                }
            }
        });

        clmMuistutus.setCellFactory(column -> new TableCell<>() {
            private final Button btnMuistuta = new Button("Merkitse muistutetuksi");
            private final Label muistutettuLabel = new Label("Muistutus lähetetty.");
            {
                btnMuistuta.setOnMouseClicked(event -> {
                    Laskutus lasku = getTableView().getItems().get(getIndex());
                    try {
                        Laskutus.merkkaaMuistutus(lasku);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    listausLaskut.refresh();
                });
            }
            @Override
            protected void updateItem(Integer muistutus, boolean empty) {
                super.updateItem(muistutus, empty);
                if (empty || muistutus == null) {
                    setGraphic(null); //Poista cell:n sisältö
                } else {
                    if (muistutus == 0) {
                        setGraphic(btnMuistuta);
                    } else {
                        setGraphic(muistutettuLabel);
                    }
                }
            }
        });

        clmMaksu.setCellFactory(column -> new TableCell<>() {
            private final Button btnMaksettu = new Button("Merkitse maksetuksi");
            {
                btnMaksettu.setOnMouseClicked(event -> {
                    Laskutus lasku = getTableView().getItems().get(getIndex());
                    try {
                        Laskutus.merkkaaMaksu(lasku);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    listausLaskut.refresh();
                });
            }
            @Override
            protected void updateItem(Integer muistutus, boolean empty) {
                super.updateItem(muistutus, empty);
                if (empty || muistutus == null) {
                    setGraphic(null); //Poista cell:n sisältö
                } else {
                    setGraphic(btnMaksettu);
                }
            }
        });

        Callback<TableColumn<Laskutus, Void>, TableCell<Laskutus, Void>> cellFactoryPoista = new Callback<>() {
            @Override
            public TableCell<Laskutus, Void> call(final TableColumn<Laskutus, Void> param) {
                return new TableCell<>() {
                    private final Button button = new Button("Poista");
                    private final Button btnPeruuta = new Button("Peruuta");

                    {
                        button.setOnAction(event -> {
                            listausLaskut.getSelectionModel().getSelectedItem();
                            Laskutus id_luku = getTableRow().getItem();
                            VBox poistoVahvistusPane = new VBox();
                            HBox poistoVahvistusBtn = new HBox();
                            poistoVahvistusPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            poistoVahvistusPane.setPadding(new Insets(10));
                            poistoVahvistusBtn.setPadding(new Insets(10,0,0,0));
                            poistoVahvistusBtn.setSpacing(10);
                            poistoVahvistusPane.setStyle("-fx-border-width: 2; -fx-border-radius: 5; -fx-border-color: black;");
                            Popup varmistus = new Popup();
                            Button btnVahvista = new Button("Vahvista poisto");
                            Label vahvistusTeksti = new Label("Poistetaanko lasku " + id_luku.getLasku_id() + "?");
                            poistoVahvistusBtn.getChildren().addAll(btnVahvista, btnPeruuta);
                            poistoVahvistusPane.getChildren().addAll(vahvistusTeksti, poistoVahvistusBtn);
                            varmistus.getContent().add(poistoVahvistusPane);
                            varmistus.show(getScene().getWindow());
                            btnPeruuta.setOnAction(e -> varmistus.hide());
                            btnVahvista.setOnAction(tapahtuu -> {
                                try {
                                    Laskutus.poistaLasku(id_luku);
                                    varmistus.hide();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        clmPoisto.setCellFactory(cellFactoryPoista);

        //määritellään sarakkeiden leveydet
        clmLaskuId.setPrefWidth(60);
        clmAsiakasNimi.setPrefWidth(120);
        clmVarausId.setPrefWidth(60);
        clmSumma.setPrefWidth(80);
        clmAlv.setPrefWidth(40);
        clmErapaiva.setPrefWidth(100);
        clmMuistutus.setPrefWidth(145);
        clmMaksu.setPrefWidth(130);

        btnLisaaLasku.setOnMouseClicked(e -> {
            try {
                Laskutus.luoLasku();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        avoimetLaskut.setOnMouseClicked(e -> {
            try {
                ObservableList<Laskutus> data = FXCollections.observableArrayList();
                query = """
                        SELECT lasku.lasku_id, asiakas.etunimi, asiakas.sukunimi, varaus.varaus_id, lasku.summa, lasku.alv, lasku.erapaiva, lasku.muistutettu, lasku.maksettu
                        FROM lasku\s
                        JOIN varaus ON lasku.varaus_id = varaus.varaus_id
                        JOIN asiakas ON varaus.asiakas_id = asiakas.asiakas_id
                        WHERE lasku.maksettu=0""";
                stmt = conn.createStatement();
                tulos = stmt.executeQuery(query);

                while (tulos.next()) {
                    Laskutus lasku = new Laskutus();
                    lasku.setLasku_id(tulos.getInt("lasku_id"));
                    lasku.setEtunimi(tulos.getString("etunimi"));
                    lasku.setSukunimi(tulos.getString("sukunimi"));
                    lasku.setVaraus_id(tulos.getInt("varaus_id"));
                    lasku.setSumma(tulos.getDouble("summa"));
                    lasku.setAlv(tulos.getDouble("alv"));
                    lasku.setMuistutettu(tulos.getInt("muistutettu"));
                    lasku.setMaksettu(tulos.getInt("maksettu"));
                    lasku.setErapaiva(tulos.getDate("erapaiva").toLocalDate());
                    data.add(lasku);
                }

                FilteredList<Laskutus> filteredDataAvoin = new FilteredList<>(data, p -> true);
                listausLaskut.getColumns().clear();
                listausLaskut.getColumns().addAll(clmLaskuId, clmAsiakasNimi, clmVarausId, clmSumma, clmAlv, clmErapaiva, clmMuistutus, clmMaksu, clmPoisto);
                listausLaskut.setItems(filteredDataAvoin);

                TextField hakuField = new TextField();
                hakuField.setPromptText("Etsi nimellä...");
                hakuField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataAvoin.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (laskutus.getEtunimi().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (laskutus.getSukunimi().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false; // Ei tuloksia
                    });
                });

                hakuField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        hakuField.clear();
                    }
                });

                TextField hakuLaskuField = new TextField();
                hakuLaskuField.setPromptText("Etsi lasku id:llä...");
                hakuLaskuField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataAvoin.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        int laskuId = hakuLaskuField.getText().isEmpty() ? 0 : Integer.parseInt(hakuLaskuField.getText());
                        return laskutus.getLasku_id() == laskuId;
                    });
                });

                hakuLaskuField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        hakuLaskuField.clear();
                    }
                });

                TextField hakuVarausField = new TextField();
                hakuVarausField.setPromptText("Etsi varaus id:llä...");
                hakuVarausField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataAvoin.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        int varausId = hakuVarausField.getText().isEmpty() ? 0 : Integer.parseInt(hakuVarausField.getText());
                        return laskutus.getVaraus_id() == varausId;
                    });
                });

                hakuVarausField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        hakuVarausField.clear();
                    }
                });

                buttonit.getChildren().clear();
                buttonit.getChildren().addAll(btnLisaaLasku, hakuField, hakuLaskuField, hakuVarausField);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        maksetutLaskut.setOnMouseClicked(e -> {
            try {
                ObservableList<Laskutus> data = FXCollections.observableArrayList();
                query = """
                        SELECT lasku.lasku_id, asiakas.etunimi, asiakas.sukunimi, varaus.varaus_id, lasku.summa, lasku.alv, lasku.maksupaiva
                        FROM lasku\s
                        JOIN varaus ON lasku.varaus_id = varaus.varaus_id
                        JOIN asiakas ON varaus.asiakas_id = asiakas.asiakas_id
                        WHERE lasku.maksettu=1""";
                stmt = conn.createStatement();
                tulos = stmt.executeQuery(query);

                //luodaan uusi Palvelu-olio jokaiselle haetulle tiedolle
                while (tulos.next()) {
                    Laskutus lasku = new Laskutus();
                    lasku.setLasku_id(tulos.getInt("lasku_id"));
                    lasku.setEtunimi(tulos.getString("etunimi"));
                    lasku.setSukunimi(tulos.getString("sukunimi"));
                    lasku.setVaraus_id(tulos.getInt("varaus_id"));
                    lasku.setSumma(tulos.getDouble("summa"));
                    lasku.setAlv(tulos.getDouble("alv"));
                    lasku.setMaksupaiva(tulos.getDate("maksupaiva").toLocalDate());
                    data.add(lasku);
                }
                FilteredList<Laskutus> filteredDataMaksettu = new FilteredList<>(data, p -> true);
                listausLaskut.getColumns().clear();
                listausLaskut.getColumns().addAll(clmLaskuId, clmAsiakasNimi, clmVarausId, clmSumma, clmAlv, clmMaksupaiva, clmPoisto);
                listausLaskut.setItems(filteredDataMaksettu);

                TextField hakuField = new TextField();
                hakuField.setPromptText("Etsi nimellä...");
                TextField hakuLaskuField = new TextField();
                hakuLaskuField.setPromptText("Etsi lasku id:llä...");
                TextField hakuVarausField = new TextField();
                hakuVarausField.setPromptText("Etsi varaus id:llä...");

                hakuField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataMaksettu.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (laskutus.getEtunimi().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (laskutus.getSukunimi().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false; // Ei tuloksia
                    });
                });

                hakuLaskuField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataMaksettu.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        int laskuId = hakuLaskuField.getText().isEmpty() ? 0 : Integer.parseInt(hakuLaskuField.getText());
                        return laskutus.getLasku_id() == laskuId;
                    });
                });

                hakuVarausField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredDataMaksettu.setPredicate(laskutus -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Näytä kaikki rivit, jos haku on tyhjä
                        }
                        int varausId = hakuVarausField.getText().isEmpty() ? 0 : Integer.parseInt(hakuVarausField.getText());
                        return laskutus.getVaraus_id() == varausId;
                    });
                });

                buttonit.getChildren().clear();
                buttonit.getChildren().addAll(btnLisaaLasku, hakuField, hakuLaskuField, hakuVarausField);

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonit.setSpacing(10);
        buttonit.setPadding(new Insets(15, 0, 10, 0));

        laskuValikko.getChildren().addAll(avoimetLaskut, maksetutLaskut);
        laskuValikko.setSpacing(10);
        laskutusNaytto.getChildren().addAll(laskuValikko, buttonit, kehys);
        VBox.setMargin(btnLisaaLasku, new Insets(0, 15, 15, 0));
        laskutusNaytto.setPadding(new Insets(15));

        BorderPane.setMargin(listausLaskut, new Insets(20, 0, 0, 0));
        naytto.getChildren().addAll(laskutusNaytto);
    }

    public void nayttoAsiakkaat() throws SQLException {
        naytto.getChildren().clear();

        Button lisaaBtn = new Button("Lisää uusi asiakas");
        Button paivitaBtn = new Button("Päivitä");
        Button peruutaBtn = new Button("Peruuta");


        BorderPane asiakasNakyma = new BorderPane();
        asiakasNakyma.setTop(lisaaBtn);
        asiakasNakyma.setBottom(paivitaBtn);
        asiakasNakyma.setPadding(new Insets(10, 0, 0, 20));



        TableView listausAsiakkaat = new TableView();

        VBox vboksi = new VBox();
        HBox buttonit = new HBox();

        listausAsiakkaat.setEditable(true);
        listausAsiakkaat.prefHeightProperty().bind(vboksi.heightProperty());
        listausAsiakkaat.prefWidthProperty().bind(vboksi.widthProperty());
        vboksi.getChildren().add(listausAsiakkaat);
        vboksi.setMinHeight(500);
        vboksi.setPrefHeight(Region.USE_COMPUTED_SIZE);
        vboksi.setMaxHeight(2000);
        vboksi.setMinWidth(931);
        vboksi.setPrefWidth(Region.USE_COMPUTED_SIZE);
        vboksi.setMaxWidth(2000);
        vboksi.setVgrow(listausAsiakkaat, Priority.ALWAYS);

        TableColumn asiakasIdTaulu = new TableColumn<>("Asiakas ID");
        asiakasIdTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, Integer>("asiakas_id"));
        TableColumn postinroTaulu = new TableColumn<>("Postinumero");
        postinroTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, Integer>("postinro"));
        TableColumn etunimiTaulu = new TableColumn<>("Etunimi");
        etunimiTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, String>("etunimi"));
        TableColumn sukunimiTaulu = new TableColumn<>("Sukunimi");
        sukunimiTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, String>("sukunimi"));
        TableColumn lahiosoiteTaulu = new TableColumn<>("Lähiosoite");
        lahiosoiteTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, String>("lahiosoite"));
        TableColumn emailTaulu = new TableColumn<>("email");
        emailTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, String>("email"));
        TableColumn puhelinnroTaulu = new TableColumn<>("Puhelinnumero");
        puhelinnroTaulu.setCellValueFactory(new PropertyValueFactory<Asiakas, Integer>("puhelinnro"));
        TableColumn<Asiakas, Void> taulukonMuokkaus = new TableColumn<>("Muokkaa");
        TableColumn<Asiakas, Void> taulukonPoisto = new TableColumn<>("Poista");

        Callback<TableColumn<Asiakas, Void>, TableCell<Asiakas, Void>> cellFactory = new Callback<TableColumn<Asiakas, Void>, TableCell<Asiakas, Void>>() {
            @Override
            public TableCell<Asiakas, Void> call(final TableColumn<Asiakas, Void> param) {
                return new TableCell<Asiakas, Void>() {

                    private final Button muokkaaBtn = new Button("Muokkaa");

                    {
                        muokkaaBtn.setOnAction(event -> {
                            Asiakas id_luku = getTableRow().getItem();
                            Asiakas.muokkaaAsiakas(id_luku);


                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(muokkaaBtn);
                        }
                    }
                };
            }
        };
        taulukonMuokkaus.setCellFactory(cellFactory);

        Callback<TableColumn<Asiakas, Void>, TableCell<Asiakas, Void>> cellFactoryPoista = new Callback<TableColumn<Asiakas, Void>, TableCell<Asiakas, Void>>() {
            @Override
            public TableCell<Asiakas, Void> call(final TableColumn<Asiakas, Void> param) {
                return new TableCell<Asiakas, Void>() {

                    private final Button button = new Button("Poista");

                    {
                        button.setOnAction(event -> {

                            listausAsiakkaat.getSelectionModel().getSelectedItem();
                            Asiakas id_luku1 = getTableRow().getItem();
                            VBox poistoVahvistusPane = new VBox();
                            HBox poistoVahvistusBtn = new HBox();
                            poistoVahvistusPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            poistoVahvistusPane.setStyle("-fx-padding: 10;" +
                                    "-fx-border-style: solid inside;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-insets: 5;" +
                                    "-fx-border-radius: 5;" +
                                    "-fx-border-color: black;");
                            Popup varmistus = new Popup();
                            Button vahvistaBtn = new Button("Vahvista poisto");
                            Label vahvistusTeksti = new Label("Poistetaanko asiakas " + id_luku1.getAsiakas_id() + ". " + id_luku1.getEtunimi() +" "
                                    + id_luku1.getSukunimi()+ "?");
                            poistoVahvistusBtn.getChildren().addAll(vahvistaBtn, peruutaBtn);
                            poistoVahvistusPane.getChildren().addAll(vahvistusTeksti, poistoVahvistusBtn);
                            varmistus.getContent().add(poistoVahvistusPane);
                            varmistus.show(getScene().getWindow());
                            peruutaBtn.setOnAction(e -> varmistus.hide());
                            vahvistaBtn.setOnAction(tapahtuu -> {
                                try {
                                    Asiakas.poistaAsiakas(id_luku1);
                                    varmistus.hide();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        taulukonPoisto.setCellFactory(cellFactoryPoista);




        asiakasIdTaulu.setPrefWidth(70);
        postinroTaulu.setPrefWidth(80);
        etunimiTaulu.setPrefWidth(80);
        sukunimiTaulu.setPrefWidth(90);
        lahiosoiteTaulu.setPrefWidth(120);
        emailTaulu.setPrefWidth(210);
        puhelinnroTaulu.setPrefWidth(80);
        taulukonMuokkaus.setPrefWidth(80);
        taulukonPoisto.setPrefWidth(80);

        lisaaBtn.setOnMouseClicked(e-> {
            try {
                Asiakas.lisaaAsiakas();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        paivitaBtn.setOnAction(e -> {
            try {
                nayttoAsiakkaat();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        ArrayList<Integer> listID = new ArrayList<>();
        ArrayList<String> listNimi = new ArrayList<>();
        try {
            PreparedStatement pstmt = VN.conn.prepareStatement("SELECT * FROM asiakas");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("asiakas_id");
                String name = rs.getString("etunimi");
                listID.add(id);
                listNimi.add(name);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<Asiakas> data =FXCollections.observableArrayList();

        query = "SELECT asiakas_id, postinro, etunimi, sukunimi, lahiosoite, email, puhelinnro FROM asiakas";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);


        data = FXCollections.observableArrayList();

        while (tulos.next()) {
            Asiakas asiakas = new Asiakas();
            asiakas.setAsiakas_id(tulos.getInt("asiakas_id"));
            asiakas.setPostinro(tulos.getString("postinro"));
            asiakas.setEtunimi(tulos.getString("etunimi"));
            asiakas.setSukunimi(tulos.getString("sukunimi"));
            asiakas.setLahiosoite(tulos.getString("lahiosoite"));
            asiakas.setEmail(tulos.getString("email"));
            asiakas.setPuhelinnro(tulos.getString("puhelinnro"));
            data.add(asiakas);
        }
        TextField hakuTF = new TextField();
        hakuTF.setPromptText("Etsi asiakasta");
        FilteredList<Asiakas> filteredData = new FilteredList<>(data, p -> true);


        hakuTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(asiakas -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                boolean isInt = newValue.matches("-?\\d+");

                String lowerCaseFilter = newValue.toLowerCase();
                int intChecker = isInt ? Integer.parseInt(newValue) : 0;
                String stringChecker = newValue.isEmpty() ? "0.0" : newValue;


                if (asiakas.getAsiakas_id() == intChecker) {
                    return true;
                } else if (asiakas.getEtunimi().toLowerCase().startsWith(stringChecker.toLowerCase())) {
                    return true;
                } else if (asiakas.getSukunimi().toLowerCase().startsWith(stringChecker.toLowerCase())) {
                    return true;
                }
                return false;
            });
        });






        listausAsiakkaat.getColumns().addAll(asiakasIdTaulu, postinroTaulu, etunimiTaulu, sukunimiTaulu, lahiosoiteTaulu
                , emailTaulu,puhelinnroTaulu,  taulukonMuokkaus, taulukonPoisto);
        listausAsiakkaat.setItems(filteredData);




        buttonit.getChildren().addAll(lisaaBtn, paivitaBtn, hakuTF);
        buttonit.setSpacing(10);
        buttonit.setPadding(new Insets(0, 0, 10, 0));
        asiakasNakyma.setTop(buttonit);
        asiakasNakyma.setCenter(vboksi);
        asiakasNakyma.setMargin(lisaaBtn, new Insets(0, 15, 15, 0));
        asiakasNakyma.setPadding(new Insets(15));

        BorderPane.setMargin(listausAsiakkaat, new Insets(20, 0, 0, 0));
        naytto.getChildren().addAll(asiakasNakyma);
    }

    public void nayttoAlueet() throws SQLException {
        naytto.getChildren().clear();

        HBox buttons = new HBox();


        BorderPane alueNaytto = new BorderPane();
        Button btnLisaaAlue = new Button("Lisää uusi alue");
        TableView listausAlue = new TableView();
        Button btnPaivita = new Button("Päivitä");
        TextField hakuField = new TextField();
        hakuField.setPromptText("Hae aluetta");


        buttons.getChildren().addAll(btnLisaaAlue, btnPaivita, hakuField);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(0,0,3,0));
        alueNaytto.setTop(buttons);
        listausAlue.setEditable(true);
        alueNaytto.setPadding(new Insets(10, 0, 0, 20));
        naytto.getChildren().addAll(alueNaytto);


        TableColumn clmAlueID = new TableColumn<>("Alue ID");
        clmAlueID.setCellValueFactory(new PropertyValueFactory<Mokki, Integer>("alue_id"));
        TableColumn clmAlueNimi = new TableColumn<>("Alueen nimi");
        clmAlueNimi.setCellValueFactory(new PropertyValueFactory<Mokki, String>("nimi"));
        TableColumn<Alue, Void> clmPoisto = new TableColumn<>("Poista");
        TableColumn<Alue, Void> clmMuokkaus = new TableColumn<>("Muokkaa");



        Callback<TableColumn<Alue, Void>, TableCell<Alue, Void>> cellFactoryPoista = new Callback<>() {
            @Override
            public TableCell<Alue, Void> call(final TableColumn<Alue, Void> param) {
                final TableCell<Alue, Void> cell = new TableCell<>() {

                    //Alueen poisto nappi
                    private final Button button = new Button("Poista");

                    {
                        button.setOnAction(event -> {

                            listausAlue.getSelectionModel().getSelectedItem();
                            TableRow<Alue> row = getTableRow();

                            Alue id_luku = row.getItem();

                            try {
                                Alue.poistaAlue(id_luku);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };

        Callback<TableColumn<Alue, Void>, TableCell<Alue, Void>> cellFactoryMuokkaa = new Callback<TableColumn<Alue, Void>, TableCell<Alue, Void>>() {
            @Override
            public TableCell<Alue, Void> call(final TableColumn<Alue, Void> param) {
                return new TableCell<Alue, Void>() {

                    private final Button button = new Button("Muokkaa");

                    {
                        button.setOnAction(event -> {
                            Alue id_luku = getTableRow().getItem();
                            Alue.muokkaaAlue(id_luku);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };

        clmMuokkaus.setCellFactory(cellFactoryMuokkaa);
        clmPoisto.setCellFactory(cellFactoryPoista);
        clmAlueID.setPrefWidth(100);
        clmAlueNimi.setPrefWidth(100);

        btnPaivita.setOnMouseClicked(e -> {
            try {
                nayttoAlueet();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        query = "SELECT alue_id, nimi FROM alue";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);

        ObservableList<Alue> data;
        data = FXCollections.observableArrayList();

        while (tulos.next()) {
            Alue alue = new Alue();
            alue.setAlue_id(tulos.getInt("alue_id"));
            alue.setNimi(tulos.getString("nimi"));

            data.add(alue);
        }
        FilteredList<Alue> filteredData = new FilteredList<>(data, p -> true);

        listausAlue.getColumns().addAll(clmAlueID, clmAlueNimi, clmPoisto, clmMuokkaus);

        listausAlue.setItems(filteredData);




        hakuField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(alue -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all rows when search is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Match against all columns of the current row
                if (alue.getNimi().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID
                }

                return false; // No match found
            });
        });

        alueNaytto.setCenter(listausAlue);
        //Alueen lisäys nappi
        btnLisaaAlue.setOnAction(e -> {
            try {
                Alue.lisaaAlue();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void nayttoMokit() throws SQLException {
        naytto.getChildren().clear();

        HBox buttons = new HBox();
        BorderPane mokkiNaytto = new BorderPane();
        Button btnLisaaMokki = new Button("Lisää uusi mökki");
        Button btnPaivita = new Button("Päivitä");
        TextField hakuField = new TextField();
        hakuField.setPromptText("Hae nimen mukaan");
        TextField hakuField2 = new TextField();
        hakuField2.setPromptText("Hae alueen mukaan");

        buttons.getChildren().addAll(btnLisaaMokki, btnPaivita, hakuField, hakuField2);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(0,0,3,0));
        mokkiNaytto.setTop(buttons);
        mokkiNaytto.setPadding(new Insets(10, 0, 0, 20));
        naytto.getChildren().addAll(mokkiNaytto);

        //Listan teko mökeille
        TableView listausMokit = new TableView();
        listausMokit.setEditable(true);

        TableColumn clmMokkiID = new TableColumn<>("Mökki ID");
        clmMokkiID.setCellValueFactory(new PropertyValueFactory<Mokki, Integer>("mokki_id"));
        TableColumn clmMokkiNimi = new TableColumn<>("Mökin nimi");
        clmMokkiNimi.setCellValueFactory(new PropertyValueFactory<Mokki, String>("mokkinimi"));
        TableColumn clmAlueID = new TableColumn<>("Alue ID");
        clmAlueID.setCellValueFactory(new PropertyValueFactory<Mokki, Integer>("alue_id"));
        TableColumn clmKatu = new TableColumn<>("Osoite");
        clmKatu.setCellValueFactory(new PropertyValueFactory<Mokki, String>("katuosoite"));
        TableColumn clmPostiNro = new TableColumn<>("Postinumero");
        clmPostiNro.setCellValueFactory(new PropertyValueFactory<Mokki, String>("postinro"));
        TableColumn clmHinta = new TableColumn<>("Hinta ");
        clmHinta.setCellValueFactory(new PropertyValueFactory<Mokki, Double>("hinta"));
        TableColumn clmKuvaus = new TableColumn<>("Kuvaus");
        clmKuvaus.setCellValueFactory(new PropertyValueFactory<Mokki, String>("kuvaus"));
        TableColumn clmHenkMaara = new TableColumn<>("Mökin koko (henkilöä)");
        clmHenkMaara.setCellValueFactory(new PropertyValueFactory<Mokki, Integer>("henkilomaara"));
        TableColumn clmVarustelu = new TableColumn<>("Varustelu");
        clmVarustelu.setCellValueFactory(new PropertyValueFactory<Mokki, String>("varustelu"));
        TableColumn clmMuokkaus = new TableColumn<>("Muokkaus");

        TableColumn<Mokki, Void> clmPoisto = new TableColumn<>("Poista");
        Callback<TableColumn<Mokki, Void>, TableCell<Mokki, Void>> cellFactoryPoisto = new Callback<>() {
            //poista napin toiminto mökeille
            @Override
            public TableCell<Mokki, Void> call(final TableColumn<Mokki, Void> param) {
                final TableCell<Mokki, Void> cell = new TableCell<>() {

                    private final Button button = new Button("Poista");

                    {
                        button.setOnAction(event -> {

                            listausMokit.getSelectionModel().getSelectedItem();
                            TableRow<Mokki> row = getTableRow();

                            Mokki id_luku = row.getItem();

                            try {
                                Mokki.poistaMokki(id_luku);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
        Callback<TableColumn<Mokki, Void>, TableCell<Mokki, Void>> cellFactoryMuokkaus = new Callback<>() {
            @Override
            public TableCell<Mokki, Void> call(final TableColumn<Mokki, Void> param) {
                return new TableCell<>() {

                    private final Button button = new Button("Muokkaa");

                    {
                        button.setOnAction(event -> {
                            Mokki id_luku = getTableRow().getItem();
                            try {
                                Mokki.muokkaaMokkia(id_luku);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };

        btnPaivita.setOnMouseClicked(e -> {
            try {
                nayttoMokit();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        clmPoisto.setCellFactory(cellFactoryPoisto);
        clmMuokkaus.setCellFactory(cellFactoryMuokkaus);
        clmMokkiID.setPrefWidth(60);
        clmMokkiNimi.setPrefWidth(60);
        clmAlueID.setPrefWidth(70);
        clmKatu.setPrefWidth(90);
        clmPostiNro.setPrefWidth(70);
        clmHinta.setPrefWidth(50);
        clmKuvaus.setPrefWidth(175);
        clmHenkMaara.setPrefWidth(50);
        clmVarustelu.setPrefWidth(130);
        //Alue -listan teko SQL-hakua varten
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

        query = "SELECT mokki_id, mokkinimi, alue_id, katuosoite, postinro, hinta, kuvaus, henkilomaara, varustelu FROM mokki";
        stmt = conn.createStatement();

        tulos = stmt.executeQuery(query);

        ObservableList<Mokki> data;

        data = FXCollections.observableArrayList();

        //Tietojen haku databasesta
        while (tulos.next()) {
            Mokki mokki = new Mokki();
            mokki.setMokkiID(tulos.getInt("mokki_id"));
            mokki.setMokkiNimi(tulos.getString("mokkinimi"));
            int indexi = listID.indexOf(tulos.getInt("alue_id"));
            mokki.setAlue_id(listNimi.get(indexi));
            mokki.setKatu(tulos.getString("katuosoite"));
            mokki.setPostiNro(tulos.getString("postinro"));
            mokki.setHinta(tulos.getDouble("hinta"));
            mokki.setKuvaus(tulos.getString("kuvaus"));
            mokki.setHenkMaara(tulos.getInt("henkilomaara"));
            mokki.setVarustelu(tulos.getString("varustelu"));
            data.add(mokki);
        }

        FilteredList<Mokki> filteredData = new FilteredList<>(data, p -> true);

        listausMokit.getColumns().addAll(clmMokkiID, clmMokkiNimi, clmAlueID, clmKatu, clmPostiNro, clmHenkMaara, clmHinta, clmVarustelu, clmKuvaus, clmPoisto, clmMuokkaus);

        listausMokit.setItems(filteredData);

        hakuField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mokki -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all rows when search is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Match against all columns of the current row
                if (mokki.getMokkinimi().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID
                } //else if (mokki.getAlue_id().toLowerCase().contains(lowerCaseFilter)) {
                // }

                return false; // No match found
            });
        });
        hakuField2.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mokki -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all rows when search is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Match against all columns of the current row
                if (mokki.getAlue_id().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID
                }

                return false; // No match found
            });
        });

        mokkiNaytto.setCenter(listausMokit);
        //Mökin lisäys
        btnLisaaMokki.setOnAction(e -> {
            try {
                Mokki.lisaaMokki();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void nayttoPalvelut() throws SQLException {
        naytto.getChildren().clear();

        BorderPane palveluNaytto = new BorderPane();
        Button btnLisaaPalvelu = new Button("Lisää uusi palvelu");
        Button btnPaivita = new Button("Päivitä");
        Button btnPeruuta = new Button("Peruuta");
        TableView<Palvelu> listausPalvelu = new TableView<>();
        VBox kehys = new VBox();
        HBox lisaaAlue = new HBox();
        HBox hakuAlue = new HBox();
        HBox hakuHinta = new HBox();
        VBox ylaPaneeli = new VBox();

        //määritellään arvoja muuttujille
        listausPalvelu.setEditable(true);
        listausPalvelu.prefHeightProperty().bind(kehys.heightProperty());
        listausPalvelu.prefWidthProperty().bind(kehys.widthProperty());
        kehys.getChildren().add(listausPalvelu);
        kehys.setMinHeight(500);
        kehys.setPrefHeight(Region.USE_COMPUTED_SIZE);
        kehys.setMaxHeight(2000);
        kehys.setMinWidth(931);
        kehys.setPrefWidth(Region.USE_COMPUTED_SIZE);
        kehys.setMaxWidth(2000);
        VBox.setVgrow(listausPalvelu, Priority.ALWAYS);

        //luodaan sarakkeet ja CellValueFactoryt tableviewiin
        TableColumn<Palvelu, Integer> clmPalveluId = new TableColumn<>("ID");
        clmPalveluId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPalvelu_id()).asObject());
        TableColumn<Palvelu, String> clmAlueId = new TableColumn<>("Alue");
        clmAlueId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlue_id()));
        TableColumn<Palvelu, String> clmPalveluNimi = new TableColumn<>("Nimi");
        clmPalveluNimi.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNimi()));
        TableColumn<Palvelu, String> clmKuvaus = new TableColumn<>("Kuvaus");
        clmKuvaus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKuvaus()));
        TableColumn<Palvelu, Double> clmHinta = new TableColumn<>("Hinta (€)");
        clmHinta.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHinta()).asObject());
        TableColumn<Palvelu, Double> clmAlv = new TableColumn<>("Alv%");
        clmAlv.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAlv()).asObject());
        TableColumn<Palvelu, Void> clmMuokkaus = new TableColumn<>("Muokkaa");
        TableColumn<Palvelu, Void> clmPoisto = new TableColumn<>("Poista");

        //Luodaan buttonit muokkaa- ja poista-funktioille
        Callback<TableColumn<Palvelu, Void>, TableCell<Palvelu, Void>> cellFactoryMuokkaa = new Callback<>() {
            @Override
            public TableCell<Palvelu, Void> call(final TableColumn<Palvelu, Void> param) {
                return new TableCell<>() {

                    private final Button button = new Button("Muokkaa");
                    {
                        button.setOnAction(event -> {
                            Palvelu id_luku = getTableRow().getItem();
                            Palvelu.muokkaaPalvelua(id_luku);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        clmMuokkaus.setCellFactory(cellFactoryMuokkaa);

        Callback<TableColumn<Palvelu, Void>, TableCell<Palvelu, Void>> cellFactoryPoista = new Callback<>() {
            @Override
            public TableCell<Palvelu, Void> call(final TableColumn<Palvelu, Void> param) {
                return new TableCell<>() {
                    private final Button button = new Button("Poista");

                    {
                        button.setOnAction(event -> {
                            listausPalvelu.getSelectionModel().getSelectedItem();
                            Palvelu id_luku = getTableRow().getItem();
                            VBox poistoVahvistusPane = new VBox();
                            HBox poistoVahvistusBtn = new HBox();
                            poistoVahvistusPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            poistoVahvistusPane.setPadding(new Insets(10));
                            poistoVahvistusBtn.setPadding(new Insets(10,0,0,0));
                            poistoVahvistusBtn.setSpacing(10);
                            poistoVahvistusPane.setStyle("-fx-border-width: 2; -fx-border-radius: 5; -fx-border-color: black;");
                            Popup varmistus = new Popup();
                            Button btnVahvista = new Button("Vahvista poisto");
                            Label vahvistusTeksti = new Label("Poistetaanko palvelu " + id_luku.getPalvelu_id() + ". " + id_luku.getNimi() + "?");
                            poistoVahvistusBtn.getChildren().addAll(btnVahvista, btnPeruuta);
                            poistoVahvistusPane.getChildren().addAll(vahvistusTeksti, poistoVahvistusBtn);
                            varmistus.getContent().add(poistoVahvistusPane);
                            varmistus.show(getScene().getWindow());
                            btnPeruuta.setOnAction(e -> varmistus.hide());
                            btnVahvista.setOnAction(tapahtuu -> {
                                try {
                                    Palvelu.poistaPalvelu(id_luku);
                                    varmistus.hide();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
            }
        };
        clmPoisto.setCellFactory(cellFactoryPoista);

        //määritellään sarakkeiden leveydet
        clmPalveluId.setPrefWidth(25);
        clmPalveluNimi.setPrefWidth(170);
        clmAlueId.setPrefWidth(80);
        clmHinta.setPrefWidth(60);
        clmAlv.setPrefWidth(40);
        clmKuvaus.setPrefWidth(435);
        clmMuokkaus.setPrefWidth(75);
        clmPoisto.setPrefWidth(60);

        //Lisää palvelu -buttonin funktio
        btnLisaaPalvelu.setOnMouseClicked(e -> Palvelu.lisaaPalvelu());

        btnPaivita.setOnMouseClicked(e -> {
            try {
                nayttoPalvelut();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        //Alue -listan teko SQL-hakua varten
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

        //Määritellään SQL-hakuun ja etsintään liittyvä toiminnallisuus
        ObservableList<Palvelu> data = FXCollections.observableArrayList();

        //Luodaan SQL-haku palveluihin
        query = "SELECT palvelu_id, alue_id, nimi, kuvaus, palvelu_hinta, alv FROM palvelu";
        stmt = conn.createStatement();
        tulos = stmt.executeQuery(query);

        //luodaan uusi Palvelu-olio jokaiselle haetulle tiedolle
        while (tulos.next()) {
            Palvelu palvelu = new Palvelu();
            palvelu.setPalvelu_id(tulos.getInt("palvelu_id"));
            palvelu.setNimi(tulos.getString("nimi"));
            int indexi = listID.indexOf(tulos.getInt("alue_id"));
            palvelu.setAlue_id(listNimi.get(indexi));
            palvelu.setKuvaus(tulos.getString("kuvaus"));
            palvelu.setHinta(tulos.getDouble("palvelu_hinta"));
            palvelu.setAlv(tulos.getDouble("alv"));
            data.add(palvelu);
        }

        FilteredList<Palvelu> filteredData = new FilteredList<>(data, p -> true);
        //lisätään sarakkeet TableViewiin ja lisätään ObservableList:ssä olevat Palvelu-oliot siihen
        listausPalvelu.getColumns().addAll(clmPalveluId, clmPalveluNimi, clmAlueId, clmHinta, clmAlv, clmKuvaus, clmMuokkaus, clmPoisto);
        listausPalvelu.setItems(filteredData);

        btnPaivita.setOnMouseClicked(e -> {
            try {
                nayttoPalvelut();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        //Luodaan Haku-funktionaalisuus
        TextField hakuField = new TextField();
        hakuField.setPromptText("Etsi...");

        TextField hakuMinHinta = new TextField();
        hakuMinHinta.setPromptText("min. hinta (€)");
        hakuMinHinta.setPrefWidth(90);

        TextField hakuMaxHinta = new TextField();
        hakuMaxHinta.setPromptText("max hinta (€)");
        hakuMaxHinta.setPrefWidth(90);

        Label hintaTeksti = new Label("-");

        hakuField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(palvelu -> {
                boolean nameMatch = newValue.isEmpty() || palvelu.getNimi().toLowerCase().contains(newValue.toLowerCase());
                boolean areaMatch = newValue.isEmpty() || palvelu.getAlue_id().toLowerCase().contains(newValue.toLowerCase());
                double minPrice = hakuMinHinta.getText().isEmpty() ? 0.0 : Double.parseDouble(hakuMinHinta.getText());
                double maxPrice = hakuMaxHinta.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(hakuMaxHinta.getText());
                boolean priceMatch = palvelu.getHinta() >= minPrice && palvelu.getHinta() <= maxPrice;
                return (nameMatch || areaMatch) && priceMatch;
            });
        });

        hakuMinHinta.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(palvelu -> {
                boolean nameMatch = hakuField.getText().isEmpty() || palvelu.getNimi().toLowerCase().contains(hakuField.getText().toLowerCase());
                boolean areaMatch = hakuField.getText().isEmpty() || palvelu.getAlue_id().toLowerCase().contains(hakuField.getText().toLowerCase());
                double minPrice = newValue.isEmpty() ? 0.0 : Double.parseDouble(newValue);
                double maxPrice = hakuMaxHinta.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(hakuMaxHinta.getText());
                boolean priceMatch = palvelu.getHinta() >= minPrice && palvelu.getHinta() <= maxPrice;
                return (nameMatch || areaMatch) && priceMatch;
            });
        });

        hakuMaxHinta.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(palvelu -> {
                boolean nameMatch = hakuField.getText().isEmpty() || palvelu.getNimi().toLowerCase().contains(hakuField.getText().toLowerCase());
                boolean areaMatch = hakuField.getText().isEmpty() || palvelu.getAlue_id().toLowerCase().contains(hakuField.getText().toLowerCase());
                double minPrice = hakuMinHinta.getText().isEmpty() ? 0.0 : Double.parseDouble(hakuMinHinta.getText());
                double maxPrice = newValue.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(newValue);
                boolean priceMatch = palvelu.getHinta() >= minPrice && palvelu.getHinta() <= maxPrice;
                return (nameMatch || areaMatch) && priceMatch;
            });
        });

        ylaPaneeli.getChildren().addAll(lisaaAlue, hakuAlue);
        ylaPaneeli.setSpacing(10);
        lisaaAlue.getChildren().addAll(btnLisaaPalvelu, btnPaivita);
        lisaaAlue.setSpacing(10);
        hakuHinta.getChildren().addAll(hakuMinHinta, hintaTeksti, hakuMaxHinta);
        hakuHinta.setSpacing(3);
        hakuAlue.getChildren().addAll(hakuField, hakuHinta);
        hakuAlue.setSpacing(20);
        lisaaAlue.setPadding(new Insets(10, 0, 3, 0));
        palveluNaytto.setTop(ylaPaneeli);
        palveluNaytto.setCenter(kehys);
        BorderPane.setMargin(btnLisaaPalvelu, new Insets(0, 15, 15, 0));
        palveluNaytto.setPadding(new Insets(15));
        BorderPane.setMargin(listausPalvelu, new Insets(20, 0, 0, 0));
        naytto.getChildren().addAll(palveluNaytto);
    }


    //Suljetaan yhteys SQL-kantaan, kun ohjelma suljetaan
    public void stop() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}