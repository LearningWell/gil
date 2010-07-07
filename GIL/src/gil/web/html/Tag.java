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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A arbritrary html element &lt;tag [id=id] [class=class]&gt;[content]&gt;/tag&gt;
 * @author Göran Larsson @ LearningWell AB
 */
public class Tag implements INodeableContent {


    String _tag = null;
    List<INodeableContent> _content = null;
    private Map<String, String> _attributes = new HashMap<String, String>();


    public static final String CLASS_ATTRIBUTE = "class";
    public static final String ID_ATTRIBUTE = "id";


    public Tag(String tag) {
        _tag = tag;
    }

    public Tag(String tag, String id) {
        this(tag);
        this.setID(id);
    }

    public Tag(String tag, String id, String aClass) {
        this(tag, id);        
        this.setClass(aClass);
    }

    public Tag setClass(String aClass) {
        _attributes.put(CLASS_ATTRIBUTE, aClass);
        return this;
    }

    public Tag setID(String id) {
        _attributes.put(ID_ATTRIBUTE, id);
        return this;
    }

    /**
     * Sets the content. Allready existing content is cleared.
     */
    public Tag setContent(INodeableContent content) {
        _content = new ArrayList<INodeableContent>();
        _content.add(content);
        return this;
    }

    /**
     * Adds content to the allready existing.
     */
    public Tag addContent(INodeableContent content) {
        if (_content == null) {
            _content = new ArrayList<INodeableContent>();
        }
        _content.add(content);
        return this;
    }

    public Tag addAttribute(String name, String value) {
        _attributes.put(name, value);
        return this;
    }

    public Node asNode(Document ownerDocument) {

        Element element = ownerDocument.createElement(_tag);
        if (_content != null) {
            for (INodeableContent c : _content) {
                element.appendChild(c.asNode(ownerDocument));
            }
        }
        for (Map.Entry<String,String> entry : _attributes.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
        return element;
    }
}
