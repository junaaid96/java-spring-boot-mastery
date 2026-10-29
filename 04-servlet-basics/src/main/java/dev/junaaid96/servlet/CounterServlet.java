package dev.junaaid96.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Shows the servlet lifecycle (init/service/destroy), sessions and POST handling.
@WebServlet("/counter")
public class CounterServlet extends HttpServlet {

    // One servlet instance serves many threads, so avoid mutable instance fields; per-user state goes in the session.
    @Override
    public void init() {
        // Called once when the container loads the servlet: open expensive resources here.
        log("CounterServlet initialised");
    }

    // POST increments a per-session counter.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // getSession() creates a session (JSESSIONID cookie) if none exists.
        HttpSession session = req.getSession();
        // Session attributes are untyped objects; default to 0.
        Integer count = (Integer) session.getAttribute("count");
        int next = (count == null ? 0 : count) + 1;
        session.setAttribute("count", next);
        // Return JSON manually (frameworks do this for you later).
        res.setContentType("application/json");
        res.getWriter().write("{\"count\":" + next + "}");
    }

    // Called once on shutdown: release resources here.
    @Override
    public void destroy() {
        log("CounterServlet destroyed");
    }
}
