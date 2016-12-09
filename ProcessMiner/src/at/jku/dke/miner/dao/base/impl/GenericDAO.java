package at.jku.dke.miner.dao.base.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import javax.transaction.TransactionRolledbackException;

import at.jku.dke.miner.beans.GenericEntity;
import at.jku.dke.miner.beans.GenericPK;
import at.jku.dke.miner.common.CriteriaFactory;
import at.jku.dke.miner.dao.base.IGenericDAO;
import at.jku.dke.miner.enums.UpdateType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class GenericDAO<T extends GenericEntity<? extends GenericPK>> extends AbstractDAO implements IGenericDAO<T> {

	@Override
	public T findById(GenericPK id) {
		if(id == null) {
			return null;
		}
		try {
			beginSession();
			T bean = findInEM(getType(), id);
			return bean;
		} catch (EntityNotFoundException e) {
			return null;
		} catch (Exception e) {
			getEM().clear();
			throw e;
		} finally {
			closeSession();
		}
	}
	
	@Override
	public T find(T bean) {
		if (bean == null || bean.getId() == null) {
			return null;
		}
		return findById(bean.getId());
	}
	
	@Override
	public List<T> findAll() {
		return findAll(null);
	}

	@Override
	public List<T> findAll(Order order) {
		TypedQuery<T> query = CriteriaFactory.create().createCriteria(getEM(), getType());
		
		return query.getResultList();
	}
	
	public List<T> findByCriteria(CriteriaFactory factory) {
		List<T> list = new ArrayList<T>();
		
		beginSession();
		
		try {
			TypedQuery<T> query = factory.createCriteria(getEM(), getType());
			list = query.getResultList();
		} finally {
			closeSession();
		}
		return list;
	}
	
	protected T findInEM(Class<T> clazz, GenericPK id) {
		T entity = null;
		
		if(clazz != null && id != null) {
			entity = getEM().find(clazz, id);
		}
		return entity;
	}
	
	protected T mergeBean(T bean, UpdateType type) {
		//StopWatch sw = new StopWatch();
		//sw.start();

		beginSession();
		try {
			beginTransaction();
			if (UpdateType.ADD.equals(type)) {
				bean.preCreate();
				getEM().persist(bean);
				getEM().flush();
				bean.postCreate();
			} else if (UpdateType.UPDATE.equals(type)) {
				T savedBean = getEM().merge(bean);
				getEM().flush();
				bean = savedBean;
			} else if (UpdateType.DELETE.equals(type)) {
				bean = removeBean(bean);
			}

			commitTransaction();
		} catch (EntityExistsException e) {
			rollbackTransaction();
			throw e;
		} catch (TransactionRolledbackException e) {
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
		finally {
			closeSession();
		}

		return bean;
	}
	
	protected T removeBean(T bean) {
		// should not be necessary with EclipseLink, removing of detached beans should be possible
		//bean = findInEM(getType(), bean.getId());
		bean = getEM().merge(bean);
		getEM().remove(bean);
		
		return bean;
	}
}