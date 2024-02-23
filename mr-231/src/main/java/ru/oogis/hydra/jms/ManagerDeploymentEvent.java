package ru.oogis.hydra.jms;

import java.io.Serializable;

public class ManagerDeploymentEvent implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	private boolean active;
	private String managerId;

	public ManagerDeploymentEvent(String p_managerId, boolean p_active)
	{
		managerId = p_managerId;
		active = p_active;
	}

	public String getManagerId()
	{
		return managerId;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean p_active)
	{
		active = p_active;
	}

	public void setManagerId(String p_managerId)
	{
		managerId = p_managerId;
	}

}
