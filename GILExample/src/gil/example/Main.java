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
package gil.example;

import gil.GIL;


/**
 * The entry point of the example system. Initializes and starts the GIL runtime.
 *
 * In order to understand the example setup, also check:
 *
 * ExampleESAdapter.java  The example implementation of the IExternalSystemAdapter interface. It is the boundary class
 *                        to the external system (ES).
 *
 * ExamplePMAdapter.java  The example implementation of the IProcessModelAdapter interface. It is the boundary class
 *                        to the process model (PM).
 * 
 * example.sel            Contains the signal exchange list with the fake signals to be transferred between
 *                        the process model and the external system.
 *
 * gil.config.xml         Contains the configuration for the example setup.
 *
 * files/index.html       The html index file opened by e.g. http://localhost:9980/index.html. The index.html also acts
 *                        as the template for all pages of the GIL web interface.
 * 
 * files/style.css        The style sheet of the GIL web interface.
 *
 *
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class Main {

    public static void main(String[] args) throws Exception {
        GIL.instance().start(9980);
        System.out.println("\n\nExample GIL application v0.1 started. \nDirect your browser to http://localhost:9980/index.html.");
        System.out.println("\nHit Enter to quit...\n\n");
        System.in.read();
        GIL.instance().stop();
    }
}
