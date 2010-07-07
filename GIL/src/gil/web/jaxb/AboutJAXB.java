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
package gil.web.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class for information about versions and such, for the subsystem of the integration solution.
 * @author Göran Larsson @ LearningWell AB
 */
@XmlRootElement(name="about")
public class AboutJAXB {


    @XmlRootElement
    public static class Subsystems {
        public VersionInfoJAXB[] subsystem;
    }
    public AboutJAXB() {
    }
    
    public AboutJAXB(VersionInfoJAXB framework, VersionInfoJAXB[] externalSystem, VersionInfoJAXB[] processModel) {
        this.framework = framework;
        this.externalSystem.subsystem = externalSystem;
        this.processModel.subsystem = processModel;
    }
    
    public VersionInfoJAXB framework;
    public Subsystems externalSystem = new Subsystems();
    public Subsystems processModel = new Subsystems();
}
