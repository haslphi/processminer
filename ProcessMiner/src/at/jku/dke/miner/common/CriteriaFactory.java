package at.jku.dke.miner.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import at.jku.dke.miner.beans.GenericEntity;
import at.jku.dke.miner.beans.GenericPK;
import at.jku.dke.miner.enums.CriteriaType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.val;

/**
 * Wrap the criteria to allow usage of generic types.
 * 
 * @author Philipp
 *
 */
public class CriteriaFactory {
	
	private Map<String, CriteriaType> orders;
	private Map<CriteriaClause, Object> andRestrictions;
	private Map<CriteriaClause, Object> orRestrictions;
	private Integer firstResult;
	private Integer maxResult;
	
	public static CriteriaFactory create() {
		return new CriteriaFactory();
	}
	
	private CriteriaFactory() {
		orders = new HashMap<>();
		andRestrictions = new HashMap<>();
		orRestrictions = new HashMap<>();
	}
	
	public CriteriaFactory andLike(String fieldName, String value) {
		andRestrictions.put(new CriteriaClause(CriteriaType.LIKE, fieldName), value);
		return this;
	}
	
	public CriteriaFactory andEquals(String fieldName, Object value) {
		andRestrictions.put(new CriteriaClause(CriteriaType.EQUALS, fieldName), value);
		return this;
	}
	
	public CriteriaFactory andIsNull(String fieldName) {
		andRestrictions.put(new CriteriaClause(CriteriaType.IS_NULL, fieldName), null);
		return this;
	}
	
	public CriteriaFactory orLike(String fieldName, String value) {
		orRestrictions.put(new CriteriaClause(CriteriaType.LIKE, fieldName), value);
		return this;
	}
	
	public CriteriaFactory orEquals(String fieldName, Object value) {
		orRestrictions.put(new CriteriaClause(CriteriaType.EQUALS, fieldName), value);
		return this;
	}
	
	public CriteriaFactory orIsNull(String fieldName) {
		andRestrictions.put(new CriteriaClause(CriteriaType.IS_NULL, fieldName), null);
		return this;
	}
	
	public CriteriaFactory ascOrder(String fieldName) {
		orders.put(fieldName, CriteriaType.ASC);
		return this;
	}
	
	public CriteriaFactory descOrder(String fieldName) {
		orders.put(fieldName, CriteriaType.DESC);
		return this;
	}
	
	public <T extends GenericEntity<? extends GenericPK>> TypedQuery<T> createCriteria(EntityManager em, Class<T> clazz) {
		final List<Predicate> andPredicates = new ArrayList<>();
		final List<Predicate> orPredicates = new ArrayList<>();
		final List<Order> orders = new ArrayList<>();
		
		CriteriaType type;
		String fieldName;
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		
		// transform and restriction
		for (val entry : andRestrictions.entrySet()) {
			type = entry.getKey().type;
			fieldName = entry.getKey().fieldName;
			
			PathContainer<T> pContainer = createPath(root, fieldName);
			
			switch (type) {
			case LIKE:
				andPredicates.add(cb.like(cb.lower(pContainer.p.get(pContainer.finalField)), addWildcard(entry.getValue().toString().toLowerCase())));
				break;
			case EQUALS:
				andPredicates.add(cb.equal(pContainer.p.get(pContainer.finalField), entry.getValue()));
				break;
			case IS_NULL:
				andPredicates.add(cb.isNull(pContainer.p.get(pContainer.finalField)));
				break;
			default:
				break;
			}
		}
		
		// transform or restriction
		for (val entry : orRestrictions.entrySet()) {
			type = entry.getKey().type;
			fieldName = entry.getKey().fieldName;
			
			PathContainer<T> pContainer = createPath(root, fieldName);
			
			switch (type) {
			case LIKE:
				orPredicates.add(cb.like(cb.lower(pContainer.p.get(pContainer.finalField)), addWildcard(entry.getValue().toString().toLowerCase())));
				break;
			case EQUALS:
				orPredicates.add(cb.equal(pContainer.p.get(pContainer.finalField), entry.getValue()));
				break;
			case IS_NULL:
				andPredicates.add(cb.isNull(pContainer.p.get(pContainer.finalField)));
				break;
			default:
				break;
			}
		}
		
		// transform order
		for (val entry : this.orders.entrySet()) {
			type = entry.getValue();
			
			PathContainer<T> pContainer = createPath(root, entry.getKey());
			
			switch (type) {
			case ASC:
				orders.add(cb.asc(pContainer.p.get(pContainer.finalField)));
				break;
			case DESC:
				orders.add(cb.desc(pContainer.p.get(pContainer.finalField)));
				break;
			default:
				break;
			}
		}
		
		// build criteria
		if(andPredicates.size() > 0 && orPredicates.size() == 0) {
			// no need to make new predicate, it is already a conjunction
			cq.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
		} else if(andPredicates.size() == 0 && orPredicates.size() > 0) {
			// make a disjunction
			Predicate p = cb.disjunction();
		    p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		    cq.where(p);
		} else if(andPredicates.size() > 0 && orPredicates.size() > 0) {
			// both types of statements combined
		    Predicate o = cb.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		    Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		    cq.where(o, p);
		} else {
			cq.where();
		}
		if(orders.size() > 0) {
			cq.orderBy(orders);
		}
		
		return em.createQuery(cq);
	}
	
	/**
	 * Create the path to the second last last path element. Return the path an the last path fieldName.
	 * 
	 * @param root
	 * @param fieldName
	 * @return
	 */
	private <T extends GenericEntity<? extends GenericPK>> PathContainer<T> createPath(Root<T> root, String fieldName) {
		Path<T> path = root;
		String[] names = StringUtils.split(fieldName, '.');
		String finalField = names[0];
		
		for (int i = 0; i < names.length-1; i++) {
			path = path.get(names[i]);
			finalField = names[i+1];
		}
		
		return new PathContainer<T>(path, finalField);
	}
	
	/**
	 * Surround String with wildcards.
	 * 
	 * @param value
	 * @return
	 */
	private static String addWildcard(String value) {
		return "%" + value + "%";
	}
	
	/**
	 * Class to combine a criteria type with a fieldName;
	 * 
	 * @author Philipp
	 *
	 */
	@EqualsAndHashCode(doNotUseGetters = true)
	@AllArgsConstructor
	private static class CriteriaClause {
		
		protected CriteriaType type;
		protected String fieldName;
	}
	
	/**
	 * Wrapper for Path and last fieldName of path.
	 * 
	 * @author Philipp
	 *
	 * @param <T>
	 */
	@EqualsAndHashCode(doNotUseGetters = true)
	@AllArgsConstructor
	private static class PathContainer<T> {
		protected Path<T> p;
		protected String finalField;
	}

}