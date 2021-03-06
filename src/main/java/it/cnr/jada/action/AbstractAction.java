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

package it.cnr.jada.action;

import it.cnr.jada.DetailedRuntimeException;
import it.cnr.jada.util.Introspector;
import it.cnr.jada.util.action.FormBP;
import it.cnr.jada.util.ejb.RemoteError;
import it.cnr.jada.util.ejb.UserTransactionTimeoutException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Optional;
import java.util.StringTokenizer;

public abstract class AbstractAction
        implements Serializable, Action {

    public AbstractAction() {
    }

    public Forward doDefault(ActionContext actioncontext)
            throws RemoteException {
        return actioncontext.findDefaultForward();
    }

    protected Forward handleException(ActionContext actioncontext, Throwable throwable) {
        try {
            throw throwable;
        } catch (NoSuchBusinessProcessException _ex) {
            return actioncontext.findForward("pageExpired");
        } catch (UserTransactionTimeoutException usertransactiontimeoutexception) {
            try {
                if (actioncontext.getUserInfo() == null)
                    throw new ActionPerformingError("Sessione scaduta");
                if (Optional.ofNullable(usertransactiontimeoutexception.getUserTransaction())
                        .map(userTransaction -> {
                            try {
                                return userTransaction.getOwner();
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(BusinessProcess.class::isInstance)
                        .isPresent()) {
                    ((BusinessProcess) usertransactiontimeoutexception.getUserTransaction().getOwner()).handleUserTransactionTimeout(actioncontext);
                }
                if (actioncontext.getBusinessProcess() instanceof FormBP) {
                    ((FormBP) actioncontext.getBusinessProcess()).setErrorMessage("Operazione scaduta. Le modifiche non salvate saranno perse.");
                }
                return actioncontext.findDefaultForward();
            } catch (Throwable throwable2) {
                return handleException(actioncontext, throwable2);
            }
        } catch (RemoteException remoteexception) {
            if (remoteexception.detail == null)
                throw new ActionPerformingError(remoteexception);
            else
                return handleException(actioncontext, remoteexception.detail);
        } catch (RemoteError remoteerror) {
            if (remoteerror.getDetail() == null)
                throw new ActionPerformingError(remoteerror);
            else
                return handleException(actioncontext, remoteerror.getDetail());
        } catch (BusinessProcessException businessprocessexception) {
            if (businessprocessexception.getDetail() == null)
                throw new ActionPerformingError(businessprocessexception);
            else
                return handleException(actioncontext, businessprocessexception.getDetail());
        } catch (DetailedRuntimeException detailedruntimeexception) {
            if (detailedruntimeexception.getDetail() == null)
                throw new ActionPerformingError(detailedruntimeexception);
            else
                return handleException(actioncontext, detailedruntimeexception.getDetail());
        } catch (InvocationTargetException invocationtargetexception) {
            return handleException(actioncontext, invocationtargetexception.getTargetException());
        } catch (Throwable throwable1) {
            throw new ActionPerformingError(throwable1);
        }
    }

    public void init(Config config) {
    }

    public boolean isThreadsafe(ActionContext actioncontext) {
        return false;
    }

    public Forward perform(ActionContext actioncontext) {
        return perform(actioncontext, actioncontext.getCurrentCommand());
    }

    protected Forward perform(ActionContext actioncontext, String s) {
        try {
            int i = s.indexOf('(');
            int j = s.lastIndexOf(')');
            String[] as = null;
            if (i > 0 && j > 0) {
                StringTokenizer stringtokenizer = new StringTokenizer(s.substring(i + 1, j), ",");
                as = new String[stringtokenizer.countTokens()];
                for (int k = 0; k < as.length; k++)
                    as[k] = stringtokenizer.nextToken();

                s = s.substring(0, i);
            }
            Method method = Introspector.getMethod(getClass(), s, as != null ? as.length + 1 : 1);
            if (method == null)
                throw new ActionPerformingError("La action non implementa il comando " + s);
            Class[] aclass = method.getParameterTypes();
            java.lang.Object[] aobj = new java.lang.Object[aclass.length];
            aobj[0] = actioncontext;
            if (as != null) {
                for (int l = 0; l < as.length; l++)
                    aobj[l + 1] = Introspector.standardParse(as[l], aclass[l + 1]);

            }
            return (Forward) method.invoke(this, aobj);
        } catch (InvocationTargetException invocationtargetexception) {
            if (invocationtargetexception.getTargetException() instanceof ActionPerformingError)
                throw (ActionPerformingError) invocationtargetexception.getTargetException();
            else
                throw new ActionPerformingError(invocationtargetexception.getTargetException());
        } catch (ParseException parseexception) {
            throw new ActionPerformingError(parseexception);
        } catch (IllegalAccessException illegalaccessexception) {
            throw new ActionPerformingError(illegalaccessexception);
        } catch (RuntimeException runtimeexception) {
            throw new ActionPerformingError(runtimeexception);
        }
    }
}