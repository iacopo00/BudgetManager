package it.unipi.budgetmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomePageController {

    @FXML
    Label id_user;

    @FXML
    Label entrate;

    @FXML
    Label uscite;

    @FXML
    Label budget;

    @FXML
    TextField spesaCausale;

    @FXML
    TextField spesaData;

    @FXML
    TextField spesaCosto;

    @FXML
    TextField numeroGara;

    @FXML
    TextField dataGara;

    @FXML
    Label campionato;

    @FXML
    TextField luogo;

    @FXML
    TextField rimborso;

    @FXML
    MenuButton scegliMese;

    @FXML
    Label meseCorrente;

    @FXML
    MenuButton menu;

    @FXML
    Label entrateText;

    @FXML
    Label usciteText;

    @FXML
    Label inserisciGara;

    @FXML
    Label inserisciSpesa;

    @FXML
    MenuItem entrateMenu;

    @FXML
    MenuItem usciteMenu;

    @FXML
    Button inviaGara;

    @FXML
    Button inviaSpesa;

    @FXML
    MenuItem gennaio;

    @FXML
    MenuItem febbraio;

    @FXML
    MenuItem marzo;

    @FXML
    MenuItem aprile;

    @FXML
    MenuItem maggio;

    @FXML
    MenuItem giugno;

    @FXML
    MenuItem luglio;

    @FXML
    MenuItem agosto;

    @FXML
    MenuItem settembre;

    @FXML
    MenuItem ottobre;

    @FXML
    MenuItem novembre;

    @FXML
    MenuItem dicembre;

    @FXML
    ChoiceBox campionatoScelto;

    private final User u;

    private final String l;

    private Linguaggio lang;

    private int currentYear;

    private boolean cambiato;

    private String meseScelto;

    public HomePageController(User u, String l) {
        this.u = u;
        this.l = l;
        LocalDate currentDate = LocalDate.now();
        this.currentYear = currentDate.getYear();
        cambiato = false;
    }

    @FXML
    public void initialize() {

        if (l.equals("Inglese")) {
            XStream xstream = new XStream();

            xstream.addPermission(AnyTypePermission.ANY);

            xstream.alias("linguaggio", Linguaggio.class);

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));

            menu.setText(lang.MENUNAVIGA);
            entrateMenu.setText(lang.MENUENTRATE);
            usciteMenu.setText(lang.MENUUSCITE);
            entrateText.setText(lang.ENTRATE);
            usciteText.setText(lang.USCITE);
            inserisciGara.setText(lang.LABELENTRATE);
            inserisciSpesa.setText(lang.LABELUSCITE);
            numeroGara.setPromptText(lang.NGARA);
            dataGara.setPromptText(lang.DATA);
            campionato.setText(lang.CAMPIONATO);
            luogo.setPromptText(lang.LUOGO);
            rimborso.setPromptText(lang.RIMBORSO);
            inviaGara.setText(lang.BOTTONEINVIAG);
            inviaSpesa.setText(lang.BOTTONEINVIAS);
            gennaio.setText(lang.GENNAIO);
            febbraio.setText(lang.FEBBRAIO);
            marzo.setText(lang.MARZO);
            aprile.setText(lang.APRILE);
            maggio.setText(lang.MAGGIO);
            giugno.setText(lang.GIUGNO);
            luglio.setText(lang.LUGLIO);
            agosto.setText(lang.AGOSTO);
            settembre.setText(lang.SETTEMBRE);
            ottobre.setText(lang.OTTOBRE);
            novembre.setText(lang.NOVEMBRE);
            dicembre.setText(lang.DICEMBRE);
            spesaCausale.setPromptText(lang.CAUSALE);
            spesaData.setPromptText(lang.DATA);
            spesaCosto.setPromptText(lang.COSTO);
        }

        id_user.setText(u.getUsername());

        String[] campionati = {
            "A",
            "A2",
            "B",
            "C1",
            "C2",
            "D",
            "PM",
            "1DM",
            "U20M/S",
            "U19M/G",
            "U19M/S",
            "U17M/E",
            "U17M/G",
            "U17M/S",
            "U15M/E",
            "U15M/G",
            "U15M/S",
            "U14M/EL",
            "U14M/S",
            "U13M/S",
            "A/F",
            "A2/F",
            "B/F",
            "C/F",
            "PF",
            "U19F",
            "U17F",
            "U15F",
            "U14F",
            "U13F"
        };

        List<String> c = Arrays.asList(campionati);
        campionatoScelto.getItems().addAll(c);

        LocalDate now = LocalDate.now();

        int mese;

        if (l.equals("Inglese")) {
            meseScelto = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            mese = now.getMonth().getValue();
        } else {
            meseScelto = now.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
            mese = now.getMonth().getValue();
        }

        String nomeMeseMaiusc = meseScelto.substring(0, 1).toUpperCase() + meseScelto.substring(1);
        Platform.runLater(() -> {
            scegliMese.setText(nomeMeseMaiusc); //imposto al mese corrente
            meseCorrente.setText(nomeMeseMaiusc + " " + now.getYear()); //imposto al mese corrente
        });
        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/603217/budget?username=" + u.getUsername() + "&mese=" + mese + "&anno=" + currentYear);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    // leggo la risposta
                    int responseCode = con.getResponseCode();
                    BufferedReader reader;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // analizzo la risposta JSON
                    Gson gson = new Gson();
                    float[] values = gson.fromJson(response.toString(), float[].class);
                    float tot_e = values[0];
                    float tot_u = values[1];

                    Platform.runLater(() -> {
                        entrate.setText(Float.toString(tot_e));
                        uscite.setText(Float.toString(tot_u));
                        budget.setText(Float.toString(tot_e - tot_u));
                    });

                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void aggiungiSpesa() {
        if (spesaCausale.getText().isEmpty()) {
            if (l.equals("Inglese")) {
                spesaCausale.setPromptText("Casual required");
            } else {
                spesaCausale.setPromptText("Casuale obbligatoria");
            }
            return;
        }

        if (spesaData.getText().isEmpty()) {
            if (l.equals("Inglese")) {
                spesaData.setPromptText("Date required");
            } else {
                spesaData.setPromptText("Data obbligatoria");
            }
            return;
        }

        if (spesaCosto.getText().isEmpty()) {
            if (l.equals("Inglese")) {
                spesaCosto.setPromptText("Cost required");
            } else {
                spesaCosto.setPromptText("Costo obbligatorio");
            }
            return;
        }
        Task task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    Spesa s = new Spesa();
                    String textFieldValue = spesaCosto.getText();
                    float spesaCostoFloat = Float.parseFloat(textFieldValue);

                    LocalDate localDate = LocalDate.parse(spesaData.getText());
                    java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

                    s.setCausale(spesaCausale.getText());
                    s.setData(sqlDate);
                    s.setCosto(spesaCostoFloat);
                    s.setUsername(id_user.getText());

                    // conversione dell'oggetto Spesa in formato JSON
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // in questo modo il file JSON avrà la data formattata nel modo corretto
                    String spesaJson = gson.toJson(s);

                    URL url = new URL("http://localhost:8080/603217/addSpesa");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    try ( // scrivo il corpo della richiesta
                             DataOutputStream outputStream = new DataOutputStream(con.getOutputStream())) {
                        outputStream.writeBytes(spesaJson);
                        outputStream.flush();
                    }

                    // leggo la risposta
                    int responseCode = con.getResponseCode();
                    BufferedReader reader;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }

                    if (reader.readLine().equals("407")) {
                        Platform.runLater(() -> {
                            if (l.equals("Inglese")) {
                                spesaCosto.setText("Cost can't be < 0");
                            } else {
                                spesaCosto.setText("Costo non può essere < 0");
                            }
                        });
                        reader.close();
                    } else {
                        reader.close();
                        refresh();

                    }

                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (NumberFormatException e) { // nel caso in cui non si sia inserito un int o un float valido
                    if (l.equals("Inglese")) {
                        spesaCosto.setText("Value not valid");
                    } else {
                        spesaCosto.setText("Valore non valido");
                    }
                } catch (DateTimeParseException dEx) { // nel caso in cui la data sia nel formato errato
                    Platform.runLater(() -> {
                        if (l.equals("Inglese")) {
                            spesaData.setText("Date format is wrong");
                        } else {
                            spesaData.setText("Formato data sbagliato");
                        }
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void aggiungiGara() {
        if (numeroGara.getText().isEmpty()) {
            Platform.runLater(() -> {
                if (l.equals("Inglese")) {
                    numeroGara.setPromptText("Game number required");
                } else {
                    numeroGara.setPromptText("Numero gara obbligatorio");
                }
            });
            return;
        }

        if (dataGara.getText().isEmpty()) {
            Platform.runLater(() -> {
                if (l.equals("Inglese")) {
                    dataGara.setPromptText("Date required");
                } else {
                    dataGara.setPromptText("Data obbligatoria");
                }
            });
            return;
        }

        if (campionatoScelto.getValue() == null) {
            Platform.runLater(() -> {
                if (l.equals("Inglese")) {
                    campionato.setText("Required");
                } else {
                    campionato.setText("Obbligatorio");
                }
            });
            return;
        }

        if (luogo.getText().isEmpty()) {
            Platform.runLater(() -> {
                if (l.equals("Inglese")) {
                    luogo.setPromptText("City required");
                } else {
                    luogo.setPromptText("Luogo obbligatorio");
                }
            });
            return;
        }

        if (rimborso.getText().isEmpty()) {
            Platform.runLater(() -> {
                if (l.equals("Inglese")) {
                    rimborso.setPromptText("Reimbursement required");
                } else {
                    rimborso.setPromptText("Rimborso obbligatorio");
                }
            });
            return;
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                boolean numeroGaraConv = false;
                boolean garaRimborsoConv = false;
                try {
                    Gara g = new Gara();
                    String textFieldValueInt = numeroGara.getText();
                    String textFieldValueFloat = rimborso.getText();
                    int numeroGaraInt = Integer.parseInt(textFieldValueInt);
                    numeroGaraConv = true;
                    float garaRimborsoFloat = Float.parseFloat(textFieldValueFloat);
                    garaRimborsoConv = true;

                    LocalDate localDate = LocalDate.parse(dataGara.getText());
                    java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

                    g.setN_gara(numeroGaraInt);
                    g.setData(sqlDate);
                    g.setCampionato(campionatoScelto.getValue().toString());
                    g.setLuogo(luogo.getText());
                    g.setRimborso(garaRimborsoFloat);
                    g.setUsername(id_user.getText());

                    // conversione dell'oggetto Gara in formato JSON
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // in questo modo il file JSON avrà la data formattata nel modo corretto
                    String garaJson = gson.toJson(g);

                    URL url = new URL("http://localhost:8080/603217/addGara");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    try ( // scrivo il corpo della richiesta
                             DataOutputStream outputStream = new DataOutputStream(con.getOutputStream())) {
                        outputStream.writeBytes(garaJson);
                        outputStream.flush();
                    }

                    // leggo la risposta
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            String serverResponse = response.toString();

                            switch (serverResponse) {
                                case "400":
                                    Platform.runLater(() -> {
                                        if (l.equals("Inglese")) {
                                            numeroGara.setText("Game already associated to this user");
                                        } else {
                                            numeroGara.setText("Numero gara già associato all'utente");
                                        }
                                    });
                                    reader.close();
                                    return null;
                                case "401":
                                    Platform.runLater(() -> {
                                        if (l.equals("Inglese")) {
                                            luogo.setText("City format is wrong: City (Province)");
                                        } else {
                                            luogo.setText("Formato luogo errato: Comune (Provincia)");
                                        }
                                    });
                                    reader.close();
                                    return null;
                                default:
                                    reader.close();
                                    refresh();
                                    break;
                            }
                        }

                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));

                    }

                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (NumberFormatException e) { // nel caso i cui non si sia inserito un int o un float valido
                    if (l.equals("Inglese")) {
                        if (!numeroGaraConv && !garaRimborsoConv) {
                            Platform.runLater(() -> {
                                numeroGara.setText("Value not valid");
                                rimborso.setText("Value not valid");
                            });
                        } else if (!numeroGaraConv) {
                            Platform.runLater(() -> {
                                numeroGara.setText("Value not valid");
                            });
                        } else {
                            Platform.runLater(() -> {
                                rimborso.setText("Value not valid");
                            });
                        }
                    } else {
                        if (!numeroGaraConv && !garaRimborsoConv) {
                            Platform.runLater(() -> {
                                numeroGara.setText("Valore non valido");
                                rimborso.setText("Valore non valido");
                            });
                        } else if (!numeroGaraConv) {
                            Platform.runLater(() -> {
                                numeroGara.setText("Valore non valido");
                            });
                        } else {
                            Platform.runLater(() -> {
                                rimborso.setText("Valore non valido");
                            });
                        }
                    }
                } catch (DateTimeParseException dEx) { // nel caso in cui il formato data non sia corretto
                    Platform.runLater(() -> {
                        if (l.equals("Inglese")) {
                            dataGara.setText("Date format is wrong");
                        } else {
                            dataGara.setText("Formato data sbagliato");
                        }
                    });
                }

                return null;
            }
        };
        new Thread(task)
                .start();
    }

    @FXML

    private void cambiaMese(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String meseSelezionato = menuItem.getText();

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        if (!l.equals("Inglese")) {

            switch (meseSelezionato) {
                case "Gennaio":
                    meseScelto = "January";
                    break;
                case "Febbraio":
                    meseScelto = "February";
                    break;
                case "Marzo":
                    meseScelto = "March";
                    break;
                case "Aprile":
                    meseScelto = "April";
                    break;
                case "Maggio":
                    meseScelto = "May";
                    break;
                case "Giugno":
                    meseScelto = "June";
                    break;
                case "Luglio":
                    meseScelto = "July";
                    break;
                case "Agosto":
                    meseScelto = "August";
                    break;
                case "Settembre":
                    meseScelto = "September";
                    break;
                case "Ottobre":
                    meseScelto = "October";
                    break;
                case "Novembre":
                    meseScelto = "November";
                    break;
                case "Dicembre":
                    meseScelto = "December";
                    break;
                default:
                    meseScelto = "Unknown";
                    break;
            }
        } else {
            meseScelto = meseSelezionato;
        }

        Month others = Month.valueOf(meseScelto.toUpperCase());
        int otherMonth = others.getValue();

        if (otherMonth > currentMonth) { // controllo che il mese selezionato venga prima del mese corrente
            currentYear--; // altrimenti è dell'anno precedente
            cambiato = true;
        }

        Platform.runLater(() -> {
            scegliMese.setText(meseSelezionato);
            meseCorrente.setText(meseSelezionato + " " + currentYear);
        });

        Task task = new Task<Void>() {
            @Override
            public Void call() {

                refresh();

                return null;
            }
        };

        new Thread(task).start();

    }

    private void switchPage(String fxml, Object javaClass) { // utile per cambiare scena
        try {
            FXMLLoader loader = new FXMLLoader(App.class
                    .getResource(fxml + ".fxml"));

            if (javaClass != null) {
                loader.setController(javaClass);
            }

            if (fxml.equals("login")) {
                LoginController.linguaScelta = l;
            }

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) id_user.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException ex) {
            Logger.getLogger(HomePageController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refresh() { // serve per ricaricare la pagina dopo aver inviato una gara/spesa
        Task task = new Task<Void>() {
            @Override
            public Void call() {

                Platform.runLater(() -> {
                    numeroGara.clear();
                    dataGara.clear();
                    luogo.clear();
                    rimborso.clear();
                    spesaData.clear();
                    spesaCausale.clear();
                    spesaCosto.clear();
                    campionatoScelto.setValue(null); // reimposta il valore del campionato scelto a null
                });

                Month m = Month.valueOf(meseScelto.toUpperCase());
                int mese = m.getValue();

                try {
                    URL url = new URL("http://localhost:8080/603217/budget?username=" + u.getUsername() + "&mese=" + mese + "&anno=" + currentYear);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    // leggo la risposta
                    int responseCode = con.getResponseCode();
                    BufferedReader reader;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // analizzo la risposta JSON
                    Gson gson = new Gson();
                    float[] values = gson.fromJson(response.toString(), float[].class);
                    float tot_e = values[0];
                    float tot_u = values[1];

                    Platform.runLater(() -> {
                        entrate.setText(Float.toString(tot_e));
                        uscite.setText(Float.toString(tot_u));
                        budget.setText(Float.toString(tot_e - tot_u));
                    });

                    if (cambiato) { // nel caso in cui sia stato cambiato l'anno corrente, mi riporto allo stato iniziale
                        currentYear++;
                        cambiato = false;
                    }

                } catch (ProtocolException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void goToHome() throws IOException {
        HomePageController controller = new HomePageController(u, l);
        switchPage("home_page", controller);
    }

    @FXML
    private void goToLogin() throws IOException {
        switchPage("login", null);
    }

    @FXML
    private void goToEntrate() throws IOException {
        EntrateController controller = new EntrateController(u, l);
        switchPage("entrate", controller);
    }

    @FXML
    private void goToUscite() throws IOException {
        UsciteController controller = new UsciteController(u, l);
        switchPage("uscite", controller);

    }
}
