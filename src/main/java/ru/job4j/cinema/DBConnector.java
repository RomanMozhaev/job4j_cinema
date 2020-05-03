package ru.job4j.cinema;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DBConnector {

    /**
     * the logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger(DBConnector.class.getName());
    /**
     * the connections pool.
     */
    private static final BasicDataSource SOURCE = new BasicDataSource();
    /**
     * the DBConnector class instance.
     */
    private static final DBConnector INSTANCE = new DBConnector();

    /**
     * the main constructor. The connection properties are set.
     */
    private DBConnector() {
        SOURCE.setUrl("jdbc:postgresql://localhost:5432/cinema");
        SOURCE.setUsername("postgres");
        SOURCE.setPassword("password");
        SOURCE.setMinIdle(5);
        SOURCE.setMaxIdle(10);
        SOURCE.setMaxOpenPreparedStatements(100);
        SOURCE.setDriverClassName("org.postgresql.Driver");
    }

    /**
     * the getter of the DBConnector instance.
     *
     * @return - the instance.
     */
    public static DBConnector getInstance() {
        return INSTANCE;
    }

    /**
     * the method for reading data from the hall table.
     *
     * @param hallId - the hall id.
     * @return the set of the places with parameters.
     */
    public Set<HallPlace> getHallSchema(int hallId) {
        Set<HallPlace> hall = new HashSet<>();
        String query = "select * from halls where hall_id = ?;";
        try (Connection connection = SOURCE.getConnection();
             PreparedStatement st = connection.prepareStatement(query)
        ) {
            st.setInt(1, hallId);
            ResultSet set = st.executeQuery();
            while (set.next()) {
                int row = set.getInt("row");
                int place = set.getInt("place");
                int account = set.getInt("account_id");
                hall.add(new HallPlace(row, place, account));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return hall;
    }

    /**
     * the method for booking place. The new user is created if th user is not in the table.
     *
     * @param userName  - customer name.
     * @param userPhone - customer phone number.
     * @param hall      - hall id.
     * @param row       - row number
     * @param place     - place number.
     * @return true if the place was booked; otherwise false.
     */
    public boolean doTransaction(String userName, int userPhone, int hall, int row, int place) {
        boolean result = false;
        int accountId = -1;
        String queryFindAccount = "select * from accounts where name = ? and phone = ?;";
        String queryAddAccount = "insert into accounts (name, phone) values (?, ?);";
        String queryBookPlace = "update halls set account_id = ? where hall_id = ? and row = ? and place = ?;";
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet set = null;
        try {
            connection = SOURCE.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement(queryFindAccount);
            st.setString(1, userName);
            st.setInt(2, userPhone);
            set = st.executeQuery();
            while (set.next()) {
                accountId = set.getInt("account_id");
            }
            if (accountId == -1) {
                st = connection.prepareStatement(queryAddAccount, Statement.RETURN_GENERATED_KEYS);
                st.setString(1, userName);
                st.setInt(2, userPhone);
                st.executeUpdate();
                set = st.getGeneratedKeys();
                while (set.next()) {
                    accountId = set.getInt(1);
                }
            }
            st = connection.prepareStatement(queryBookPlace);
            st.setInt(1, accountId);
            st.setInt(2, hall);
            st.setInt(3, row);
            st.setInt(4, place);
            st.executeUpdate();
            connection.commit();
            result = true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException sqlEx) {
                    LOG.error(sqlEx.getMessage(), sqlEx);
                }
            }
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return result;
    }
}
