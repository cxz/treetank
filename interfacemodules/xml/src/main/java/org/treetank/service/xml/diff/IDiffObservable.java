/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.service.xml.diff;

import org.treetank.data.interfaces.ITreeStructData;
import org.treetank.exception.TTException;
import org.treetank.service.xml.diff.DiffFactory.EDiff;

/**
 * Observable class to fire diffs for interested observers, which implement the {@link IDiffObserver}
 * interface.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
interface IDiffObservable {
    /**
     * Fire a diff for exactly one node comparsion. Must call the
     * diffListener(EDiff) method defined in the {@link IDiffObserver} interface.
     * 
     * @param paramDiff
     *            the encountered diff
     * @param paramNewNode
     *            current {@link ITreeStructData} in new revision
     * @param paramOldNode
     *            current {@link ITreeStructData} in old revision
     * @param paramDepth
     *            current {@link DiffDepth} instance
     */
    void fireDiff(final EDiff paramDiff, final ITreeStructData paramNewNode, final ITreeStructData paramOldNode,
        final DiffDepth paramDepth);

    /**
     * Diff computation done, thus inform listeners.
     * 
     * @throws TTException
     *             if closing transactions failes
     */
    void done() throws TTException;

    /**
     * Add an observer. This means add an instance of a class which implements
     * the {@link IDiffObserver} interface.
     * 
     * @param paramObserver
     *            instance of the class which implements {@link IDiffObserver}.
     */
    void addObserver(final IDiffObserver paramObserver);

    /**
     * Remove an observer.
     * 
     * @param paramObserver
     *            instance of the class which implements {@link IDiffObserver}.
     */
    void removeObserver(final IDiffObserver paramObserver);
}
