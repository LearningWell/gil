/*
    Copyright (C) 2010 LearningWell AB (www.learningwell.com), Kärnkraftsäkerhet och Utbildning AB (www.ksu.se)

    This file is part of GIL (Generic Integration Layer).

    GIL is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GIL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with GIL.  If not, see <http://www.gnu.org/licenses/>.
*/
package gil.web;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.io.IOException;

/**
 * This class is the web server hosting the GIL REST resources och and the static html/css files located in the
 * files folder given in the contstructor call.
 * @author Göran Larsson @ LearningWell AB
 */
public class WebServer {

    GrizzlyWebServer _ws = null;

    /**
     * Starts the GIL web server.
     * @param port The listening port for the web server.
     * @param filesFolderPath The path where static html/css files shall be placed.
     * @param resourcesPath The path where Jersey REST services shall be accessed e.g. "/api".
     * @throws IOException If eny arror when initializing the web server.
     */
    public void start(int port, String filesFolderPath, String resourcesPath) throws IOException {

        if (_ws != null)
            throw new IllegalStateException("Web server allready started");

        _ws = new GrizzlyWebServer(port);

        // Jersey web resources
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.addInitParameter("com.sun.jersey.config.property.packages", "gil");
        jerseyAdapter.setServletInstance(new ServletContainer());
        jerseyAdapter.setContextPath(resourcesPath);

        GrizzlyAdapter staticResourceAdapter = new GrizzlyAdapter(filesFolderPath) {
            @Override
            public void service(GrizzlyRequest request, GrizzlyResponse response){
        }};

        staticResourceAdapter.setHandleStaticResources(true);
        _ws.addGrizzlyAdapter(staticResourceAdapter, new String[] {""});

        _ws.addGrizzlyAdapter(jerseyAdapter, new String[] {resourcesPath});
        _ws.start();
    }
    
    public void stop() {
        if (_ws != null) {
            _ws.stop();
            _ws = null;
        }
    }
}
