//----------------------------------------------------------------------
//This code is developed as part of the Java CoG Kit project
//The terms of the license can be found at http://www.cogkit.org/license
//This message may not be removed or altered.
//----------------------------------------------------------------------

/*
 * Created on Oct 20, 2005
 */
package org.globus.cog.abstraction.impl.scheduler.moab;

// import org.apache.log4j.Logger;
import org.globus.cog.abstraction.impl.scheduler.common.AbstractProperties;

public class Properties extends AbstractProperties {

    private static final long serialVersionUID = 1L;

    // private static Logger logger = 
    // Logger.getLogger(Properties.class);

	public static final String PROPERTIES = "provider-moab.properties";
	
	public static final String POLL_INTERVAL = "poll.interval";
	public static final String QSUB = "moab-qsub";
	public static final String QSTAT = "moab-qstat";
	public static final String QDEL = "moab-qdel";
	public static final String USE_MPPWIDTH = "use.mppwidth";

	private static Properties properties;

	public static synchronized Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			properties.load(PROPERTIES);
		}
		return properties;
	}
	
	protected void setDefaults() {
		setPollInterval(5);
		setSubmitCommand("moab-qsub");
		setPollCommand("moab-qstat");
		setRemoveCommand("moab-qdel");
	}

	public String getPollCommandName() {
		return QSTAT;
	}


	public String getRemoveCommandName() {
		return QDEL;
	}


	public String getSubmitCommandName() {
		return QSUB;
	}
}
