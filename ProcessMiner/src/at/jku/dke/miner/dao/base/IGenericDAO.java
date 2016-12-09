package at.jku.dke.miner.dao.base;

import java.util.List;

import javax.persistence.criteria.Order;

import at.jku.dke.miner.beans.GenericEntity;
import at.jku.dke.miner.beans.GenericPK;
import at.jku.dke.miner.common.CriteriaFactory;

public interface IGenericDAO<T extends GenericEntity<? extends GenericPK>> {
	Class<T> getType();
	
	T findById(GenericPK id);
	T find(T bean);
	
	List<T> findAll();
	List<T> findAll(Order order);
	List<T> findByCriteria(CriteriaFactory factory);
}
