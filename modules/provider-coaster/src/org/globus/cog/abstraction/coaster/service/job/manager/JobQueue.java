//----------------------------------------------------------------------
//This code is developed as part of the Java CoG Kit project
//The terms of the license can be found at http://www.cogkit.org/license
//This message may not be removed or altered.
//----------------------------------------------------------------------

/*
 * Created on Feb 13, 2008
 */
package org.globus.cog.abstraction.coaster.service.job.manager;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.globus.cog.abstraction.coaster.service.LocalTCPService;
import org.globus.cog.abstraction.interfaces.ExecutionService;
import org.globus.cog.abstraction.interfaces.JobSpecification;
import org.globus.cog.abstraction.interfaces.Service;
import org.globus.cog.abstraction.interfaces.Task;

public class JobQueue {
	public static final Logger logger = Logger.getLogger(JobQueue.class);
	
    private AbstractQueueProcessor local, coaster;
    
    public JobQueue(LocalTCPService localService) throws IOException {
        local = new LocalQueueProcessor();
        coaster = newQueueProcessor(localService);
    }
    
    private AbstractQueueProcessor newQueueProcessor(LocalTCPService localService) {
    	String qp = System.getProperty("coaster.qp");
    	if (qp == null) {
    		qp = "block";
    	}
    	if ("block".equals(qp)) {
    	    BlockQueueProcessor bqp = new BlockQueueProcessor();
    	    Collection<URI> addrs = bqp.getSettings().getLocalContacts(localService.getPort());
    	    if (addrs == null) {
    	        bqp.getSettings().setCallbackURI(localService.getContact());
    	    }
    	    else {
    	        bqp.getSettings().setCallbackURIs(addrs);
    	    }
    	    return bqp;
    	}
    	else if ("passive".equals(qp)) {
    		return new PassiveQueueProcessor(localService.getContact());
    	}
    	else {
    		throw new IllegalArgumentException("Invalid queue processor requested: " + qp);
    	}
    }
    
    

    public void start() {
        local.start();
        coaster.start();
    }

    public void enqueue(Task t) {
        Service s = t.getService(0);
        String jm = null;
        JobSpecification spec = (JobSpecification) t.getSpecification();
        if (s instanceof ExecutionService) {
            jm = ((ExecutionService) s).getJobManager();
        }
        if (spec.isBatchJob()) {
        	if (logger.isInfoEnabled()) {
        		logger.info("Job batch mode flag set. Routing through local queue.");
        	}
        }
        if (s.getProvider().equalsIgnoreCase("coaster") && !spec.isBatchJob()) {
            if (logger.isInfoEnabled()) {
                logger.info("Adding task " + t + " to coaster queue");
            }
            coaster.enqueue(t);
        }
        else {
            if (logger.isInfoEnabled()) {
                logger.info("Adding task " + t + " to local queue");
            }
            local.enqueue(t);
        }
    }
    
    public QueueProcessor getCoasterQueueProcessor() {
        return coaster;
    }

    public void shutdown() {
        local.shutdown();
        coaster.shutdown();
    }
}