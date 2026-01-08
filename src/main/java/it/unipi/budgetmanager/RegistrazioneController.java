/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.budgetmanager;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

/**
 *
 * @author iacopomassei
 */
public class RegistrazioneController {

    private String l;

    private Linguaggio lang;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    Label risposta;

    @FXML
    Button btnReg;

    @FXML
    Label registratiLabel;
    
    @FXML
    Hyperlink tornaLogin;

    public RegistrazioneController(String l) {
        this.l = l;
    }

    @FXML
    public void initialize() {
        if (l.equals("Inglese")) {
            XStream xstream = new XStream();

            xstream.addPermission(AnyTypePermission.ANY);

            xstream.alias("linguaggio", Linguaggio.class);

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));
            btnReg.setText(lang.BOTTONEREG);
            registratiLabel.setText(lang.REGLABEL);
        }
    }

    @FXML
    private void reg() {
        User u = new User();
        u.setUsername(username.getText());
        u.setPassword(password.getText());

        if (u.getUsername().isEmpty()) { // controllo che vengano inseriti tutti i campi
            if (u.getPassword().isEmpty()) {
                Platform.runLater(() -> {
                    username.setPromptText("Username obbligatorio");
                    password.setPromptText("Password obbligatoria");
                });
                return;
            } else {
                Platform.runLater(() -> {
                    username.setPromptText("Username obbligatorio");
                });
                return;
            }
        } else if (u.getPassword().isEmpty()) {
            Platform.runLater(() -> {
                password.setPromptText("Password obbligatoria");
            });
            return;
        }

        // Conversione dell'oggetto User in formato JSON
        Gson gson = new Gson();
        String userJson = gson.toJson(u);

        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/603217/signUp");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    // scrivo il corpo della richiesta
                    DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
                    outputStream.writeBytes(userJson);
                    outputStream.flush();

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

                    if ("101".equals(response.toString())) { // controllo il codice della risposta
                        Platform.runLater(() -> {
                            if (l.equals("Inglese")) {
                                risposta.setText("Welcome!");
                                tornaLogin.setText("Go back login");
                            } else {
                                risposta.setText("Benvenuto!");
                                tornaLogin.setText("Torna al login");
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            if (l.equals("Inglese")) {
                                risposta.setText("Username already exists");
                            } else {
                                risposta.setText("Username già in uso");
                            }
                        });
                    }

                } catch (MalformedURLException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };
        new Thread(task)
                .start();
    }

    @FXML
    private void goToLogin() throws IOException {
        switchPage("login", null);

    }

    private void switchPage(String fxml, Object javaClass) { //funzione che permette di cambiare scena
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
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException ex) {
            Logger.getLogger(HomePageController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
