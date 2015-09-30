package imageviewer;



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Default implementation for a UI component which follows the builder pattern
 * 
 * @author ggrec
 *
 */
@SuppressWarnings("unchecked")
abstract public class AEFContainer<T extends AEFContainer<T>> implements IAEFContainer<T>
{

	// ====================== 2. Instance Fields =============================

	private Composite container;

	private Object layoutData;


	// ==================== 5. Creators ====================

	@Override
	final public T build(final Composite parent)
	{
		if (!isBuilt())
		{
			container = new Composite(parent, SWT.NULL);
			container.setLayout(new GridLayout());
			updateLayoutData();

			createContent(container);
		}

		return (T) this;
	}

	abstract protected void createContent(Composite parent);


	// ==================== 6. Action Methods ====================

	private void updateLayoutData()
	{
		if (isBuilt() && layoutData != null)
			container.setLayoutData(layoutData);
	}


	protected void preDispose()
	{

	}


	final public T dispose()
	{
		if (isBuilt())
		{
			preDispose();

			container.dispose();
			container = null;
		}

		return (T) this;
	}


	final public T setVisible(final boolean visible)
	{
		if (isBuilt())
		{
			container.setVisible(visible);

			if (layoutData instanceof GridData)
			{
				((GridData)layoutData).exclude = !visible;
				layoutParent();
			}
		}

		return (T) this;
	}


	final public boolean isVisible()
	{
		return isBuilt() && container.isVisible();
	}


	/**
	 * @param neighbour The AEF Container next to which to move this
	 * @param above TRUE for Above, FALSE for below.
	 */
	final public T move(final AEFContainer<?> neighbour, final boolean above)
	{
		return move(neighbour.container, above);
	}


	/**
	 * @param neighbour The control next to which to move this' container
	 * @param above TRUE for Above, FALSE for below.
	 */
	final public T move(final Control neighbour, final boolean above)
	{
		if (isBuilt())
		{
			if (above)
				container.moveAbove(neighbour);
			else
				container.moveBelow(neighbour);

			layoutParent();
		}

		return (T) this;
	}


	/**
	 * Only call when the container is built
	 */
	private void layoutParent()
	{
		container.getParent().layout();
	}


	// ==================== 7. Getters & Setters ====================

	@Override
	final public boolean isBuilt()
	{
		return container != null && !container.isDisposed();
	}


	@Override
	final public T setLayoutData(final Object layoutData)
	{
		this.layoutData = layoutData;
		updateLayoutData();
		return (T) this;
	}

}
