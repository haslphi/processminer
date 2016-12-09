package at.jku.dke.miner.beans;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * Generic primary key class, to force all descendants to override hashCode and equals method.
 * 
 * @author Philipp
 *
 */
@MappedSuperclass
public abstract class GenericPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7658933779344885249L;

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
