package ru.job4j.cinema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * the servlet for booking place in the cinema hall.
 */

public class PaymentServlet extends HttpServlet {

    /**
     * the instance of the service level.
     */
    private final Service service = Service.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(sb::append);
        ObjectMapper mapper = new ObjectMapper();
        String json = sb.toString();
        HashMap map = mapper.readValue(json, HashMap.class);
        String name = (String) map.get("name");
        String phone = (String) map.get("phone");
        String hall = (String) map.get("hall");
        String row = (String) map.get("row");
        String place = (String) map.get("place");
        boolean result = this.service.doPayment(name, phone, hall, row, place);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONObject status = new JSONObject();
        status.put("result", result);
        writer.append(status.toString());
        writer.flush();
    }
}
