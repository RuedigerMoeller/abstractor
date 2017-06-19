package org.nustaq.http.servlet;

import org.nustaq.http.example.ServletApp;
import org.nustaq.kontraktor.Actor;
import org.nustaq.kontraktor.Actors;
import org.nustaq.kontraktor.remoting.encoding.Coding;
import org.nustaq.kontraktor.remoting.encoding.SerializerType;
import org.nustaq.kontraktor.util.Log;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ruedi on 19.06.17.
 */
@WebServlet(
    name = "MyServlet",
    urlPatterns = {"/ep/*"},
    asyncSupported = true
)
public class KontraktorServlet extends HttpServlet {

    Actor facade;
    ServletActorConnector connector;

    @Override
    public void init(ServletConfig config) throws ServletException {
        facade = Actors.AsActor(ServletApp.class);
        connector = new ServletActorConnector(facade,this, new Coding(SerializerType.JsonNoRef), fail -> System.out.println("FAIL "+fail));
        super.init(config);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.write("hello kontraktor".getBytes());
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext aCtx = req.startAsync(req, resp);
        ServletRequest contextRequest = aCtx.getRequest();
        ServletInputStream inputStream = contextRequest.getInputStream();
        inputStream.setReadListener(new ReadListener() {
            byte buffer[];
            int index = 0;

            @Override
            public void onDataAvailable() throws IOException {
                System.out.println("available ");
                if ( buffer == null ) {
                    int available = aCtx.getRequest().getContentLength();
                    buffer = new byte[available];
                }
                int c;
                while( inputStream.isReady() && (c=inputStream.read()) != -1 ) {
                    buffer[index++] = (byte) c;
                }
                if ( index == buffer.length ) {
                    facade.execute( () -> {
                        connector.requestReceived(aCtx,buffer);
                    });
                }
            }

            @Override
            public void onAllDataRead() throws IOException {
                // not called reliably by tomcat 8.5.x
            }

            @Override
            public void onError(Throwable throwable) {
                Log.Error(KontraktorServlet.this,throwable);
            }
        });
    }

}