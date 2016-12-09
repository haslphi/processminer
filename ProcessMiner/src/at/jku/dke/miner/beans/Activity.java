package at.jku.dke.miner.beans;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "miner.Activity")
@Getter
@Setter
@NoArgsConstructor
public class Activity extends GenericEntity<ActivityPK> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8033913946477870301L;
	
	public static final String COLUMN_ACTVITY = "activity";
	public static final String COLUMN_SORT_NO = "sortNo";
	
	@EmbeddedId
	private ActivityPK id;
	
	@Column(name = COLUMN_ACTVITY, length = 50, nullable = false)
	private String activity;
	
	@Column(name = COLUMN_SORT_NO, nullable = false)
	private Integer sortNo;
	
	@Override
	public ActivityPK getId() {
		return id;
	}

	@Override
	public void setId(ActivityPK id) {
		this.id = id;
	}
	
}
