package ru.job4j.cinema;

/**
 * the class for the place in the hall.
 */
public class HallPlace {

    /**
     * row number.
     */
    private int row;
    /**
     * place number.
     */
    private int place;
    /**
     * the status.
     */
    private int accountId;

    public HallPlace(int row, int place, int accountId) {
        this.row = row;
        this.place = place;
        this.accountId = accountId;
    }

    public int getRow() {
        return row;
    }

    public int getPlace() {
        return place;
    }

    public int getAccountId() {
        return accountId;
    }
}
