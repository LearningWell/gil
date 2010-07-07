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
package gil.web.html.page;

import java.net.URI;
import gil.core.LogfileMetadata;
import gil.common.Helpers;
import gil.web.html.Div;
import gil.web.html.Heading;
import gil.web.html.INodeableContent;
import gil.web.html.Link;
import gil.web.html.PlainText;
import gil.web.html.Table;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class LogfilesPage extends MasterPage {

    public LogfilesPage(LogfileMetadata[] logfiles, URI baseUri) {
        
        Table table = new Table(new INodeableContent[] {new PlainText("Name"), new PlainText("Size"),
        new PlainText("Date"), new PlainText("Download")});
        for (LogfileMetadata f : logfiles) {
            table.addRow(new INodeableContent[] {new PlainText(f.getName()), new PlainText(Long.toString(f.getSizeInBytes())),
            new PlainText(Helpers.dateTimeToString(f.getLastModified())), new Link(baseUri + "/" + f.getName(), "Download")});
        }
        Div div = new Div("");
        div.addContent(new Heading("Logfiles", Heading.H2)).addContent(table);
        this.setSectionContent("content", div);
    }
}
