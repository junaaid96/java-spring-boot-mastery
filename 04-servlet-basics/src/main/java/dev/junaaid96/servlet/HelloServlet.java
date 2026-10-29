package dev.junaaid96.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Updated from the original javax HelloServlet. @WebServlet registers the URL mapping without web.xml.
@WebServlet(name = "hello", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    // The container calls doGet for HTTP GET requests (service() dispatches by method).
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Read an optional query parameter: /hello?name=Ana
        String name = req.getParameter("name");
        // Fall back to a default when it is missing or blank.
        String who = (name == null || name.isBlank()) ? "World" : name;
        // Always declare content type and charset before writing the body.
        res.setContentType("text/plain;charset=UTF-8");
        // getWriter() returns a character stream to the client.
        res.getWriter().write("Hello, " + who + "!");
    }
}
