package com.xantrix.webapp.security;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@Log
public class AuthEntryPoint extends BasicAuthenticationEntryPoint
{
    //private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);

    private static String REALM = "REAME";

    @Override
    @SneakyThrows//per evitare l'inserimento
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
    {
        String ErrMsg = "Userid e/o Password non corrette!";
        log.warning("Errore Sicurezza: " + authException.getMessage());
        // Authentication failed, send error response.
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

        PrintWriter writer = response.getWriter();
        writer.println(ErrMsg);
    }

    @Override
    @SneakyThrows
    public void afterPropertiesSet()
    {
        setRealmName(REALM);
        super.afterPropertiesSet();
    }

}