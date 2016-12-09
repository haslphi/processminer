package at.jku.dke.miner.beans;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Embeddable
public class ActivityPK extends GenericPK {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094142565507650775L;

	public static final String COLUMN_ID = "id";
	
	@Column(name = COLUMN_ID, length = 1, nullable = false)
	private String id;
	

}
