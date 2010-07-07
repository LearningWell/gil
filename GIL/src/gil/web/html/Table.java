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
package gil.web.html;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class for building a table and render it as html.
 * @author Göran Larsson @ LearningWell AB
 */
public class Table implements INodeableContent {

    INodeableContent[] _headers;
    List<INodeableContent[]> _rows = new ArrayList<INodeableContent[]>();

    /**
     * Creates the table with the number of columns given in headers.
     * @param headers 
     */
    public Table(INodeableContent[] headers) {
        _headers = headers;
    }

    /**
     * Adds a row with the given cells. The numbers of cells must be equal to the numbers
     * of columns. 
     * @param cells
     */
    public void addRow(INodeableContent[] cells) {
        if (cells.length != _headers.length)
            throw new IllegalArgumentException("Expects cellValues count to be same as headers count");

        _rows.add(cells);
    }

    /**
     * Builds a html tree of this table.
     * @param ownerDocument The DOM Document that will own the created element.
     * @return The newly created table element.
     */
    public Node asNode(Document ownerDocument) {

        Node table = ownerDocument.createElement("table");

        Node header = this.appendRowTag(table);
        for (INodeableContent colTitle :_headers) {
            Node headerCell = ownerDocument.createElement("th");
            header.appendChild(headerCell);
            headerCell.appendChild(colTitle.asNode(ownerDocument));
        }
        for (INodeableContent[] rowCells  : _rows) {
            this.appendRowNode(table, rowCells);
        }
        return table;
    }

    private Node appendRowNode(Node parent, INodeableContent[] cells) {
        Node row = this.appendRowTag(parent);

        for (INodeableContent cellValue : cells) {
            Node rowCell = parent.getOwnerDocument().createElement("td");
            row.appendChild(rowCell);
            rowCell.appendChild(cellValue.asNode(parent.getOwnerDocument()));
        }
        return row;
    }

    private Node appendRowTag(Node parent) {
        Node row = parent.getOwnerDocument().createElement("tr");
        parent.appendChild(row);
        return row;
    }
}
