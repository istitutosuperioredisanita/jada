/*
 * Copyright (C) 2019  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.jada.util.action;

import it.cnr.jada.action.ActionContext;
import it.cnr.jada.action.BusinessProcessException;
import it.cnr.jada.bulk.BulkInfo;
import it.cnr.jada.util.Config;
import it.cnr.jada.util.RemoteIterator;
import it.cnr.jada.util.RemotePagedIterator;
import it.cnr.jada.util.jsp.Table;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

// Referenced classes of package it.cnr.jada.util.action:
//            FormBP, Selection

public abstract class BulkListBP extends FormBP
        implements Serializable {

    private Table table;
    private RemoteIterator iterator;
    private Selection selection;
    private Dictionary columns;
    private int currentPage;
    private List pageContents;
    private int firstPage;
    private int pageFrameSize;
    private int pageSize;
    private int pageCount;
    private int elementsCount;
    private RemotePagedIterator pagedIterator;
    private Class bulkClass;
    private BulkInfo bulkInfo;

    public BulkListBP() {
        pageFrameSize = 10;
        pageSize = 10;
        table = new Table("mainTable");
        table.setSelection(selection = new Selection());
    }

    public Enumeration fetchPage()
            throws RemoteException {
        if (pageContents == null)
            if (pagedIterator != null) {
                pagedIterator.moveToPage(currentPage);
                pageContents = Arrays.asList(pagedIterator.nextPage());
            } else {
                pageContents = new ArrayList(pageSize);
                int i = currentPage * pageSize;
                iterator.moveTo(i);
                for (int j = 0; j < pageSize && i < elementsCount; i++) {
                    pageContents.add(iterator.nextElement());
                    j++;
                }

            }
        return Collections.enumeration(pageContents);
    }

    public BulkInfo getBulkInfo() {
        return bulkInfo;
    }

    public Dictionary getColumns() {
        return columns;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int i)
            throws RemoteException {
        currentPage = Math.min(pageCount - 1, Math.max(0, i));
        pageContents = null;
        firstPage = pageFrameSize * (currentPage / pageFrameSize);
    }

    public int getFirstPage() {
        return firstPage;
    }

    public RemoteIterator getIterator() {
        return iterator;
    }

    public int getLastPage()
            throws RemoteException {
        return Math.min(firstPage + pageFrameSize, pageCount);
    }

    public int getPageCount()
            throws RemoteException {
        return pageCount;
    }

    public int getPageFrameSize() {
        return pageFrameSize;
    }

    public void setPageFrameSize(int i) {
        pageFrameSize = i;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int i)
            throws RemoteException {
        pageSize = i;
        if (pagedIterator != null)
            pagedIterator.setPageSize(pageSize);
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection1) {
        selection = selection1;
    }

    protected void init(it.cnr.jada.action.Config config, ActionContext actioncontext)
            throws BusinessProcessException {
        try {
            pageSize = Integer.parseInt(Config.getHandler().getProperty(getClass(), "pageSize"));
        } catch (NumberFormatException _ex) {
        }
        try {
            pageFrameSize = Integer.parseInt(Config.getHandler().getProperty(getClass(), "pageFrameSize"));
        } catch (NumberFormatException _ex) {
        }
        try {
            setBulkClassName(config.getInitParameter("bulkClassName"));
            reset();
        } catch (ClassNotFoundException _ex) {
            throw new RuntimeException("Non trovata la classe bulk");
        } catch (RemoteException remoteexception) {
            throw new BusinessProcessException(remoteexception);
        }
        super.init(config, actioncontext);
    }

    public void reset()
            throws RemoteException {
        firstPage = 0;
        pageContents = null;
        elementsCount = iterator.countElements();
        pageCount = ((elementsCount + pageSize) - 1) / pageSize;
    }

    public void setBulkClassName(String s)
            throws ClassNotFoundException {
        bulkClass = getClass().getClassLoader().loadClass(s);
        bulkInfo = BulkInfo.getBulkInfo(bulkClass);
    }

    public void setIterator(ActionContext actioncontext, RemoteIterator remoteiterator)
            throws RemoteException {
        setIterator(actioncontext, iterator, getPageSize(), getPageFrameSize());
    }

    public void setIterator(ActionContext actioncontext, RemoteIterator remoteiterator, int i, int j)
            throws RemoteException {
        iterator = remoteiterator;
        if (remoteiterator instanceof RemotePagedIterator)
            pagedIterator = (RemotePagedIterator) remoteiterator;
        pageFrameSize = j;
        setPageSize(i);
        reset();
    }

    public Selection setSelection(ActionContext actioncontext) {
        selection.setFocus(actioncontext, "mainTable");
        selection.setSelection(actioncontext, "mainTable");
        return selection;
    }

    public void writeHTMLTable(PageContext pagecontext, String s, String s1, String s2)
            throws IOException, ServletException {
        JspWriter jspwriter = pagecontext.getOut();
        table.setSelection(selection);
        table.setColumns(getColumns());
        table.setRows(Collections.enumeration(pageContents != null ? pageContents : Collections.EMPTY_LIST));
        table.writeScrolledTable(this, pagecontext.getOut(), s1, s2, getFieldValidationMap(), 0, this.getParentRoot().isBootstrap());
        jspwriter.println("<div class=\"Toolbar\"><table witdth=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
        jspwriter.println("</tr></table></div>");
    }
}