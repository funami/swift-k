/*
 * Swift Parallel Scripting Language (http://swift-lang.org)
 * Code from Java CoG Kit Project (see notice below) with modifications.
 *
 * Copyright 2005-2014 University of Chicago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//----------------------------------------------------------------------
//This code is developed as part of the Java CoG Kit project
//The terms of the license can be found at http://www.cogkit.org/license
//This message may not be removed or altered.
//----------------------------------------------------------------------

/*
 * Created on Nov 24, 2013
 */
package org.globus.cog.abstraction.coaster.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.cog.abstraction.coaster.service.local.LocalRequestManager;
import org.globus.cog.abstraction.impl.execution.coaster.WorkerShellCommand;
import org.globus.cog.coaster.ProtocolException;
import org.globus.cog.coaster.channels.ChannelException;
import org.globus.cog.coaster.channels.ChannelManager;
import org.globus.cog.coaster.channels.CoasterChannel;
import org.globus.cog.coaster.commands.Command;
import org.globus.cog.coaster.commands.Command.Callback;
import org.globus.cog.coaster.commands.InfoCommand;

public class CoasterClient implements Callback {
    private String url;
    
    public CoasterClient(String url) {
       List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
       loggers.add(LogManager.getRootLogger());
       for ( Logger logger : loggers ) {
          logger.setLevel(Level.OFF);
       }

        this.url = url;
    }
    
    public void runCommand(String cmd, String[] args) 
            throws ChannelException, ProtocolException, IOException, InterruptedException {
        Command c = null;
        if (cmd.equals("submitjob")) {
            // TODO this would need to parse the args in a meaningful way
            // and feed them to a SubmitJobCommand
        }
        else if (cmd.equals("list")) {
            c = new InfoCommand(args[0], args.length > 1 ? args[1] : "");
        }
        else if (cmd.equals("runcmd")) {
            c = new WorkerShellCommand(args[0], join(args, 1, " ")) {
                @Override
                public void handleSignal(byte[] data) {
                    // live output is sent as signals
                    System.out.print(new String(data));
                }
            };
        }
        if (c == null) {
            System.err.println("Command not handled: " + cmd);
        }
        else {
            CoasterChannel channel = ChannelManager.getManager().getOrCreateChannel(url, 
                null, LocalRequestManager.INSTANCE);
            // do async execute since we can process the
            // replies/errors instead of having exceptions thrown by execute()
            c.executeAsync(channel, this);
            c.waitForReply();
        }
    }

    @Override
    public void replyReceived(Command cmd) {
        List<byte[]> reply = cmd.getInDataChunks();
        for (byte[] b : reply) {
            System.out.println(new String(b));
        }
    }

    @Override
    public void errorReceived(Command cmd, String msg, Exception t) {
        System.err.println(msg);
        t.printStackTrace();
    }

    private String join(String[] a, int start, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = start; i < a.length; i++) {
            if (first) {
                first = false;
            }
            else {
                sb.append(sep);
            }
            sb.append(a[i]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            String url = args[0];
            String cmd = args[1];
            String[] a = new String[args.length - 2];
            System.arraycopy(args, 2, a, 0, a.length);
            CoasterClient client = new CoasterClient(url);
            client.runCommand(cmd, a);
            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
