
package org.treetank.xpath.expr;

import java.util.ArrayList;
import java.util.List;

import org.treetank.api.IAxis;
import org.treetank.api.IReadTransaction;
import org.treetank.axislayer.AbstractAxis;

/**
 * <h1>VariableAxis</h1>
 * <p>
 * Evaluated the given binding sequence, the variable is bound to and stores in
 * a list that can be accessed by other sequences and notifies its observers, as
 * soon as a new value of the binding sequence has been evaluated.
 * </p>
 * 
 * @author Tina Scherer
 */
public class VariableAxis extends AbstractAxis implements IAxis {

  // /**
  // * The result values, the variable is bound to (that have been evaluated so
  // * far).
  // */
  // private final List<Long> mValues;

  /** Sequence that defines the values, the variable is bound to. */
  private final IAxis bindingSeq;

  private final List<VarRefExpr> mVarRefs;

  /**
   * Constructor. Initializes the internal state.
   * 
   * @param rtx
   *          Exclusive (immutable) trx to iterate with.
   * @param inSeq
   *          sequence, the variable is bound to.
   */
  public VariableAxis(final IReadTransaction rtx, final IAxis inSeq) {

    super(rtx);
    // mValues = values;
    bindingSeq = inSeq;
    mVarRefs = new ArrayList<VarRefExpr>();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void reset(final long nodeKey) {
    super.reset(nodeKey);
    if (bindingSeq != null) {
      bindingSeq.reset(nodeKey);
    }
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() {

    resetToLastKey();

    if (bindingSeq.hasNext()) {
      // mValues.add(getTransaction().getNodeKey());
      notifyObs();
      return true;
    }

    resetToStartKey();
    return false;

  }

  /**
   * Tell all observers that the a new item of the binding sequence has been
   * evaluated.
   */
  private void notifyObs() {

    for (VarRefExpr varRef : mVarRefs) {
      varRef.update(getTransaction().getNodeKey());
    }
  }

  /**
   * Add an observer to the list.
   * 
   * @param observer  axis that wants to be notified of any change of this axis
   */
  public void addObserver(final VarRefExpr observer) {

    mVarRefs.add(observer);
  }

}
