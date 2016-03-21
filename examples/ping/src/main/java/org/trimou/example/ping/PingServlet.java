package org.trimou.example.ping;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
@WebServlet("/ping")
public class PingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private MustacheEngine engine;

    @Inject
    private PingService service;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        service.ping(req.getRemoteAddr());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        // Note that we use the provided Writer instance
        engine.getMustache("pingLogServlet").render(resp.getWriter(),
                ImmutableMap.of("pings", service.getPings()));
    }

}
