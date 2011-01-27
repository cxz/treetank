/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */
package com.treetank.gui.view;

import javax.swing.JComponent;

/**
 * <h1>IView</h1>
 * 
 * <p>
 * Interface every view has to implement.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public interface IView {
    /**
     * Get name of the view.
     * 
     * @return name of the view
     */
    String name();
    
    /**
     * Called when the data reference has changed.
     */
    void refreshInit();

    /**
     * Called when updates have been done regarding to the data structure.
     */
    void refreshUpdate();

    /**
     * Returns if this view is currently visible.
     * 
     * @return result of check
     */
    boolean isVisible();

    /**
     * Called when frame is going to dispose.
     */
    void dispose();
    
    /**
     * Get the component.
     * 
     * @return the component
     */
    JComponent component();
}
