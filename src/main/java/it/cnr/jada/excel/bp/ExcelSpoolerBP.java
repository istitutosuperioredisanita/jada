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

/*
 * Created on Jan 26, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.cnr.jada.excel.bp;

import it.cnr.jada.action.ActionContext;
import it.cnr.jada.action.BusinessProcessException;
import it.cnr.jada.action.Config;
import it.cnr.jada.bulk.BulkInfo;
import it.cnr.jada.excel.bulk.Excel_spoolerBulk;
import it.cnr.jada.excel.ejb.BframeExcelComponentSession;
import it.cnr.jada.util.action.SelezionatoreListaBP;
import it.cnr.jada.util.jsp.Button;
import it.cnr.jada.util.jsp.JSPUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mspasiano
 * <p>
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExcelSpoolerBP extends SelezionatoreListaBP {

    /**
     *
     */
    public ExcelSpoolerBP() {
        super();
        table.setMultiSelection(true);
        setBulkInfo(BulkInfo.getBulkInfo(Excel_spoolerBulk.class));
    }

    public BframeExcelComponentSession createComponentSession() throws BusinessProcessException {
        return
                (BframeExcelComponentSession) createComponentSession(
                        "BFRAMEEXCEL_EJB_BframeExcelComponentSession",
                        BframeExcelComponentSession.class);
    }

    public it.cnr.jada.util.jsp.Button[] createToolbar() {
        Button[] toolbar = new Button[4];
        int i = 0;
        toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()), "Toolbar.refresh");
        toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()), "Toolbar.download");
        toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()), "Toolbar.delete");
        toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()), "Toolbar.close");
        return toolbar;
    }

    protected void init(Config config, ActionContext context) throws BusinessProcessException {
        super.init(config, context);
        Excel_spoolerBulk excel_spooler = new Excel_spoolerBulk();
        setModel(context, excel_spooler);
        refresh(context);
    }

    public boolean isScaricaButtonEnabled() {
        return
                getFocusedElement() != null &&
                        ((Excel_spoolerBulk) getFocusedElement()).isEseguita();
    }

    public void refresh(ActionContext context) throws BusinessProcessException {
        try {
            setIterator(context, createComponentSession().queryJobs(
                    context.getUserContext()));
        } catch (Throwable e) {
            throw new BusinessProcessException(e);
        }
    }

    // R.P. 04/09/2009 eliminato per la schedulazione
	/* Riscritto perchè in questo caso non voglio che quando l'utente
	 * seleziona una riga nel selezionatore venga anche impostato
	 * il modello del BulkBP
	 
	protected void setFocusedElement(it.cnr.jada.action.ActionContext context,Object element) throws it.cnr.jada.action.BusinessProcessException {
		OggettoBulk model = getModel();
		super.setFocusedElement(context,element);
		setModel(context,model);
	}
	*/
    public void writeToolbar(javax.servlet.jsp.PageContext pageContext) throws java.io.IOException, javax.servlet.ServletException {
        Button[] toolbar = getToolbar();
        Excel_spoolerBulk excel_spooler = (Excel_spoolerBulk) getFocusedElement();
        if (excel_spooler != null && excel_spooler.isEseguita())
            toolbar[1].setHref("doPrint('" + JSPUtils.getAppRoot((HttpServletRequest) pageContext.getRequest()) + "offline_excel/" + excel_spooler.getNome_file() + "?pg=" + excel_spooler.getPg_estrazione().longValue() + "')");
        else
            toolbar[1].setHref(null);
        writeToolbar(pageContext.getOut(), toolbar);
    }

    public boolean isEMailEnabled() {
        return this.getModel() != null && ((Excel_spoolerBulk) this.getModel()).getFl_email() != null && (((Excel_spoolerBulk) this.getModel()).getFl_email());
    }
}
