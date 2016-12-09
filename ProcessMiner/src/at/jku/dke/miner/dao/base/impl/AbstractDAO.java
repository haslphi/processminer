package at.jku.dke.miner.dao.base.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.TransactionRolledbackException;

import at.jku.dke.miner.common.MinerEntityManager;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AbstractDAO {
	
	private static final ThreadLocal<ManagedTransaction> threadLocalTransaction =  new ThreadLocal<ManagedTransaction>();
	
	/**
	 * Helper short cut method
	 * @return singleton MinerEntityManager
	 */
	protected EntityManager getEM() {
		return MinerEntityManager.getInstance().getEntityManager();
	}
	
	/**
	 * Begin session
	 */
	protected void beginSession() {
		MinerEntityManager.getInstance().beginSession();
	}

	/**
	 * Close session
	 */
	protected void closeSession() {
		MinerEntityManager.getInstance().closeSession();
	}
	
	/**
	 * Start a new transaction, if not already existing.
	 * 
	 * @throws TransactionRolledbackException
	 */
	protected void beginTransaction() throws TransactionRolledbackException {
		ManagedTransaction ta = threadLocalTransaction.get();
		if(ta == null) {
			ta = new ManagedTransaction();
			threadLocalTransaction.set(ta);
		}
		ta.begin();
	}

	/**
	 * Commit transaction (if nested, the nesting count is decreased, no commit is done) <br>
	 * Commit is only done for top level transaction.
	 * 
	 * @throws TransactionRolledbackException
	 */
	protected void commitTransaction() throws TransactionRolledbackException {
		ManagedTransaction ta = threadLocalTransaction.get();
		if(ta != null) {
			if(ta.commit() <= 0) {
				threadLocalTransaction.remove();
			}
		}
	}

	protected void rollbackTransaction() {
		ManagedTransaction ta = threadLocalTransaction.get();
		if(ta != null) {
			ta.rollback();
			threadLocalTransaction.remove();
		}
	}

	public class ManagedTransaction {

		private EntityTransaction transaction;
		private int count = 0;
		private boolean isActive = false;

		protected ManagedTransaction() {}

		public void begin() throws TransactionRolledbackException {
			//LogUtilities.log().debug("transaction begin ({0}, {1}, {2})", count+1, isActive, Thread.currentThread().getId());
			if(count > 0 && !isActive) {
				throw new TransactionRolledbackException("Transaction was rolled back");
			}
			if(count++ == 0) {
				transaction = getEM().getTransaction();
				transaction.begin();
				isActive = true;
			}
		}

		public int commit() throws TransactionRolledbackException {
			//LogUtilities.log().debug("transaction commit ({0}, {1}, {2})", count, isActive, Thread.currentThread().getId());
			if(count > 0 && !isActive) {
				throw new TransactionRolledbackException("Transaction was rolled back");
			}
			if(--count <= 0) {
				//LogUtilities.log().debug("committing to database {0}", Thread.currentThread().getId());
				transaction.commit();
				isActive = false;
			}
			return count;
		}

		public void rollback() {
			//LogUtilities.log().debug("transaction rollback ({0}, {1}, {2})", count, isActive, Thread.currentThread().getId());
			if(isActive) {
				transaction.rollback();
			}
			isActive = false;
			count = 0;
		}

		public boolean isActive() {
			return isActive;
		}

		@Override
		public String toString() {
			return String.format("active: %b, ref-count: %d, Transaction: [active: %b, commited: %b, rolled back: %b, timeout: %d]",
					isActive,
					count,
					transaction.isActive()
					);
		}
	}
}