

package org.treetank.xpath.comparators;

import org.treetank.api.IAxis;
import org.treetank.api.IReadTransaction;
import org.treetank.utils.TypedValue;
import org.treetank.xpath.AtomicValue;
import org.treetank.xpath.functions.XPathError;
import org.treetank.xpath.functions.XPathError.ErrorType;
import org.treetank.xpath.types.Type;

/**
 * <h1>NodeComp</h1>
 * <p>
 * Node comparisons are used to compare two nodes, by their identity or by their
 * document order.
 * </p>
 * 
 * @author Tina Scherer
 */
public class NodeComp extends AbstractComparator implements IAxis {

  /**
   * Constructor. Initializes the internal state.
   * 
   * @param rtx
   *          Exclusive (immutable) trx to iterate with.
   * @param operand1
   *          First value of the comparison
   * @param operand2
   *          Second value of the comparison
   * @param comp
   *          comparison kind
   */
  public NodeComp(final IReadTransaction rtx, final IAxis operand1,
      final IAxis operand2, final CompKind comp) {

    super(rtx, operand1, operand2, comp);
  }

  /**
   * {@inheritDoc}
   */
  protected AtomicValue[] atomize(final IAxis operand) {

    final IReadTransaction rtx = getTransaction();
    // store item key as atomic value
    AtomicValue atomized = new AtomicValue(TypedValue.getBytes(((Long) rtx
        .getNodeKey()).toString()), rtx.keyForName("xs:integer"));
    final AtomicValue[] op = { atomized };

    // the operands must be singletons in case of a node comparison
    if (operand.hasNext()) {
      throw new XPathError(ErrorType.XPTY0004);
    } else {
      return op;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Type getType(final int key1, final int key2) {

    return Type.INTEGER;

  }

  /**
   * {@inheritDoc}
   */
  protected boolean compare(final AtomicValue[] operand1,
      final AtomicValue[] operand2) {

    final String op1 = TypedValue.parseString(operand1[0].getRawValue());
    final String op2 = TypedValue.parseString(operand2[0].getRawValue());

    return getCompKind().compare(op1, op2,
        getType(operand1[0].getTypeKey(), operand2[0].getTypeKey()));
  }

}