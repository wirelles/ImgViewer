package imageviewer;


import org.eclipse.swt.widgets.Composite;

/**
 * All UI components which follow the builder pattern must implement this interface.
 * Parameterized because we may want to chain builder methods.
 * 
 * @author ggrec
 *
 * @param <T> The type of the implementing widget
 */
public interface IAEFContainer<T extends IAEFContainer<T>>
{

	T build(Composite parent);

	T setLayoutData(Object layoutData);

	boolean isBuilt();

}
