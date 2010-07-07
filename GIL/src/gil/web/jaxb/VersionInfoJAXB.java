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
 * JAXB class for information/versioninfo about software subsystems.
 * @author Göran Larsson @ LearningWell AB
 */
@XmlRootElement
public class VersionInfoJAXB {

    public String name;
    public String version;
    public String company;    
    public String description;
    
    public VersionInfoJAXB() {}

    public VersionInfoJAXB(String name, String version, String company, String description) {
        this.name = name;
        this.version = version;
        this.company = company;
        this.description = description;
    }
}
