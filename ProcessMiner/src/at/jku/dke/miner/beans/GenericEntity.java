package at.jku.dke.miner.beans;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * Root entity for all persistence entities.
 * 
 * @author Philipp
 *
 * @param <ID> a generic primary key
 */
@MappedSuperclass
public abstract class GenericEntity<ID extends GenericPK> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7939838273153004262L;
	
	public abstract ID getId();
	public abstract void setId(ID id);
	
	/**
	 * Check if this is a new bean, or if the bean is already persisted.
	 * 
	 * @return
	 */
	public boolean isNew() {
		return getId() == null;
	}
	
	/**
	 * Override to do some things (e.g. initialization of custom id) directly before persisting the bean.
	 */
	public void preCreate() {
	}
	
	/**
	 * Override to do some things directly before merging the bean.
	 */
	public void preUpdate() {
	}
	
	/**
	 * Override to do some things directly before removing the bean.
	 */
	public void preDelete() {
	}
	
	/**
	 * Override to do some things directly after adding bean.
	 */
	public void postCreate() {
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericEntity<?> other = (GenericEntity<?>) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}