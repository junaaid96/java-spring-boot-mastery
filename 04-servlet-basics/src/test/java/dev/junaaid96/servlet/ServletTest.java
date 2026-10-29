package dev.junaaid96.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Unit-test servlets by mocking the request/response objects the container would normally create.
class ServletTest {

    @Test
    void helloUsesNameParameter() throws Exception {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var body = new StringWriter();
        when(req.getParameter("name")).thenReturn("Ana");          // simulate ?name=Ana
        when(res.getWriter()).thenReturn(new PrintWriter(body));   // capture output
        new HelloServlet().doGet(req, res);
        assertThat(body.toString()).isEqualTo("Hello, Ana!");
    }

    @Test
    void counterStoresStateInSession() throws Exception {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var session = mock(HttpSession.class);
        var body = new StringWriter();
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("count")).thenReturn(2);         // previous value
        when(res.getWriter()).thenReturn(new PrintWriter(body));
        new CounterServlet().doPost(req, res);
        verify(session).setAttribute("count", 3);
        assertThat(body.toString()).isEqualTo("{\"count\":3}");
    }

    @Test
    void filterCallsChainAndAddsHeader() throws Exception {
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        new LoggingFilter().doFilter(mock(HttpServletRequest.class), res, chain);
        verify(chain).doFilter(any(), eq(res));                    // request continued
        verify(res).setHeader(eq("X-Elapsed-Micros"), anyString());
    }
}
