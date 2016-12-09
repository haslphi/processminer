package at.jku.dke.miner.dao.impl;

import at.jku.dke.miner.beans.Activity;
import at.jku.dke.miner.dao.IActivityDAO;
import at.jku.dke.miner.dao.base.impl.GenericDAO;

public class ActivityDAO extends GenericDAO<Activity> implements IActivityDAO {

	@Override
	public Class<Activity> getType() {
		return Activity.class;
	}

}
