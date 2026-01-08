package it.unipi.budgetmanager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private Linguaggio lang;

    protected static String linguaScelta;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    Button signUp;

    @FXML
    MenuButton lingua;

    @FXML
    Button login;

    @FXML
    Label erroreLogin;

    @FXML
    public void initialize() {
        if (linguaScelta == null) {
            linguaScelta = "Italiano";
        }

        XStream xstream = new XStream();

        xstream.addPermission(AnyTypePermission.ANY);

        xstream.alias("linguaggio", Linguaggio.class);

        if (linguaScelta.equals("Inglese")) {

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));

            lingua.setText(lang.LINGUA);

            login.setText(lang.LOGIN);

            signUp.setText(lang.BOTTONEREG);

        } else {
            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_it.xml"));

            lingua.setText(lang.LINGUA);

            login.setText(lang.LOGIN);

            signUp.setText(lang.BOTTONEREG);

        }
    }

    @FXML
    private void download() throws IOException {

        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/603217", "root", "root1234");  Statement st1 = co.createStatement();) {
            DatabaseMetaData metadata = co.getMetaData();
            ResultSet userTable = metadata.getTables(null, null, "user", null); 
            ResultSet garaTable = metadata.getTables(null, null, "gara", null); 
            ResultSet spesaTable = metadata.getTables(null, null, "spesa", null); 
            if (userTable.next() & garaTable.next() & spesaTable.next()) { // se esistono le tabelle allora proseguo
                ResultSet userRow = st1.executeQuery("SELECT * FROM user");
                if (userRow.next()) { // se esiste una row, allora non devo inizializzare il db
                    return;
                }
            } // se non esistono row o non esistono le tabelle, inizializzo
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root1234");  Statement st = co.createStatement();) {

            FileReader fileSQL = new FileReader(getClass().getResource("buildSQL.sql").getFile());
            BufferedReader inSQL = new BufferedReader(fileSQL); // leggo il file buildSQL

            StringBuffer query = new StringBuffer();
            String inputQuery;
            while ((inputQuery = inSQL.readLine()) != null) {
                if (!inputQuery.isEmpty()) { //salto righe vuote
                    query.append(inputQuery);
                    if (inputQuery.endsWith(";")) {
                        st.execute(query.toString());
                        query.setLength(0); //ripristino il buffer
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Gson gson = new Gson();
        FileReader file = new FileReader(getClass().getResource("budget_21-22.json").getFile());

        StringBuffer content;
        try ( BufferedReader in = new BufferedReader(file)) { // leggo il file riga per riga e salvo in uno StringBuffer
            String inputLine;
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }

        JsonElement json = gson.fromJson(content.toString(), JsonElement.class);
        JsonObject rootObject = json.getAsJsonObject();
        JsonArray gara = rootObject.get("gara").getAsJsonArray();
        JsonObject user = rootObject.get("utente").getAsJsonObject();

        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/603217", "root", "root1234");  PreparedStatement ps = co.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");) {

            ps.setString(1, user.get("username").getAsString());
            ps.setString(2, user.get("password").getAsString());

            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < gara.size(); i++) {
            JsonObject g = gara.get(i).getAsJsonObject();

            try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/603217", "root", "root1234");  PreparedStatement ps = co.prepareStatement("INSERT INTO gara (n_gara, data, campionato, luogo, rimborso, username) VALUES (?, ?, ?, ?, ?, ?)");) {
                ps.setInt(1, g.get("n_gara").getAsInt());
                ps.setDate(2, Date.valueOf(g.get("data").getAsString()));
                ps.setString(3, g.get("campionato").getAsString());
                ps.setString(4, g.get("luogo").getAsString());
                ps.setFloat(5, g.get("rimborso").getAsFloat());
                ps.setString(6, g.get("username").getAsString());

                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    private void login() {
        User u = new User();
        u.setUsername(username.getText());
        u.setPassword(password.getText());

        // Conversione dell'oggetto User in formato JSON
        Gson gson = new Gson();
        String userJson = gson.toJson(u);

        Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/603217/login");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    // Scrivo il corpo della richiesta
                    DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
                    outputStream.writeBytes(userJson);
                    outputStream.flush();

                    // Leggo la risposta
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

                    if (response.toString().equals("102")) { // sono riuscito ad entrare
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("home_page.fxml"));
                                HomePageController controller = new HomePageController(u, linguaScelta);
                                loader.setController(controller);
                                Parent root = loader.load();

                                Scene scene = new Scene(root);
                                Stage stage = (Stage) username.getScene().getWindow();
                                stage.setScene(scene);
                            } catch (IOException e) {
                                System.out.println(e.getLocalizedMessage());
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            if (linguaScelta.equals("Inglese")) {
                                login.setText("Try again");
                                erroreLogin.setText("Wrong username or password");
                            } else {
                                login.setText("Riprova");
                                erroreLogin.setText("Username o password errato");
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
        new Thread(task).start();
    }

    @FXML
    private void cambiaLingua(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource(); // individuo il MenuItem selezionato
        linguaScelta = menuItem.getText();

        XStream xstream = new XStream();

        xstream.addPermission(AnyTypePermission.ANY);

        xstream.alias("linguaggio", Linguaggio.class);

        if (linguaScelta.equals("Inglese")) {

            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_en.xml"));

            lingua.setText(lang.LINGUA);

            login.setText(lang.LOGIN);

            signUp.setText(lang.BOTTONEREG);

        } else {
            lang = (Linguaggio) xstream.fromXML(getClass().getResource("lingue/lingua_it.xml"));

            lingua.setText(lang.LINGUA);

            login.setText(lang.LOGIN);

            signUp.setText(lang.BOTTONEREG);
        }
    }

    @FXML
    private void registrazione() {
        Platform.runLater(() -> { // cambio schermata
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("registrazione.fxml"));
                RegistrazioneController controller = new RegistrazioneController(linguaScelta);
                loader.setController(controller);
                Parent root = loader.load();

                Scene scene = new Scene(root);
                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        });
    }
}
