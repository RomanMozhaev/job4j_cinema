package ru.job4j.cinema;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * the servlet for updating hall places.
 */
public class HallServlet extends HttpServlet {

    /**
     * the instance of the service level.
     */
    private final Service service = Service.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hallId = req.getParameter("id");
        Map<Integer, Boolean> schema = this.service.getHallSchema(Integer.parseInt(hallId));
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONObject status = new JSONObject();
        for (Map.Entry<Integer, Boolean> entry : schema.entrySet()) {
            status.put(entry.getKey().toString(), entry.getValue());
        }
        writer.append(status.toString());
        writer.flush();
    }
}
