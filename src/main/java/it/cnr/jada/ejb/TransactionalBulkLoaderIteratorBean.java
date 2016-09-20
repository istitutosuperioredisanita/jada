package it.cnr.jada.ejb;

import it.cnr.jada.DetailedRuntimeException;
import it.cnr.jada.UserContext;
import it.cnr.jada.comp.ComponentException;
import it.cnr.jada.persistency.PersistencyException;
import it.cnr.jada.persistency.sql.Query;
import it.cnr.jada.util.Log;
import it.cnr.jada.util.ejb.EJBCommonServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.TransactionSynchronizationRegistry;

@Stateful(name="JADAEJB_TransactionalBulkLoaderIterator")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TransactionalBulkLoaderIteratorBean extends BaseBulkLoaderIteratorBean implements TransactionalBulkLoaderIterator, SessionSynchronization{
	static final long serialVersionUID = 0x2c7e5503d9bf9553L;
	private static final Log logger = Log.getInstance(TransactionalBulkLoaderIteratorBean.class);
    @Resource private SessionContext mySessionCtx;	
    @Resource private TransactionSynchronizationRegistry registry;
    
	private Connection connection;

	public TransactionalBulkLoaderIteratorBean(){
	}

	protected Connection getConnection() throws PersistencyException{
		return connection;
	}

	@Override
	protected void initialize() throws PersistencyException {
		logger.debug("Initialize TransactionalBulkLoaderIteratorBean " + this.toString());
		super.ejbActivate();
		super.initialize();
	}
	
	protected void initializeConnection() throws PersistencyException{
		try{
			if(connection == null)
				connection = EJBCommonServices.getConnection(super.userContext);      
		}catch(SQLException sqlexception){
			throw new PersistencyException(sqlexception);
		}catch(EJBException ejbexception){
			throw new PersistencyException(ejbexception);
		}
	}
	
	private void closeConnection(){
		try{
			if(super.homeCache != null && super.homeCache.getConnection() != null)
				super.homeCache.getConnection().close();
			if (connection != null)
				connection.close();
		}catch(SQLException _ex) { 
		}
		connection = null;
		super.homeCache = null;
		super.broker = null;	
	}
	
	public void close(){
		logger.debug("Close TransactionalBulkLoaderIteratorBean " + this.toString());	
		super.close();
		closeConnection();
	}

	@PrePassivate
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void ejbPassivate(){
		closeConnection();
	}

	@Remove
	public void ejbRemove(){
		closeConnection();
		super.ejbRemove();
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.writeObject(super.query);
		out.writeInt(super.position);
		out.writeObject(super.bulkClass);
		out.writeObject(super.userContext);
		out.writeInt(super.pageSize);
		out.writeObject(super.fetchPolicyName);
		out.defaultWriteObject();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		super.doCount = true;
		super.query = (Query) in.readObject();
		super.position = in.readInt();
		super.bulkClass = (Class<?>) in.readObject();
		super.userContext = (UserContext) in.readObject();
		super.pageSize = in.readInt();
		super.fetchPolicyName = (String) in.readObject();
		in.defaultReadObject();
	}
	@Override
	public void setPageSize(int i) throws DetailedRuntimeException,
			EJBException {
		super.setPageSize(i);
		logger.debug("setPageSize with TransactionKey:" + registry.getTransactionKey());
	}
	@Override
	public void moveTo(int i) throws DetailedRuntimeException, EJBException {
		super.moveTo(i);
		logger.debug("moveTo 	with TransactionKey:" + registry.getTransactionKey());
	}
	@Override
	public void moveToPage(int i) throws DetailedRuntimeException, EJBException {
		super.moveToPage(i);
		logger.debug("moveToPage 	with TransactionKey:" + registry.getTransactionKey());
	}
	public void open(UserContext usercontext) throws ComponentException,
			EJBException, DetailedRuntimeException {
		logger.debug("open 	with TransactionKey:" + registry.getTransactionKey());
		super.open(usercontext);
	}
	@Override
	public void afterBegin() throws EJBException, RemoteException {
		logger.debug("afterBegin with TransactionKey:" + registry.getTransactionKey());		
	}

	@Override
	public void beforeCompletion() throws EJBException, RemoteException {
		logger.debug("beforeCompletion with TransactionKey:" + registry.getTransactionKey());		
	}

	@Override
	public void afterCompletion(boolean committed) throws EJBException,
			RemoteException {
		logger.debug("afterCompletion with TransactionKey:" + registry.getTransactionKey());				
	}
}
