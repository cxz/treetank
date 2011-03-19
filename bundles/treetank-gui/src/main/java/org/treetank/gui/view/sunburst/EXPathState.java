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
package org.treetank.gui.view.sunburst;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * XPath enum to determine if current item is found by an XPath expression or not.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
enum EXPathState {
    /** Item is found. */
    ISFOUND {
        /**
         * {@inheritDoc}
         */
        @Override
        void setStroke(final PApplet paramParent, final int paramColor) {
            paramParent.stroke(1);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void setStrokeBuffer(final PGraphics paramBuffer, final int paramColor) {
            paramBuffer.stroke(1);
        }
    },

    /** Default: Item is not found. */
    ISNOTFOUND {
        /**
         * {@inheritDoc}
         */
        @Override
        void setStroke(final PApplet paramParent, final int paramColor) {
            paramParent.stroke(paramColor);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void setStrokeBuffer(final PGraphics paramBuffer, final int paramColor) {
            paramBuffer.stroke(paramColor);
        }
    };

    /**
     * Set stroke.
     * 
     * @param paramParent
     *            parent processing buffer
     * @param paramColor
     *            the color to use
     */
    abstract void setStroke(final PApplet paramParent, final int paramColor);
    
    /**
     * Set stroke buffer.
     * 
     * @param paramBuffer
     *            Processing {@link PGraphics}
     * @param paramColor
     *            the color to use
     */
    abstract void setStrokeBuffer(final PGraphics paramBuffer, final int paramColor);
}