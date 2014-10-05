/*
 * Copyright 2012 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * Created on Dec 26, 2006
 */
package org.griphyn.vdl.karajan.lib;

import k.rt.ExecutionException;
import k.rt.Stack;

import org.globus.cog.karajan.analyzer.ArgRef;
import org.globus.cog.karajan.analyzer.ChannelRef;
import org.globus.cog.karajan.analyzer.Signature;
import org.griphyn.vdl.mapping.DSHandle;
import org.griphyn.vdl.mapping.InvalidPathException;

public class FileName extends SwiftFunction {
    private ArgRef<DSHandle> var;
    private ChannelRef<String> cr_vargs;

    @Override
    protected Signature getSignature() {
        return new Signature(params("var"));
    }

    @Override
	public Object function(Stack stack) {
		DSHandle var = this.var.getValue(stack);
		try {
            return var.map(var.getPathFromRoot());
        }
        catch (InvalidPathException e) {
            throw new ExecutionException(this, e);
        }
	}
}
