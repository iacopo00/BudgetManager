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
public class EntrateController {

    @FXML
    MenuItem elimina;

    @FXML
    TextField anno;

    @FXML
    TextField mese;

    @FXML
    TextField luogo;

    @FXML
    TextField campionato;

    @FXML
    Label annoLabel;

    @FXML
    Label meseLabel;

    @FXML
    Label luogoLabel;

    @FXML
    Label campionatoLabel;

    @FXML
    Button btnFiltro;

    @FXML
    TextField conteggio;

    @FXML
    TableView<Gara> tabellaGare = new TableView();

    private ObservableList<Gara> ol;

    private final User u;
    private String l;
    private Linguaggio lang;

    public EntrateController(User u, String l) {
        this.u = u;
        this.l = l;
    }

    /**
     *
     */
    @FXML
    public void initialize() {
        TableColumn garaCol;
        TableColumn dataCol;
        TableColumn champCol;
        TableColumn luogoCol;
        TableColumn rimbCol;
        if (l.equals("Inglese")) {
            garaCol = new TableColumn<>("Game");
            dataCol = new TableColumn<>("Date");
            champCol = new TableColumn<>("Championship");
            luogoCol = new TableColumn<>("City");
            rimbCol = new TableColumn<>("Reimbursement");

            XStream xstream = new XStream();

            xstream.addPermission(AnyTypePermission.ANY);

            xstream.alias("linguaggio", Linguaggio.class);

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));

            annoLabel.setText(lang.ANNO);
            meseLabel.setText(lang.MESE);
            luogoLabel.setText(lang.LUOGO);
            campionatoLabel.setText(lang.CAMPIONATO);
            btnFiltro.setText(lang.BOTTONEFILTRA);
            elimina.setText(lang.ELIMINA);

        } else {
            garaCol = new TableColumn<>("N_Gara");
            dataCol = new TableColumn<>("Data");
            champCol = new TableColumn<>("Campionato");
            luogoCol = new TableColumn<>("Luogo");
            rimbCol = new TableColumn<>("Rimborso");
        }
        garaCol.setCellValueFactory(new PropertyValueFactory<>("n_gara"));
        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
        champCol.setCellValueFactory(new PropertyValueFactory<>("campionato"));
        luogoCol.setCellValueFactory(new PropertyValueFactory<>("luogo"));
        rimbCol.setCellValueFactory(new PropertyValueFactory<>("rimborso"));

        tabellaGare.getColumns().addAll(garaCol, dataCol, champCol, luogoCol, rimbCol);

        ol = FXCollections.observableArrayList();

        tabellaGare.setItems(ol);

        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    URL url = new URL("http://localhost:8080/603217/gare?username=" + u.getUsername());
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
                    JsonArray gara = json.getAsJsonArray();

                    for (int i = 0; i < gara.size(); i++) {
                        JsonObject garaJson = gara.get(i).getAsJsonObject();

                        Gara g = new Gara();
                        g.setN_gara(garaJson.get("n_gara").getAsInt());
                        g.setData(Date.valueOf(garaJson.get("data").getAsString()));
                        g.setLuogo(garaJson.get("luogo").getAsString());
                        g.setCampionato(garaJson.get("campionato").getAsString());
                        g.setRimborso(garaJson.get("rimborso").getAsFloat());
                        ol.add(g);
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
                    URL url1 = new URL("http://localhost:8080/603217/contaGare?username=" + u.getUsername());
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
                    URL url = new URL("http://localhost:8080/603217/eliminaGara?numeroGara=" + tabellaGare.getSelectionModel().getSelectedItem().getN_gara() + "&username=" + u.getUsername());
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

                    if(response.toString().equals("104")){
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
    private void filtra() {
        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {

                try {
                    URL url = new URL("http://localhost:8080/603217/filtraGare?anno=" + anno.getText() + "&mese=" + mese.getText() + "&luogo=" + luogo.getText() + "&campionato=" + campionato.getText() + "&username=" + u.getUsername());
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
                    JsonArray gara = json.getAsJsonArray();
                    ol.clear();

                    for (int i = 0; i < gara.size(); i++) {
                        JsonObject garaJson = gara.get(i).getAsJsonObject();

                        Gara g = new Gara();
                        g.setN_gara(garaJson.get("n_gara").getAsInt());
                        g.setData(Date.valueOf(garaJson.get("data").getAsString()));
                        g.setLuogo(garaJson.get("luogo").getAsString());
                        g.setCampionato(garaJson.get("campionato").getAsString());
                        g.setRimborso(garaJson.get("rimborso").getAsFloat());
                        ol.add(g);
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
                    URL url1 = new URL("http://localhost:8080/603217/contoFiltro?anno=" + anno.getText() + "&mese=" + mese.getText() + "&luogo=" + luogo.getText() + "&campionato=" + campionato.getText() + "&username=" + u.getUsername());
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
                luogo.clear();
                campionato.clear();

                try {
                    URL url = new URL("http://localhost:8080/603217/gare?username=" + u.getUsername());
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
                    JsonArray gara = json.getAsJsonArray();

                    for (int i = 0; i < gara.size(); i++) {
                        JsonObject garaJson = gara.get(i).getAsJsonObject();

                        Gara g = new Gara();
                        g.setN_gara(garaJson.get("n_gara").getAsInt());
                        g.setData(Date.valueOf(garaJson.get("data").getAsString()));
                        g.setLuogo(garaJson.get("luogo").getAsString());
                        g.setCampionato(garaJson.get("campionato").getAsString());
                        g.setRimborso(garaJson.get("rimborso").getAsFloat());
                        ol.add(g);
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
                    URL url1 = new URL("http://localhost:8080/603217/contaGare?username=" + u.getUsername());
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
    private void goToHome() throws IOException {
        HomePageController controller = new HomePageController(u, l);
        switchPage("home_page", controller);
    }

    private void switchPage(String fxml, Object javaClass) { // utile per cambiare scena e tornare alla home
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
