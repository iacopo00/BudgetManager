/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.budgetmanager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author iacopomassei
 */
public class UsciteController {

    @FXML
    MenuItem elimina;

    @FXML
    Label annoLabel;

    @FXML
    Label meseLabel;

    @FXML
    TextField anno;

    @FXML
    TextField mese;

    @FXML
    Button btnFiltro;

    @FXML
    TextField conteggio;

    private final User u;
    private String l;
    private Linguaggio lang;

    public UsciteController(User u, String l) {
        this.u = u;
        this.l = l;
    }

    @FXML
    TableView<Spesa> tabellaSpese = new TableView();

    private ObservableList<Spesa> ol;

    @FXML
    public void initialize() {
        TableColumn idCol = new TableColumn("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn causCol;
        TableColumn dataCol;
        TableColumn costoCol;

        if (l.equals("Inglese")) {
            causCol = new TableColumn("Causal");
            dataCol = new TableColumn("Date");
            costoCol = new TableColumn("Cost");

            XStream xstream = new XStream();

            xstream.addPermission(AnyTypePermission.ANY);

            xstream.alias("linguaggio", Linguaggio.class);

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));

            annoLabel.setText(lang.ANNO);
            meseLabel.setText(lang.MESE);
            btnFiltro.setText(lang.BOTTONEFILTRA);
            elimina.setText(lang.ELIMINA);
        } else {
            causCol = new TableColumn("Causale");
            dataCol = new TableColumn("Data");
            costoCol = new TableColumn("Costo");
        }

        causCol.setCellValueFactory(new PropertyValueFactory<>("causale"));
        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
        costoCol.setCellValueFactory(new PropertyValueFactory<>("costo"));

        tabellaSpese.getColumns().addAll(idCol, causCol, dataCol, costoCol);

        ol = FXCollections.observableArrayList();

        tabellaSpese.setItems(ol);

        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    URL url = new URL("http://localhost:8080/603217/spese?username=" + u.getUsername());
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
                    JsonElement json = gson.fromJson(response.toString(), JsonElement.class);
                    JsonArray spesa = json.getAsJsonArray();

                    for (int i = 0; i < spesa.size(); i++) {
                        JsonObject spesaJson = spesa.get(i).getAsJsonObject();

                        Spesa s = new Spesa();
                        s.setId(spesaJson.get("id").getAsInt());
                        s.setData(Date.valueOf(spesaJson.get("data").getAsString()));
                        s.setCausale(spesaJson.get("causale").getAsString());
                        s.setCosto(spesaJson.get("costo").getAsFloat());
                        ol.add(s);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        new Thread(task).start();

        Task task1 = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url1 = new URL("http://localhost:8080/603217/contaSpese?username=" + u.getUsername());
                    HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json");
                    con1.setDoOutput(true);

                    // leggo la risposta
                    int responseCode1 = con1.getResponseCode();
                    BufferedReader reader1;
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                    } else {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getErrorStream()));
                    }
                    String line1;
                    StringBuilder response1 = new StringBuilder();
                    while ((line1 = reader1.readLine()) != null) {
                        response1.append(line1);
                    }
                    reader1.close();

                    Gson gson = new Gson();
                    String conta = gson.fromJson(response1.toString(), String.class);

                    conteggio.setText(conta);
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        new Thread(task1).start();

    }

    @FXML
    private void filtra() {
        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    URL url = new URL("http://localhost:8080/603217/filtraSpese?anno=" + anno.getText() + "&mese=" + mese.getText() + "&username=" + u.getUsername());
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

                    /// analizzo la risposta JSON
                    Gson gson = new Gson();
                    JsonElement json = gson.fromJson(response.toString(), JsonElement.class);
                    JsonArray spesa = json.getAsJsonArray();
                    ol.clear();

                    for (int i = 0; i < spesa.size(); i++) {
                        JsonObject spesaJson = spesa.get(i).getAsJsonObject();

                        Spesa s = new Spesa();
                        s.setId(spesaJson.get("id").getAsInt());
                        s.setData(Date.valueOf(spesaJson.get("data").getAsString()));
                        s.setCausale(spesaJson.get("causale").getAsString());
                        s.setCosto(spesaJson.get("costo").getAsFloat());
                        ol.add(s);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        new Thread(task).start();

        Task task1 = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url1 = new URL("http://localhost:8080/603217/contoFiltroSpesa?anno=" + anno.getText() + "&mese=" + mese.getText() + "&username=" + u.getUsername());
                    HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json");
                    con1.setDoOutput(true);

                    // leggo la risposta
                    int responseCode1 = con1.getResponseCode();
                    BufferedReader reader1;
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                    } else {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getErrorStream()));
                    }
                    String line1;
                    StringBuilder response1 = new StringBuilder();
                    while ((line1 = reader1.readLine()) != null) {
                        response1.append(line1);
                    }
                    reader1.close();
                    
                    Gson gson = new Gson();
                    String conta = gson.fromJson(response1.toString(), String.class);

                    conteggio.setText(conta);
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        new Thread(task1).start();
    }
    

    @FXML
    private void resetFilter() {
        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {
                
                anno.clear();
                mese.clear();

                try {
                    URL url = new URL("http://localhost:8080/603217/spese?username=" + u.getUsername());
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

                    /// analizzo la risposta JSON
                    Gson gson = new Gson();
                    JsonElement json = gson.fromJson(response.toString(), JsonElement.class);
                    JsonArray spesa = json.getAsJsonArray();
                    ol.clear();

                    for (int i = 0; i < spesa.size(); i++) {
                        JsonObject spesaJson = spesa.get(i).getAsJsonObject();

                        Spesa s = new Spesa();
                        s.setId(spesaJson.get("id").getAsInt());
                        s.setData(Date.valueOf(spesaJson.get("data").getAsString()));
                        s.setCausale(spesaJson.get("causale").getAsString());
                        s.setCosto(spesaJson.get("costo").getAsFloat());
                        ol.add(s);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        new Thread(task).start();

        Task task1 = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url1 = new URL("http://localhost:8080/603217/contaSpese?username=" + u.getUsername());
                    HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json");
                    con1.setDoOutput(true);

                    // leggo la risposta
                    int responseCode1 = con1.getResponseCode();
                    BufferedReader reader1;
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                    } else {
                        reader1 = new BufferedReader(new InputStreamReader(con1.getErrorStream()));
                    }
                    String line1;
                    StringBuilder response1 = new StringBuilder();
                    while ((line1 = reader1.readLine()) != null) {
                        response1.append(line1);
                    }
                    reader1.close();
                    
                    Gson gson = new Gson();
                    String conta = gson.fromJson(response1.toString(), String.class);

                    conteggio.setText(conta);
                } catch (IOException ex) {
                    Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        new Thread(task1).start();
    }

    @FXML
    public void remove() {
        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    URL url = new URL("http://localhost:8080/603217/eliminaSpesa?id=" + tabellaSpese.getSelectionModel().getSelectedItem().getId());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("DELETE");
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

                    if (response.toString().equals("104")) {
                        resetFilter();
                    }
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

    private void switchPage(String fxml, Object javaClass) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

            if (javaClass != null) {
                loader.setController(javaClass);
            }

            if (fxml.equals("login")) {
                LoginController.linguaScelta = l;
            }

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) anno.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(HomePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
