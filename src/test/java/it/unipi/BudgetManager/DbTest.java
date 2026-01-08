package it.unipi.BudgetManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class DbTest {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DbTest.class);

    @Test
    public void testConnessione() {
        Connection co = null;
        try {
            co = DriverManager.getConnection("jdbc:mysql://localhost:3306/603217", "root", "root");
            if (co.isValid(0) && !co.isClosed()) {
                logger.info("Okay");
                System.out.println("CONNESSIONE RIUSCITA");
            } else {
                logger.info("ERRORE");
                fail("CONNESSIONE FALLITA");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (co != null) {
                try {
                    co.close();
                } catch (SQLException e) {
                    fail("Errore durante la chiusura della connessione");
                }
            }
        }
    }
}
