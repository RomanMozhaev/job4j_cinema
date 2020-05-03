package ru.job4j.cinema;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * the class for preparing requests from servlets to database and responses from database to servlets.
 */
public class Service {

    /**
     * the instance of the service class.
     */
    private static final Service INSTANCE = new Service();

    /**
     * the instance of the class for connecting to the database.
     */
    private final DBConnector dbCon = DBConnector.getInstance();

    /**
     * the main constructor.
     */
    private Service() {
    }

    /**
     * the getter of the service instance.
     *
     * @return the instance.
     */
    public static Service getInstance() {
        return INSTANCE;
    }

    /**
     * the method for getting cinema hall schema and returning it to the servlet.
     *
     * @param hallId - the hall id.
     * @return the map of the places with their status.
     */
    public Map<Integer, Boolean> getHallSchema(int hallId) {
        Map<Integer, Boolean> occupiedPlaces = new HashMap<>();
        Set<HallPlace> hall = this.dbCon.getHallSchema(hallId);
        for (HallPlace hallPlace : hall) {
            int row = hallPlace.getRow();
            int place = hallPlace.getPlace();
            int accountId = hallPlace.getAccountId();
            int sitNum = row * 10 + place;
            if (accountId >= 0) {
                occupiedPlaces.put(sitNum, true);
            } else {
                occupiedPlaces.put(sitNum, false);
            }
        }
        return occupiedPlaces;
    }

    /**
     * the method for booking the place.
     *
     * @param name  - customer name.
     * @param phone - customer phone.
     * @param hall  - hall id.
     * @param row   - row number in the hall.
     * @param place - place number on the row.
     * @return true if the place was booked; otherwise false.
     */
    public boolean doPayment(String name, String phone, String hall, String row, String place) {
        int userPhone = Integer.parseInt(phone);
        int hallId = Integer.parseInt(hall);
        int rowNum = Integer.parseInt(row);
        int placeNum = Integer.parseInt(place);
        return this.dbCon.doTransaction(name, userPhone, hallId, rowNum, placeNum);
    }
}
